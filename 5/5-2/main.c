#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

typedef enum {
    ILLEGAL, // illegal token

    // 예약어
    INT, 
    IF,
    ELSE,
    WHILE,
    RETURN,

    //연산자
    ASSIGN, // =
    PLUS, // +
    MINUS, // -

    //관계연산자
    EQUAL, // ==
    NEQUAL, // !=
    LT, // <
    LTE, // <=
    GT, // >
    GTE, // >=

    //구분자
    LPAREN,    // (
    RPAREN,    // )
    LBRACE,    // {
    RBRACE,    // }
    SEMICOLON  // ;
} TokenType;

typedef struct Token {
    TokenType type;
    char value[64];
    struct Token* next;
} Token;

Token* tokenize(char* input) {
    Token* head = NULL;
    Token* tail = NULL;
    int pos = 0;

    while (input[pos] != '\0') {
        if(isspace(input[pos])) {
            pos++;
            continue;
        }else if(isalpha(input[pos]) || input[pos] == '_') {
            // letter면 identifier/keyword 읽는 함수 호출
        } else if(isdigit(input[pos])) {
            // digit이면 number 읽는 함수 호출
        } else if(input[pos] == '=' || input[pos] == '!' || input[pos] == '<' || input[pos] == '>') {
            // =, !, <, > 이면 lookahead 필요한 연산자 함수 호출
        } else if(input[pos] == '+' || input[pos] == '-' ||
                  input[pos] == '(' || input[pos] == ')' || 
                  input[pos] == '{' || input[pos] == '}' || 
                  input[pos] == ';') {
            // +, -, /, (, ), {, }, ; 면 즉시 토큰 생성
        } else {
            // 그 외의 문자는 ILLEGAL 토큰으로 처리
            Token* newToken = (Token*)malloc(sizeof(Token));
            newToken->type = ILLEGAL;
            newToken->value[0] = input[pos];
            newToken->value[1] = '\0';
            newToken->next = NULL;

            if (head == NULL) {
                head = newToken;
                tail = newToken;
            } else {
                tail->next = newToken;
                tail = newToken;
            }
        }
    }

    return head;
}

Token* readIdentifierOrKeyword(char* input, int* pos) {
    // 1. 시작 위치 저장
    // 2. letter나 digit인 동안 pos 전진 (q1 → q1 루프)
    // 3. 멈춘 지점까지 문자열 잘라내기 (strncpy 등 고려)
    // 4. 잘라낸 문자열이 keyword 목록에 있는지 비교
    //    (keyword 목록: "int", "if", "else", "while", "return")
    // 5. 있으면 KEYWORD, 없으면 IDENTIFIER 토큰 생성
}