addi s1, x0, 1
addi s0, x0, 2
bne s0, s1, label1 
addi ra x0 2
label1:
addi s1, s1, 1
beq s0, s0, end 
addi s1 s0 2
end:
add s1, s0, s1