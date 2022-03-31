                                            % begin of main function
            entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,1              % load intnum: 1
            sw         -20(r14),r1          
            addi       r1,r0,2              % load intnum: 2
            sw         -24(r14),r1          
            addi       r1,r0,3              % load intnum: 3
            sw         -28(r14),r1          
            lw         r1,-20(r14)          % + operation
            lw         r2,-32(r14)          
            add        r3,r1,r2             
            sw         -36(r14),r3          
            lw         r1,-36(r14)          % assign y
            sw         -12(r14),r1          
            addi       r1,r0,10             % load intnum: 10
            sw         -40(r14),r1          
            lw         r1,-12(r14)          % + operation
            lw         r2,-40(r14)          
            add        r3,r1,r2             
            sw         -44(r14),r3          
            addi       r1,r0,10             % load intnum: 10
            sw         -48(r14),r1          
            lw         r1,-8(r14)           % + operation
            lw         r2,-48(r14)          
            add        r3,r1,r2             
            sw         -52(r14),r3          
            lw         r1,-52(r14)          % print integer
            jl         r15,putint           
            addi       r1,r0,0              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,1              % load intnum: 1
            sw         -56(r14),r1          
            lw         r1,-8(r14)           % + operation
            lw         r2,-56(r14)          
            add        r3,r1,r2             
            sw         -60(r14),r3          
            lw         r1,-60(r14)          % print integer
            jl         r15,putint           
            addi       r1,r0,0              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,0              % load intnum: 0
            sw         -64(r14),r1          
            lw         r1,-64(r14)          % assign z
            sw         -16(r14),r1          
            addi       r1,r0,10             % load intnum: 10
            sw         -68(r14),r1          
            lw         r1,-16(r14)          % print integer
            jl         r15,putint           
            addi       r1,r0,0              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
