#include "stdio.h"
#include "stdlib.h"
#include "string.h"

struct node {
    char data;
    struct node *left;
    struct node *right;
};

struct node* create_node(char data) {
  struct node* new_node = (struct node*)malloc(sizeof(struct node));

  if(new_node == NULL) {
    printf("메모리 할당 안됨\n");
    return NULL;
  }

  new_node->data = data;
  new_node->left = NULL;
  new_node->right = NULL;
  return new_node;
}

struct node* build_tree(char* str) {
  if(str == NULL) {
    return NULL;
  }

  struct node* nodes[strlen(str)];
  int count = 0;

  for(int i = 0; i < strlen(str); i++) {
    if(str[i] != ' ') {
      nodes[count++] = create_node(str[i]);
    }
  }

  for(int i = 0; i < count; i++) {
    if(2*i + 1 < count) {
      nodes[i]->left = nodes[2*i + 1];
    }
    if(2*i + 2 < count) {
      nodes[i]->right = nodes[2*i + 2];
    }
  }
  
  return nodes[0];
}

void pre_order(struct node* root, char* result) {
  if(root == NULL) {
    return;
  }

  strncat(result, &root->data, 1);
  pre_order(root->left, result);
  pre_order(root->right, result);
}

void in_order(struct node* root, char* result) {
  if(root == NULL) {
    return;
  }

  in_order(root->left, result);
  strncat(result, &root->data, 1);
  in_order(root->right, result);
}

void post_order(struct node* root, char* result) {
  if(root == NULL) {
    return;
  }

  post_order(root->left, result);
  post_order(root->right, result);
  strncat(result, &root->data, 1);
}

void free_tree(struct node* root) {
  if(root == NULL) {
    return;
  }

  free_tree(root->left);
  free_tree(root->right);
  free(root);
}

int main() {
  char str[] = "Hello World";
  struct node* root = build_tree(str);

  char* result = (char*)malloc(strlen(str) + 1);
  
  result[0] = '\0'; 
  pre_order(root, result);
  printf("Pre-order traversal: %s\n", result);
  
  result[0] = '\0'; 
  in_order(root, result);
  printf("In-order traversal: %s\n", result);

  result[0] = '\0'; 
  post_order(root, result);
  printf("Post-order traversal: %s\n", result);

  free_tree(root);
  free(result);
  return 0;
}