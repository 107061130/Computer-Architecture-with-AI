---
title: ACAL 2024 Curriculum Lab 6 - RISC-V Instruction Set Architecture
robots: noindex, nofollow
---
# <center>ACAL 2024 Curriculum Lab 6 <br /><font color="＃1560bd"> RISC-V Instruction Set Architecture</font></center>

###### tags: `AIAS Spring 2024`

[toc]

## Introduction
- 在課堂上有介紹了基本的 RISC-V ISA，在 LAB6 中我們將會：
    1. 學習如何利用 Venus 進行組合語言的編寫與除錯。
    2. 學習如何寫RISC-V assembly 以及function calls。
    3. 學習如何利用 qemu進行 assembly code的模擬，並使用 C/Assembly hybrid的方法提升程式執行效率。
    4. 利用 Venus 網站完成 HW6-3 的費波那契演算法和數獨演算法。
- 如果你不是初學者, 有一定的基礎的話, 你可以試著完成Bonus Homework

## RISC-V Assembly Programming
### References

如果你對於如何寫RISC-V Assembly 的方式不夠瞭解, 你可以看看下面的youtube video 加強你對RISC-V Assembly programming 的背景知識, 如果你要寫比較複雜的應用程式, 你會需要了解Assembly program 的function/procedure calls 是如何被實作的, 每一個ISA 都會定義自己的calling convention, 你可以參考附上的reference, 加強自己對RISC-V calling convention 的理解

- youtube video by Gedare Bloom @ University of Colorado, Colorado Springs
    - [Assembly Programming with RISC-V: Part 1 - Assembly Basics, Registers, and Load/Store Instructions](https://www.youtube.com/watch?v=XVU_RNiz09A)
    - [Assembly Programming with RISC-V: Part 2 - Arithmetic/Logic Instructions](https://www.youtube.com/watch?v=PMzbRMsGpwU)
    - [Assembly Programming with RISC-V: Part 3 - Control Flow Instructions](https://www.youtube.com/watch?v=QfXav1X7EP8)
    - [Assembly Programming with RISC-V: Part 4 - Function Calls (Procedure)](https://www.youtube.com/watch?v=GFlkl2DKqiY)
- [RISC-V Assembly Programmer's Manual](https://github.com/riscv-non-isa/riscv-asm-manual/blob/master/riscv-asm.md)
- [Understanding RISC-V Calling Convention](https://inst.eecs.berkeley.edu/~cs61c/resources/RISCV_Calling_Convention.pdf)
- [RISC-V Instruction Set Manual, Volume I: RISC-V User-Level ISA](https://five-embeddev.com/riscv-isa-manual/latest/preface.html#preface)

### Venus Simulator
-  Lab6-1, Lab6-2的作業可以透過線上網站 Venus 進行編譯與除錯，請先進入 Venus網站。
    -  Venus website link : https://www.kvakil.me/venus/
- Venus 基本操作說明
    1. 網站中分為 `Editor` 和 `Simulator` 兩種模式。請在 `Editor` 中放入想要模擬的程式碼，完成後可以在 `Simulator` 中查看模擬結果。
    2. `Simulator` 模式動作說明。
:::warning
   - 系統預設 `ecall` 為程式碼**終止指令**。
   - `ecall`會根據a0中的值做動作，如果無法正確停止在前一行加上`addi a0 x0 1 `
   - 程式中請使用 `#` 替程式寫上註解。
   - 程式中 function 所使用的標籤 (label) 請以 `:` 作結尾。
   - 程式中一行僅能接受一個指令。
   - 所編寫之程式碼會被**逐行讀取**，編寫時請依照**執行順序依序撰寫**。
   - 完成程式碼編寫後請切換到 `Simulator` 模式查看程式執行結果，若是有不可接受的語法系統會跳出提醒，依據提醒修改即可。
   - example : 
        ```mipsasm=
        main:
        #your main function 

        label:
        #your call function

        end:
        ecall
        ```
:::
:::danger
- 注意 : 網頁並<font color='#f00'>**不會儲存**</font>你所撰寫的程式碼，在**重新整理**之前請先保存程式碼。
:::
- 對於一般的新手而言, 因為Venus simulator 可以讓你觀察每一條指令執行之後Register/Memory 值的變化, 你會比較容易知道自己的程式碼哪裡出現問題, 如果沒有這樣的simulator, Assembly programming 的偵錯通常會比較困難, 有經驗的Assembly programmer, 常常會有自己的偵錯方法, 比如寫一個能用C printf() debug 的框架, 我們會在 Lab3-3給大家一個範例參考。

- 補充說明
在 Venus的 simulator中會使用 ecall作為程式結束時需要呼叫的指令, 這個 ecall指令會根據 `a0` register所擁有的值做出相對應的動作。

在 Lab6 中同學可能會遇到 Venus simulator ==跳出 ecall error的訊息==, 這是因為 TA一開始的設定是希望同學單純使用 ecall指令作為 assembly的結束, 而資料以 `a0`和 `a1` register儲存, 所以沒有考慮 ecall error出現的情形。

如果同學想要修掉 ecall error這個問題的話, ==**可以將原先 `a0`, `a1` register需要儲存的值以其他 register代替, 只要用註解說明清楚就好**==。至於 ecall會因為 `a0`, `a1` register而產生甚麼樣相對應的動作, 同學想要了解的話可以參考下面這個 link, 會對 ecall有更深入的了解 :
- https://github.com/kvakil/venus/wiki/Environmental-Calls

Lab 6
===
## Lab6-0 Environment and Repo Setup
- Build Course docker and bring up a docker container
    - 在開始lab之前必須先將課堂的docker container run起來，並把一些環境建好，可以參考下面的tutorial : [Lab 0 - Course Environment Setup](https://course.playlab.tw/md/33cXunaGSdmYFej1DJNIqQ)

:::warning
- You may setup passwordless ssh login if you like. Please refer to [Use SSH keys to communicate with GitLab](https://docs.gitlab.com/ee/user/ssh.html)
- Also, if you would like to setup the SSH Key in our Container. Please refer to this [document](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Set-up-the-SSH-Key) to set up the SSH Key in acal-curriculum workspace.
:::

```shell=
## bring up the ACAL docker container 
## clone the lab06 files
$  cd ~/projects
$  git clone ssh://git@course.playlab.tw:30022/acal-curriculum/lab06.git
$  cd lab06

## show the remote repositories 
$  git remote -v
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab06.git (fetch)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab06.git (push)

## add your private upstream repositories
## make sure you have create project repo under your gitlab account
$  git remote add gitlab ssh://git@course.playlab.tw:30022/<your ldap name>/lab06.git

$  git remote -v
gitlab	ssh://git@course.playlab.tw:30022/<your ldap name>/lab06.git (fetch)
gitlab	ssh://git@course.playlab.tw:30022/<your ldap name>/lab06.git (push)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab06.git (fetch)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab06.git (push)
```

- When you are done with your code, you have to push your code back to your own gitlab account with the following command :
```shell=
## the first time
$  git push --set-upstream gitlab main
## after the first time
$  git fetch origin main
## remember to solve conflicts
$  git merge origin/main
## then push back to your own repo
$  git push gitlab main
```

## Lab6-1 Venus Simulation
- 在熟悉 Venus 操作後，下方提供 `lab6-1_ex_c.c` 和 `lab6-1_ex_assembly.S` 程式碼，這兩份程式碼為==相互轉換後的結果==。**請將 `lab3-1_ex_assembly.S`透過 Venus 進行模擬功能**並回答下列問題。這些問題的答案==不需要交回==，但是許多重要概念在 HW3中會被大量使用，希望同學熟悉。
- `lab6-1_ex_assembly.S`會將 `source[]`中的資料 copy到 `dest[10]`中，同學在進行模擬時可以在 Simulator模式中將右方的 Register欄位切換至 Memory觀察資料移動和指令的關係。
![](https://course.playlab.tw/md/uploads/b8d024d0-005a-44b3-8e29-d14df8c14574.png)
- 問題討論 : 
    1. 在 C file 中參數 `int k` 相對應的暫存器為何。
    2. `source` 和 `dest` 的資料在 assembly code中如何利用。
    3. 在組合語言 `lab6-1_ex_assembly.s` 中是如何進行 for迴圈。
    4. 在組合語言 `lab6-1_ex_assembly.s` 中是如何更改指標 (pointer) 中的值，儲存 & 拿取所需要的資料。
    5. 在組合語言 `lab6-1_ex_assembly.s` 中如何預先在記憶體中給定資料，資料型態、資料大小有甚麼需要限制的。
- `lab6-1_ex_c.c`
```cpp=
int source[] = {3, 1, 4, 1, 5, 9, 0};
int dest[10];

int main () {
    int k;
    for (k=0 ; source[k]!=0 ; k++) {
	    dest[k] = source[k];
    }
    return 0;
}
```
- `lab6-1_ex_assembly.S`
```assembly=
.data
source:                    ## 對應到 int source[]的資料格式
    .word	3
    .word	1
    .word	4
    .word	1
    .word	5
    .word	9
    .word	0
dest:                      ## 對應到 int dest[]的資料格式
    .word	0
    .word	0
    .word	0
    .word	0
    .word	0
    .word	0
    .word	0
    .word	0
    .word	0
    .word	0

.text
main:
    addi     t0, x0, 0     ## t0為和 base address的 offset
    la       t1, source    ## t1為 source資料的 base address
    la       t2, dest      ## t2為 dest資料的 base address
loop:
    slli     t3, t0, 2     ## 將 t0 shift因為一個 int資料大小為 4個 bytes，左移 2等同於乘以 4的動作 
    add      t4, t1, t3    ## 將 base address + offset
    lw       t5, 0(t4)     ## 取出記憶體中資料
    beq      t5, x0, exit  ## 取到 array結束 (結尾"0")
    add      t6, t2, t3
    sw       t5, 0(t6)     ## 存回 dest目標記憶體中
    addi     t0, t0, 1     ## 還有資料需要處理，將 t0+1後繼續執行
    jal      x0, loop  
exit:
    addi     a0, x0, 10
    add      a1, x0, x0
    ecall    #Terminate
```

## Lab 6-2 Recursive in Assembly Code
### Lab 6-2-1 Calling Convention
Lab6-2-1為 calling convention的概念解釋，並**不需要利用 Venus進行模擬**。Venus模擬部分會在 Lab6-2-2中進行。
- 在 `Lab3-1`中對於 Venus 的比較熟悉後， `Lab3-2`則是要練習如何在組合語言中利用 calling convention完成遞迴 (recursive)函數。在遇到比較複雜的問題時，我們會需要利用 calling convention的方法。而遞迴則是一個不斷進行 calling convention的過程。
- 以下方的程式碼為例子：
```cpp=
//this example code is incomplete
function Recursive(int x) {
    int y = 0;
        
    //...
    if(x!= 0) {
        Recursive(y);            //"self-calling"，開始進行遞迴動作
    }
    //...
    int z = Recursive(y) + 1;    //遞迴完成後，從這裡接續下去執行
}
```
- 觀察上方的範例程式中的第7行，當程式需要進行 calling convention動作時，需要被儲存的值為：
    1. 函式的參數值 (e.g. x值)。
    2. 函式內暫存變數的值 (e.g. y值)。
    3. 返回位址 (return address)。遞迴完成後，接續執行的程式位置。
![](https://course.playlab.tw/md/uploads/5b6ba7e2-6a55-4ece-81d4-94385f04e48a.png)
- 而在 assembly code中，我們會利用 stack pointer + memory進行資料的儲存，可以參考下方程式碼。程式碼主要進行 stack pointer移動，並儲存我們在上面說的**需要被儲存的值**，藉此確保程式需要呼叫另外一個 function或是返回上一個 function時資料正確。
```mipsasm=
call_function:
    addi   sp, sp, -8       #moving stack pointer from (sp) to (sp-8)
    sw     ra, 0(sp)        #store "return address" into memory
    sw     t0, 4(sp)        #store "temp data" into memory
        
return:
    lw     t1, 4(sp)        #load "temp data"
    lw     ra, 0(sp)        #load "return address"
    addi   sp, sp, 8        #move stack pointer from (sp-8) to (sp)
        
    jr     ra               #jump back to return address
```

### Lab 6-2-2 Recursive Function
Lab6-2-2為上方 calling convention的練習，會完成一個階乘的 function。同學可以將 Lab6-2-2提供的 `lab3_ex2_assembly.S`**利用 Venus觀察模擬結果**。
- 下方 `Lab6-2` 利用組合語言完成階乘 (Factorial) 的運算。給定輸入 ==$n$== ，使程式可以回傳 ==$n!$== 之值。定義輸入 ==$n$== 會被放在 `x10` (register a0) 中，而輸出的結果 ==$n!$== 需要被放在 `x11` (register a1)中。
    - ex : input ==$n=5$==, output ==$5!=120$==.
    - `lab6_ex2_assembly.S`相對的 memory位置也一併放在下方，同學可以互相參照。
- `lab6_ex2_assembly.S`
```mipsasm=
## 為了方便同學理解遞迴的 calling convention如何進行，此範例設定 n=2
## 同學可以嘗試更大的數字並利用 Venus模擬觀察結果。
    
main:
    li     a0, 2              # 暫存器 a0 儲存 n 值，這邊設定 n=2
    jal    fact               # 開始進行 factorial 運算
    j      exit
fact:
    addi   sp, sp, -8         # 進行 stack pointer 的移動
    sw     ra, 0(sp)          # 儲存 return address
    sw     a0, 4(sp)          # 儲存 temp data

    li     t0, 2              
    blt    a0, t0, ret_one    # 檢查是否運算到 n=1 

    addi   a0, a0, -1         # 確認目前之n 沒有 >= 2 則繼續 call function
    jal    fact       
    lw     t0, 4(sp)          
    mul    a1, t0, a1 
    j      done

ret_one:
    li     a1, 1

done:
    lw     ra, 0(sp)
    addi   sp, sp, 8 
    jr     ra     
    
exit:
    ecall                    # Terminate

```  
- Memory 
    ![](https://course.playlab.tw/md/uploads/f40521b9-a04d-4ea9-b269-12decff1c865.png =75%x)
    
可以看到在 Venus中記憶體顯示的儲存情形如上圖。因為 `jal`指令會將 pc+4的值儲存至 `ra`暫存器中，所以可以觀察到 `0x7fffffe8`記憶體中的值為 8。
```armasm=6
jal    fact       ## PC = 4
j      exit       ## PC = 8
```
繼續觀察第二次的 `jal`發現 `0x7fffffe0`記憶體中的值為 40，所以 `jal`可以在 function return後回到正確的 pc位址繼續運作。
```armasm=17
jal    fact       ## PC = 36
lw     t0, 4(sp)  ## PC = 40   
```
:::warning 
- Calling convention的動作在 HW3中會佔很大一部分的比例，請確實熟悉 `Lab 6-1` 和 `Lab 6-2` 的內容。
:::

## Lab 6-3 Simulation in QEMU
在 Lab 6-1 跟 Lab 6-2 中, 我們寫的Assembly file 在Venus simlator 上執行, 與一般RISC-V machine 執行程式碼的實際流程是不太一樣的, 為了教學目的, Lab 6-1 & 6-2 簡化了整個流程, 並且使用文字格式檔案作為simulator 的input, 但是這不是真實硬體會執行的檔案格式, 在Lab 6-3, 我們提供一個在RISC-V machine 上程式碼能被執行的一個正常流程讓同學參考, 希望同學能了解下面幾件事情

- 在真正的硬體上, 要執行一個程式碼時, 如何能編譯產生硬體能接受的檔案格式
- 如果我們希望開發過程中所寫的程式碼, 不需要經過修改, 就能在開發出來的硬體上面執行時, 那我們的開發流程會長什麼樣子, 同時模擬硬體行為的軟體模擬器會長什麼樣子

### Lab6-3-1 Working Environment Setup
在 Lab6-3及本次 HW中我們所使用的 cpu emulator會以 QEMU為主， 檔案部分已經幫同學準備好放在 docker中，但是會需要同學在進入 docker後依照下方指令 build up QEMU，在 Lab6-3及後面的作業才能順利地進行模擬。

1. QEMU Pros and Cons
    - Pros 
        - 執行速度很快可以在上面跑OS 或很複雜的程式
        - 如果用來做開發晶片流程中的硬體模擬器, 上面的軟體可以幾乎不用修改, 在將來直接跑在設計出來的晶片上
    - Cons
        - 比較複雜, 要修改要增加新的指令的話, 必須對QEMU 本身的設計原理有深入的了解，相對於將來Lab7 我們要實作的emulator 困難度多很多 
    - build up QEMU (lab 的 image 裡本身就有安裝好 QEMU，這段可以跳過)
    ```shell=
    ## clone qemu in run.sh
    $ cd ~/projects 
    $ git clone https://git.qemu.org/git/qemu.git
    $ cd qemu
    $ ./configure --target-list=riscv32-linux-user,riscv32-softmmu

    ## It may take some times...
    $ make -j $(nproc)

    ## check search path can find qemu
    # 需要重複做的指令(每次重新開需要做的指令)
    $ sudo make install

    $ which qemu-system-riscv32
    ```
2. [rv32emu - RISC-V RV32I emulator with ELF support](https://github.com/sysprog21/rv32emu) - Pros and Cons
    - lightweight (**+**)
    - Easy to use - A simple emulator relys on the SDL library to  provide low level access to audio, keyboard, mouse, joystick, and stdlib functions such as printf() (**+**)
    - Can only verify user-mode program with no support for OS and system calls (**-**)

而使用 QEMU需要用到的 cross compiler & toolchain也一併幫同學準備並且設置好了。接下來請**依照下方流程完成 Lab3-3**即可。而在 Lab3-3資料夾中的 `boot.S`和 `virt.lds`檔案是完成 qemu模擬所需要用到的檔案，在 Lab6 中並不會解釋用途，同學如果想了解可以參考下面的網址 : 
[Playlab Baremetal Framework on RISCV](https://course.playlab.tw/md/fMS-uOXCRmWN2_WU8TTaxw)

### Lab6-3-2 Compile a RISC-V binary in the ELF file format
根據下方指令操作我們可以先將原先包含在 Lab3-3資料夾中的 `hello_world.c` compile成 RISCV binary的形式。完成後先將assembly dump出來，再利用指令查看一下所產生的 ELF file資訊。

#### Lab6-3-2-1 Run a simple RISC-V ELF file on the QEMU simulator
1. **Compile**
    ```shell
    ## open lab3 folder
    $ cd ~/projects/lab06/lab3

    ## checkout all files
    $ ls -l
    -rw-r--r-- 1 base base 384 Feb 11 13:23 example_1_main.c
    -rw-r--r-- 1 base base 532 Feb 11 13:23 example_1_sum.S
    -rw-r--r-- 1 base base 358 Feb 11 13:23 example_2_main.c
    -rw-r--r-- 1 base base 531 Feb 11 13:23 example_2_sum.S
    -rw-r--r-- 1 base base 126 Feb 11 13:23 hello_world.c

    ## compile hello_world.c into RISC-V binary
    $ riscv64-unknown-elf-gcc -march=rv32im -mabi=ilp32 -nostartfiles -Tvirt.lds boot.S hello_world.c -o hello_world

    ## dump assembly
    $ riscv64-unknown-elf-objdump -d hello_world

    ## check ELF file info
    $ riscv64-unknown-elf-readelf -h hello_world
    ```
    - `hello_world.c`
    ```cpp=
    // lab3 - hello_world.c
    #include <stdio.h>
    #include <stdlib.h>

    int main(){

       printf("hello world!\n");
       return 0;
    }
    ```
2. **Simulation**
    完成上方步驟後接下來我們會使用剛剛產生的 ELF file利用 QEMU進行模擬，同學請根據下方指令操作。
    ```shell=
    ## command
    $ cd ~/projects/lab06/lab3
    $ qemu-system-riscv32 -nographic -machine virt -bios none -kernel hello_world

    ## result
    hello world!Baremetal code runs to the end. (pressing `ctrl+a x` to quit)
    ## 按下 `ctrl+a`, 放開後按下 `x`離開模擬。
    ```

### Lab6-3-3 C / Assembly Hybrid Programming
剛剛我們先將 c code compile成 RISCV binary, 再利用 QEMU進行 simulation。接下來則是要示範如果我們只想要將 c code的一部分或是一個 function以 assembly code的方式去跑，應該要如何操作。
#### Example 1 : Simple Assembly Code
- c code在執行時如果所有 function都交給 comiler自動 compile的話，所產生的 assembly code有些是沒有效率的。在這種情況下會選擇自行手刻 assembly code並讓 c code呼叫。 Example 1中提供簡單程式舉例如何將 c code中的一個 function 轉換為 assembly code並執行。
- sum_ex1_asm.S 的細節請參考[Understanding RISC-V Calling Convention](https://inst.eecs.berkeley.edu/~cs61c/resources/RISCV_Calling_Convention.pdf)裡面的說明，下方指令會編譯RISCV 的binary, 然後在qemu 上跑simulation, 比對C 與Assembly 的實作結果是否一致。

首先我們先利用 `example_1_main.c`和 `example_1_sum_wrong.S`進行模擬。
```shell=
## open Lab6-3 folder
$ cd ~/projects/lab6/Lab6-3

## compile RISC-V binary
$ riscv64-unknown-elf-gcc -march=rv32im -mabi=ilp32 -nostartfiles -Tvirt.lds boot.S example_1_main.c example_1_sum_wrong.S -o example_1


## Run simulation
$ qemu-system-riscv32 -nographic -machine virt -bios none -kernel example_1

## you will see the following output
C code sum_c(5)=15

## ...your simulation will stuck, press `ctrl+a x` to exit simulation.
```  
同學應該會發現程式會卡在 `C code sum_c=15`這個部分而無法完成模擬。這是因為在 riscv中有規定 calling covention格式，如果在 call function中使用到 `s0`, `s1`, `s2`等暫存器，calling convention規定必須要在 ret時回覆該暫存器原來的值 (如下圖所示)，否則會造成程式錯誤。
![](https://course.playlab.tw/md/uploads/b152391e-a944-4f49-9ab6-b2547f8d9419.png)

而為了避免這個問題我們會使用 Lab3-2所說的 stack pointer來儲存這些暫存值，讓我們在 function中可以隨意更動 `s0`, `s1`, `s2`, 只要確保 ret時有回歸原先的值即可。接下來請同學將`example_1_main.c`中的第29行改為 :
```cpp=29
out = sum_ex1_asm(n);  ## call the function with right calling convention
```
- `example_1_sum.S`
```armasm=
    .text                        # code section 
    .global sum_ex1_asm          # declar the sum_asm function as a  global function
    .type sum_ex1_asm, @function # define sum_asm as a function 
sum_ex1_asm:
prologue :
    addi   sp,sp, -16 
    sw     ra,0(sp) 
    sw     s0,4(sp) 
    sw     s1,8(sp)
    sw     s2,12(sp)
    li     s0,1 
    mv     s1,a0 
    mv     s2,zero
loop_start :
    bgt    s0,s1,loop_end
    mv     a0,s0 
    add    s2,s2,a0 
    addi   s0,s0,1 
    j      loop_start
loop_end :
    mv     a0,s2

epilogue :
    ## Result will be correct if you did not store "ra" 
    lw     ra,0(sp)   
    lw     s0,4(sp)
    lw     s1,8(sp)
    lw     s2,12(sp) 
    addi   sp,sp,16 
    jr     ra

    .size sum_ex1_asm, .-sum_ex1_asm
```
使程式呼叫 `example_1_sum.S`這份 assembly code, 同學可以看到在這份 assembly code中我們會先使用 stack pointer & memory儲存 `s0`, `s1`, `s2`這些**在此 function中會被改動的 register**，在 ret之前再將值返還。接下來可以使用下面指令再次進行模擬，會發現這次程式就可以成功執行完畢。
```shell=
$ riscv64-unknown-elf-gcc -march=rv32im -mabi=ilp32 -nostartfiles -Tvirt.lds boot.S example_1_main.c example_1_sum.S example_1_sum_wrong.S -o example_1

## Run simulation
$ qemu-system-riscv32 -nographic -machine virt -bios none -kernel example_1

## Result, code runs to the end successfully.
C code sum_c=15
ASM code sum_ex1_asm=15
Baremetal code runs to the end. (pressing `ctrl+a x` to quit)
```
:::warning
lab6資料夾中提供 lab code，請同學實際操作並了解其中的差異。
    1. `example_1_main.c` 
    2. `example_1_sum.S`
    3. `example_1_sum_wrong.S`
:::

#### Example 2: Function call in Assembly
Example 1 提供了一個簡單 assembly code範例，Example 2則是考慮在一個 function中繼續呼叫另外一個 function的動作，和 Example 1 中相同會需要使用到 calling convention的動作。 Example 1 & Example 2 的模擬結果會相同，但是所應用的情境有所差異。請同學一樣利用下方指令在 qemu上進行模擬。
```shell=
$ cd ~/projects/lab06/lab3

## compile RISC-V binary
$ riscv64-unknown-elf-gcc -march=rv32im -mabi=ilp32 -nostartfiles -Tvirt.lds boot.S example_2_sum.S example_2_main.c -o example_2

## Run simulation
$ qemu-system-riscv32 -nographic -machine virt -bios none -kernel example_2

## you will see the following output
C code sum_c(5)=15
ASM code sum_asm(5)=15
```  
- `example_2_main.c`
```cpp=
// main.c
#include <stdio.h>
#include <stdlib.h>

int formula(int n){
  return (1+n)*n/2; 
}

int sum_c(int n){
  return formula(n);
}

int sum_asm(int n);

int main(){
  int n = 5;
  int out = 0;
  out = sum_c(n);
  printf("C code sum_c(%d)=%d\n", n, out);  
  out = sum_asm(n);
  printf("ASM code sum_asm(%d)=%d\n", n, out);
  return 0;
}
```
同學可以注意到下方的 `example_2_sum.S`我們並沒有如同 `example_1_sum.S`一樣先將 `s0`, `s1`, `s2`這些暫存器的資料利用 stack pointer儲存，而是改用 `t0`, `t1`, `t2`代替。可以這樣做的原因是因為在 calling convention的規範中 `t0`, `t1`, `t2`暫存器並沒有被規定在 function結束後需要返還原本的值，所以這樣使用並不會造成問題。
![](https://course.playlab.tw/md/uploads/a8ce995d-a962-400c-8198-0d53f18a6f37.png)
- `example_2_sum.S`
```armasm=
    .text                        # code section 
    .global sum_ex2_asm          # declar the sum_asm function as a  global function
    .type sum_ex2_asm, @function # define sum_asm as a function 
sum_ex2_asm:
prologue :
    addi sp,sp,-4
    sw   ra,0(sp)

funct_start :
    jal  formula

epilogue :
    lw   ra,0(sp)
    addi sp,sp,4
    jr   ra

formula :
    mv   t0,a0
    li   t1,1
    li   t2,2
    add  t1,t0,t1
    mul  t1,t0,t1
    div  t1,t1,t2
    mv   a0,t1
    jr   ra
    
    .size sum_ex2_asm, .-sum_ex2_asm
```
### Lab6-3-4 Run a sample baremetal code on QEMU (Optional)
在晶片開發的流程中, 為了減少晶片製造出來之後的移植(porting)工作, 也為了能讓早期的軟體開發更貼近真實硬體製造出來的需求, 很多開發者會採用更複雜的硬體模擬軟體, 比如說QEMU 來作為軟體開發的平台, 也因此在上面開發出來的binary 就必須要處理一些與硬體相關的參數設定與設計假設, 下面的文件中描述了一個完整的baremetal framework是如何實作出來的, 在這份文件中所使用的QEMU, 是一個RISC-V full-system emulator, 可以在上面跑完整的OS (e.g. Linux) 以及跟晶片量產之後幾乎相同的software stack. 

- [Playlab Baremetal Framework on RISCV](https://course.playlab.tw/md/fMS-uOXCRmWN2_WU8TTaxw)

Debug Tips
===
## 1. Entry point address & setting the CPU’s program counter
如果在實作的過程中遇到問題，提供同學一些可以找到 bug的方法。先利用 [Entry point address & setting the CPU’s program counter](https://course.playlab.tw/md/fMS-uOXCRmWN2_WU8TTaxw#Entry-point-address-amp-setting-the-CPU%E2%80%99s-program-counter)找到利用qemu進行模擬實的entry point，使用以下指令。
```shell=
$ readelf -h <your ELF file>
```
![](https://course.playlab.tw/md/uploads/76ab5c64-29da-4839-bf91-879bee1703c3.png)
以 `example_1`的 ELF file為例，TA的 entry point為 0x80000000。

## 2. Enabling QEMU instruction tracing
接著再利用 [Enabling-QEMU-instruction-tracing](https://course.playlab.tw/md/fMS-uOXCRmWN2_WU8TTaxw#Enabling-QEMU-instruction-tracing) 查看 assembly code的執行情形，指令如下。
```shell=
$ qemu-system-riscv32 -nographic -machine virt -bios none -kernel <your Elf file> -d in_asm
```
同樣以 `example_1`的 ELF file為例，會發現如果使用 `example_1_asm_wrong .S`這個錯誤的 assembly code進行模擬會停在 `x800000e0`這個 address。
![](https://course.playlab.tw/md/uploads/7a119ba3-cb57-4acb-9261-cd5fd2429bd7.png)

## 3. Object Dump
最後利用下方指令將 assembly code dump出來，再回去對照上方 assembly code卡住的位址。
```shell=
$ riscv64-unknown-elf-objdump -d <your ELF file>
```
依樣以 `example_1`為例。使用此指令會出現如下圖包含 function, address & instruction的資訊，這時候我們需要去找到剛剛程式停止的那個 address (上方範例為 `x800000e0`這個address)，根據它去做 debug。
![](https://course.playlab.tw/md/uploads/197e71e3-02be-44b9-95ba-ac9f88b430f3.png)
依上圖為例就是在 `example_1_main.c`中的
```cpp=29
out = sum_ex1_asm_wrong(n);
```
完成後要進行下一行
```cpp=30
puts("ASM code sum_ex1_asm=");
```
發生了錯誤，同學在遇到錯誤時可以參考這個流程進行 debug。

### Debug Tips Reference
- [Enabling-QEMU-instruction-tracing](https://course.playlab.tw/md/fMS-uOXCRmWN2_WU8TTaxw#Enabling-QEMU-instruction-tracing)

Homework
===
- HW6-1 需要利用組合語言完成費波那契數列 (Fibonacci Series) 的運算，詳細作業要求在 HW6-1 中。作業中需要利用組合語言進行**遞迴**的動作，仍未熟悉組合語言遞迴部分可以參考 `exercise2` 或是老師上課內容。
- HW6-2 會利用 Lab3-3所使用的 qemu emulator進行操作，使用 c code和 assemnly code交叉驗證同學所做的 HW3-1的正確性。
- HW6-3 作業需要利用組合語言完成一數獨演算法，此 LAB3 參考 [UCI CS152 LAB1](https://www.ics.uci.edu/~swjun/courses/2021W-CS152/material/lab1.pdf) 之組合語言練習。作業中同樣會需要運用到**遞迴**的手法。
- 作業中會需要用到的指令如下，請同學請自行將指令更換成所需要模擬的檔案
```shell=
$ cd ~/projects/lab06/<your_folder_name>

## compile RISC-V binary
$ riscv64-unknown-elf-gcc -march=rv32im -mabi=ilp32 -nostartfiles -Tvirt.lds boot.S <assembly_code> <c_code> -o <object_name>

## Run simulation
$ qemu-system-riscv32 -nographic -machine virt -bios none -kernel <object_name>
```
### Hw6-1 費波那契數列 Fibonacci Series
- Hw6-1 請完成費波那契數列演算法，下方提供演算法 C code 和示意圖。
- 作業要求：
    1. 請完成 HW\w6-1並在 **Venus上完成模擬並依照 homework template繳交**。建議使用 Lab3-2提到的遞迴方法。
    2. 預先設定 ==$n$== 值定義在 `x10` (register a0) 中，而輸出的結果 ==$fibonacci(n)$== 需要被放在 `x11` (register a1)中。 **繳交作業時請將 n定義為 16並算出正確答案**。

- `Fibonacci_Series.c`
    ```cpp=
    int fibonacci(int n) { 
        if(n == 0) 
            return 0;
        else if(n == 1) 
            return 1;
        else 
            return fibonacci(n-1)+fibonacci(n-2); 
    }
    ```
- 演算法示意圖：
    ![](https://course.playlab.tw/md/uploads/e5225c22-d650-4d68-be1a-e64210cf1530.png  =50%x)

### Hw6-2 C/Assembly Hybrid Programming 
- 完成 HW6-1後請依照 Lab6-3 的方法產生出 ELF file並完成 HW6-1中費波那契數列 function的模擬，作業要求和程式碼格式可以參照下方作業範例 `main.c`和 `fibonacci.S`。
- 作業要求
    - 請依照下方範例程式碼完成作業。
    - 完成後請依照 **HW Preparation and Background**的指令進行檔案的 compile & simulation.
- 作業範例
    - `main.c`
    ```cpp=
    //main.cpp
    #include <stdio.h>
    #include <stdlib.h>
    
    
    int fibonnaci(int n){ //TODO
    }
    
    int fibonnaci_asm(int n);
    int main(){
        int n = 6     //setup input value n, modify by yourself
        int out = 0;
        out = fibonacci_c(n);
        printf("C code fibonnaci_c(%d)=%d\n", n, out);  
        out = fibonnaci_asm(n);
        printf("ASM code fibonnaci_asm(%d)=%d\n", n, out);
        return 0;
    } 
    ```
    - `fibonacci.S`
    ```armasm=
        .text                          # code section 
        .global fibonacci_asm          # declar the sum_asm function as a  global function
        .type fibonacci_asm, @function # define sum_asm as a function 
    fibonnaci_asm:
        
        #TODO
        
        .size fibonnaci_asm, .-fibonnaci_asm
    ```
### Hw6-3 2x2 Sudoku
Hw6-3需要完成2x2簡易數獨的組合語言，數獨規則說明和作業要求如下。
- 作業要求 & 注意事項：
    1. 實作C 版的演算法在 main.c:sudoku_2x2_c()並更改 test_c_data中的值。
    2. 實作Assembly 版的演算法在 sudoku_2x2_asm.S並更改 test_asm_data中的值。
    3. 撰寫數獨演算法之測資可以參考main.c 裡所提供的 test_c_data 跟test_asm_data，你也可以修改這兩個array以自己的資料進行測試。你繳交作業後, TA會以 TA的測資 (共三筆) 進行驗證, 但是TA 的測資不會事先公告給大家。
    4. 請依照lab3/HW3-3 所提供的code template 完成此次作業並依照 Lab3-2, Lab3-3的教學利用 qemu完成模擬。
    5. 完成模擬出現的 *same result* 或是 *different result*等字樣只是用來判斷你用 c code以及 assembly code所完成的 function是一樣的，**==並不代表所解出來的數獨答案是對的==**，請同學特別注意。
- 數獨規則：
    ![](https://course.playlab.tw/md/uploads/0315aef0-9dd2-4c23-9f87-b6ebbd807039.png =35%x)![](https://course.playlab.tw/md/uploads/a144ddc0-8060-4d3e-a0da-dda1d33855f1.png  =35%x)
    
    1. 每一行、列和子方框中不可出現重複數字。
    2. 以 2x2 大小的數獨為例，可出現的數字為1~4。
    3. 提供之測試資料中，`0` 為空白，即需要被填入的資料。
- 演算法：
    - 數獨演算法下方 TA有提供 `Backtracking Algorithm` 的 pseudocode，撰寫作業時可以直接使用此演算法。
    - 除了 `Backtracking Algorithm` 演算法外，也可以自行上網搜尋演算法，確保答案正確即可。
    - `Backtracking Algorithm`
        ```cpp=
        # backtracking algorithm
        boolean solve(int index) {
            if ( index >= 16 ) {
                // 如果檢查完所有的格子，回傳 True
                return true;                                 
            }
            // set是一個儲存所有資料的array
            if ( set[index] > 0 ) {
                // 如果格子中已經有值了則會往下一格判斷
                return solve(index+1);                       
            } else {
                // 判斷目前這格在 1~4是否有符合條件
                for ( n = 1 to 4 ) {
                     // 如果有的話就往下一格作判斷（遞迴）直到每一格都符合條件為止
                    set[index] = n;
                    // check function用來檢查當前這格放入這個數值是否正確
                    if ( check(index) and solve(index+1) ) 
                        // solve(index+1) function則是繼續判斷下一格的值
                        return true;                         
                }
            }
            // returns the value to 0 to mark it as empty
            set[index] = 0;          
            return false;   // no solution
                                   
        }
        ```
- Hw6-3作業範本
    - `main.c`
    ```cpp=
    #include <stdlib.h>
    #include "sudoku_2x2_c.h"
    #define SIZE 16

    # You can modify your own test data here.
    char test_c_data[16] = { 0, 0, 2, 0, 
                             0, 0, 0, 4,
                             2, 3, 0, 0, 
                             0, 4, 0, 0 };

    char test_asm_data[16] = { 0, 0, 2, 0, 
                               0, 0, 0, 4,
                               2, 3, 0, 0, 
                               0, 4, 0, 0 };

    void print_sudoku_result() {
        int i;
        char str[25];
        puts("Output c & assembly function result\n");
        puts("c result :\n");
        for( i=0 ; i<SIZE ; i++) {   
            int j= *(test_c_data+i);
            itoa(j, str, 10);
            puts(str);
        }

        puts("\n\nassembly result :\n");
        for( i=0 ; i<SIZE ; i++) {
            int j= *(test_asm_data+i);
            itoa(j, str, 10);
            puts(str);
        }

        int flag = 0;
        for( i=0 ; i<SIZE ; i++) {
            if (*(test_c_data+i) != *(test_asm_data+i)) {
                flag = 1;
                break;
            }
        }

        if (flag == 1){
            puts("\n\nyour c & assembly got different result ... QQ ...\n");
        } else {
            puts("\n\nyour c & assembly got same result!\n");
        }
    }


    void sudoku_2x2_asm(char *test_asm_data); // TODO, sudoku_2x2_asm.S

    void sudoku_2x2_c(char *test_c_data); // TODO, sudoku_2x2_c.c

    int main() {
        sudoku_2x2_c(test_c_data);
        sudoku_2x2_asm(test_asm_data);
        print_sudoku_result();
        return 0;
    }
    ```
    - `sudoku_2x2_asm.S`
    ```armasm=
        .text                           # code section 
        .global sudoku_2x2_asm          # declare the asm function as a global function
        .type sudoku_2x2_asm, @function # define sum_asm as a function 
    sudoku_2x2_asm:
        
        #TODO
        
        .size sudoku_2x2_asm, .-sudoku_2x2_asm
    ```


## Homework Submission Rule
- **Step 1**
    請在自己的 GitLab內建立 `lab06` repo，並將本次 Lab 撰寫的程式碼放入這個repo。另外記得開權限給助教還有老師。
- **Step 2**
    - 請參考[(校名_學號_姓名) ACAL 2024 Spring Lab 6 HW Submission Template](https://course.playlab.tw/md/_bTa9AW5T3yADCYewXmNxQ)，建立(複製一份)並自行撰寫 CodiMD 作業說明文件。請勿更動template裡的內容。
    - 關於 gitlab 開權限給助教群組的方式可以參照以下連結
        - [ACAL 2024 Curriculum GitLab 作業繳交方式說明 : Manage Permission](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Manage-Permission)
- **Step 3**
    - When you are done, please submit your homework document link to the Playlab 作業中心, <font style="color:blue"> 清華大學與陽明交通大學的同學請注意選擇對的作業中心鏈結</font>
        - [清華大學Playlab 作業中心](https://nthu-homework.playlab.tw/course?id=2)
        - [陽明交通大學作業繳交中心](https://course.playlab.tw/homework/course?id=2)

    
    

