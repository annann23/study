#include <stdio.h>
#include <string.h>  
#include <stdlib.h> 
#include <ctype.h>

void remove_blank(char *str) {
    int i,j = 0;
    int len = strlen(str) + 1;
    char *result = (char*)malloc(len); 
        
	for(i=0; i<len; i++) {
    	if(str[i] != ' ') result[j++] = str[i]; 
    }
    result[j] = '\0';

    strcpy(str, result);
    free(result);
}

void to_upper_case(char *str) {
    int i = 0;
    for (i = 0; str[i] != '\0'; i++) {
        str[i] = toupper(str[i]);
    }
}

void change_string(char *str) {
	remove_blank(str);
	to_upper_case(str);
}

int main() {
    char test[] = "a b c d";
    change_string(test);
    printf("%s\n", test);

    return 0;
}