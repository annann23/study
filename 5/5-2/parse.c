#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include "parse.h"

Node *newNode(NodeType type);
Token *peek(Parser *p);
bool checkType(Parser *p, TokenType type);
Token *advance(Parser *p);
Token *expect(Parser *p, TokenType type);

Node *parseAdditive(Parser *p);
Node *parsePrimary(Parser *p);
Node *parseCondition(Parser *p);
Node *parseBlock(Parser *p);

Node *parseStatement(Parser *p);
Node *parseDeclare(Parser *p);
Node *parseAssign(Parser *p);
Node *parseIf(Parser *p);
Node *parseWhile(Parser *p);
Node *parseReturn(Parser *p);

static const char *nodeTypeName(NodeType type);

Node *newNode(NodeType type)
{
    Node *node = (Node *)malloc(sizeof(Node));
    node->nodeType = type;
    node->tokenType = ILLEGAL;
    node->value[0] = '\0';
    node->left = NULL;
    node->right = NULL;
    node->next = NULL;
    return node;
}

Token *peek(Parser *p)
{
    return p->current;
}

bool checkType(Parser *p, TokenType type) {
    return p->current != NULL && p->current->type == type;
}

Token *advance(Parser *p) {
    Token *token = p->current;
    if (p->current != NULL)
    {
        p->current = p->current->next;
    }
    return token;
}

Token *expect(Parser *p, TokenType type) {
    if (!checkType(p, type))
    {
        fprintf(stderr, "Parse error: expected %s but got %s\n",
                tokenName(type),
                p->current != NULL ? tokenName(p->current->type) : "EOF");
        exit(1);
    }
    return advance(p);
}

Node *parsePrimary(Parser *p) {
    if (checkType(p, IDENTIFIER))
    {
        Token *token = advance(p);
        Node *node = newNode(NODE_IDENTIFIER);
        strncpy(node->value, token->value, sizeof(node->value) - 1);
        return node;
    }

    if (checkType(p, NUMBER))
    {
        Token *token = advance(p);
        Node *node = newNode(NODE_NUMBER);
        strncpy(node->value, token->value, sizeof(node->value) - 1);
        return node;
    }

    if (checkType(p, LPAREN))
    {
        advance(p);
        Node *inner = parseAdditive(p);
        expect(p, RPAREN);
        return inner;
    }

    fprintf(stderr, "Parse error: unexpected token %s in expression\n",
            peek(p) != NULL ? tokenName(peek(p)->type) : "EOF");
    exit(1);
}

Node *parseAdditive(Parser *p)
{
    Node *left = parsePrimary(p);

    while (checkType(p, PLUS) || checkType(p, MINUS))
    {
        TokenType op = advance(p)->type;
        Node *right = parsePrimary(p);

        Node *binary = newNode(NODE_BINARY_EXPR);
        binary->tokenType = op;
        binary->left = left;
        binary->right = right;

        left = binary;
    }

    return left;
}

// condition = identifier rel_op (number | identifier) ;
Node *parseCondition(Parser *p) {
    Node *left = parsePrimary(p);

    TokenType op = peek(p) != NULL ? peek(p)->type : ILLEGAL;
    if (op != EQUAL && op != NEQUAL && op != LT && op != LTE && op != GT && op != GTE)
    {
        fprintf(stderr, "Parse error: expected relational operator but got %s\n",
                peek(p) != NULL ? tokenName(peek(p)->type) : "EOF");
        exit(1);
    }
    advance(p);

    Node *right = parsePrimary(p);

    Node *node = newNode(NODE_BINARY_EXPR);
    node->tokenType = op;
    node->left = left;
    node->right = right;

    return node;
}

// block_statement = "{" { statement } "}"
Node *parseBlock(Parser *p) {
    expect(p, LBRACE);

    Node *head = NULL;
    Node *tail = NULL;

    while (!checkType(p, RBRACE) && peek(p) != NULL)
    {
        Node *stmt = parseStatement(p);
        if (head == NULL)
        {
            head = stmt;
            tail = stmt;
        }
        else
        {
            tail->next = stmt;
            tail = stmt;
        }
    }

    expect(p, RBRACE);

    return head;
}


//statement = assign | declare | if | while | return
Node *parseStatement(Parser *p) {
    if (peek(p) == NULL)
    {
        return NULL;
    }

    switch (peek(p)->type)
    {
    case INT:
        return parseDeclare(p);
    case IDENTIFIER:
        return parseAssign(p);
    case IF:
        return parseIf(p);
    case WHILE:
        return parseWhile(p);
    case RETURN:
        return parseReturn(p);
    default:
        fprintf(stderr, "Parse error: unexpected token %s at statement start\n",
                tokenName(peek(p)->type));
        exit(1);
    }
}

// declare = "int" identifier [ assgin_op  ((identifier arith_op number) | number )]";"
Node *parseDeclare(Parser *p) {
    expect(p, INT);

    Token *nameToken = expect(p, IDENTIFIER);
    Node *target = newNode(NODE_IDENTIFIER);
    strncpy(target->value, nameToken->value, sizeof(target->value) - 1);

    Node *node = newNode(NODE_DECLARE);
    node->left = target;

    if (checkType(p, ASSIGN))
    {
        advance(p);
        node->right = parseAdditive(p); // 초기화 없으면 NULL로 남음
    }

    expect(p, SEMICOLON);

    return node;
}

//assign = identifier assgin_op ((identifier arith_op number) | number ) ";"
Node *parseAssign(Parser *p) {
    Token *nameToken = expect(p, IDENTIFIER);
    Node *target = newNode(NODE_IDENTIFIER);
    strncpy(target->value, nameToken->value, sizeof(target->value) - 1);

    expect(p, ASSIGN);
    Node *value = parseAdditive(p);
    expect(p, SEMICOLON);

    Node *node = newNode(NODE_ASSIGN);
    node->tokenType = ASSIGN;
    node->left = target;
    node->right = value;

    return node;
}

// if = "if" func_statement ["else" "{" { statement } "}" ] ;
Node *parseIf(Parser *p) {
    expect(p, IF);
    expect(p, LPAREN);
    Node *condition = parseCondition(p);
    expect(p, RPAREN);
    Node *thenBlock = parseBlock(p);

    Node *body = newNode(NODE_IF_BODY);
    body->left = thenBlock;

    if (checkType(p, ELSE))
    {
        advance(p);
        body->right = parseBlock(p); // else 없으면 NULL로 남음
    }

    Node *node = newNode(NODE_IF);
    node->left = condition;
    node->right = body;

    return node;
}

// while = "while" func_statement ;
Node *parseWhile(Parser *p) {
    expect(p, WHILE);
    expect(p, LPAREN);
    Node *condition = parseCondition(p);
    expect(p, RPAREN);
    Node *body = parseBlock(p);

    Node *node = newNode(NODE_WHILE);
    node->left = condition;
    node->right = body;

    return node;
}

//return = "return" { number | identifier } ";"
Node *parseReturn(Parser *p) {
    expect(p, RETURN);
    Node *value = parsePrimary(p);
    expect(p, SEMICOLON);

    Node *node = newNode(NODE_RETURN);
    node->right = value;

    return node;
}

// func_statement = "int" identifier "(" ")" block ;
Node *parseFunction(Parser *p) {
    expect(p, INT);
    Token *nameToken = expect(p, IDENTIFIER);

    expect(p, LPAREN);
    expect(p, RPAREN);

    Node *body = parseBlock(p);

    Node *node = newNode(NODE_FUNCTION);
    strncpy(node->value, nameToken->value, sizeof(node->value) - 1);
    node->right = body;

    return node;
}

static const char *nodeTypeName(NodeType type) {
    switch (type)
    {
    case NODE_FUNCTION: return "Function";
    case NODE_DECLARE: return "Declare";
    case NODE_ASSIGN: return "Assign";
    case NODE_IF: return "If";
    case NODE_IF_BODY: return "IfBody";
    case NODE_WHILE: return "While";
    case NODE_RETURN: return "Return";
    case NODE_BLOCK: return "Block";
    case NODE_BINARY_EXPR: return "BinaryExpr";
    case NODE_IDENTIFIER: return "Identifier";
    case NODE_NUMBER: return "Number";
    }
    return "?";
}

void printAST(Node *node, int depth)
{
    for (; node != NULL; node = node->next)
    {
        for (int i = 0; i < depth; i++)
        {
            printf("  ");
        }

        if (node->nodeType == NODE_BINARY_EXPR || node->nodeType == NODE_ASSIGN)
        {
            printf("%s(%s)\n", nodeTypeName(node->nodeType), tokenName(node->tokenType));
        }
        else if (node->nodeType == NODE_IDENTIFIER || node->nodeType == NODE_NUMBER || node->nodeType == NODE_FUNCTION)
        {
            printf("%s(%s)\n", nodeTypeName(node->nodeType), node->value);
        }
        else
        {
            printf("%s\n", nodeTypeName(node->nodeType));
        }

        printAST(node->left, depth + 1);
        printAST(node->right, depth + 1);
    }
}
