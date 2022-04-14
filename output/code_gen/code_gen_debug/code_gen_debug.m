            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,0              % load intnum: 0
            sw         -20(r14),r1          
            lw         r1,-20(r14)          % load value for z
            sw         -16(r14),r1          % assign z
 tag1                                       % while statement
            addi       r1,r0,10             % load intnum: 10
            sw         -24(r14),r1          
            lw         r1,-16(r14)          % relExpr z leq t2_684822005
            lw         r2,-24(r14)          
            cle        r3,r1,r2             
            sw         -28(r14),r3          
            lw         r3,-28(r14)          
            bz         r3,tag2              
            lw         r1,-16(r14)          % print var: z
            jl         r15,putint           
            addi       r3,r0,0              % print a newline
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r3,1              
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r0,1              % load intnum: 1
            sw         -32(r14),r3          
            lw         r3,-16(r14)          % addOp operation
            lw         r2,-32(r14)          
            add        r1,r3,r2             
            sw         -36(r14),r1          
            lw         r3,-36(r14)          % load value for z
            sw         -16(r14),r3          % assign z
            j          tag1                 
 tag2                                       
            hlt                             % end of function definition: main
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
