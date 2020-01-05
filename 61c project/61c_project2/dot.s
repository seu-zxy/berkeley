.globl dot

.text
# =======================================================
# FUNCTION: Dot product of 2 int vectors
# Arguments:
#   a0 is the pointer to the start of v0
#   a1 is the pointer to the start of v1
#   a2 is the length of the vectors
#   a3 is the stride of v0
#   a4 is the stride of v1
# Returns:
#   a0 is the dot product of v0 and v1
# =======================================================
dot:
    addi sp, sp, -28
    sw ra, 0(sp)
    sw a1, 4(sp)
    sw a2, 8(sp)
    sw a3, 12(sp)
    sw a4, 16(sp)
    sw s1, 20(sp)
    sw s2, 24(sp)
    # Prologue
    add t0, x0, x0  #loop counter
    add t1, x0, x0  #temporary element product
    add t2, x0, x0  #temporary result
    beq t0, a2, loop_end
    mv s1, a0       #current pointer to v0 element
    mv s2, a1       #current pointer to v1 element
    lw t5, 0(a0)    #t5 is the value of v0 
    lw t6, 0(a1)    #t6 is the value of v1
    
loop_start:
    addi t0, t0, 1 
    mul t1, t5, t6  #t1 save the value for *
    add t2, t2, t1  #add all the value to t2
    beq t0, a2, loop_end
    slli t3, t0, 2  #get the index *4 
    mul t3, t3, a3  #get the index *4 * a3
    add s1, a0, t3  #s1 is the pointer 
    slli t4, t0, 2  
    mul t4, t4, a4  #get the index *4 * a2
    add s2, a1, t4  #s2 is the pointer 
    lw t5, 0(s1)   
    lw t6, 0(s2)
    jal ra loop_start    
                                                                           








loop_end:
    mv a0, t2
    lw ra, 0(sp)
    lw a1, 4(sp)
    lw a2, 8(sp)
    lw a3, 12(sp)
    lw a4, 16(sp)
    lw s1, 20(sp)
    lw s2, 24(sp)
    addi sp, sp, 28

    # Epilogue
    
    ret
