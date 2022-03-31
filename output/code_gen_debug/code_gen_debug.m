                                            % begin of main function
            entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,0              % load intnum: 0
            sw         -20(r14),r1          
            lw         r1,-20(r14)          % assign z
            sw         -16(r14),r1          
 tag1                                       % while statement
            addi       r1,r0,10             % load intnum: 10
            sw         -24(r14),r1          
            lw         r1,-16(r14)          % relExpr z leq t2_749927456
            lw         r2,-24(r14)          
            cle        r3,r1,r2             
            sw         -28(r14),r3          
            lw         r3,-28(r14)          
            bz         r3,tag2              
            lw         r1,-16(r14)          % print integer
            jl         r15,putint           
            addi       r2,r0,0              % print a newline
            lb         r1,newline(r2)       
            putc       r1                   
            addi       r2,r2,1              
            lb         r1,newline(r2)       
            putc       r1                   
            addi       r2,r0,1              % load intnum: 1
            sw         -32(r14),r2          
            lw         r2,-16(r14)          % addOp operation
            lw         r1,-32(r14)          
            add        r4,r2,r1             
            sw         -36(r14),r4          
            lw         r2,-36(r14)          % assign z
            sw         -16(r14),r2          
            j          tag1                 
 tag2                                       
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
