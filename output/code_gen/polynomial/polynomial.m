                                            % begin of function definition: evaluate
 evaluate                                   
            sw         -4(r14),r15          % push r15 on stack frame
            addi       r1,r0,0              % load intnum: 0
            sw         -16(r14),r1          
                                            % return
            lw         r1,-16(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: evaluate
                                            % begin of function definition: evaluate
 evaluate                                   
            sw         -4(r14),r15          % push r15 on stack frame
            lw         r1,0(r14)            % load value for result
            sw         -16(r14),r1          % assign result
            lw         r1,-16(r14)          % mulOp operation
            lw         r2,-8(r14)           
            mul        r3,r1,r2             
            sw         -24(r14),r3          
            lw         r1,-24(r14)          % addOp operation
            lw         r2,-8(r14)           
            add        r3,r1,r2             
            sw         -32(r14),r3          
            lw         r1,-32(r14)          % load value for result
            sw         -16(r14),r1          % assign result
            lw         r1,-16(r14)          % mulOp operation
            lw         r2,-8(r14)           
            mul        r3,r1,r2             
            sw         -40(r14),r3          
            lw         r1,-40(r14)          % addOp operation
            lw         r2,-16(r14)          
            add        r3,r1,r2             
            sw         -48(r14),r3          
            lw         r1,-48(r14)          % load value for result
            sw         -16(r14),r1          % assign result
                                            % return
            lw         r1,-16(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: evaluate
                                            % begin of function definition: build
 build                                      
            sw         -4(r14),r15          % push r15 on stack frame
            lw         r1,-8(r14)           % load value for new_function
            addi       r2,r0,0              
            sw         new_function(r2),r1  % assign new_function
            lw         r1,-16(r14)          % load value for new_function
            addi       r2,r0,8              
            sw         new_function(r2),r1  % assign new_function
            lw         r1,-24(r14)          % load value for new_function
            addi       r2,r0,16             
            sw         new_function(r2),r1  % assign new_function
                                            % return
            lw         r1,-32(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: build
                                            % begin of function definition: build
 build                                      
            sw         -4(r14),r15          % push r15 on stack frame
            lw         r1,-8(r14)           % load value for new_function
            addi       r2,r0,0              
            sw         new_function(r2),r1  % assign new_function
            lw         r1,-16(r14)          % load value for new_function
            addi       r2,r0,8              
            sw         new_function(r2),r1  % assign new_function
                                            % return
            lw         r1,-24(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: build
                                            % begin of function definition: evaluate
 evaluate                                   
            sw         -4(r14),r15          % push r15 on stack frame
            lw         r1,-24(r14)          % load value for result
            sw         -16(r14),r1          % assign result
            lw         r1,0(r14)            % mulOp operation
            lw         r2,-8(r14)           
            mul        r3,r1,r2             
            sw         -32(r14),r3          
            lw         r1,-32(r14)          % addOp operation
            lw         r2,-8(r14)           
            add        r3,r1,r2             
            sw         -40(r14),r3          
            lw         r1,-40(r14)          % load value for result
            sw         -16(r14),r1          % assign result
                                            % return
            lw         r1,-16(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: evaluate
            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,2              % load intnum: 2
            sw         -12(r14),r1          
                                            % function call to build
            lw         r1,-12(r14)          % pass t9_817686795 into parameter
            sw         -84(r14),r1          
            lw         r1,-16(r14)          % pass t10_817686795 into parameter
            sw         -92(r14),r1          
            subi       r14,r14,76           % increment stack frame
            jl         r15,build            % jump to funciton: build
            addi       r14,r14,76           % decrement stack frame
            lw         r1,-76(r14)          % get return value from build
            sw         -24(r14),r1          
            addi       r1,r0,16             % load value for f1
            lw         r2,f1(r1)            
            sw         -8(r14),r2           % assign f1
                                            % function call to build
            lw         r1,r14               % pass sign into parameter
            sw         -84(r14),r1          
            lw         r1,-36(r14)          % pass t13_817686795 into parameter
            sw         -92(r14),r1          
            lw         r1,-44(r14)          % pass t14_817686795 into parameter
            sw         -100(r14),r1         
            subi       r14,r14,76           % increment stack frame
            jl         r15,build            % jump to funciton: build
            addi       r14,r14,76           % decrement stack frame
            lw         r1,-76(r14)          % get return value from build
            sw         -52(r14),r1          
            addi       r1,r0,24             % load value for f2
            lw         r2,f2(r1)            
            sw         -8(r14),r2           % assign f2
            addi       r1,r0,1              % load intnum: 1
            sw         -56(r14),r1          
            lw         r1,-56(r14)          % load value for counter
            sw         -8(r14),r1           % assign counter
 tag1                                       % while statement
            addi       r1,r0,10             % load intnum: 10
            sw         -60(r14),r1          
            lw         r1,-8(r14)           % relExpr counter leq t17_817686795
            lw         r2,-60(r14)          
            cle        r3,r1,r2             
            sw         -64(r14),r3          
            lw         r3,-64(r14)          
            bz         r3,tag2              
            lw         r1,-8(r14)           % print var: counter
            jl         r15,putint           
            addi       r3,r0,0              % print a newline
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r3,1              
            lb         r2,newline(r3)       
            putc       r2                   
                                            % function call to evaluate
            lw         r3,-8(r14)           % pass counter into parameter
            sw         -84(r14),r3          
            subi       r14,r14,76           % increment stack frame
            jl         r15,evaluate         % jump to funciton: evaluate
            addi       r14,r14,76           % decrement stack frame
            lw         r3,-76(r14)          % get return value from evaluate
            sw         -68(r14),r3          
            addi       r3,r0,16             % print object: f1 field: evaluate
            lw         r1,f1(r3)            
            jl         r15,putint           
            addi       r3,r0,0              % print a newline
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r3,1              
            lb         r2,newline(r3)       
            putc       r2                   
                                            % function call to evaluate
            lw         r3,-8(r14)           % pass counter into parameter
            sw         -84(r14),r3          
            subi       r14,r14,76           % increment stack frame
            jl         r15,evaluate         % jump to funciton: evaluate
            addi       r14,r14,76           % decrement stack frame
            lw         r3,-76(r14)          % get return value from evaluate
            sw         -72(r14),r3          
            addi       r3,r0,24             % print object: f2 field: evaluate
            lw         r1,f2(r3)            
            jl         r15,putint           
            addi       r3,r0,0              % print a newline
            lb         r2,newline(r3)       
            putc       r2                   
            addi       r3,r3,1              
            lb         r2,newline(r3)       
            putc       r2                   
            j          tag1                 
 tag2                                       
            hlt                             % end of function definition: main
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
 new_function res        24                   
 new_function res        16                   
 f1         res        16                   
 f2         res        24                   
