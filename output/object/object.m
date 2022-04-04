                                            % begin of function definition: build
 build                                      
            sw         -4(r14),r15          % push r15 on stack frame
            lw         r1,-8(r14)           % load value for new_function
            sw         -16(r14),r1          % assign new_function
            lw         r1,-12(r14)          % load value for new_function
            sw         -16(r14),r1          % assign new_function
                                            % return
            lw         r1,-16(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: build
                                            % begin of function definition: evaluate
 evaluate                                   
            sw         -4(r14),r15          % push r15 on stack frame
            addi       r1,r0,0              % load intnum: 0
            sw         -16(r14),r1          
            lw         r1,-16(r14)          % load value for result
            sw         -12(r14),r1          % assign result
            lw         r1,r14               % mulOp operation
            lw         r2,-8(r14)           
            mul        r3,r1,r2             
            sw         -20(r14),r3          
            lw         r1,r14               % addOp operation
            lw         r2,-20(r14)          
            add        r3,r1,r2             
            sw         -24(r14),r3          
            lw         r1,-24(r14)          % load value for result
            sw         -12(r14),r1          % assign result
            lw         r1,-12(r14)          % print var: result
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
                                            % return
            lw         r1,-12(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: evaluate
            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,1              % load intnum: 1
            sw         -12(r14),r1          
            lw         r1,-12(r14)          % load value for l1
            sw         -8(r14),r1           % assign l1
            lw         r1,-8(r14)           % load value for temp
            sw         -8(r14),r1           % assign temp
            lw         r1,-8(r14)           % print var: temp
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            lw         r1,-8(r14)           % load value for l1
            sw         -8(r14),r1           % assign l1
            lw         r1,-8(r14)           % print var: l1
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,3              % load intnum: 3
            sw         -16(r14),r1          
                                            % function call to evaluate
            lw         r1,-16(r14)          % pass t5_1594873248 into parameter
            sw         -32(r14),r1          
            subi       r14,r14,24           % increment stack frame
            jl         r15,evaluate         % jump to funciton: evaluate
            addi       r14,r14,24           % decrement stack frame
            lw         r1,-24(r14)          % get return value from evaluate
            sw         -20(r14),r1          
            lw         r1,-8(r14)           % addOp operation
            lw         r2,-8(r14)           
            add        r3,r1,r2             
            sw         -24(r14),r3          
            lw         r1,-24(r14)          % print var: t7_1594873248
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            hlt                             % end of function definition: main
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
