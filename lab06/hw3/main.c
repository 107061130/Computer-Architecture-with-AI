//main.c
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include "sudoku_2x2_c.h"
#define SIZE 16

char test_c_data[16] = { 0, 0, 2, 0, 
                         0, 0, 0, 4,
                         2, 3, 0, 0, 
                         0, 4, 0, 0 };


/*
char test_c_data[16] = { 0, 2, 0, 3, 
                         1, 3, 4, 0,
                         2, 0, 0, 4, 
                         3, 0, 2, 0 };

*/

char test_asm_data[16] = { 0, 0, 2, 0, 
                           0, 0, 0, 4,
                           2, 3, 0, 0, 
                           0, 4, 0, 0 };


/*
char test_asm_data[16] = {0, 2, 0, 3, 
                          1, 3, 4, 0,
                          2, 0, 0, 4, 
                          3, 0, 2, 0 };
*/
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