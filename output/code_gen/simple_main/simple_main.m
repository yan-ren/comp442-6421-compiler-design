            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,1              % load intnum: 1
            sw         -20(r14),r1          
            addi       r1,r0,2              % load intnum: 2
            sw         -24(r14),r1          
            addi       r1,r0,3              % load intnum: 3
            sw         -28(r14),r1          
            lw         r1,-24(r14)          % mulOp operation
            lw         r2,-28(r14)          
            mul        r3,r1,r2             
            sw         -32(r14),r3          
            lw         r1,-20(r14)          % addOp operation
            lw         r2,-32(r14)          
            add        r3,r1,r2             
            sw         -36(r14),r3          
            lw         r1,-36(r14)          % load value for y
            sw         -12(r14),r1          % assign y
            jl         r15,getint           % read integer
            sw         -8(r14),r1           
                                            % if statement
            addi       r1,r0,10             % load intnum: 10
            sw         -40(r14),r1          
            lw         r1,-12(r14)          % addOp operation
            lw         r2,-40(r14)          
            add        r3,r1,r2             
            sw         -44(r14),r3          
            lw         r1,-8(r14)           % relExpr x gt t7_1664598529
            lw         r2,-44(r14)          
            cgt        r3,r1,r2             
            sw         -48(r14),r3          
            lw         r3,-48(r14)          
            bz         r3,tag1              
            addi       r3,r0,10             % load intnum: 10
            sw         -52(r14),r3          
            lw         r3,-8(r14)           % addOp operation
            lw         r2,-52(r14)          
            add        r1,r3,r2             
            sw         -56(r14),r1          
            lw         r1,-56(r14)          % print var: t10_1664598529
            jl         r15,putint           
            addi       r3,r0,0              % print a newline
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r3,1              
            lb         r2,newline(r3)       
            putc       r2                   
            j          tag2                 
 tag1                                       
            addi       r3,r0,1              % load intnum: 1
            sw         -60(r14),r3          
            lw         r3,-8(r14)           % addOp operation
            lw         r2,-60(r14)          
            add        r1,r3,r2             
            sw         -64(r14),r1          
            lw         r1,-64(r14)          % print var: t12_1664598529
            jl         r15,putint           
            addi       r3,r0,0              % print a newline
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r3,1              
            lb         r2,newline(r3)       
            putc       r2                   
 tag2                                       
            addi       r3,r0,0              % load intnum: 0
            sw         -68(r14),r3          
            lw         r3,-68(r14)          % load value for z
            sw         -16(r14),r3          % assign z
 tag3                                       % while statement
            addi       r3,r0,10             % load intnum: 10
            sw         -72(r14),r3          
            lw         r3,-16(r14)          % relExpr z leq t14_1664598529
            lw         r2,-72(r14)          
            cle        r1,r3,r2             
            sw         -76(r14),r1          
            lw         r1,-76(r14)          
            bz         r1,tag4              
            lw         r1,-16(r14)          % print var: z
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,1              % load intnum: 1
            sw         -80(r14),r1          
            lw         r1,-16(r14)          % addOp operation
            lw         r2,-80(r14)          
            add        r3,r1,r2             
            sw         -84(r14),r3          
            lw         r1,-84(r14)          % load value for z
            sw         -16(r14),r1          % assign z
            j          tag3                 
 tag4                                       
            hlt                             % end of function definition: main
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
