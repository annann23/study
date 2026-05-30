#include <stdio.h>
#include <string.h>

typedef struct {
    char  label[50];
    void (*render)(void *self); 
} Button;

typedef struct {
    Button base;
    char   icon[50];
} IconButton;

void create_default(void *btn) {
    Button *button = (Button *)btn;
    printf("<button>%s</button>\n", button->label);
}   

void create_icon_button(void *btn) {
    IconButton *button = (IconButton *)btn;
    printf("<button><img src='%s'>%s</button>\n", button->icon, button->base.label);
}

void create_button(Button *button) {
    button->render(button);
}

int main() {
    Button default_btn = {"Default", create_default};
    IconButton icon_btn = {{"Icon Button", create_icon_button}, "icon.png"};
    create_button(&default_btn);
    create_button((Button *)&icon_btn);
    return 0;
}