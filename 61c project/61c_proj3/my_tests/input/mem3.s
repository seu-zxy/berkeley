addi s0, x0,-1
addi s1, x0, 1
bltu s1, s0, label1
bne s1, s0, end
label1:
addi x0, x0, 2
end:
add s1, s0, s0