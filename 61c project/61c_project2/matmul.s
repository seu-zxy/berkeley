.globl matmul

.text
# =======================================================
# FUNCTION: Matrix Multiplication of 2 integer matrices
# 	d = matmul(m0, m1)
#   If the dimensions don't match, exit with exit code 2
# Arguments:
# 	a0 is the pointer to the start of m0
#	a1 is the # of rows (height) of m0
#	a2 is the # of columns (width) of m0
#	a3 is the pointer to the start of m1
# 	a4 is the # of rows (height) of m1
#	a5 is the # of columns (width) of m1
#	a6 is the pointer to the the start of d
# Returns:
#	None, sets d = matmul(m0, m1)
# =======================================================
matmul:
    bne a2,a4, mismatched_dimensions 
    # Error if mismatched dimensions
    addi sp, sp, -32
    sw a0, 0(sp)
    sw a1, 4(sp)
    sw a2, 8(sp)
    sw a3, 12(sp)
    sw a4, 16(sp)
    sw a5, 20(sp)
    sw a6, 24(sp)
    sw ra, 28(sp)
    # Prologue
    add t0, x0, x0   #record the line index of m0
    add t1, x0, x0   #record the row number of m1

outer_loop_start:
    mul t2, a2, t0   
    slli t2, t2, 2  #t2 is the adder to get to the line head address m0
    add t3, a0, t2  #t3 is the head of the line address  m0



inner_loop_start:
    slli t4, t1, 2  #t4 is the address to get to the row m1  
    add t5, a3, t4  # t5 is the address of the row m1 
    mul t6, t0, a5
    add t6, t6, t1  # t6 is the index of the out  t6 = t0 * a5 + t1
    slli t6, t6, 2  # t6 *4 get the address
    addi sp, sp, -28
    sw t0, 0(sp)
    sw t1, 4(sp)
    sw t2, 8(sp)
    sw t3, 12(sp)
    sw t4, 16(sp)
    sw t5, 20(sp)
    sw t6, 24(sp)
    #save all the temporaty in case used in dot 
    mv a0, t3     #a0 is the address of the first one 
    mv a1, t5     #a1 is the address of the second one      
    #a2 is itself, so do not need to change 
    addi a3, x0, 1  #a3 is 1  
    mv a4, a5     #a4 is the stream of the second one is equal to m2 column
    jal ra dot
    #get its value back for temporary register
    lw t0, 0(sp)
    lw t1, 4(sp)
    lw t2, 8(sp)
    lw t3, 12(sp)
    lw t4, 16(sp)
    lw t5, 20(sp)
    lw t6, 24(sp)
    addi sp, sp, 28
    add t6, t6, a6  #get to the address to save 
    sw a0, 0(t6)
    #get the value back
    lw a0, 0(sp)
    lw a1, 4(sp)
    lw a3, 12(sp)
    lw a4, 16(sp)
    addi t1, t1, 1  #t1++
    bne t1, a5, inner_loop_start 
inner_loop_end:
    add t1, x0, x0
    addi t0, t0, 1
    bne t0, a1, outer_loop_start

outer_loop_end:
    lw a0, 0(sp)
    lw a1, 4(sp)
    lw a2, 8(sp)
    lw a3, 12(sp)
    lw a4, 16(sp)
    lw a5, 20(sp)
    lw a6, 24(sp)
    lw ra, 28(sp)
    addi sp, sp, 32
    # Epilogue
    
    
    ret


mismatched_dimensions:
    li a1 2
    jal exit2
