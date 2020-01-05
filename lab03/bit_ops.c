#include <stdio.h>
#include "bit_ops.h"

// Return the nth bit of x.
// Assume 0 <= n <= 31
unsigned get_bit(unsigned x,
                 unsigned n) {
    unsigned p=1<<n;
    p=p&x;
    return p>>n;
}
// Set the nth bit of the value of x to v.
// Assume 0 <= n <= 31, and v is 0 or 1
void set_bit(unsigned * x,
             unsigned n,
             unsigned v) {
    int val=get_bit(*x,n);
    if(val!=v){
    	*x=*x-(val<<n)+(v<<n);
    }
    	
}
// Flip the nth bit of the value of x.
// Assume 0 <= n <= 31
void flip_bit(unsigned * x,
              unsigned n) {
    unsigned val=get_bit(*x,n);
    if(val==1){
    	*x=*x-(1<<n);
    }
    else *x=*x+(1<<n);
}

