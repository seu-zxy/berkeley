addi s0 x0 2
addi s1 x0 0
bne s0 s1 label1
addi s0 s0 20
jalr x0 ra 0
label1:
addi s1 s1 16
jalr ra s1 0
addi s0 s0 30