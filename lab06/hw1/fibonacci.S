main:
    li     a0, 16            # 暫存器 a0 儲存 n 值，這邊設定 n=16
    jal    fibonacci         # 開始進行 fibonacci 運算
    j      exit
    
fibonacci:
    addi   sp, sp, -8        # 進行 stack pointer 的移動
    sw     ra, 0(sp)         # 儲存 return address
    sw     a0, 4(sp)         # 儲存 temp data

    slti   t0, a0, 2         # if n < 2 then $t0 = 1, else $t0 = 0
    beq    t0, zero, L1      # if $t0 == 0 then jump to branch L1
    
    add    a1, a1, a0        # let a1 add a0
    addi   sp, sp, 8         # let sp point to upper stack
    jr     ra                # return to who calls fibonacci

L1:
    addi a0, a0, -1         # n = n - 1
    jal fibonacci           # return fibonacci(n - 1)
    addi a0, a0, -1         # n = n - 1
    jal fibonacci           # return fibonacci(n - 2)
    
    lw a0, 4(sp)            # recover the value of argument
    lw ra, 0(sp)            # recover return address
    addi, sp, sp, 8         # let $sp point to upper stack
    jr ra                   # return to who calls fibonacci
    
exit:
    ecall                    # Terminate
