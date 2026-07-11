#ifndef PARSE_H
#define PARSE_H

#include "lexer.h"

typedef enum {
    NODE_FUNCTION, // type identifier "(" ")" 블록
    NODE_DECLARE,
    NODE_ASSIGN,
    NODE_IF,
    NODE_WHILE,
    NODE_RETURN,
    NODE_BLOCK,
    NODE_BINARY_EXPR,
    NODE_IDENTIFIER,
    NODE_NUMBER
} NodeType;

typedef struct Node {
    NodeType nodeType;
    TokenType tokenType;
    char value[64];
    struct Node **children; 
    int childCount; 
    int childCapacity;
    struct Node *next; 
} Node;

typedef struct {
    Token *current;
} Parser;

typedef struct Variable {
    char name[64];
    struct Variable* next;
} Variable;

typedef struct Scope {
    Variable* variables; 
    struct Scope* parent;
} Scope;

Node *parseFunction(Parser *p);
void printAST(Node *node, int depth, const char *prefix, int isLast);

Scope* pushScope(Scope* current);
Scope* popScope(Scope* current);
void analyze(Node* node, Scope* currentScope, int* errorCount);

#endif
