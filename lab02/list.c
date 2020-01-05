#include "list.h"

/* Add a node to the iend of the linked list. Assume head_ptr is non-null. */
void append_node (node** head_ptr, int new_data) {
	/* First lets allocate memory for the new node and initialize its attributes */
	/* YOUR CODE HERE */
    node *result=malloc(sizeof(struct node));
    result->val=new_data;
    result->next=NULL;
	/* If the list is empty, set the new node to be the head and return */
	if (*head_ptr == NULL) {
		/* YOUR CODE HERE */
		*head_ptr=result;
                return *head_ptr;
	}
	node* curr = *head_ptr;
	while ( curr->next!= NULL) {
		curr = curr->next;
	}
	curr->next=result;
	
}

/* Reverse a linked list in place (in other words, without creating a new list).
   Assume that head_ptr is non-null. */
void reverse_list (node** head_ptr) {
	node* prev = NULL;
	node* curr = *head_ptr;
	node* next = NULL;
	while (curr!= NULL) {
		next=curr->next;
		curr->next=prev;
		prev=curr;
                curr=next;
	}
    *head_ptr=prev;
}



