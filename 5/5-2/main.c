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

    // 변수명
    IDENTIFIER, 
    // 숫자
    NUMBER, 
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

const char* tokenName(TokenType type)
{
    switch(type)
    {
        case INT: return "INT";
        case IF: return "IF";
        case ELSE: return "ELSE";
        case WHILE: return "WHILE";
        case RETURN: return "RETURN";

        case IDENTIFIER: return "IDENTIFIER";
        case NUMBER: return "NUMBER";

        case ASSIGN: return "ASSIGN";
        case PLUS: return "PLUS";
        case MINUS: return "MINUS";

        case EQUAL: return "EQUAL";
        case NEQUAL: return "NEQUAL";
        case LT: return "LT";
        case LTE: return "LTE";
        case GT: return "GT";
        case GTE: return "GTE";

        case LPAREN: return "LPAREN";
        case RPAREN: return "RPAREN";
        case LBRACE: return "LBRACE";
        case RBRACE: return "RBRACE";
        case SEMICOLON: return "SEMICOLON";

        default: return "ILLEGAL";
    }
}

typedef struct Token {
    TokenType type;
    char value[64];
    struct Token* next;
} Token;

Token* readIdentifierOrKeyword(char* input, int* pos);
Token* readNumber(char* input, int* pos);
Token* readOperator(char* input, int* pos);
Token* createToken(TokenType type, const char* value);
void connectToken(Token** head, Token** tail, Token* newToken);
void appendToken(Token** head, Token** tail, TokenType type, const char* value);
TokenType getSingleTokenType(char ch);

Token* tokenize(char* input) {
    Token* head = NULL;
    Token* tail = NULL;
    int pos = 0;

    while (input[pos] != '\0') {
        if(isspace(input[pos])) {
            pos++;
            continue;
        }else if(isalpha(input[pos]) || input[pos] == '_') {
            connectToken(&head, &tail, readIdentifierOrKeyword(input, &pos));
        } else if(isdigit(input[pos])) {
            connectToken(&head, &tail, readNumber(input, &pos));
        } else if(input[pos] == '=' || input[pos] == '!' || input[pos] == '<' || input[pos] == '>') {
            connectToken(&head, &tail, readOperator(input, &pos));
        } else if(input[pos] == '+' || input[pos] == '-' ||
                  input[pos] == '(' || input[pos] == ')' || 
                  input[pos] == '{' || input[pos] == '}' || 
                  input[pos] == ';') {

            char value[2];
            value[0]=input[pos];
            value[1]='\0';
            appendToken(&head, &tail, getSingleTokenType(input[pos]), value);
            pos++;
        } else {
            char value[2];
            value[0] = input[pos];
            value[1] = '\0';
            appendToken(&head, &tail, ILLEGAL, value);
            pos++;
        }
    }

    return head;
}

Token* readIdentifierOrKeyword(char* input, int* pos) {
    int start = *pos;

    while(isalnum(input[*pos]) || input[*pos] == '_') {
        (*pos)++;
    }

    int length = *pos - start;
    char buffer[64];
    strncpy(buffer, input + start, length);
    buffer[length] = '\0';

    if(strcmp(buffer, "int") == 0) {
        return createToken(INT, buffer);
    } else if(strcmp(buffer, "if") == 0) {
        return createToken(IF, buffer);
    } else if(strcmp(buffer, "else") == 0) {
        return createToken(ELSE, buffer);
    } else if(strcmp(buffer, "while") == 0) {
        return createToken(WHILE, buffer);
    } else if(strcmp(buffer, "return") == 0) {
        return createToken(RETURN, buffer);
    } else {
        return createToken(IDENTIFIER, buffer);
    }
}

Token* readNumber(char* input, int* pos) {
    int start = *pos;

    while(isdigit(input[*pos])) {
        (*pos)++;
    }

    int length = *pos - start;
    char buffer[64];
    strncpy(buffer, input + start, length);
    buffer[length] = '\0';

    return createToken(NUMBER, buffer);
}

Token* readOperator(char* input, int* pos) {
    char current = input[*pos];

    if (current == '=') {
        if (input[*pos + 1] == '=') {
            *pos += 2;
            return createToken(EQUAL, "==");
        } else {
            *pos += 1;
            return createToken(ASSIGN, "=");
        }
    }

    if (current == '!') {
        if (input[*pos + 1] == '=') {
            *pos += 2;
            return createToken(NEQUAL, "!=");
        } else {
            *pos += 1;
            return createToken(ILLEGAL, "");
        }
    }

    if (current == '<') {
        if (input[*pos + 1] == '=') {
            *pos += 2;
            return createToken(LTE, "<=");
        } else {
            *pos += 1;
            return createToken(LT, "<");
        }
    }

    if (current == '>') {
        if (input[*pos + 1] == '=') {
            *pos += 2;
            return createToken(GTE, ">=");
        } else {
            *pos += 1;
            return createToken(GT, ">");
        }
    }

    return createToken(ILLEGAL, "");
}

Token* createToken(TokenType type, const char* value) {
    Token* newToken = (Token*)malloc(sizeof(Token));
    newToken->type = type;
    strncpy(newToken->value, value, sizeof(newToken->value) - 1);
    newToken->next = NULL;
    return newToken;
}

void connectToken(Token** head, Token** tail, Token* newToken) {
    if (*head == NULL) {
        *head = newToken;
        *tail = newToken;
    } else {
        (*tail)->next = newToken;
        *tail = newToken;
    }
}

void appendToken(Token** head, Token** tail, TokenType type, const char* value) {
    Token* newToken = createToken(type, value);
    connectToken(head, tail, newToken);
}

TokenType getSingleTokenType(char ch) {
    switch (ch) {
        case '+': return PLUS;
        case '-': return MINUS;
        case '(': return LPAREN;
        case ')': return RPAREN;
        case '{': return LBRACE;
        case '}': return RBRACE;
        case ';': return SEMICOLON;
        default:  return ILLEGAL;
    }
}

int main() {

    char input[] =
        "int main() {\n"
        "    int x;\n"
        "    int y;\n"
        "    int z;\n"
        "\n"
        "    x = 10;\n"
        "    y = 20;\n"
        "\n"
        "    if (z == 60) {\n"
        "        z = z - 1;\n"
        "    } else {\n"
        "        z = z + 1;\n"
        "    }\n"
        "\n"
        "    while (z != 0) {\n"
        "        z = z - 1;\n"
        "    }\n"
        "\n"
        "    return z;\n"
        "}";

    Token* head = tokenize(input);

    Token* current = head;
    while (current != NULL) {
        printf("%s : %s\n", tokenName(current->type), current->value);
        current = current->next;
    }

    current = head;
    while (current != NULL) {
        Token* temp = current;
        current = current->next;
        free(temp);
    }

    return 0;
}