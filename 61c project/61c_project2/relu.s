.globl relu

.text
# ==============================================================================
# FUNCTION: Performs an inplace element-wise ReLU on an array of ints
# Arguments:
# 	a0 is the pointer to the array
#	a1 is the # of elements in the array
# Returns:
#	None
# ==============================================================================
relu:
    # Prologue
    addi sp, sp, -12
    sw a0, 0(sp)
    sw a1, 4(sp)
    sw ra, 8(sp)
    add t2, x0, x0  #t2 is like i in c
loop_start:
    slli t3, t2, 2  #cause a word is 4, so we *4
    add a0, a0, t3  #use a0 to be the address 
    lw t1, (0)a0       #save the value in the address to t1
    blt t1, x0, loop_continue  #if t1 < 0 go to loop continue 
    addi t2, t2, 1
    lw a0, 0(sp)   #get a0 back
    bne t2, a1, loop_start
    jal loop_end






loop_continue:
     sw x0, 0(a0)  #save 0 to the array
     addi t2, t2, 1 # i++
     lw a0, 0(sp) #get a0 back
     bne t2, a1, loop_start
loop_end:
    lw a0, 0(sp)
    lw a1, 4(sp)
    lw ra, 8(sp)
    addi sp, sp, 12
    # Epilogue

    
	ret
