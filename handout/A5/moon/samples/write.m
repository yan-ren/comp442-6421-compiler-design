entry
% printint
addi r1,r0,14
jl r15,putint
% print newline
addi r1, r0, 0
lb r2,newline(r1)
putc r2

addi r1, r1, 1
lb r2,newline(r1)
putc r2
%
% printint
addi r1,r0,14
jl r15,putint
hlt
newline db 13, 10
% align
