addi x1 x0 1
addi s0 x0 2
bne x1 s0 label1
label4:
addi x1 x0 2
jal x0 label5
label2:
bltu s0 x1 end
bltu x1 s0 end
label1:
addi s1 x0 5
blt s1 x1 label2
blt x1 s1 label2
end:
addi s1 s1 20
jal x0 label3
label3:
jal x0 label4
label5:
addi s1 x0 2