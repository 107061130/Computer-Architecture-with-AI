.text
.macro init

main:
    li      sp, 10000
    li      a1, 0
CPOP_test1:
    addi    a1, a1, 1
    addi    t1,x0,0
    cpop    t2,t1
    bne     t2,x0, EXIT
    
CLZ_test2:
    addi    a1, a1, 1
    addi    t1,x0,0
    clz     t2,t1
    addi    t3,x0,32
    bne     t3,t2,EXIT

CTZ_test3:
    addi    a1, a1, 1
    addi    t1,x0,0
    ctz     t2,t1
    bne     t3,t2,EXIT

ANDN_test4:
    addi    a1, a1, 1
    addi    t1,x0 ,0xA5
    addi    t2,x0 ,0xF0
    andn    t3,t1,t2
    addi    t4,x0,5
    bne     t3,t4,EXIT
ORN_test5:
    addi    a1, a1, 1
    addi    t1, x0,10
    addi    t2, x0,-5
    orn     t3,t1,t2
    addi    t4,x0,14
    bne     t3,t4,EXIT
XNOR_test6:
    addi    a1, a1, 1
    addi    t1, x0,10
    addi    t2, x0,-5
    xnor    t3,t1,t2
    addi    t4,x0,14
    bne     t3,t4,EXIT
MIN_test7:
    addi    a1, a1, 1
    addi    t1, x0,-1
    min     t2, t1,x0
    bne     t1,t2,EXIT
MAX_test8:
    addi    a1, a1, 1
    addi    t1, x0,-1
    max     t2, t1,x0
    bne     t2,x0,EXIT
MINU_test9:
    addi    a1, a1, 1
    addi    t1, x0,-1
    minu    t2, t1,x0
    bne     x0,t2,EXIT
MAXU_test10:
    addi    a1, a1, 1
    addi    t1, x0,-1
    maxu    t2, t1,x0
    bne     t1,t2,EXIT
SEXTB_test11:
    addi    a1, a1, 1
    addi    t1, x0,0xfe
    sext.b   t0, t1
    addi    t2, x0,-2
    bne     t0,t2,EXIT
SEXTH_test12:
    addi    a1, a1, 1
    addi    t1, x0,0xfe
    sext.h   t0, t1
    bne     t0,t1,EXIT

BSET_test13:
    addi    a1, a1, 1
    addi    t1, x0, 5
    addi    t2, x0, 2
    bset    t0, t1, t2
    addi    t3, x0, 5
    bne     t0, t3, EXIT

BCLR_test14:
    addi    a1, a1, 1
    addi    t1, x0,5
    addi    t2, x0,2
    bclr    t0, t1,t2
    addi    t3, x0,1
    bne     t0, t3, EXIT
BINV_test15:
    addi    a1, a1, 1
    addi    t1, x0,5
    addi    t2, x0,2
    binv    t0, t1,t2
    addi    t3, x0,1
    bne     t0, t3, EXIT
    addi    a1, a1, 1

EXIT:
    nop
    nop
    nop
    nop
    nop
    hcf
