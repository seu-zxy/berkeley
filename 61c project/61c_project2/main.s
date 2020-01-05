.import read_matrix.s
.import write_matrix.s
.import matmul.s
.import dot.s
.import relu.s
.import argmax.s
.import utils.s

.globl main

.text
main:
    # =====================================
    # COMMAND LINE ARGUMENTS
    # =====================================
    # Args:
    #   a0: int argc
    #   a1: char** argv
    #
    # Usage:
    #   main.s <M0_PATH> <M1_PATH> <INPUT_PATH> <OUTPUT_PATH>
    # Exit if incorrect number of command line args
    addi t1, x0, 5     #we should have 5 agrgs
    bne a0, t1, wronginput
    addi sp, sp, -40
    sw a1, 0(sp)          #save argv





	# =====================================
    # LOAD MATRICES
    # =====================================
   
    li a0, 4
    jal ra malloc 
    mv t1, a0      #get the address for row t1
    li a0, 4
    jal ra malloc 
    mv t2, a0       #get the address for column t2
    sw t1, 4(sp)
    sw t2, 8(sp)        #save them to sp
    #end mallocing 



    # Load pretrained m0
     #a0 filename  a1 pointer to row a2 pointer to column    return a0 the pointer to array
    #malloc a0 the bytes we want to have, a1 been used, return a0 the pointer
    lw a1, 0(sp)    #get the argv back
    lw a0, 4(a1)    #get m0
    mv a1, t1
    mv a2, t2
    jal ra read_matrix
    lw t1, 4(sp)
    lw t2, 8(sp)
    mv s1, a0         #s1 is the pointer to aaray mo
    lw t3, 0(t1)      #t3 row mo
    lw t4, 0(t2)      #t4 column m0
    sw t3, 12(sp)
    sw t4, 16(sp)
    





    # Load pretrained m1
    lw a1, 0(sp)    #get the argv back
    lw a0, 8(a1)    #get m1
    mv a1, t1
    mv a2, t2
    jal ra read_matrix
    lw t1, 4(sp)
    lw t2, 8(sp)
    mv s2, a0       #s2 the pointer to m1
    lw t3, 0(t1)   #t5 row m1
    lw t4, 0(t2)   #t6 column m1
    sw t3, 20(sp)
    sw t4, 24(sp)




    # Load input matrix
    lw a1, 0(sp)    #get the argv back
    lw a0, 12(a1)    #get input
    mv a1, t1
    mv a2, t2
    jal ra read_matrix
    lw t1, 4(sp)
    lw t2, 8(sp)
    mv s3, a0        # s3 the pointer  input
    lw t3, 0(t1)    #t3 row input
    lw t4, 0(t2)    #t4 column input
    sw t3, 28(sp)
    sw t4, 32(sp)



    # =====================================
    # RUN LAYERS
    # =====================================
    # 1. LINEAR LAYER:    m0 * input
     lw t1, 12(sp)
    lw t2, 16(sp)
    lw t3, 28(sp)
    lw t4, 32(sp)    #t1 row mo  t2 col m0   t3 row input   t4 col input
    
    #malloc space for output 
    #malloc a0 the bytes we want to have, a1 been used, return a0 the pointer
    mul a0, t1, t4
    slli a0,a0, 2
    jal ra malloc
    mv t5, a0      #save the out1 address to t5
    mv a0, s1
    mv a1, t1
    mv a2, t2
    mv a3, s3
    mv a4, t3
    mv a5, t4
    mv a6, t5       #results save in a6
    jal ra matmul
    mv s4, a6       #results 1 save in s4
    # 2. NONLINEAR LAYER: ReLU(m0 * input)
    #relu a0 pointer a1 number of elements
    lw t1, 12(sp)
    lw t2, 32(sp)
    mul a1, t1, t2
    mv a0, s4
    jal ra relu       #no return still save in a0
    mv s4, a0
    # 3. LINEAR LAYER:    m1 * ReLU(m0 * input)
    lw t1, 20(sp)
    lw t2, 24(sp)
    lw t3, 12(sp)
    lw t4, 32 (sp)
    mul a0, t1, t4
    slli a0, a0, 2
    jal ra malloc
    mv s5, a0           #save to s5
    mv a0, s2
    mv a1, t1
    mv a2, t2
    mv a3, s4
    mv a4, t3
    mv a5, t4
    mv a6, s5
    jal ra matmul
    # =====================================
    # WRITE OUTPUT
    # =====================================
    # Write output matrix
    #a0 file, a1 array, a2
    lw s0, 0(sp)
    lw a0 16(s0) # Load pointer to output filename
    mv a1, s5
    lw a2, 20(sp)
    lw a3, 32(sp)
    jal ra write_matrix





    # =====================================
    # CALCULATE CLASSIFICATION/LABEL
    # =====================================
    # Call argmax
    mv a0, s5
    lw t1, 20(sp)
    lw t2, 32(sp)
    mul a1, t1, t2
    jal ra argmax


    # Print classification
    mv a1, a0
    jal ra print_int
    addi sp, sp, 40



    # Print newline afterwards for clarity
    li a1 '\n'
    jal print_char

    jal exit

wronginput:
    li a1, 3
    jal exit2