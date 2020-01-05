#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include "lfsr.h"

void lfsr_calculate(uint16_t *reg) {
    uint16_t x0=*reg&1;
    uint16_t x2=((*reg&(1<<2))>>2)^x0;
    uint16_t x3=((*reg&(1<<3))>>3)^x2;
    uint16_t x5=((*reg&(1<<5))>>5)^x3;
    *reg=(*reg>>1)+(x5<<15);
}

