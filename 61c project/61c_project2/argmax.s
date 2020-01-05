.globl argmax

.text
# =================================================================
# FUNCTION: Given a int vector, return the index of the largest
#	element. If there are multiple, return the one
#	with the smallest index.
# Arguments:
# 	a0 is the pointer to the start of the vector
#	a1 is the # of elements in the vector
# Returns:
#	a0 is the first index of the largest element
# =================================================================
argmax:
    addi sp, sp, -8
    sw a1, 0(sp)
    sw ra, 4(sp)
    # Prologue
    add t0, x0, x0  #t0 is the index number
    lw t4, 0(a0)     #t4 is like temp
    add t5, x0, x0   # t5 is the number of the largest number 
loop_start:
    addi t0, t0, 1
    mv t1, a0
    slli t2, t0, 2  #t2 is 4 times of t0
    add t1, t1, t2  # get to the index
    beq t0, a1, loop_end
    lw t3, 0(t1)     #t3 is the number saved in the index of the array
    bge t4, t3, loop_start

loop_continue:
    mv t4, t3
    mv t5, t0
    jal loop_start

loop_end:
    mv a0, t5
    lw a1, 0(sp)
    lw ra, 4(sp)
    addi sp, sp, 8
    # Epilogue

    ret
