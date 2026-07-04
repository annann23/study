#ifndef LEXER_H
#define LEXER_H

typedef enum
{
    ILLEGAL, // illegal token

    // 예약어
    INT,
    IF,
    ELSE,
    WHILE,
    RETURN,

    // 변수명
    IDENTIFIER,
    // 숫자
    NUMBER,
    // 연산자
    ASSIGN, // =
    PLUS,   // +
    MINUS,  // -

    // 관계연산자
    EQUAL,  // ==
    NEQUAL, // !=
    LT,     // <
    LTE,    // <=
    GT,     // >
    GTE,    // >=

    // 구분자
    LPAREN,   // (
    RPAREN,   // )
    LBRACE,   // {
    RBRACE,   // }
    SEMICOLON // ;
} TokenType;

typedef struct Token
{
    TokenType type;
    char value[64];
    struct Token *next;
} Token;

const char *tokenName(TokenType type);
Token *tokenize(char *input);

#endif
