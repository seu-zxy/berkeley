addi t1, x0, -10
addi t2, t0, 2
blt t1, t2, label1
beq t1, t2, end
label1:
add t1, t2, t1
end:
add t1, x0, x0
