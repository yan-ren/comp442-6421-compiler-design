                                            % begin of function definition: build
 build                                      
            sw         -4(r14),r15          % push r15 on stack frame
            lw         r1,-8(r14)           % load value for new_function
            addi       r2,r0,0              
            sw         new_function(r2),r1  % assign new_function
            lw         r1,-12(r14)          % load value for new_function
            addi       r2,r0,4              
            sw         new_function(r2),r1  % assign new_function
                                            % return
            lw         r1,-16(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            hlt                             % end of function definition: build
                                            % begin of function definition: evaluate
 evaluate                                   
            sw         -4(r14),r15          % push r15 on stack frame
            addi       r1,r0,2              % load intnum: 2
            sw         -16(r14),r1          
            lw         r1,-16(r14)          % load value for result
            sw         -12(r14),r1          % assign result
            addi       r1,r0,1              % load intnum: 1
            sw         -20(r14),r1          
            lw         r1,-12(r14)          % mulOp operation
            lw         r2,-8(r14)           
            mul        r3,r1,r2             
            sw         -24(r14),r3          
            lw         r1,-20(r14)          % addOp operation
            lw         r2,-24(r14)          
            add        r3,r1,r2             
            sw         -28(r14),r3          
            lw         r1,-28(r14)          % load value for result
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
            addi       r2,r0,0              
            sw         l1(r2),r1            % assign l1
            addi       r1,r0,0              % load value for temp
            lw         r2,l1(r1)            
            sw         -8(r14),r2           % assign temp
            lw         r1,-8(r14)           % print var: temp
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,1              % load intnum: 1
            sw         -16(r14),r1          
            addi       r1,r0,0              % load l1
            lw         r2,l1(r1)            
            lw         r3,-16(r14)          
            add        r4,r2,r3             
            sw         -20(r14),r4          
            lw         r1,-20(r14)          % load value for l1
            addi       r2,r0,4              
            sw         l1(r2),r1            % assign l1
            addi       r1,r0,4              % print object: l1 field: b
            lw         r1,l1(r1)            
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,3              % load intnum: 3
            sw         -20(r14),r1          
                                            % function call to evaluate
            lw         r1,-20(r14)          % pass t8_234250762 into parameter
            sw         -36(r14),r1          
            subi       r14,r14,28           % increment stack frame
            jl         r15,evaluate         % jump to funciton: evaluate
            addi       r14,r14,28           % decrement stack frame
            lw         r1,-28(r14)          % get return value from evaluate
            sw         -24(r14),r1          
            addi       r1,r0,0              % load l1
            lw         r2,l1(r1)            
            addi       r3,r0,4              % load value for l1
            lw         r4,l1(r3)            
            add        r5,r2,r4             
            sw         -28(r14),r5          
            lw         r1,-28(r14)          % print var: t10_234250762
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
 new_function res        8                    
 l1         res        8                    
