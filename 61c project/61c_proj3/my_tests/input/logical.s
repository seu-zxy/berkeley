addi t0, x0, 1
addi t1, x0, 2
addi t2, x0, 3
xor a0, t1, t2
srl s0, a0, t0
or ra, s0, t0
remu sp, t1, t2
and t0, t1, a0
