                                            % begin of function definition: bubbleSort
 bubbleSort                                 
            sw         -4(r14),r15          % push r15 on stack frame
            lw         r1,-12(r14)          % load value for n
            sw         -16(r14),r1          % assign n
            addi       r1,r0,0              % load intnum: 0
            sw         -32(r14),r1          
            lw         r1,-32(r14)          % load value for i
            sw         -20(r14),r1          % assign i
            addi       r1,r0,0              % load intnum: 0
            sw         -36(r14),r1          
            lw         r1,-36(r14)          % load value for j
            sw         -24(r14),r1          % assign j
            addi       r1,r0,0              % load intnum: 0
            sw         -40(r14),r1          
            lw         r1,-40(r14)          % load value for temp
            sw         -28(r14),r1          % assign temp
 tag1                                       % while statement
            addi       r1,r0,1              % load intnum: 1
            sw         -44(r14),r1          
            lw         r1,-16(r14)          % addOp operation
            lw         r2,-44(r14)          
            sub        r3,r1,r2             
            sw         -48(r14),r3          
            lw         r1,-20(r14)          % relExpr i lt t5_1916700921
            lw         r2,-48(r14)          
            clt        r3,r1,r2             
            sw         -52(r14),r3          
            lw         r3,-52(r14)          
            bz         r3,tag2              
 tag3                                       % while statement
            addi       r3,r0,1              % load intnum: 1
            sw         -56(r14),r3          
            lw         r3,-20(r14)          % addOp operation
            lw         r2,-56(r14)          
            sub        r1,r3,r2             
            sw         -60(r14),r1          
            lw         r3,-16(r14)          % addOp operation
            lw         r2,-60(r14)          
            sub        r1,r3,r2             
            sw         -64(r14),r1          
            lw         r3,-24(r14)          % relExpr j lt t9_1916700921
            lw         r2,-64(r14)          
            clt        r1,r3,r2             
            sw         -68(r14),r1          
            lw         r1,-68(r14)          
            bz         r1,tag4              
                                            % if statement
            addi       r1,r0,1              % load intnum: 1
            sw         -72(r14),r1          
            lw         r1,-24(r14)          % addOp operation
            lw         r2,-72(r14)          
            add        r3,r1,r2             
            sw         -76(r14),r3          
            lw         r1,-24(r14)          % relExpr for both array operands
            muli       r2,r1,4              
            lw         r3,arr(r2)           
            lw         r4,-76(r14)          
            muli       r5,r4,4              
            lw         r6,arr(r5)           
            cgt        r7,r3,r6             
            sw         -80(r14),r7          
            lw         r1,-80(r14)          
            bz         r1,tag5              
            j          tag6                 
 tag5                                       
 tag6                                       
            addi       r1,r0,1              % load intnum: 1
            sw         -84(r14),r1          
            lw         r1,-24(r14)          % addOp operation
            lw         r2,-84(r14)          
            add        r3,r1,r2             
            sw         -88(r14),r3          
            lw         r1,-88(r14)          % load value for j
            sw         -24(r14),r1          % assign j
            j          tag3                 
 tag4                                       
            addi       r1,r0,1              % load intnum: 1
            sw         -92(r14),r1          
            lw         r1,-20(r14)          % addOp operation
            lw         r2,-92(r14)          
            add        r3,r1,r2             
            sw         -96(r14),r3          
            lw         r1,-96(r14)          % load value for i
            sw         -20(r14),r1          % assign i
            j          tag1                 
 tag2                                       
            addi       r1,r0,0              % load intnum: 0
            sw         -100(r14),r1         
                                            % return
            lw         r1,-100(r14)         % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: bubbleSort
                                            % begin of function definition: printArray
 printArray                                 
            sw         -4(r14),r15          % push r15 on stack frame
            lw         r1,-12(r14)          % load value for n
            sw         -16(r14),r1          % assign n
            addi       r1,r0,0              % load intnum: 0
            sw         -24(r14),r1          
            lw         r1,-24(r14)          % load value for i
            sw         -20(r14),r1          % assign i
 tag7                                       % while statement
            lw         r1,-20(r14)          % relExpr i lt n
            lw         r2,-16(r14)          
            clt        r3,r1,r2             
            sw         -28(r14),r3          
            lw         r3,-28(r14)          
            bz         r3,tag8              
            lw         r3,-20(r14)          
            muli       r2,r3,4              
            lw         r1,arr(r2)           % print integer array: arr
            jl         r15,putint           
            addi       r3,r0,0              % print a newline
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r3,1              
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r0,1              % load intnum: 1
            sw         -32(r14),r3          
            lw         r3,-20(r14)          % addOp operation
            lw         r2,-32(r14)          
            add        r1,r3,r2             
            sw         -36(r14),r1          
            lw         r3,-36(r14)          % load value for i
            sw         -20(r14),r3          % assign i
            j          tag7                 
 tag8                                       
            addi       r3,r0,0              % load intnum: 0
            sw         -40(r14),r3          
                                            % return
            lw         r3,-40(r14)          % load returned value from mem
            sw         0(r14),r3            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: printArray
            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r3,r0,0              % load intnum: 0
            sw         -44(r14),r3          
            addi       r3,r0,64             % load intnum: 64
            sw         -48(r14),r3          
            lw         r3,-48(r14)          % load value for arr
            lw         r2,-44(r14)          
            muli       r1,r2,4              
            sw         arr(r1),r3           % assign arr
            addi       r3,r0,1              % load intnum: 1
            sw         -52(r14),r3          
            addi       r3,r0,34             % load intnum: 34
            sw         -56(r14),r3          
            lw         r3,-56(r14)          % load value for arr
            lw         r2,-52(r14)          
            muli       r1,r2,4              
            sw         arr(r1),r3           % assign arr
            addi       r3,r0,2              % load intnum: 2
            sw         -60(r14),r3          
            addi       r3,r0,25             % load intnum: 25
            sw         -64(r14),r3          
            lw         r3,-64(r14)          % load value for arr
            lw         r2,-60(r14)          
            muli       r1,r2,4              
            sw         arr(r1),r3           % assign arr
            addi       r3,r0,3              % load intnum: 3
            sw         -68(r14),r3          
            addi       r3,r0,12             % load intnum: 12
            sw         -72(r14),r3          
            lw         r3,-72(r14)          % load value for arr
            lw         r2,-68(r14)          
            muli       r1,r2,4              
            sw         arr(r1),r3           % assign arr
            addi       r3,r0,4              % load intnum: 4
            sw         -76(r14),r3          
            addi       r3,r0,22             % load intnum: 22
            sw         -80(r14),r3          
            lw         r3,-80(r14)          % load value for arr
            lw         r2,-76(r14)          
            muli       r1,r2,4              
            sw         arr(r1),r3           % assign arr
            addi       r3,r0,5              % load intnum: 5
            sw         -84(r14),r3          
            addi       r3,r0,11             % load intnum: 11
            sw         -88(r14),r3          
            lw         r3,-88(r14)          % load value for arr
            lw         r2,-84(r14)          
            muli       r1,r2,4              
            sw         arr(r1),r3           % assign arr
            addi       r3,r0,6              % load intnum: 6
            sw         -92(r14),r3          
            addi       r3,r0,90             % load intnum: 90
            sw         -96(r14),r3          
            lw         r3,-96(r14)          % load value for arr
            lw         r2,-92(r14)          
            muli       r1,r2,4              
            sw         arr(r1),r3           % assign arr
            addi       r3,r0,7              % load intnum: 7
            sw         -100(r14),r3         
                                            % function call to printArray
            lw         r3,-8(r14)           % pass arr into parameter
            sw         -132(r14),r3         
            lw         r3,-100(r14)         % pass t39_1916700921 into parameter
            sw         -136(r14),r3         
            subi       r14,r14,124          % increment stack frame
            jl         r15,printArray       % jump to funciton: printArray
            addi       r14,r14,124          % decrement stack frame
            lw         r3,-124(r14)         % get return value from printArray
            sw         -104(r14),r3         
            addi       r3,r0,7              % load intnum: 7
            sw         -108(r14),r3         
                                            % function call to bubbleSort
            lw         r3,-8(r14)           % pass arr into parameter
            sw         -132(r14),r3         
            lw         r3,-108(r14)         % pass t41_1916700921 into parameter
            sw         -136(r14),r3         
            subi       r14,r14,124          % increment stack frame
            jl         r15,bubbleSort       % jump to funciton: bubbleSort
            addi       r14,r14,124          % decrement stack frame
            lw         r3,-124(r14)         % get return value from bubbleSort
            sw         -112(r14),r3         
            addi       r3,r0,7              % load intnum: 7
            sw         -116(r14),r3         
                                            % function call to printArray
            lw         r3,-8(r14)           % pass arr into parameter
            sw         -132(r14),r3         
            lw         r3,-116(r14)         % pass t43_1916700921 into parameter
            sw         -136(r14),r3         
            subi       r14,r14,124          % increment stack frame
            jl         r15,printArray       % jump to funciton: printArray
            addi       r14,r14,124          % decrement stack frame
            lw         r3,-124(r14)         % get return value from printArray
            sw         -120(r14),r3         
            hlt                             % end of function definition: main
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
 arr        res        28                   
