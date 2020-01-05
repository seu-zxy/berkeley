.globl write_matrix

.text
# ==============================================================================
# FUNCTION: Writes a matrix of integers into a binary file
#   If any file operation fails or doesn't write the proper number of bytes,
#   exit the program with exit code 1.
# FILE FORMAT:
#   The first 8 bytes of the file will be two 4 byte ints representing the
#   numbers of rows and columns respectively. Every 4 bytes thereafter is an
#   element of the matrix in row-major order.
# Arguments:
#   a0 is the pointer to string representing the filename   #should it be stored?
#   a1 is the pointer to the start of the matrix in memory
#   a2 is the number of rows in the matrix
#   a3 is the number of columns in the matrix
# Returns:
#   None
# ==============================================================================
write_matrix:
    addi sp, sp, -20
    sw ra, 0(sp)
    #sw a0, 4(sp)
    sw a1, 8(sp)
    sw a2, 12(sp)
    sw a3, 16(sp)
    # Prologue
    
    #open the file  a1 name, a2 permission, return a0 the descriptor 
    addi t1, x0, -1  #t1 for the exception
    mv a1, a0   #open file named a0
    addi a2, x0, 1 # a2 = 1 write
    jal ra fopen
    beq a0, t1, eof_or_error   # if a0 = -1 fileopen failed
    sw a0, 4(sp) #save the file descriptor

    #malloc the row num and column num
    addi a0, x0, 8
    jal ra malloc

    #fwrite the row and column num
    lw t2, 12(sp)
    lw t3, 16(sp)    
    mv a2, a0    #save the return of malloc to a2
    sw t2, 0(a2) #save the row and column to a2
    sw t3, 4(a2)
    lw a1, 4(sp) #set a1 to the descriptor
    addi t4, x0, 2  #num of elements
    mv a3, t4   #set a3
    addi a4, x0, 4 #size of element
    jal ra fwrite
    bne a0, a3, eof_or_error


    #fwrite matrix a1 descriptor returned by fopen, a2 pointer to a buffer containing the things to write 
    #a3 number of elements, a4 size of element, ao, return elements  
    lw a1, 4(sp)  #set a1 to the descriptor
    lw a2, 8(sp)  #a2 gets the matrix pointer
    lw t2, 12(sp)
    lw t3, 16(sp)
    mul a3, t2, t3  #a3 gets the number of elements
    addi a4, x0, 4  #a4 gets the size
    jal ra fwrite
    bne a0, a3, eof_or_error

    #fclose
    lw a1, 4(sp) #set a1 to the descriptor
    jal ra fclose
    addi t1, x0, -1 #t1 for the exception
    beq a0, t1, eof_or_error

    # Epilogue
    lw ra, 0(sp)
    lw a0, 4(sp)
    lw a1, 8(sp)
    lw a2, 12(sp)
    lw a3, 16(sp)
    addi sp, sp, 20
    ret

eof_or_error:
    li a1 1
    jal exit2
    