#include <stdio.h>
#include <string.h>

typedef struct {
    char name[20];
    char code[10];
} Color;

Color color_map[] = {
    {"red",    "#FF0000"},
    {"green",  "#00FF00"},
    {"blue",   "#0000FF"},
    {"yellow", "#FFFF00"},
};

typedef struct {
    char text[50];
    int width;
    Color color;
} Button;

typedef struct {
    Button base;
    char icon[50];
} IconButton;

void button_onclick(Button *btn) {
    printf("Button clicked: %s\n", btn->text);
}

void icon_button_onclick(IconButton *btn) {
    printf("Icon Button clicked: %s\n", btn->base.text);
}


int main() {
   
}