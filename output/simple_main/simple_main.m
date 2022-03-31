                                            % begin of main function
            entry                           % start program
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
            lw         r1,-36(r14)          % assign y
            sw         -12(r14),r1          
            jl         r15,getint           % read integer
            sw         -8(r14),r1           
                                            % if statement
            addi       r1,r0,10             % load intnum: 10
            sw         -40(r14),r1          
            lw         r1,-12(r14)          % addOp operation
            lw         r2,-40(r14)          
            add        r3,r1,r2             
            sw         -44(r14),r3          
            lw         r1,-8(r14)           % relExpr x gt t7_749927456
            lw         r2,-44(r14)          
            cgt        r3,r1,r2             
            sw         -48(r14),r3          
            lw         r3,-48(r14)          
            bz         r3,tag1              
            addi       r2,r0,10             % load intnum: 10
            sw         -52(r14),r2          
            lw         r2,-8(r14)           % addOp operation
            lw         r1,-52(r14)          
            add        r4,r2,r1             
            sw         -56(r14),r4          
            lw         r1,-56(r14)          % print integer
            jl         r15,putint           
            addi       r2,r0,0              % print a newline
            lb         r1,newline(r2)       
            putc       r1                   
            addi       r2,r2,1              
            lb         r1,newline(r2)       
            putc       r1                   
            j          tag2                 
 tag1                                       
            addi       r2,r0,1              % load intnum: 1
            sw         -60(r14),r2          
            lw         r2,-8(r14)           % addOp operation
            lw         r1,-60(r14)          
            add        r4,r2,r1             
            sw         -64(r14),r4          
            lw         r1,-64(r14)          % print integer
            jl         r15,putint           
            addi       r2,r0,0              % print a newline
            lb         r1,newline(r2)       
            putc       r1                   
            addi       r2,r2,1              
            lb         r1,newline(r2)       
            putc       r1                   
 tag2                                       
            addi       r3,r0,0              % load intnum: 0
            sw         -68(r14),r3          
            lw         r3,-68(r14)          % assign z
            sw         -16(r14),r3          
 tag3                                       % while statement
            addi       r3,r0,10             % load intnum: 10
            sw         -72(r14),r3          
            lw         r3,-16(r14)          % relExpr z leq t14_749927456
            lw         r2,-72(r14)          
            cle        r1,r3,r2             
            sw         -76(r14),r1          
            lw         r1,-76(r14)          
            bz         r1,tag4              
            lw         r1,-16(r14)          % print integer
            jl         r15,putint           
            addi       r2,r0,0              % print a newline
            lb         r3,newline(r2)       
            putc       r3                   
            addi       r2,r2,1              
            lb         r3,newline(r2)       
            putc       r3                   
            addi       r2,r0,1              % load intnum: 1
            sw         -80(r14),r2          
            lw         r2,-16(r14)          % addOp operation
            lw         r3,-80(r14)          
            add        r4,r2,r3             
            sw         -84(r14),r4          
            lw         r2,-84(r14)          % assign z
            sw         -16(r14),r2          
            j          tag3                 
 tag4                                       
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
