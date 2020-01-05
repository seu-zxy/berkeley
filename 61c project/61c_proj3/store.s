lui ra 65536
sw t0 0(ra)
lw t1 0(ra)
addi s0 ra 4
sw s1 0(s0)
lh sp 0(s0)
addi s0 s0 1
lb t2 0(s0)
addi s0 s0 1
lh t2 0(s0)

