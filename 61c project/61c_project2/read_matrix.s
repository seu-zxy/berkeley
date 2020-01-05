.globl read_matrix

.text
# ==============================================================================
# FUNCTION: Allocates memory and reads in a binary file as a matrix of integers
#   If any file operation fails or doesn't read the proper number of bytes,
#   exit the program with exit code 1.
# FILE FORMAT:
#   The first 8 bytes are two 4 byte ints representing the # of rows and columns
#   in the matrix. Every 4 bytes afterwards is an element of the matrix in
#   row-major order.
# Arguments:
#   a0 is the pointer to string representing the filename
#   a1 is a pointer to an integer, we will set it to the number of rows
#   a2 is a pointer to an integer, we will set it to the number of columns
# Returns:
#   a0 is the pointer to the matrix in memory
# ==============================================================================
read_matrix:
    addi sp, sp, -20
    sw ra, 0(sp)
    sw a1, 8(sp)
    sw a2, 12(sp)
    sw a3, 16(sp)
    # Prologue

    #open the file  a1 file a2 permission  return a0 the index to the file 
    addi t1, x0, -1   #set t1 to -1
	mv a1, a0      #file open a1 is the string of the file
    add a2, x0, x0 #a2 = 0 read 
    jal ra fopen
    beq a0, t1, eof_or_error #if a0 == -1, means not successfully open the file 
    #get enough space for row number and line number   use a0, a1
   
    #malloc a0 the bytes we want to have, a1 been used, return a0 the pointer
    sw a0, 4(sp)  #save the pointer for the file
    addi a0, x0, 8 
    jal ra malloc
    mv t2, a0     #get a 8 byte space for t2 to save row
    #finished mallocing two space then we get first 8 bytes by using fread
    
    #fread a1 file, a2 the place to save , a3 the bytes we want to have   return a0 really bytes get  a2 the pointer we save value 
    lw a1, 4(sp)        #get the file pointer
    mv a2, t2
    addi a3, x0, 8 
    jal ra fread

    addi a3, x0, 8  #since we do not know whether it changes a3, we get it back to 8
    bne a0, a3, eof_or_error # if a0 != 8 exit
    lw t3, 0(a2)       #get the row 
    lw t4, 4(a2)       #get the colloum
    lw a1, 8(sp)       #get the address back
    lw a2, 12(sp)      #get the address back
    sw t3, 0(a1)      #save row to the a1
    sw t4, 0(a2)      #save column to the a2
  
    #finish get rows and columns, then malloc space for array
   
    #malloc a0 the bytes we want to have, a1 been used, return a0 the pointer
    mul t3, t3, t4    #t3 * t4 is the total number of array we save it in t3
    slli t3, t3, 2    # t3 *4 get all bytes
    mv a0, t3
    jal ra malloc
    mv t4, a0        #set t4 to be the address of array
    #finish mallocing space ,then use fread 
    
    #fread a1 file, a2 the place to save , a3 the bytes we want to have   return a0 really bytes get  a2 the pointer we save value 
    lw a1, 4(sp)        #get the file pointer
    mv a2, t4            #get the array pointer
    mv a3, t3           #get the all bytes we need 
    jal ra fread
    bne a0, t3, eof_or_error
    #end the file  a1 the file pointer a0 the result
    lw a1, 4(sp)
    jal ra fclose
    bne a0, x0, eof_or_error
    mv a0, a2          #save the array pointer to a0
    # Epilogue
    lw ra, 0(sp)
    lw a1, 8(sp)
    lw a2, 12(sp)
    lw a3, 16(sp)
    addi sp, sp, 20
    ret

eof_or_error:
    li a1 1
    jal exit2
    