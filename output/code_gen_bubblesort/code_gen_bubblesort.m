            lw         r1,0(r14)            % assign n
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
            addi       r1,r0,1              % load intnum: 1
            sw         -44(r14),r1          
            lw         r1,-16(r14)          % + operation
            lw         r2,-44(r14)          
            sub        r3,r1,r2             
            sw         -48(r14),r3          
            addi       r1,r0,1              % load intnum: 1
            sw         -52(r14),r1          
            lw         r1,-20(r14)          % + operation
            lw         r2,-52(r14)          
            sub        r3,r1,r2             
            sw         -56(r14),r3          
            lw         r1,-16(r14)          % + operation
            lw         r2,-56(r14)          
            sub        r3,r1,r2             
            sw         -60(r14),r3          
            addi       r1,r0,1              % load intnum: 1
            sw         -64(r14),r1          
            lw         r1,-24(r14)          % + operation
            lw         r2,-64(r14)          
            add        r3,r1,r2             
            sw         -68(r14),r3          
            lw         r1,0(r14)            % assign temp
            sw         -28(r14),r1          
            addi       r1,r0,1              % load intnum: 1
            sw         -72(r14),r1          
            lw         r1,-24(r14)          % + operation
            lw         r2,-72(r14)          
            add        r3,r1,r2             
            sw         -76(r14),r3          
            lw         r1,0(r14)            % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,1              % load intnum: 1
            sw         -80(r14),r1          
            lw         r1,-24(r14)          % + operation
            lw         r2,-80(r14)          
            add        r3,r1,r2             
            sw         -84(r14),r3          
            lw         r1,0(r14)            % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,1              % load intnum: 1
            sw         -88(r14),r1          
            lw         r1,-24(r14)          % + operation
            lw         r2,-88(r14)          
            add        r3,r1,r2             
            sw         -92(r14),r3          
            lw         r1,-92(r14)          % assign j
            sw         -24(r14),r1          
            addi       r1,r0,1              % load intnum: 1
            sw         -96(r14),r1          
            lw         r1,-20(r14)          % + operation
            lw         r2,-96(r14)          
            add        r3,r1,r2             
            sw         -100(r14),r3         
            lw         r1,-100(r14)         % assign i
            sw         -20(r14),r1          
            lw         r1,0(r14)            % assign n
            sw         -16(r14),r1          
            addi       r1,r0,0              % load intnum: 0
            sw         -24(r14),r1          
            lw         r1,-24(r14)          % assign i
            sw         -20(r14),r1          
            lw         r1,-8(r14)           % print integer
            jl         r15,putint           
            addi       r1,r0,0              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r1,1              
            lb         r2,newline(r1)       
            putc       r2                   
            addi       r1,r0,1              % load intnum: 1
            sw         -28(r14),r1          
            lw         r1,-20(r14)          % + operation
            lw         r2,-28(r14)          
            add        r3,r1,r2             
            sw         -32(r14),r3          
            lw         r1,-32(r14)          % assign i
            sw         -20(r14),r1          
                                            % begin of main function
            entry                           % start program
            addi       r14,r0,topaddr       
            addi       r1,r0,7              % load intnum: 7
            sw         -40(r14),r1          
            addi       r1,r0,0              % load intnum: 0
            sw         -44(r14),r1          
            addi       r1,r0,64             % load intnum: 64
            sw         -48(r14),r1          
            lw         r1,-48(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,1              % load intnum: 1
            sw         -52(r14),r1          
            addi       r1,r0,34             % load intnum: 34
            sw         -56(r14),r1          
            lw         r1,-56(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,2              % load intnum: 2
            sw         -60(r14),r1          
            addi       r1,r0,25             % load intnum: 25
            sw         -64(r14),r1          
            lw         r1,-64(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,3              % load intnum: 3
            sw         -68(r14),r1          
            addi       r1,r0,12             % load intnum: 12
            sw         -72(r14),r1          
            lw         r1,-72(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,4              % load intnum: 4
            sw         -76(r14),r1          
            addi       r1,r0,22             % load intnum: 22
            sw         -80(r14),r1          
            lw         r1,-80(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,5              % load intnum: 5
            sw         -84(r14),r1          
            addi       r1,r0,11             % load intnum: 11
            sw         -88(r14),r1          
            lw         r1,-88(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,6              % load intnum: 6
            sw         -92(r14),r1          
            addi       r1,r0,90             % load intnum: 90
            sw         -96(r14),r1          
            lw         r1,-96(r14)          % assign arr
            sw         -8(r14),r1           
            addi       r1,r0,7              % load intnum: 7
            sw         -100(r14),r1         
            addi       r1,r0,7              % load intnum: 7
            sw         -108(r14),r1         
            addi       r1,r0,7              % load intnum: 7
            sw         -116(r14),r1         
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
