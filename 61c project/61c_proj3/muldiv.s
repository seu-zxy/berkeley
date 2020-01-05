addi t0, x0, 1
addi t1, x0, 2
addi t2, x0, 3
mul s0, t1, t2
mulh s1, t1, t2
divu a0, s0, t2
slt s1, a0, t2
mulhu ra, s1, t0
