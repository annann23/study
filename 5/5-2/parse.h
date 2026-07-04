#ifndef PARSE_H
#define PARSE_H

#include "lexer.h"

typedef enum
{
    NODE_FUNCTION, // type identifier "(" ")" 블록
    NODE_DECLARE,
    NODE_ASSIGN,
    NODE_IF,
    NODE_IF_BODY, // if/else 부모
    NODE_WHILE,
    NODE_RETURN,
    NODE_BLOCK,
    NODE_BINARY_EXPR,
    NODE_IDENTIFIER,
    NODE_NUMBER
} NodeType;

typedef struct Node
{
    NodeType nodeType;
    TokenType tokenType;
    char value[64];
    struct Node *left;
    struct Node *right;
    struct Node *next;
} Node;

typedef struct
{
    Token *current;
} Parser;

Node *parseFunction(Parser *p);
void printAST(Node *node, int depth);

#endif
