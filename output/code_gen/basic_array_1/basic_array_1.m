            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,1              % load intnum: 1
            sw         -52(r14),r1          
            lw         r1,-52(r14)          % load value for i
            sw         -40(r14),r1          % assign i
            addi       r1,r0,0              % load intnum: 0
            sw         -56(r14),r1          
            addi       r1,r0,44             % load intnum: 44
            sw         -60(r14),r1          
            lw         r1,-60(r14)          % load value for arr
            lw         r2,-56(r14)          
            muli       r3,r2,4              
            sw         arr(r3),r1           % assign arr
            addi       r1,r0,1              % load intnum: 1
            sw         -64(r14),r1          
            addi       r1,r0,45             % load intnum: 45
            sw         -68(r14),r1          
            lw         r1,-68(r14)          % load value for arr
            lw         r2,-64(r14)          
            muli       r3,r2,4              
            sw         arr(r3),r1           % assign arr
            addi       r1,r0,2              % load intnum: 2
            sw         -72(r14),r1          
            addi       r1,r0,46             % load intnum: 46
            sw         -76(r14),r1          
            lw         r1,-76(r14)          % load value for arr
            lw         r2,-72(r14)          
            muli       r3,r2,4              
            sw         arr(r3),r1           % assign arr
            addi       r1,r0,4              % load intnum: 4
            sw         -80(r14),r1          
            addi       r1,r0,1              % load intnum: 1
            sw         -84(r14),r1          
            lw         r1,-40(r14)          % addOp operation
            lw         r2,-84(r14)          
            add        r3,r1,r2             
            sw         -88(r14),r3          
            lw         r1,-88(r14)          % load value for arr
            muli       r2,r1,4              
            lw         r3,arr(r2)           
            lw         r4,-80(r14)          
            muli       r5,r4,4              
            sw         arr(r5),r3           % assign arr
            addi       r1,r0,4              % load intnum: 4
            sw         -92(r14),r1          
            lw         r1,-92(r14)          
            muli       r2,r1,4              
            lw         r1,arr(r2)           % print integer array: arr
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,0              % load intnum: 0
            sw         -96(r14),r1          
            lw         r1,-96(r14)          % load value for j
            muli       r2,r1,4              
            lw         r3,arr(r2)           
            sw         -44(r14),r3          % assign j
            lw         r1,-44(r14)          % print var: j
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,1              % load intnum: 1
            sw         -100(r14),r1         
            lw         r1,-40(r14)          % addOp operation
            lw         r2,-100(r14)         
            add        r3,r1,r2             
            sw         -104(r14),r3         
            lw         r1,-104(r14)         % load value for j
            muli       r2,r1,4              
            lw         r3,arr(r2)           
            sw         -44(r14),r3          % assign j
            lw         r1,-44(r14)          % print var: j
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,1              % load intnum: 1
            sw         -108(r14),r1         
            lw         r1,-40(r14)          % addOp operation
            lw         r2,-108(r14)         
            add        r3,r1,r2             
            sw         -112(r14),r3         
            lw         r1,-40(r14)          % load value for arr
            lw         r2,-112(r14)         
            muli       r3,r2,4              
            sw         arr(r3),r1           % assign arr
            addi       r1,r0,2              % load intnum: 2
            sw         -116(r14),r1         
            lw         r1,-116(r14)         
            muli       r2,r1,4              
            lw         r1,arr(r2)           % print integer array: arr
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
 arr        res        28                   
