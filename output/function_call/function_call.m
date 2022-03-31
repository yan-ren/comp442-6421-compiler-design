            lw         r1,-8(r14)           % + operation
            lw         r2,-24(r14)          
            add        r3,r1,r2             
            sw         -28(r14),r3          
            lw         r1,-28(r14)          % assign retval
            sw         -20(r14),r1          
            lw         r1,-20(r14)          % print integer
            jl         r15,putint           
            addi       r1,r0,0              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
                                            % begin of main function
            entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,1              % load intnum: 1
            sw         -20(r14),r1          
            lw         r1,-20(r14)          % assign a
            sw         -8(r14),r1           
            addi       r1,r0,2              % load intnum: 2
            sw         -24(r14),r1          
            lw         r1,-24(r14)          % assign b
            sw         -12(r14),r1          
            addi       r1,r0,3              % load intnum: 3
            sw         -28(r14),r1          
            lw         r1,-28(r14)          % assign c
            sw         -16(r14),r1          
            lw         r1,-36(r14)          % + operation
            lw         r2,-16(r14)          
            add        r3,r1,r2             
            sw         -40(r14),r3          
            lw         r1,-40(r14)          % assign a
            sw         -8(r14),r1           
            lw         r1,-8(r14)           % print integer
            jl         r15,putint           
            addi       r1,r0,0              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
