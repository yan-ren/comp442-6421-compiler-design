                                            % begin of function definition: bubbleSort
 bubbleSort                                 
            sw         -4(r14),r15          
            lw         r1,-12(r14)          % assign n
            sw         -16(r14),r1          
            addi       r1,r0,0              % load intnum: 0
            sw         -32(r14),r1          
            lw         r1,-32(r14)          % assign i
            sw         -20(r14),r1          
            addi       r1,r0,0              % load intnum: 0
            sw         -36(r14),r1          
            lw         r1,-36(r14)          % assign j
            sw         -24(r14),r1          
            addi       r1,r0,0              % load intnum: 0
            sw         -40(r14),r1          
            lw         r1,-40(r14)          % assign temp
            sw         -28(r14),r1          
 tag1                                       % while statement
            addi       r1,r0,1              % load intnum: 1
            sw         -44(r14),r1          
            lw         r1,-16(r14)          % addOp operation
            lw         r2,-44(r14)          
            sub        r3,r1,r2             
            sw         -48(r14),r3          
            lw         r1,-20(r14)          % relExpr i lt t5_1419064126
            lw         r2,-48(r14)          
            clt        r3,r1,r2             
            sw         -52(r14),r3          
            lw         r3,-52(r14)          
            bz         r3,tag2              
 tag3                                       % while statement
            addi       r2,r0,1              % load intnum: 1
            sw         -56(r14),r2          
            lw         r2,-20(r14)          % addOp operation
            lw         r1,-56(r14)          
            sub        r4,r2,r1             
            sw         -60(r14),r4          
            lw         r2,-16(r14)          % addOp operation
            lw         r1,-60(r14)          
            sub        r4,r2,r1             
            sw         -64(r14),r4          
            lw         r2,-24(r14)          % relExpr j lt t9_1419064126
            lw         r1,-64(r14)          
            clt        r4,r2,r1             
            sw         -68(r14),r4          
            lw         r4,-68(r14)          
            bz         r4,tag4              
                                            % if statement
            addi       r1,r0,1              % load intnum: 1
            sw         -72(r14),r1          
            lw         r1,-24(r14)          % addOp operation
            lw         r2,-72(r14)          
            add        r5,r1,r2             
            sw         -76(r14),r5          
            lw         r1,-8(r14)           % relExpr arr gt arr
            lw         r2,-8(r14)           
            cgt        r5,r1,r2             
            sw         -80(r14),r5          
            lw         r5,-80(r14)          
            bz         r5,tag5              
            lw         r2,-8(r14)           % assign temp
            sw         -28(r14),r2          
            addi       r2,r0,1              % load intnum: 1
            sw         -84(r14),r2          
            lw         r2,-24(r14)          % addOp operation
            lw         r1,-84(r14)          
            add        r6,r2,r1             
            sw         -88(r14),r6          
            lw         r2,-8(r14)           % assign arr
            sw         -8(r14),r2           
            addi       r2,r0,1              % load intnum: 1
            sw         -92(r14),r2          
            lw         r2,-24(r14)          % addOp operation
            lw         r1,-92(r14)          
            add        r6,r2,r1             
            sw         -96(r14),r6          
            lw         r2,-28(r14)          % assign arr
            sw         -8(r14),r2           
            j          tag6                 
 tag5                                       
 tag6                                       
            addi       r5,r0,1              % load intnum: 1
            sw         -100(r14),r5         
            lw         r5,-24(r14)          % addOp operation
            lw         r2,-100(r14)         
            add        r1,r5,r2             
            sw         -104(r14),r1         
            lw         r5,-104(r14)         % assign j
            sw         -24(r14),r5          
            j          tag3                 
 tag4                                       
            addi       r5,r0,1              % load intnum: 1
            sw         -108(r14),r5         
            lw         r5,-20(r14)          % addOp operation
            lw         r2,-108(r14)         
            add        r1,r5,r2             
            sw         -112(r14),r1         
            lw         r5,-112(r14)         % assign i
            sw         -20(r14),r5          
            j          tag1                 
 tag2                                       
                                            % begin of function definition: printArray
 printArray                                 
            sw         -4(r14),r15          
            lw         r5,-12(r14)          % assign n
            sw         -16(r14),r5          
            addi       r5,r0,0              % load intnum: 0
            sw         -24(r14),r5          
            lw         r5,-24(r14)          % assign i
            sw         -20(r14),r5          
 tag7                                       % while statement
            lw         r5,-20(r14)          % relExpr i lt n
            lw         r2,-16(r14)          
            clt        r1,r5,r2             
            sw         -28(r14),r1          
            lw         r1,-28(r14)          
            bz         r1,tag8              
            lw         r1,-8(r14)           % print integer
            jl         r15,putint           
            addi       r2,r0,0              % print a newline
            lb         r5,newline(r2)       
            putc       r5                   
            addi       r2,r2,1              
            lb         r5,newline(r2)       
            putc       r5                   
            addi       r2,r0,1              % load intnum: 1
            sw         -32(r14),r2          
            lw         r2,-20(r14)          % addOp operation
            lw         r5,-32(r14)          
            add        r6,r2,r5             
            sw         -36(r14),r6          
            lw         r2,-36(r14)          % assign i
            sw         -20(r14),r2          
            j          tag7                 
 tag8                                       
            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r2,r0,0              % load intnum: 0
            sw         -44(r14),r2          
            addi       r2,r0,64             % load intnum: 64
            sw         -48(r14),r2          
            lw         r2,-48(r14)          % assign arr
            sw         -8(r14),r2           
            addi       r2,r0,1              % load intnum: 1
            sw         -52(r14),r2          
            addi       r2,r0,34             % load intnum: 34
            sw         -56(r14),r2          
            lw         r2,-56(r14)          % assign arr
            sw         -8(r14),r2           
            addi       r2,r0,2              % load intnum: 2
            sw         -60(r14),r2          
            addi       r2,r0,25             % load intnum: 25
            sw         -64(r14),r2          
            lw         r2,-64(r14)          % assign arr
            sw         -8(r14),r2           
            addi       r2,r0,3              % load intnum: 3
            sw         -68(r14),r2          
            addi       r2,r0,12             % load intnum: 12
            sw         -72(r14),r2          
            lw         r2,-72(r14)          % assign arr
            sw         -8(r14),r2           
            addi       r2,r0,4              % load intnum: 4
            sw         -76(r14),r2          
            addi       r2,r0,22             % load intnum: 22
            sw         -80(r14),r2          
            lw         r2,-80(r14)          % assign arr
            sw         -8(r14),r2           
            addi       r2,r0,5              % load intnum: 5
            sw         -84(r14),r2          
            addi       r2,r0,11             % load intnum: 11
            sw         -88(r14),r2          
            lw         r2,-88(r14)          % assign arr
            sw         -8(r14),r2           
            addi       r2,r0,6              % load intnum: 6
            sw         -92(r14),r2          
            addi       r2,r0,90             % load intnum: 90
            sw         -96(r14),r2          
            lw         r2,-96(r14)          % assign arr
            sw         -8(r14),r2           
            addi       r2,r0,7              % load intnum: 7
            sw         -100(r14),r2         
                                            % function call to printArray
            lw         r2,-8(r14)           % pass arr into parameter
            sw         -132(r14),r2         
            lw         r2,-100(r14)         % pass t41_1419064126 into parameter
            sw         -136(r14),r2         
            subi       r14,r14,124          % increment stack frame
            jl         r15,printArray       % jump to funciton: printArray
            addi       r14,r14,124          % decrement stack frame
            lw         r2,-124(r14)         % get return value from printArray
            sw         -104(r14),r2         
            addi       r2,r0,7              % load intnum: 7
            sw         -108(r14),r2         
                                            % function call to bubbleSort
            lw         r2,-8(r14)           % pass arr into parameter
            sw         -132(r14),r2         
            lw         r2,-108(r14)         % pass t43_1419064126 into parameter
            sw         -136(r14),r2         
            subi       r14,r14,124          % increment stack frame
            jl         r15,bubbleSort       % jump to funciton: bubbleSort
            addi       r14,r14,124          % decrement stack frame
            lw         r2,-124(r14)         % get return value from bubbleSort
            sw         -112(r14),r2         
            addi       r2,r0,7              % load intnum: 7
            sw         -116(r14),r2         
                                            % function call to printArray
            lw         r2,-8(r14)           % pass arr into parameter
            sw         -132(r14),r2         
            lw         r2,-116(r14)         % pass t45_1419064126 into parameter
            sw         -136(r14),r2         
            subi       r14,r14,124          % increment stack frame
            jl         r15,printArray       % jump to funciton: printArray
            addi       r14,r14,124          % decrement stack frame
            lw         r2,-124(r14)         % get return value from printArray
            sw         -120(r14),r2         
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
 main_arr   res        28                   
