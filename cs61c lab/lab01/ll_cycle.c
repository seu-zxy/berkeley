#include <stddef.h>
#include "ll_cycle.h"

int ll_has_cycle(node *head) {
    /* your code here */
    int det=0;
    int findn=0;
     node *tortoise;
     node *hare;
    if (head!=NULL){
    tortoise=head;
    if (head->next!=NULL)
    hare=head->next;
    }
    if(head!=NULL){
    while(det==0 && findn==0){
    if (hare->next==NULL||hare->next->next==NULL){
    det=0;
    findn=1;
    }
    else {
    hare=hare->next->next;
    tortoise=tortoise->next;

    }
    if (tortoise==hare)
    det=1;
    }  
    }
    if (det==0)
    return 0;
    if (det==1)
    return 1;
}
