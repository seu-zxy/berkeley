.import ../read_matrix.s
.import ../utils.s

.data
file_path: .asciiz "./test_input.bin"

.text
main:
    # Read matrix into memory
    #a0 pointer to file name a1 rows a2 columns
    #malloc a0 the bytes we want to have, a1 been used, return a0 the pointer
    addi a0, x0, 4
    addi t1, x0, -1
    jal ra malloc
    addi sp, sp, -8
    sw a0, 0(sp)

    addi a0, x0, 4
    jal ra malloc
    sw a0, 4(sp)

    #finish get malloc 
    lw a1, 0(sp)
    lw a2, 4(sp)
    addi sp, sp, 8
    la a0, file_path
    jal ra read_matrix
    
    # Print out elements of matrix
    lw a1, 0(a1)
    lw a2, 0(a2)
    jal ra print_int_array

    # Terminate the program
    addi a0, x0, 10
    ecall