(NTHU_111064528_張瀚) ACAL 2024 Spring Lab 6 HW 
===

## Gitlab code link


- Gitlab link - https://course.playlab.tw/git/111064528/lab06

## HW6-1 - Fibonacci Series
### Assembly Code
> 請放上你的程式碼並加上註解，讓 TA明白你是如何完成的。
```mipsasm=
main:
    li     	a0, 16            # 暫存器 a0 儲存 n 值，這邊設定 n=16
    jal    	fibonacci         # 開始進行 fibonacci 運算
    j      	exit
    
fibonacci:
    addi   	sp, sp, -8        # 進行 stack pointer 的移動
    sw     	ra, 0(sp)         # 儲存 return address
    sw     	a0, 4(sp)         # 儲存 temp data

    slti   	t0, a0, 2         # if n < 2 then $t0 = 1, else $t0 = 0
    beq    	t0, zero, L1      # if $t0 == 0 then jump to branch L1
    
    add    	a1, a1, a0        # let a1 add a0
    addi   	sp, sp, 8         # let sp point to upper stack
    ret                # return to who calls fibonacci

L1:
    addi   	a0, a0, -1        # n = n - 1
    jal    	fibonacci         # return fibonacci(n - 1)
    addi   	a0, a0, -1        # n = n - 1
    jal    	fibonacci         # return fibonacci(n - 2)
    
    lw     	a0, 4(sp)         # recover the value of argument
    lw 	   	ra, 0(sp)         # recover return address
    addi,  	sp, sp, 8         # let $sp point to upper stack
    ret                # return to who calls fibonacci
    
exit:
    ecall                     # Terminate
```
### Simulation Result
![](https://course.playlab.tw/md/uploads/b0854834-1301-49f5-893e-e112923a61c9.png)




## HW6-2 - Fibonacci Series with C/Assembly Hybrid 
### Assembly Code & C Code

```mipsasm=
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
    ret                       # return to who calls fibonacci
    
    .size fibonacci_asm, .-fibonacci_asm
```

- `fibonacci.c`
```cpp=
int fibonacci_c(int n) { 
    if(n == 0) {
        return 0;
    }
    else if(n == 1) {
        return 1;
    }
    else {
        return fibonacci_c(n-1)+fibonacci_c(n-2);        
    }
}
```

### Simulation Result
![](https://course.playlab.tw/md/uploads/68885547-812e-4728-acd6-2f10a05e3c1d.png)


## HW6-3 - 2x2 Sudoku
### Assembly Code & C Code
- `main.c`
```c=
//main.c
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include "sudoku_2x2_c.h"
#define SIZE 16
/*
char test_c_data[16] = { 0, 0, 2, 0, 
                         0, 0, 0, 4,
                         2, 3, 0, 0, 
                         0, 4, 0, 0 };

*/

char test_c_data[16] = { 0, 2, 0, 3, 
                         1, 3, 4, 0,
                         2, 0, 0, 4, 
                         3, 0, 2, 0 };


/*
char test_asm_data[16] = { 0, 0, 2, 0, 
                           0, 0, 0, 4,
                           2, 3, 0, 0, 
                           0, 4, 0, 0 };

*/

char test_asm_data[16] = {0, 2, 0, 3, 
                          1, 3, 4, 0,
                          2, 0, 0, 4, 
                          3, 0, 2, 0 };

void print_sudoku_result() {
    int i;
    char str[25];
    puts("c result :\n");
    for( i=0 ; i<SIZE ; i++) {   
        int j= *(test_c_data+i);
        itoa(j, str, 10);
        puts(str);
        if (i % 4 == 3) puts("\n");
    }

    puts("\nassembly result :\n");
    for( i=0 ; i<SIZE ; i++) {
        int j= *(test_asm_data+i);
        itoa(j, str, 10);
        puts(str);
        if (i % 4 == 3) puts("\n");
    }

    int flag = 0;
    for( i=0 ; i<SIZE ; i++) {
        if (*(test_c_data+i) != *(test_asm_data+i)) {
            flag = 1;
            break;
        }
    }

    if (flag == 1){
        puts("\nyour c & assembly got different result ... QQ ...\n\n");
    }
    else {
        puts("\nyour c & assembly got same result!\n\n");
    }
}

void sudoku_2x2_asm(char *test_asm_data); // TODO, sudoku_2x2_asm.S

void sudoku_2x2_c(char *test_c_data); // TODO, sudoku_2x2_c.S
                        
int main() {
    puts("Before:\n");
    print_sudoku_result();
    sudoku_2x2_c(test_c_data);
    sudoku_2x2_asm(test_asm_data);
    puts("After:\n");
    print_sudoku_result();
    return 0;
}
```
- `sudoku_2x2_asm.S`
```mipsasm=
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
    slti   	t0, a1, 16        # t0 = 1, if loacation < 16
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
    beq     t2, t3, return   # if loop end, return
    li      a3, 0
    
    # caller save t0 ~ t3
    addi   	sp, sp, -16        
    sw     	t0, 0(sp)        
    sw     	t1, 4(sp)   
    sw     	t2, 8(sp)        
    sw     	t3, 12(sp)
    mv      a4, t2
    
    jal     check
    
    # load t0 ~ t3 back      
    lw     	t0, 0(sp)        
    lw     	t1, 4(sp)   
    lw     	t2, 8(sp)        
    lw     	t3, 12(sp)
    addi   	sp, sp, 16
    
    beq     a3, x0, for_loop # if a3 == 0, check fail, back too loop
    
    # check pass
    sb      t2, 0(t0)        # board[location] = i
    addi    a1, a1, 1        # Move to next location
    # caller save t0 ~ t3
    addi   	sp, sp, -16        
    sw     	t0, 0(sp)        
    sw     	t1, 4(sp)   
    sw     	t2, 8(sp)        
    sw     	t3, 12(sp)
    
    jal     solve            # Recursive call
    
    # load t0 ~ t3 back      
    lw     	t0, 0(sp)        
    lw     	t1, 4(sp)   
    lw     	t2, 8(sp)        
    lw     	t3, 12(sp)
    addi   	sp, sp, 16 
    
    bnez    a2, return       # if solve(board, location + 1) == 1, return
    

    addi   	sp, sp, 16 
    
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
```

- `sudoku_2x2_c.c`
```c=
bool check(char *board, int location, char c) {
    int row = location / 4;
    int col = location % 4;
    // check row
    for (int i = 0; i < 4; i++) {
        if (board[row * 4 + i] == c) return false;
    }

    // check col
    for (int i = 0; i < 4; i++) {
        if (board[col + i * 4] == c) return false;
    }
    
    // check block
    row = row - row % 2;
    col = col - col % 2;

    if (board[row*4 + col] == c) return false;
    if (board[row*4 + col + 1] == c) return false;
    if (board[(row+1)*4 + col] == c) return false;
    if (board[(row+1)*4 + col+1] == c) return false;
    
    return true;
}

bool solve(char *board, int location) {
    // traverse to board end, return true
    if (location == 16) return true;

    // if not fill with number in advance, try all possioble number
    if (board[location] == 0) {
        for (char i = 1; i < 5; i++) {
            // check if number i in location is feasible or not
            if (check(board, location, i)) {
                board[location] = i;
                if (solve(board, location + 1)) return true;
                board[location] = 0;
            }
        }
    }
    else return solve(board, location + 1);
}

void sudoku_2x2_c(char *test_c_data){
    solve(test_c_data, 0);
}
```

### Simulation Result
![](https://course.playlab.tw/md/uploads/ed400edd-fe8b-4aa3-957a-0a6127c1e07e.png)
![](https://course.playlab.tw/md/uploads/c0a87787-8d0d-42bb-af2d-eda2de046ca8.png)

