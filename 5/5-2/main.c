#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include "lexer.h"
#include "parse.h"

typedef enum
{
    q0, // 시작 상태
    q1, // letter
    q2, 
    q3, // digit
    q4,
    q5, // =
    q6,
    q7, 
    q8, // !
    q9, 
    q10, // >
    q11,
    q12,
    q13, // <
    q14,
    q15,
    q16, // +
    q17, // -
    q18, // {
    q19, // }
    q20, // (
    q21, // )
    q22,  // ;
    qE // 오류
} State;

const char *tokenName(TokenType type)
{
    switch (type)
    {
    case INT:
        return "INT";
    case IF:
        return "IF";
    case ELSE:
        return "ELSE";
    case WHILE:
        return "WHILE";
    case RETURN:
        return "RETURN";

    case IDENTIFIER:
        return "IDENTIFIER";
    case NUMBER:
        return "NUMBER";

    case ASSIGN:
        return "ASSIGN";
    case PLUS:
        return "PLUS";
    case MINUS:
        return "MINUS";

    case EQUAL:
        return "EQUAL";
    case NEQUAL:
        return "NEQUAL";
    case LT:
        return "LT";
    case LTE:
        return "LTE";
    case GT:
        return "GT";
    case GTE:
        return "GTE";

    case LPAREN:
        return "LPAREN";
    case RPAREN:
        return "RPAREN";
    case LBRACE:
        return "LBRACE";
    case RBRACE:
        return "RBRACE";
    case SEMICOLON:
        return "SEMICOLON";

    default:
        return "ILLEGAL";
    }
}

Token *readIdentifierOrKeyword(const char *word);
Token *createToken(TokenType type, const char *value);
void connectToken(Token **head, Token **tail, Token *newToken);
void appendToken(Token **head, Token **tail, TokenType type, const char *value);
TokenType getSingleTokenType(char ch);

Token *tokenize(char *input)
{
    Token *head = NULL;
    Token *tail = NULL;
    int pos = 0;
    char buffer[64];
    int bufLen = 0;

    State state = q0;

    while (input[pos] != '\0')
    {
        char currentChar = input[pos];

        switch (state) {
            case q0:
                bufLen = 0;
                memset(buffer, 0, sizeof(buffer));

                if (isspace(currentChar))
                {
                    pos++;
                }
                else if (isalpha(currentChar) || currentChar == '_')
                {
                    state = q1;
                }
                else if (isdigit(currentChar))
                {
                    state = q3;
                }
                else if (currentChar == '=')
                {
                    pos++;
                    state = q5;
                }
                else if (currentChar == '!')
                {
                    pos++;
                    state = q8;
                }
                else if (currentChar == '>')
                {
                    pos++;
                    state = q10;
                }
                else if (currentChar == '<')
                {
                    pos++;
                    state = q13;
                }
                else if (currentChar == '+')
                {
                    state = q16;
                }
                else if (currentChar == '-')
                {
                    state = q17;
                }
                else if (currentChar == '{')
                {
                    state = q18;
                }
                else if (currentChar == '}')
                {
                    state = q19;
                }
                else if (currentChar == '(')
                {
                    state = q20;
                }
                else if (currentChar == ')')
                {
                    state = q21;
                }
                else if (currentChar == ';')
                {
                    state = q22;
                }
                else
                {
                    char value[2];
                    value[0] = currentChar;
                    value[1] = '\0';
                    appendToken(&head, &tail, ILLEGAL, value);
                    pos++;
                }
                break;
            case q1:
                if (isalnum(currentChar) || currentChar == '_')
                {
                    buffer[bufLen++] = currentChar;
                    pos++;
                }
                else
                {
                    state = q2;
                }
                break;
            case q2:
                buffer[bufLen] = '\0';
                connectToken(&head, &tail, readIdentifierOrKeyword(buffer));
                state = q0;
                break;
            case q3:
                if (isdigit(currentChar))
                {
                    buffer[bufLen++] = currentChar;
                    pos++;
                }
                else
                {
                    state = q4;
                }
                break;
            case q4:
                buffer[bufLen] = '\0';
                connectToken(&head, &tail, createToken(NUMBER, buffer));
                state = q0;
                break;
            case q5:
                if (currentChar == '=')
                {
                    pos++;
                    state = q6;
                }
                else
                {
                    state = q7;
                }
                break;
            case q6:
                connectToken(&head, &tail, createToken(EQUAL, "=="));
                state = q0;
                break;
            case q7:
                connectToken(&head, &tail, createToken(ASSIGN, "="));
                state = q0;
                break;
            case q8:
                if (currentChar == '=')
                {
                    pos++;
                    state = q9;
                }
                else
                {
                    state = qE;
                }
                break;
            case q9:
                connectToken(&head, &tail, createToken(NEQUAL, "!="));
                state = q0;
                break;
            case q10:
                if (currentChar == '=')
                {
                    pos++;
                    state = q12;
                }
                else
                {
                    state = q11;
                }
                break;
            case q11:
                connectToken(&head, &tail, createToken(GT, ">"));
                state = q0;
                break;
            case q12:
                connectToken(&head, &tail, createToken(GTE, ">="));
                state = q0;
                break;
            case q13:
                if (currentChar == '=')
                {
                    pos++;
                    state = q15;
                }
                else
                {
                    state = q14;
                }
                break;
            case q14:
                connectToken(&head, &tail, createToken(LT, "<"));
                state = q0;
                break;
            case q15:
                connectToken(&head, &tail, createToken(LTE, "<="));
                state = q0;
                break;
            case q16:
            case q17:
            case q18:
            case q19:
            case q20:
            case q21:
            case q22:
            {
                char value[2];
                value[0] = currentChar;
                value[1] = '\0';
                appendToken(&head, &tail, getSingleTokenType(currentChar), value);
                pos++;
                state = q0;
                break;
            }
            case qE:
            {
                char value[2];
                value[0] = currentChar;
                value[1] = '\0';
                appendToken(&head, &tail, ILLEGAL, value);
                pos++;
                state = q0;
                break;
            }
            default:
                break;
        }
    }

    return head;
}

Token *readIdentifierOrKeyword(const char *word)
{
    if (strcmp(word, "int") == 0)
    {
        return createToken(INT, word);
    }
    else if (strcmp(word, "if") == 0)
    {
        return createToken(IF, word);
    }
    else if (strcmp(word, "else") == 0)
    {
        return createToken(ELSE, word);
    }
    else if (strcmp(word, "while") == 0)
    {
        return createToken(WHILE, word);
    }
    else if (strcmp(word, "return") == 0)
    {
        return createToken(RETURN, word);
    }
    else
    {
        return createToken(IDENTIFIER, word);
    }
}

Token *createToken(TokenType type, const char *value)
{
    Token *newToken = (Token *)malloc(sizeof(Token));
    newToken->type = type;
    strncpy(newToken->value, value, sizeof(newToken->value) - 1);
    newToken->next = NULL;
    return newToken;
}

void connectToken(Token **head, Token **tail, Token *newToken)
{
    if (*head == NULL)
    {
        *head = newToken;
        *tail = newToken;
    }
    else
    {
        (*tail)->next = newToken;
        *tail = newToken;
    }
}

void appendToken(Token **head, Token **tail, TokenType type, const char *value)
{
    Token *newToken = createToken(type, value);
    connectToken(head, tail, newToken);
}

TokenType getSingleTokenType(char ch)
{
    switch (ch)
    {
    case '+':
        return PLUS;
    case '-':
        return MINUS;
    case '(':
        return LPAREN;
    case ')':
        return RPAREN;
    case '{':
        return LBRACE;
    case '}':
        return RBRACE;
    case ';':
        return SEMICOLON;
    default:
        return ILLEGAL;
    }
}

int main()
{

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

    Token *head = tokenize(input);

    Token *current = head;
    while (current != NULL)
    {
        printf("%s : %s\n", tokenName(current->type), current->value);
        current = current->next;
    }

    Parser parser = { head };
    Node *ast = parseFunction(&parser);
    printAST(ast, 0);


    //메모리 해제
    current = head;
    while (current != NULL)
    {
        Token *temp = current;
        current = current->next;
        free(temp);
    }

    return 0;
}