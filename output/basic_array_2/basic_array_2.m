            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,0              % load intnum: 0
            sw         -44(r14),r1          
            addi       r1,r0,44             % load intnum: 44
            sw         -48(r14),r1          
            lw         r1,-48(r14)          % load value for arr
            lw         r2,-44(r14)          
            muli       r3,r2,4              
            sw         arr(r3),r1           % assign arr
            addi       r1,r0,1              % load intnum: 1
            sw         -52(r14),r1          
            addi       r1,r0,56             % load intnum: 56
            sw         -56(r14),r1          
            lw         r1,-56(r14)          % load value for arr
            lw         r2,-52(r14)          
            muli       r3,r2,4              
            sw         arr(r3),r1           % assign arr
            addi       r1,r0,4              % load intnum: 4
            sw         -60(r14),r1          
            addi       r1,r0,0              % load intnum: 0
            sw         -64(r14),r1          
            addi       r1,r0,1              % load intnum: 1
            sw         -68(r14),r1          
            lw         r1,-64(r14)          % addOp operation
            lw         r2,-68(r14)          
            add        r3,r1,r2             
            sw         -72(r14),r3          
            lw         r1,-72(r14)          % load value for arr
            muli       r2,r1,4              
            lw         r3,arr(r2)           
            lw         r4,-60(r14)          
            muli       r5,r4,4              
            sw         arr(r5),r3           % assign arr
            addi       r1,r0,4              % load intnum: 4
            sw         -76(r14),r1          
            lw         r1,-76(r14)          
            muli       r2,r1,4              
            lw         r1,arr(r2)           % print integer array: arr
            jl         r15,putint           
            addi       r1,r0,0              % print a newline
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,5              % load intnum: 5
            sw         -80(r14),r1          
            addi       r1,r0,0              % load intnum: 0
            sw         -84(r14),r1          
            addi       r1,r0,4              % load intnum: 4
            sw         -88(r14),r1          
            lw         r1,-84(r14)          % add operation for both array operands
            muli       r2,r1,4              
            lw         r3,arr(r2)           
            lw         r4,-88(r14)          
            muli       r5,r4,4              
            lw         r6,arr(r5)           
            add        r7,r3,r6             
            sw         -92(r14),r7          
            lw         r1,-92(r14)          % load value for arr
            lw         r2,-80(r14)          
            muli       r3,r2,4              
            sw         arr(r3),r1           % assign arr
            addi       r1,r0,5              % load intnum: 5
            sw         -96(r14),r1          
            lw         r1,-96(r14)          
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
