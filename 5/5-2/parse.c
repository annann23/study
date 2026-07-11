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
Token *expectRelop(Parser *p);
Node *parseBlock(Parser *p);

Node *parseStatement(Parser *p);
Node *parseDeclare(Parser *p);
Node *parseAssign(Parser *p);
Node *parseIf(Parser *p);
Node *parseWhile(Parser *p);
Node *parseReturn(Parser *p);

static const char *nodeTypeName(NodeType type);

void addVariables(Scope* scope, const char* name);
int lookupVariable(Scope* scope, const char* name); 
int isVariableDeclared(Scope* scope, const char* name);

Node *newNode(NodeType type)
{
    Node *node = (Node *)malloc(sizeof(Node));
    node->nodeType = type;
    node->tokenType = ILLEGAL;
    node->value[0] = '\0';
    node->children = NULL; 
    node->childCount = 0;
    node->childCapacity = 0;
    return node;
}

void addChild(Node* parent, Node* child) {
    if (parent->childCount == parent->childCapacity) {
        parent->childCapacity = parent->childCapacity == 0 ? 2 : parent->childCapacity * 2;
        parent->children = realloc(parent->children, sizeof(Node*) * parent->childCapacity);
    }
    parent->children[parent->childCount++] = child;
}

Token *peek(Parser *p) {
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
        addChild(binary, left);
        addChild(binary, right);

        left = binary;
    }

    return left;
}

// condition = identifier rel_op (number | identifier) ;
Node *parseCondition(Parser *p) {
    Node *left = parsePrimary(p);
    Token *relopToken = expectRelop(p);
    Node *right = parsePrimary(p);

    Node *node = newNode(NODE_BINARY_EXPR);
    node->tokenType = relopToken->type;
    addChild(node, left);
    addChild(node, right);

    return node;
}

Token *expectRelop(Parser *p) {
    TokenType type = peek(p) != NULL ? peek(p)->type : ILLEGAL;
    if (type == EQUAL || type == NEQUAL ||
        type == LT    || type == LTE    ||
        type == GT    || type == GTE) {
        return advance(p);
    }
    fprintf(stderr, "Parse error: expected relational operator but got %s\n",
        peek(p) != NULL ? tokenName(peek(p)->type) : "EOF");
    exit(1);
}

// block_statement = "{" { statement } "}"
Node *parseBlock(Parser *p) {
    expect(p, LBRACE);

    Node *block = newNode(NODE_BLOCK);  

    while (!checkType(p, RBRACE) && peek(p) != NULL)
    {
        Node *stmt = parseStatement(p);
        addChild(block, stmt); 
    }

    expect(p, RBRACE);
    return block;
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
    addChild(node, target);

    if (checkType(p, ASSIGN))
    {
        advance(p);
        addChild(node, parseAdditive(p));
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
    addChild(node, target);
    addChild(node, value);

    return node;
}

// if = "if" func_statement ["else" "{" { statement } "}" ] ;
Node *parseIf(Parser *p) {
    expect(p, IF);
    expect(p, LPAREN);
    Node *condition = parseCondition(p);
    expect(p, RPAREN);
    Node *thenBlock = parseBlock(p);

    Node *node = newNode(NODE_IF);
    addChild(node, condition); 
    addChild(node, thenBlock); 

   while (checkType(p, ELSE)) {
        advance(p); 

        if (checkType(p, IF)) {
            advance(p);
            expect(p, LPAREN);
            Node *elseIfCondition = parseCondition(p);
            expect(p, RPAREN);
            Node *elseIfBlock = parseBlock(p);

            addChild(node, elseIfCondition);  
            addChild(node, elseIfBlock);
        } else {
            Node *elseBlock = parseBlock(p);
            addChild(node, elseBlock); 
            break; 
        }
    }

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
    addChild(node, condition);
    addChild(node, body);

    return node;
}

//return = "return" { number | identifier } ";"
Node *parseReturn(Parser *p) {
    expect(p, RETURN);
    Node *value = parsePrimary(p);
    expect(p, SEMICOLON);

    Node *node = newNode(NODE_RETURN);
    addChild(node, value);

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
    addChild(node, body);

    return node;
}

static const char *nodeTypeName(NodeType type) {
    switch (type)
    {
    case NODE_FUNCTION: return "Function";
    case NODE_DECLARE: return "Declare";
    case NODE_ASSIGN: return "Assign";
    case NODE_IF: return "If";
    case NODE_WHILE: return "While";
    case NODE_RETURN: return "Return";
    case NODE_BLOCK: return "Block";
    case NODE_BINARY_EXPR: return "BinaryExpr";
    case NODE_IDENTIFIER: return "Identifier";
    case NODE_NUMBER: return "Number";
    }
    return "?";
}

Scope* pushScope(Scope* current) {
    Scope* newScope = (Scope*)malloc(sizeof(Scope));
    newScope->variables = NULL;
    newScope->parent = current;
    
    return newScope;
}

void addVariables(Scope* scope, const char* name) {
    Variable* newVar = (Variable*)malloc(sizeof(Variable));

    strncpy(newVar->name, name, sizeof(newVar->name) - 1);
    newVar->name[sizeof(newVar->name) - 1] = '\0';

    newVar->next = scope->variables;
    scope->variables = newVar;
}

Scope* popScope(Scope* current) {
    Variable* var = current->variables;

    while (var != NULL) {
        Variable* tmp = var;
        var = var->next;
        free(tmp);
    }

    Scope* parent = current->parent;

    free(current);
    return parent;
}

int lookupVariable(Scope* scope, const char* name) {
    while (scope != NULL) {
        Variable* var = scope->variables;
        while (var != NULL) {
            if (strcmp(var->name, name) == 0) {
                return 1; 
            }
            var = var->next;
        }
        scope = scope->parent;
    }
    return 0; 
}

int isVariableDeclared(Scope* scope, const char* name) {
    Variable* var = scope->variables;
    while (var != NULL) {
        if (strcmp(var->name, name) == 0) {
            return 1; 
        }
        var = var->next;
    }
    return 0; 
}

void analyze(Node* node, Scope* currentScope, int* errorCount) {
    for (; node != NULL; node = node->next) {
        switch (node->nodeType) {

            case NODE_BLOCK: {
                Scope* blockScope = pushScope(currentScope);
                for (int i = 0; i < node->childCount; i++) {
                    analyze(node->children[i], blockScope, errorCount);
                }
                popScope(blockScope);
                break;
            }

            case NODE_DECLARE: {
                char* name = node->children[0]->value;
                if (isVariableDeclared(currentScope, name)) {
                    fprintf(stderr, "중복 선언된 변수입니다: '%s'\n", name);
                    (*errorCount)++;
                }
                addVariables(currentScope, name);
                break;
            }

            case NODE_IDENTIFIER: {
                if (!lookupVariable(currentScope, node->value)) {
                    fprintf(stderr, "선언되지 않은 변수입니다: '%s'\n", node->value);
                    (*errorCount)++;
                }
                break;
            }

            default: {
                for (int i = 0; i < node->childCount; i++) {
                    analyze(node->children[i], currentScope, errorCount);
                }
                break;
            }
        }
    }
}

void printAST(Node *node, int depth, const char *prefix, int isLast) {
    for (; node != NULL; node = node->next) {
        int currentIsLast = (node->next == NULL);

        if (strlen(prefix) != 0 || node->next != NULL) {
            printf("%s", prefix);
            printf("%s", currentIsLast ? "└── " : "├── ");
        }

        if (node->nodeType == NODE_BINARY_EXPR || node->nodeType == NODE_ASSIGN) {
            printf("%s(%s)\n", nodeTypeName(node->nodeType), tokenName(node->tokenType));
        } else if (node->nodeType == NODE_IDENTIFIER || node->nodeType == NODE_NUMBER || node->nodeType == NODE_FUNCTION) {
            printf("%s(%s)\n", nodeTypeName(node->nodeType), node->value);
        } else {
            printf("%s\n", nodeTypeName(node->nodeType));
        }

        char newPrefix[256];
        snprintf(newPrefix, sizeof(newPrefix), "%s%s",
                 prefix,
                 currentIsLast ? "    " : "│   ");

        for (int i = 0; i < node->childCount; i++) {
            int childIsLast = (i == node->childCount - 1);
            printAST(node->children[i], depth + 1, newPrefix, childIsLast);
        }
    }
}