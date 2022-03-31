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
            lw         r1,-52(r14)          
            bz         r1,tag2              
 tag3                                       % while statement
            addi       r2,r0,1              % load intnum: 1
            sw         -56(r14),r2          
            lw         r2,-20(r14)          % addOp operation
            lw         r3,-56(r14)          
            sub        r4,r2,r3             
            sw         -60(r14),r4          
            lw         r2,-16(r14)          % addOp operation
            lw         r3,-60(r14)          
            sub        r4,r2,r3             
            sw         -64(r14),r4          
            lw         r2,-68(r14)          
            bz         r2,tag4              
                                            % if statement
            addi       r3,r0,1              % load intnum: 1
            sw         -72(r14),r3          
            lw         r3,-24(r14)          % addOp operation
            lw         r4,-72(r14)          
            add        r5,r3,r4             
            sw         -76(r14),r5          
            lw         r3,-80(r14)          
            bz         r3,tag5              
            lw         r4,-8(r14)           % assign temp
            sw         -28(r14),r4          
            addi       r4,r0,1              % load intnum: 1
            sw         -84(r14),r4          
            lw         r4,-24(r14)          % addOp operation
            lw         r5,-84(r14)          
            add        r6,r4,r5             
            sw         -88(r14),r6          
            lw         r4,-8(r14)           % assign arr
            sw         -8(r14),r4           
            addi       r4,r0,1              % load intnum: 1
            sw         -92(r14),r4          
            lw         r4,-24(r14)          % addOp operation
            lw         r5,-92(r14)          
            add        r6,r4,r5             
            sw         -96(r14),r6          
            lw         r4,-28(r14)          % assign arr
            sw         -8(r14),r4           
            j          tag6                 
 tag5                                       
 tag6                                       
            addi       r3,r0,1              % load intnum: 1
            sw         -100(r14),r3         
            lw         r3,-24(r14)          % addOp operation
            lw         r4,-100(r14)         
            add        r5,r3,r4             
            sw         -104(r14),r5         
            lw         r3,-104(r14)         % assign j
            sw         -24(r14),r3          
            j          tag3                 
 tag4                                       
            addi       r3,r0,1              % load intnum: 1
            sw         -108(r14),r3         
            lw         r3,-20(r14)          % addOp operation
            lw         r4,-108(r14)         
            add        r5,r3,r4             
            sw         -112(r14),r5         
            lw         r3,-112(r14)         % assign i
            sw         -20(r14),r3          
            j          tag1                 
 tag2                                       
            lw         r3,-12(r14)          % assign n
            sw         -16(r14),r3          
            addi       r3,r0,0              % load intnum: 0
            sw         -24(r14),r3          
            lw         r3,-24(r14)          % assign i
            sw         -20(r14),r3          
 tag7                                       % while statement
            lw         r3,-28(r14)          
            bz         r3,tag8              
            lw         r1,-8(r14)           % print integer
            jl         r15,putint           
            addi       r4,r0,0              % print a newline
            lb         r5,newline(r4)       
            putc       r5                   
            addi       r4,r4,1              
            lb         r5,newline(r4)       
            putc       r5                   
            addi       r4,r0,1              % load intnum: 1
            sw         -32(r14),r4          
            lw         r4,-20(r14)          % addOp operation
            lw         r5,-32(r14)          
            add        r6,r4,r5             
            sw         -36(r14),r6          
            lw         r4,-36(r14)          % assign i
            sw         -20(r14),r4          
            j          tag7                 
 tag8                                       
                                            % begin of main function
            entry                           % start program
            addi       r14,r0,topaddr       
            addi       r4,r0,7              % load intnum: 7
            sw         -40(r14),r4          
            addi       r4,r0,0              % load intnum: 0
            sw         -44(r14),r4          
            addi       r4,r0,64             % load intnum: 64
            sw         -48(r14),r4          
            lw         r4,-48(r14)          % assign arr
            sw         -8(r14),r4           
            addi       r4,r0,1              % load intnum: 1
            sw         -52(r14),r4          
            addi       r4,r0,34             % load intnum: 34
            sw         -56(r14),r4          
            lw         r4,-56(r14)          % assign arr
            sw         -8(r14),r4           
            addi       r4,r0,2              % load intnum: 2
            sw         -60(r14),r4          
            addi       r4,r0,25             % load intnum: 25
            sw         -64(r14),r4          
            lw         r4,-64(r14)          % assign arr
            sw         -8(r14),r4           
            addi       r4,r0,3              % load intnum: 3
            sw         -68(r14),r4          
            addi       r4,r0,12             % load intnum: 12
            sw         -72(r14),r4          
            lw         r4,-72(r14)          % assign arr
            sw         -8(r14),r4           
            addi       r4,r0,4              % load intnum: 4
            sw         -76(r14),r4          
            addi       r4,r0,22             % load intnum: 22
            sw         -80(r14),r4          
            lw         r4,-80(r14)          % assign arr
            sw         -8(r14),r4           
            addi       r4,r0,5              % load intnum: 5
            sw         -84(r14),r4          
            addi       r4,r0,11             % load intnum: 11
            sw         -88(r14),r4          
            lw         r4,-88(r14)          % assign arr
            sw         -8(r14),r4           
            addi       r4,r0,6              % load intnum: 6
            sw         -92(r14),r4          
            addi       r4,r0,90             % load intnum: 90
            sw         -96(r14),r4          
            lw         r4,-96(r14)          % assign arr
            sw         -8(r14),r4           
            addi       r4,r0,7              % load intnum: 7
            sw         -100(r14),r4         
            addi       r4,r0,7              % load intnum: 7
            sw         -108(r14),r4         
            addi       r4,r0,7              % load intnum: 7
            sw         -116(r14),r4         
hlt
 newline    db         13,10                % The bytes 13 and 10 are return and linefeed, respectively
 align                                      
