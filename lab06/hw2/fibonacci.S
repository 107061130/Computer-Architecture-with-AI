## fibonacci.S
    .text                          # code section 
    .global fibonacci_asm          # declar the sum_asm function as a  global function
    .type fibonacci_asm, @function # define sum_asm as a function

fibonacci_asm:
    addi   	sp, sp, -4        # 進行 stack pointer 的移動
    sw     	ra, 0(sp)         # 儲存 return address
    li      a1, 0             # set a1 to zero
    
    jal    	fibonacci
    
    lw 	   	ra, 0(sp)         # recover return address
    addi  	sp, sp, 4         # let $sp point to upper stack
    
    move    a0, a1            # move a1 to a0 because c will only read a0 as return value
    ret

# same as 6-1
fibonacci:
    addi   	sp, sp, -8        # 進行 stack pointer 的移動
    sw     	ra, 0(sp)         # 儲存 return address
    sw     	a0, 4(sp)         # 儲存 temp data

    slti   	t0, a0, 2         # if n < 2 then $t0 = 1, else $t0 = 0
    beq    	t0, zero, L1      # if $t0 == 0 then jump to branch L1
    
    add    	a1, a1, a0        # let a1 add a0
    addi   	sp, sp, 8         # let sp point to upper stack
    ret                       # return to who calls fibonacci

L1:
    addi   	a0, a0, -1        # n = n - 1
    jal    	fibonacci         # return fibonacci(n - 1)
    addi   	a0, a0, -1        # n = n - 1
    jal    	fibonacci         # return fibonacci(n - 2)
    
    lw     	a0, 4(sp)         # recover the value of argument
    lw 	   	ra, 0(sp)         # recover return address
    addi  	sp, sp, 8         # let $sp point to upper stack
    ret                       #return to who calls fibonacci
    
    .size fibonacci_asm, .-fibonacci_asm