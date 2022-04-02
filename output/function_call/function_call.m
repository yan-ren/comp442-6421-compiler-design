                                            % begin of function definition: f
 f                                          
            sw         -4(r14),r15          
            lw         r1,-12(r14)          % mulOp operation
            lw         r2,-16(r14)          
            mul        r3,r1,r2             
            sw         -24(r14),r3          
            lw         r1,-8(r14)           % addOp operation
            lw         r2,-24(r14)          
            add        r3,r1,r2             
            sw         -28(r14),r3          
            lw         r1,-28(r14)          % assign retval
            sw         -20(r14),r1          
            lw         r1,-20(r14)          % print integer
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
                                            % return
            lw         r1,-20(r14)          % load returned value from mem
            sw         0(r14),r1            
            lw         r15,-4(r14)          % retrieve r15 from stack
            jr         r15                  % jump back to calling function
            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,1              % load intnum: 1
            sw         -20(r14),r1          
            lw         r1,-20(r14)          % assign a
            sw         -8(r14),r1           
            addi       r1,r0,2              % load intnum: 2
            sw         -24(r14),r1          
            lw         r1,-24(r14)          % assign b
            sw         -12(r14),r1          
            addi       r1,r0,3              % load intnum: 3
            sw         -28(r14),r1          
            lw         r1,-28(r14)          % assign c
            sw         -16(r14),r1          
                                            % function call to f
            lw         r1,-8(r14)           % pass a into parameter
            sw         -52(r14),r1          
            lw         r1,-12(r14)          % pass b into parameter
            sw         -56(r14),r1          
            lw         r1,-16(r14)          % pass c into parameter
            sw         -60(r14),r1          
            subi       r14,r14,44           % increment stack frame
            jl         r15,f                % jump to funciton: f
            addi       r14,r14,44           % decrement stack frame
            lw         r1,-44(r14)          % get return value from f
            sw         -32(r14),r1          
            lw         r1,-32(r14)          % mulOp operation
            lw         r2,-12(r14)          
            mul        r3,r1,r2             
            sw         -36(r14),r3          
            lw         r1,-36(r14)          % addOp operation
            lw         r2,-16(r14)          
            add        r3,r1,r2             
            sw         -40(r14),r3          
            lw         r1,-40(r14)          % assign a
            sw         -8(r14),r1           
            lw         r1,-8(r14)           % print integer
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
