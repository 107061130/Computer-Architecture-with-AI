# sudoku_2x2_asm.S
   
    .text                           # code section 
    .global sudoku_2x2_asm          # declare the asm function as a global function
    .type sudoku_2x2_asm, @function # define sum_asm as a function
    
# a0 test_data
# a1 location
# a2 return true / false for solve
# a3 return true / false for check
# a4 is (char c) of check(char *board, int location, char c)    

sudoku_2x2_asm:
    addi   	sp, sp, -4        # 進行 stack pointer 的移動
    sw     	ra, 0(sp)         # 儲存 return address
    li   	a1, 0             # set a1 to zero
    li   	a2, 0             # set a2 to zero  
    
    jal     solve             # Call solve function
    
    lw 	   	ra, 0(sp)         # recover return address
    addi  	sp, sp, 4         # let sp point to upper stack
    ret                       # return 
 
    
# solve function in .c file
solve:
	addi   	sp, sp, -8        
    sw     	ra, 0(sp)         # store return address
    sw     	a1, 4(sp)         # store location
    
    # check loaction == 16
    slti   	t0, a1, 16        # to = 1, if loacation < 16
    beq     t0, x0, finish    # if location >= 16 then jump to branch finish
    
    # check board[loaction] == 0
    add     t0, a1, a0        # caculate load address
    lb      t1, 0(t0)         # Load board[location]  to t1
    bnez    t1, next_location     # If board[location] != 0, call solve(location+1)
    
    # check and fill number
    li   	t1, 5            
    li      t2, 0

	# for (i = 1; i < 5; i++)
for_loop:
	addi    t2, t2, 1        # i = i + 1
    beq     t2, t1, return   # if loop end, return
    li      a3, 0
    
    # caller save t0 ~ t3
    addi   	sp, sp, -12        
    sw     	t0, 0(sp)        
    sw     	t1, 4(sp)   
    sw     	t2, 8(sp)        
    mv      a4, t2
    
    jal     check
    
    # load t0 ~ t3 back      
    lw     	t0, 0(sp)        
    lw     	t1, 4(sp)   
    lw     	t2, 8(sp)        
    addi   	sp, sp, 12
    
    beq     a3, x0, for_loop # if a3 == 0, check fail, back too loop
    
    # check pass
    sb      t2, 0(t0)        # board[location] = i
    addi    a1, a1, 1        # Move to next location
    # caller save t0 ~ t3
    addi   	sp, sp, -12        
    sw     	t0, 0(sp)        
    sw     	t1, 4(sp)   
    sw     	t2, 8(sp)        
    
    jal     solve            # Recursive call
    
    # load t0 ~ t3 back      
    lw     	t0, 0(sp)        
    lw     	t1, 4(sp)   
    lw     	t2, 8(sp)        
    addi   	sp, sp, 12 
    
    bnez    a2, return       # if solve(board, location + 1) == 1, return
    
    lw      a1, 4(sp)        # load a1 back
    sb      x0, 0(t0)        # board[location] = 0
    j       for_loop
    
finish:
	li      a2, 1 	         # if location >= 16, set a2 = 1, means return true
    j       return

next_location:
	addi    a1, a1, 1        # Move to next location
    jal     solve            # Recursive call
    j       return           # Exit with whatever recursive call returns

return:
    lw      ra, 0(sp)         # Restore ra
    lw      a1, 4(sp)         # Restore location
    addi    sp, sp, 8         # Restore stack pointer
    ret                       # return 

# check fill number in location is feasible / a3 = 1 is pass
# a0 board / a1 location / a4 c / a3 return value
check:
	# check row
    li     t0, 0
    li     t1, 4
    # t2 = row = location / 4 * 4
    srli   t2, a1, 2 
    slli   t2, t2, 2
    # for (int i = 0; i < 4; i++)
for1:
    beq    t0, t1, check_col
    add    t3, t2, t0         # get row + i
    add    t3, t3, a0         # get load address board[row + i]
    lb     t4, 0(t3)          # t4 = board[row + i]
 	beq    a4, t4, return_false  # if board[row + i] == c, return
    addi   t0, t0, 1
    j      for1
    
 check_col:
    # check col
    li     t0, 0
    li     t1, 16
    # col = location % 4
    andi   t2, a1, 3
    #for (int i = 0; i < 16; i += 4)
for2:
    beq    t0, t1, check_block
    add    t3, t2, t0       # get col + i
    add    t3, t3, a0       # get load address board[col + i]
    lb     t4, 0(t3)        # t4 = board[col + i]
    beq    a4, t4, return_false  # if board[col + i] == c, return
    addi   t0, t0, 4
    j      for2
    
 check_block:    
    # check block
    # row = location / 4
    # row = row - row % 2
    srli   t0, a1, 2
    andi   t1, t0, 1
    sub    t0, t0, t1
    
    # col = location % 4
    # col = col - col % 2
    andi   t1, a1, 3
    andi   t2, t1, 1
    sub    t1, t1, t2
   
    # t0 = offset = board[row*4 + col]
    slli   t0, t0, 2
    add    t0, t0, t1
    add    t0, t0, a0
    
    lb     t4, 0(t0)
    beq    a4, t4, return_false  # if board[row*4 + col] == c, return
    
    lb     t4, 1(t0)
    beq    a4, t4, return_false  # if board[row*4 + col + 1] == c, return
    
    lb     t4, 4(t0)
    beq    a4, t4, return_false  # if board[(row+1)*4 + col] == c, return
    
    lb     t4, 5(t0)
    beq    a4, t4, return_false  # if board[(row+1)*4 + col+1] == c, return
    
    # pass all check, a3 = 1
    li     a3, 1
    
return_false:
	ret

    .size sudoku_2x2_asm, .-sudoku_2x2_asm
