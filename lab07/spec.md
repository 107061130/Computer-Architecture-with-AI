---
title: ACAL 2024 Curriculum Lab 7 - RISC-V Instruction Set Emulator
robots: noindex, nofollow
---

# <center>ACAL 2024 Curriculum Lab 7 <br /><font color="＃1560bd">RISC-V Instruction Set Emulator</font></center>

###### tags: `AIAS Spring 2024`

[toc]

## Introduction
- 這個 Lab 中會運用 RISC-V emulator 進行 assembly code 執行過程的模擬，我們將會：
    1. 學習如何利用 C++ 撰寫 emulator 進行組合語言的模擬。
    2. 針對既有的 emulator 程式碼進行修改，擴增已實作的 RISC-V ISA。
- Homework 中將請同學分組共同在既有的 RISC-V emulator 中實作 RISC-V 的 Bit-manipulation extension， TA 會準備一個相關文件說明如何運用 Git 進行多人開發的協作與程式碼管理。

# Lab Introduction & Demonstration

## Lab 7-0 Environment and Emulator
- Build course workspace and bring up a docker container
    - 在開始 lab 之前必須先將課堂的 docker workspace 啟動並進入環境，相關操作可以複習先前的說明文件 - [Lab 0 - Course Environment Setup](https://course.playlab.tw/md/33cXunaGSdmYFej1DJNIqQ)

### Lab 7-0-1 Environment and Repo Setup

:::warning
- You may setup passwordless ssh login if you like. Please refer to [Use SSH keys to communicate with GitLab](https://docs.gitlab.com/ee/user/ssh.html)
- Also, if you would like to setup the SSH Key in our Container. Please refer to this [document](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Set-up-the-SSH-Key) to set up the SSH Key in acal-curriculum workspace.
:::

```shell
## bring up the ACAL docker container 
## clone the lab 07 repository
$ cd ~/projects
$ git clone ssh://git@course.playlab.tw:30022/acal-curriculum/lab07.git
$ cd lab07

## show remote repositories 
$ git remote -v
origin ssh://git@course.playlab.tw:30022/acal-curriculum/lab07.git (fetch)
origin ssh://git@course.playlab.tw:30022/acal-curriculum/lab07.git (push)

## add your private upstream repositories
## make sure you have create project repo under your gitlab account
$ git remote add gitlab ssh://git@course.playlab.tw:30022/<YOUR_LDAP_NAME>/lab07.git

$ git remote -v
gitlab ssh://git@course.playlab.tw:30022/<YOUR_LDAP_NAME>/lab07.git (fetch)
gitlab ssh://git@course.playlab.tw:30022/<YOUR_LDAP_NAME>/lab07.git (push)
origin ssh://git@course.playlab.tw:30022/acal-curriculum/lab07.git (fetch)
origin ssh://git@course.playlab.tw:30022/acal-curriculums/lab07.git (push)
```

When you are done with your code, you have to push your code back to your own gitlab account with the following command :

```shell
## the first time
$ git push --set-upstream gitlab main
## the second time and later
$ git fetch origin main
## remember to solve conflicts
$ git merge origin/main
## then push back to your own repo
$ git push gitlab main
```

### Lab 7-0-2 RISC-V Assembly Emulator

#### Implement Assembly Emulator
Lab 7 會基於第三方的 open source project - sangwoojun/rv32emulator 進行開發實作，其原始的 repo 位於 [GitHub](https://github.com/sangwoojun/rv32emulator)。在我們稍早 clone 的 Lab 7 repository 中已經包含需要的程式碼，因此同學不需要再重新 clone 一份 GitHub 的檔案。

Lab 7 repository 中的 `lab1/` 是一個已經可以編譯執行的版本，如果要進行 assembly code 的模擬，請將欲執行的 assembly code 以 `.txt` 檔案放入該資料夾。在 `lab1/` 已經先幫同學準備好一份範例的 `test.txt` 方便同學嘗試，也請同學參考下方的 command 進行操作，==Emulator操作說明== 中則有執行過程中可用的操作指令可以參考。

```shell
## in lab 7-1 folder

## list the files in the folder
$ ls -l
total 124
-rwxrwxrwx 1 user user 28026 Mar  2 09:54 emulator.cpp
-rwxrwxrwx 1 user user 92455 Mar  2 09:54 linenoise.hpp
-rwxrwxrwx 1 user user    67 Mar  2 09:54 Makefile
drwxrwxrwx 1 user user  4096 Mar  2 09:54 obj
-rwxrwxrwx 1 user user  1661 Mar  2 09:54 README.md
-rwxrwxrwx 1 user user    96 Mar  2 09:54 test.txt

## use make command to compile emulator.cpp
$ make
mkdir -p obj
g++ -o obj/emulator emulator.cpp -g -std=c++11

## use test.txt to test whether your emulator work successfully
## you can replace test.txt with any other assembly code you want
$ ./obj/emulator test.txt
Parsing input file

Next: addi x28, x0, 2
[inst:      1 pc:      0, src line    4]
>>

## press `enter` til the end of process
Reached an unimplemented instruction!
Instruction: mul x30, x28, x29
inst:      3 pc:      8 src line: 6
x00:0x00000000 x01:0x00000000 x02:0x00000000 x03:0x00000000 x04:0x00000000 x05:0x00000000 x06:0x00000000 x07:0x00000000
x08:0x00000000 x09:0x00000000 x10:0x00000000 x11:0x00000000 x12:0x00000000 x13:0x00000000 x14:0x00000000 x15:0x00000000
x16:0x00000000 x17:0x00000000 x18:0x00000000 x19:0x00000000 x20:0x00000000 x21:0x00000000 x22:0x00000000 x23:0x00000000
x24:0x00000000 x25:0x00000000 x26:0x00000000 x27:0x00000000 x28:0x00000002 x29:0x00000003 x30:0x00000000 x31:0x00000000
Execution done!
```

- Emulator 操作說明
    - Emulator 在程式碼結束後會自動印出所有 register 暫存的值。
    - 在成功編譯並開始執行 emulator 後可以利用下方的輸入指令控制 emulator。
        - `s` : 執行下一行指令 (或是直接按下 `enter`)。
        - `c` : 執行到程式碼結束。
        - `q` : 無條件終止運作。
        - `r` : 讀取暫存器值，如果僅輸入 `r`會讀取所有預設暫存器 (x0~x31)的值。也可以指定暫存器輸出，e.g., `rx0`指令可以輸出 `x0`暫存器的值。
    - 上方為簡單的摘要， [GitHub repository](https://github.com/sangwoojun/rv32emulator) 中則有更詳細的說明。

同學按照指令操作會發現 Emulator會跳出以下字樣：

```bash
Reached an unimplemented instruction!
```

並且可以發現 register `x30` 並沒有被寫入值，也就是說我們的 `MUL` 指令並沒有被成功執行。這是因為我們目前所使用的 emulator 只包含了基礎的 RISC-V RV32I ISA，而 `test.txt` 中有使用到 `MUL` 指令，此指令為 RISC-V 擴增指令集 (M-Standard Extension) 中的指令，所以我們當然沒有辦法順利執行。

在接下來的 [Lab 7-1 - Instruction Extension Flow](#Lab-7-1---Instruction-Extension-Flow) 中我們會將 emulator 改寫成可以支援 `MUL` 指令。請同學依照下面的說明將 TA 註解掉的部分逐一啟用，了解讓 emulator 可以順利執行 `MUL` 指令在實作上需要哪些步驟共同完成。

## Lab 7-1 - Instruction Extension Flow
在 Lab 6 中運用到 RISC-V 的標準指令集 (Standard ISA) 進行操作，但其實標準指令集並不包含一些常用但較為複雜的運算，像是常常被使用到的 `MUL` 和 `DIV` 等乘除法指令。在知道如何使用 emulator 後我們要學習怎麼對現有的 emulator 進行指令集的擴增，方便在後續可以使用更多功能更強大的指令。 [RISC-V Instruction Set Manual](https://riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf) 擁有許多擴增指令提供我們去做選擇，這個 lab 中我們會對 emulator 嘗試新增 `MUL` 指令，而在 homework 中我們會練習 M Standard Extension 和 Bit Manipulation Extension 的指令新增。

:::warning
- 下方所列出的行數為 code 中 **function 開頭的行數**，同學可以對照著 repository 中的 source code 閱讀。
- 講解時只會針對進行 extension 需要修改的部分講解，並不會完整介紹整份 `emulator.cpp`，剩餘部分請同學自行閱讀或是可以預約 office hour 與 TA 討論。
:::

1. `typedef enum instr_type` - `line 48`
    列出的是**目前 emulator 所支援的所有 Assembly 指令**。這邊如果要增加 `MUL` 的指令，可以直接在 function 內增加指令（如下方範例）。
    ```cpp=48
    typedef enum {
      UNIMPL = 0,

      //instruction added
      MUL,
      //*****************

    } instr_type;
    ```
2. `instr_type parse_instr` - `line 95`
    接續上方新增指令的動作，在 `parse_instr` function 中加入相對應的 operand parse 方式。
    ```cpp=95
    instr_type parse_instr(char* tok) {
      //instruction added
      if ( streq(tok , "mul")) return MUL;
      //*****************
    }
    ```
3. (1) `int parse_instr` - `line 501` 
    `line 522` 的 switch 條件需要根據擴增的指令新增，確保該指令輸入格式的正確性。格式正確指的是我們在自己準備的 `test_data.txt` 檔案中每個指令的格式是否符合 emulator 解讀的要求，emulator 預設以 `[inst] [o1] [o2] [o3] [o4]` 的格式進行讀取，除 `[inst]` 外的 `[o1] [o2] [o3] [o4]` 可以用「空格」或是「半形逗號」隔開。
    接著以 `MUL` 指令為例，可以先看到 [RISC-V Instruction Set Manual](https://riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf) 中 p.35 所提供的 `MUL` 指令格式。正確的指令應該會像是：`MUL rd rs1 rs2`，對應到上方格式後發現 `[o4]` 部分應該為空，而其他部分均需要有值寫入。
    ![](https://course.playlab.tw/md/uploads/0ecbc626-6f4f-4161-8b16-4e13b1fde929.png)
    在 `line 526~531` 我們需要指定輸入指令型態為**暫存器**或是固定數值。根據 `MUL` 指令的規則，所有 operand 都是**暫存器**形態，因此我們透過 `line 528~530` 的 code 將 operand `o1` ~ `o3` 型態全部定義為暫存器。
    ```cpp=522
    switch( op ) {
        case UNIMPL: return 1;

        //instruction added
        case MUL:
            if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
            i->a1.reg = parse_reg(o1 , line);
            i->a2.reg = parse_reg(o2 , line);
            i->a3.reg = parse_reg(o3 , line);
            return 1;
        //*****************
    ```
3. (2) 如果遇到和 `MUL` 不同的指令格式
    上方提到指定輸入指令型態的部分，如果指令格式並非和 `MUL` 相同，則我們需要進行的處理就會不同。以 `ADDI` 指令為例，格式為 `ADDI rd rs1 imm`，而 immediate 的值為 12 bits。
    ![](https://course.playlab.tw/md/uploads/c771269d-35b0-47d0-bb53-7b435656f3c9.png)
    對照程式碼 `line 564~566` 可以發現我們一樣先將 `[inst] [o1] [o2] [o3] [o4]` 的 `[o1] [o2]` 型態給定為暫存器，並將 `o3` 給定為 sign extend 至 12 bits 後的值。而在 signextend function 的部分，有部分指令需要 signextend 的長度並不是 12，需要對照著 RISCV ISA改寫成正確的數值。
    ```cpp=562
    case ADDI: case SLTI: case SLTIU: case ANDI: case ORI: case XORI: case SLLI: case SRLI: case SRAI:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line, "Invalid format" );
        i->a1.reg = parse_reg(o1, line);
        i->a2.reg = parse_reg(o2, line);
        i->a3.imm = signextend(parse_imm(o3, 12, line), 12);    // signextend to 12 bits
        return 1;
    ```
4. execute - `line 642`
    完成上述步驟後需要讓 emulator 可以對資料進行正確的改動，需要更動 execute function 中 `line 765` 的 switch function。這邊可以看到因為要新增的指令是 `MUL`，所以在 execute function 中要做的動作就是將正確的 register 值取出，相乘後再放回正確的 register 中。
    ```cpp=765
        switch (i.op) {

          //instruction added
          case MUL: rf[i.a1.reg] = rf[i.a2.reg] * rf[i.a3.reg]; break;
          //*****************
        }
    ```
5. 完成上述步驟後請再根據下方指令 compile 一次 emulator。
    ```shell
    ## in lab 7-1 folder
    
    ## make
    $ make

    ## execute
    $ ./obj/emulator test.txt

    ## press `enter` til the end
    Reached Halt and Catch Fire instruction!
    x00:0x00000000 x01:0x00000000 x02:0x00000000 x03:0x00000000 x04:0x00000000 x05:0x00000000 x06:0x00000000 x07:0x00000000
    x08:0x00000000 x09:0x00000000 x10:0x00000000 x11:0x00000000 x12:0x00000000 x13:0x00000000 x14:0x00000000 x15:0x00000000
    x16:0x00000000 x17:0x00000000 x18:0x00000000 x19:0x00000000 x20:0x00000000 x21:0x00000000 x22:0x00000000 x23:0x00000000
    x24:0x00000000 x25:0x00000000 x26:0x00000000 x27:0x00000000 x28:0x00000002 x29:0x00000003 x30:0x00000006 x31:0x00000000
    Execution done!
    ```

這次可以發現 emulator 是在遇到 `hcf` 指令（emulator 中預設的程式結束指令）時才結束，也就是可以順利執行 M Standard Extension 中的 `MUL` 指令。觀察 register 中的結果也可以發現 `x28`、`x29` 和 `x30` 的值也和我們的 assembly code 描述一致。在 homework 中需要自行擴增作業要求的指令，因此請同學**務必熟悉 Lab7-1**。

:::warning
- `hcf` 即為終止程序的指令。
- 完成修改後需要再次執行 `Makefile` 產生新的執行檔。
:::

# Homework 7

## HW 7-1 RISC-V M-Standard Extension
此處我們要進行 M Standard Extension 中 4 個指令的擴增，請同學完成下方列出的指令。各指令的動作可以參考 [RISC-V Instruction Set Manual](https://riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf) 中的 Chapter 6，下方也有 TA 的說明。

### M Standard Extension 指令列表
![](https://course.playlab.tw/md/uploads/17cb6a22-817d-476d-b206-52f6dcbac799.png)

#### 指令說明
兩個 XLEN-bit 進行乘法操作後的結果會是 `2 * XLEN-bit`, 所以下方的「低位元」指的就是較低位元的 `XLEN-bit`，而「高位元」指的就是較高位元的 `XLEN-bit`。取餘數的話就不會有高低位元的問題。

- `MUL`：將 ==低位元== 的 `XLEN-bit` 存回目標暫存器。
- `MULHU`：預設乘法資料形式為 ==unsigned × unsigned==，將==高位元==的 `XLEN-bit` 儲存。
- `REM`：預設取餘數資料形式為 ==signed % signed==。
- `REMU`：預設取餘數資料形式為 ==unsigned % unsigned==。

### 作業要求
請同學參考上方指令格式和 Lab 7-1 中的指令擴增方法依照作業要求完成 HW 7-1。

1. 請依據該指令需要完成的動作自行編寫 assembly code 進行測試，範例可以參考 Lab 7-1 中的 `test.txt`，並且在 HW report 中呈現測試結果。詳細格式請參考 Homework Document Template。
2. 同學在進行測試時以自己編寫的 assembly code 為主，但是 TA 會另外準備一份測資用以檢測 function 的正確性。

### 測試指令
完成作業後測試指令如下，同學需要將測試是否正確的程式碼放入 HW 7-1 的 test_assembly folder 中，同一個 function 的程式碼可以不只有一份，檔名區分清楚就好。

```shell
## make
$  make
$  ./obj/emulator ./test_assembly/<ASM_FILE>
```

## HW 7-2 RISC-V Bit Manipulation Extension
HW 7-2 進行支援 RV32 的 Bit Manipulation Extension 的指令擴增，相關的 spec 可以參考 reference 或是下方表格中的連結，而需要完成的指令可以參考下方表格。在 HW 7-2 中各組同學需要合力完成 Bit Manipulation Extension 的所有指令，TA 在下方有提供一個 Gitlab 共同開發的文件，請同學閱讀後遵循開發流程完成作業。

### 作業要求
- 請參考 Gitlab 共同開發文件完成 HW 7-2，請注意 HW7-2 和 HW7-1 中一樣需要請同學自己提供測試的 assembly code。
- 只需要 **完成支援 RV32 的 Bit manipulation extension 指令**（RV32 欄位有打勾的 32 個指令），如下方表格所示。各指令的 spec 可以參考 instruction 連結或是作業 reference 中的 RISCV-Bitmanipulation Spec。
- Zba, Zbb, Zbc and Zbs 代表的意思如下，在 spec 連結中也有紀錄。
    - Zba - address generation
    - Zbb - basic bit manipulation
    - Zbc - carryless multiplication
    - Zbs - single-bit instructions
- 下面的指令表最後一欄有 owner，請每一組的組長要協調分工認領指令，把這個表放在你們的 group repo code 裡面的 `README.md` 裡，老師跟 TA 才知道哪些指令是誰做的。

### 指令表

| RV32 | RV64     | Mnemonic                  | Instruction       | Zba      | Zbb | Zbc | Zbs |Owner|
| ---- | -------- | ------------------------- | ----------------- | -------- | --- | --- | --- |---|
|      | &#10003; | add.uw _rd_, _rs1_, _rs2_ | [insns-add_uw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/add_uw.adoc) | &#10003; |     |     |     |  |
|&#10003;|&#10003;|andn _rd_, _rs1_, _rs2_|[insns-andn](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/andn.adoc)||&#10003;|| | |
|&#10003;|&#10003;|clmul _rd_, _rs1_, _rs2_|[insns-clmul](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clmul.adoc)|||&#10003;| | |
|&#10003;|&#10003;|clmulh _rd_, _rs1_, _rs2_|[insns-clmulh](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clmulh.adoc)|||&#10003;|| |
|&#10003;|&#10003;|clmulr _rd_, _rs1_, _rs2_|[insns-clmulr](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clmulr.adoc)|||&#10003;|| |
|&#10003;|&#10003;|clz _rd_, _rs_|[insns-clz](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clz.adoc)||&#10003;||
||&#10003;|clzw _rd_, _rs_|[insns-clzw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clzw.adoc)||&#10003;| | | |
|&#10003;|&#10003;|cpop _rd_, _rs_|[insns-cpop](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/cpop.adoc)||&#10003;|| | |
||&#10003;|cpopw _rd_, _rs_|[insns-cpopw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/cpopw.adoc)||&#10003;|| ||
|&#10003;|&#10003;|ctz _rd_, _rs_|[insns-ctz](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/ctz.adoc)||&#10003;|| | |
||&#10003;|ctzw _rd_, _rs_|[insns-ctzw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/ctzw.adoc)||&#10003;||||
|&#10003;|&#10003;|max _rd_, _rs1_, _rs2_|[insns-max](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/max.adoc)||&#10003;|| | |
|&#10003;|&#10003;|maxu _rd_, _rs1_, _rs2_|[insns-maxu](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/maxu.adoc)||&#10003;|| | |
|&#10003;|&#10003;|min _rd_, _rs1_, _rs2_|[insns-min](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/min.adoc)||&#10003;|| | |
|&#10003;|&#10003;|minu _rd_, _rs1_, _rs2_|[insns-minu](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/minu.adoc)||&#10003;|| | |
|&#10003;|&#10003;|orc.b _rd_, _rs_|[insns-orc_b](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/orc_b.adoc)||&#10003;|| | |
|&#10003;|&#10003;|orn _rd_, _rs1_, _rs2_|[insns-orn](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/orn.adoc)||&#10003;|| | |
|&#10003;|&#10003;|rev8 _rd_, _rs_|[insns-rev8](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/rev8.adoc)||&#10003;|| | |
|&#10003;|&#10003;|rol _rd_, _rs1_, _rs2_|[insns-rol](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/rol.adoc)||&#10003;|| | |
||&#10003;|rolw _rd_, _rs1_, _rs2_|[insns-rolw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/orlw.adoc)||&#10003;|| | |
|&#10003;|&#10003;|ror _rd_, _rs1_, _rs2_|[insns-ror](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/ror.adoc)||&#10003;|| | |
|&#10003;|&#10003;|rori _rd_, _rs1_, _shamt_|[insns-rori](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/rori.adoc)||&#10003;|| | |
||&#10003;|roriw _rd_, _rs1_, _shamt_|[insns-roriw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/roriw.adoc)||&#10003;|| | |
||&#10003;|rorw _rd_, _rs1_, _rs2_|[insns-rorw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/rorw.adoc)||&#10003;|| | |
|&#10003;|&#10003;|bclr _rd_, _rs1_, _rs2_|[insns-bclr](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bclr.adoc)||||&#10003;| | |
|&#10003;|&#10003;|bclri _rd_, _rs1_, _imm_|[insns-bclri](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bclri.adoc)||||&#10003;| | |
|&#10003;|&#10003;|bext _rd_, _rs1_, _rs2_|[insns-bext](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bext.adoc)||||&#10003;| | |
|&#10003;|&#10003;|bexti _rd_, _rs1_, _imm_|[insns-bexti](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bexti.adoc)||||&#10003;| | |
|&#10003;|&#10003;|binv _rd_, _rs1_, _rs2_|[insns-binv](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/binv.adoc)||||&#10003;| | |
|&#10003;|&#10003;|binvi _rd_, _rs1_, _imm_|[insns-binvi](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/binvi.adoc)||||&#10003;| | |
|&#10003;|&#10003;|bset _rd_, _rs1_, _rs2_|[insns-bset](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bset.adoc)||||&#10003;| | |
|&#10003;|&#10003;|bseti _rd_, _rs1_, _imm_|[insns-bseti](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bseti.adoc)||||&#10003;| | |
|&#10003;|&#10003;|sext.b _rd_, _rs_|[insns-sext_b](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sext_b.adoc)||&#10003;|| | |
|&#10003;|&#10003;|sext.h _rd_, _rs_|[insns-sext_h](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sext_h.adoc)||&#10003;|| | |
|&#10003;|&#10003;|sh1add _rd_, _rs1_, _rs2_|[insns-sh1add](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh1add.adoc)|&#10003;||| | |
||&#10003;|sh1add.uw _rd_, _rs1_, _rs2_|[insns-sh1add_uw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh1add_uw.adoc)|&#10003;||| | |
|&#10003;|&#10003;|sh2add _rd_, _rs1_, _rs2_|[insns-sh2add](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh2add.adoc)|&#10003;||||
||&#10003;|sh2add.uw _rd_, _rs1_, _rs2_|[insns-sh2add_uw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh2add_uw.adoc)|&#10003;||| ||
|&#10003;|&#10003;|sh3add _rd_, _rs1_, _rs2_|[insns-sh3add](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh3add.adoc)|&#10003;||| | |
||&#10003;|sh3add.uw _rd_, _rs1_, _rs2_|[nsns-sh3add_uw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh3add_uw.adoc)|&#10003;||| ||
||&#10003;|slli.uw _rd_, _rs1_, _imm_|[insns-slli_uw](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/slli_uw.adoc)|&#10003;||| ||
|&#10003;|&#10003;|xnor _rd_, _rs1_, _rs2_|[insns-xnor](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/xnor.adoc)||&#10003;|| | |
|&#10003;|&#10003;|zext.h _rd_, _rs_|[insns-zext_h](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/zext_h.adoc)||&#10003;|| |

:::warning
作業中只須完成 rv32 的部分。
:::

## Gitlab 共同開發Document
- [gitlab 多人協同工作教學文件 - 以HW7-2 為例](https://course.playlab.tw/md/Qqyg9O2JQYauvm_nNOr8Mw)

# Bonus 
## Bonus #1 - Emulator 加速
本次的 Bonus 要請同學試著加速我們在 Lab 7 中所使用到的 emulator。`emulator.cpp` 中有加入測試程式執行時間的部分，測試 emulator 優化程度的 `benchmark.txt` 也一同放在 `bonus` 資料夾中，同學請使用下方程式碼進行優化後程式碼的測試。TA 將 `Makefile` 改寫成會直接 compile & execute 的模式，執行完 benchmark 所花費的時間會直接 print 出來，同學可以用此數值作為優化程度的依據，最後寫進 HW report中。

```shell
## only need the make command 
$  make 
Your Emulator Duration Before Acceleration = : xxxxxxx s.

Your Emulator Duration After Acceleration = : xxxxxxx s.
```

在 bonus 中 TA 將檔案分為 `emulator.cpp` 和 `emulator_accelerate.cpp`，同學可以在 `emulator_accelerate.cpp` 中進行更動，嘗試優化 emulator。提供的 benchmark 執行一次大約會需要 5 分鐘左右的時間。同學在自己測試時，為了方便可以自行更動 benchmark 中 loop 的次數，但最後 TA 仍然會以原始的 benchmark 參數評斷優化程度。

## Bonus #2 - Another ISA Simulator Implementation 

我們在這個 lab 使用的 rv32emulator 因為簡單、易於修改，拿來當同學寫 ISA simulator 的入門練習比較適合，但是這個 emulator 有不少缺點，其中最大的一個缺點是 input 是 assembly program，所以無法使用我們一般 compile 好的 elf file 來執行。為了期末project 方便, 下面的 simulator 可以接受 elf file，這個bonus 的目的是把 RISC-V bitwise extension 移植到這個功能比較強大的 ISA emulator 上，這個 bonus 將會幫助同學未來Labs的進行。

- [RISC-V Instruction Set Simulator](https://github.com/ultraembedded/riscv/tree/master/isa_sim)

# Homework and Bonus Submission Rule

- **Step 1**
    - 請在自己的 GitLab 內建立 `lab07` repo，並將本次 lab 撰寫的程式碼放入這個 repo，另外記得開權限給助教還有老師。
- **Step 2**
    - 請參考 [(校名_學號_姓名) ACAL 2024 Spring Lab 7 HW Submission Template](https://course.playlab.tw/md/kdj2MTqcTQe1X2ilPhYAHA)，建立(複製一份)並自行撰寫 CodiMD 作業說明文件。請勿更動template 裡的內容。
    - 關於 gitlab 開權限給助教群組的方式可以參照以下連結
        - [ACAL 2024 Curriculum GitLab 作業繳交方式說明 : Manage Permission](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Manage-Permission)
- **Step 3**
    - When you are done, please submit your homework document link to the Playlab 作業中心, <font style="color:blue"> 清華大學與陽明交通大學的同學請注意選擇對的作業中心鏈結</font>
        - [清華大學Playlab 作業中心](https://nthu-homework.playlab.tw/course?id=2)
        - [陽明交通大學作業繳交中心](https://course.playlab.tw/homework/course?id=2)
    

References 
===
- [RV32 Emulator Github Source](https://github.com/sangwoojun/rv32emulator) 
- [RISC-V Instruction Set Manual](https://riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf)
- [RISCV-Bitmanipulation Spec (GitHub source)](https://github.com/riscv/riscv-bitmanip/tree/main/bitmanip/insns)
- [RISC-V Bit-Manipulation ISA-extensions (pdf)](https://raw.githubusercontent.com/riscv/riscv-bitmanip/master/bitmanip-draft.pdf)
