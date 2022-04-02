            align                           
                                            % begin of main function
 main       entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,0              % load intnum: 0
            sw         -44(r14),r1          
            addi       r1,r0,44             % load intnum: 44
            sw         -48(r14),r1          
            lw         r1,-48(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,1              % load intnum: 1
            sw         -52(r14),r1          
            addi       r1,r0,56             % load intnum: 56
            sw         -56(r14),r1          
            lw         r1,-56(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,4              % load intnum: 4
            sw         -60(r14),r1          
            addi       r1,r0,0              % load intnum: 0
            sw         -64(r14),r1          
            addi       r1,r0,1              % load intnum: 1
            sw         -68(r14),r1          
            lw         r1,-8(r14)           % addOp operation
            lw         r2,-8(r14)           
            add        r3,r1,r2             
            sw         -72(r14),r3          
            lw         r1,-72(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,4              % load intnum: 4
            sw         -76(r14),r1          
            lw         r1,-8(r14)           % print integer
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
            lw         r1,-8(r14)           % addOp operation
            lw         r2,-8(r14)           
            add        r3,r1,r2             
            sw         -92(r14),r3          
            lw         r1,-92(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,5              % load intnum: 5
            sw         -96(r14),r1          
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
 main_arr   res        28                   
