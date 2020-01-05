addi t0, x0, 5   #t0 = 5
addi t1, t0, -4   #t1 = 12
addi s0, t0, 9   #s0 = 14
addi s1, t0, -10   #s1 = 6
addi t2, t0, 2   #t2 = 7
addi a0, t0, 3   #a0 = 8
addi ra, t0, -2   #ra = 9
addi sp, t0, 4   #sp = 9
slli t0, s0, 4
slti a0, t0, 3
xori s1, a0, -5
srli t1, t0, 3
srai t2, t0, 9
ori sp, s1, -10
andi ra, t0, -9
