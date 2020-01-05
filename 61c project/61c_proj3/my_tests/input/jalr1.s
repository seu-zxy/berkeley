add s0 x0 x0
addi a0 x0 -9
bne s0 s0 label1
addi s0 s0 -1
addi s1, x0, 0
addi s1 s1 36 
jr s1
label1:
  addi s0, s0, 1
  j end 
end:
  addi a0 a0 9
