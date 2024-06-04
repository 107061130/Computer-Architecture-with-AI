# <center>ACAL 2024 Curriculum Lab 9 <br /><font color="＃1560bd"> 5-Stage Pipelined RISC-V CPU Design</font></center>

###### tags: `AIAS Spring 2024`

[toc]

## Introduction 
  In this Lab, we will implement a 5-stage pipelined CPU and some performance counters for performance analysis. You should have a good working understanding of digital design and basic computer architecture concepts as required in [ACAL 2024 Curriculum Lab 8 - Single-Cycle RISC-V CPU Design](https://course.playlab.tw/md/1HJ1oSsdTbieugOf4DaCwA) to succeed in this course. 
  
Lab 9
===
## Lab 9-0 Environment and Repo Setup
- Build Course docker and bring up a docker container
    - 在開始lab之前必須先將課堂的docker container run起來，並把一些環境建好，可以參考下面的tutorial : [Lab 0 - Course Environment Setup](https://course.playlab.tw/md/33cXunaGSdmYFej1DJNIqQ)
### Step 1 : Clone the Lab 09 repository
:::warning
- You may setup passwordless ssh login if you like. Please refer to [Use SSH keys to communicate with GitLab](https://docs.gitlab.com/ee/user/ssh.html)
- Also, if you would like to setup the SSH Key in our Container. Please refer to this [document](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Set-up-the-SSH-Key) to set up the SSH Key in acal-curriculum workspace.
:::

```shell=
## bring up the AIAS course docker container

## clone the lab9 files
$  cd ~/projects
$  git clone ssh://git@course.playlab.tw:30022/acal-curriculum/lab09.git
$  cd lab09

## In this lab, we have multiple branches for all the labs
## When you are doing a lab, remember to check out that branch
## first using the `git checkout <branch>` command
$ git branch -a
* master
  remotes/origin/HEAD -> origin/main
  remotes/origin/lab1
  remotes/origin/lab2
  remotes/origin/lab3
  remotes/origin/main

## add your private upstream repositories
## make sure you have create project repo under your gitlab account
$  git remote add gitlab ssh://git@course.playlab.tw:30022/<your ldap name>/lab09.git

$  git remote -v
gitlab ssh://git@course.playlab.tw:30022/<your ldap name>/lab09.git (fetch)
gitlab ssh://git@course.playlab.tw:30022/<your ldap name>/lab09.git (push)
origin ssh://git@course.playlab.tw:30022/acal-curriculum/lab09.git (fetch)
origin ssh://git@course.playlab.tw:30022/acal-curriculum/lab09.git (push)
```
### Step 2 : Setup your project upstream for submission
- **When you are done with your code**, you have to push your code back to your own gitlab account with the following command.

```shell=
## the first time
$  git push --set-upstream gitlab master

## after the first time
$  git fetch origin <branch name>
$  git push gitlab <branch name>
```
### Step 3: Setup RISC-V test program

- You will need tests to verify your design in Lab 9. We have prepared a test suite for you to get started. You will learn how to use the test suite and software tools to verify the correctness of your design. 

- File and folder description
    - **riscv-test/rv32ui_FullTest** - for full test of all instruction and **mergesort.s**
    - **riscv-test/rv32ui_SingleTest** - for single test of all instruction and **TestDataHazard.s**
    - Each Folder has a **golden.txt** for you to verify whether the result of simulation is correct (using register result).
#### Compile and generate the HEX file
```shell=
$ cd riscv-test
$ make all
```
Other Option
```shell=
## compile and dump data.hex and inst.hex (byte/line)
$ make hex  
## compile and dump info and code
$ make dump 
## compile and dump asm code (inst.asm)
$ make asm  
## make dump hex asm
$ make all
## clean up
$ make clean
```
<!--
:::info
- If these `make` commands cannot generate any files, please check whether your RISC-V toolchain can be accessed or not with the following command.

    ```bash
    $ which riscv32-unknown-elf-gcc
    /path/to/riscv32-unknown-elf-gcc    # can be accessed normally
    ```
- 
:::
-->

#### Create a new testbench in assembly
1. Go to **riscv-test/rv32ui_FullTest**, create a new assembly file (e.g.  **NewASM.S**).
2. Copy the following template to the new assembly file, **NewASM.S**, and your code and data to the corresponding sections. 
```cpp=
# See LICENSE for license details.

#*****************************************************************************
# NewASM.S <-- (modify file name here)
#-----------------------------------------------------------------------------

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
RVTEST_CODE_BEGIN

... (your inst) ... <-- (add inst here)

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

... (your data) ... <-- (add data here)

RVTEST_DATA_END
```

3. In **riscv-test/rv32ui_FullTest/Makefrag**, add the assembly file into the source list as shown in the line 23 as below.
```Makefile=
#=======================================================================
# Makefrag for rv32ui tests
#-----------------------------------------------------------------------

rv32ui_sc_tests = \
	simple \
	add addi \
	and andi \
	auipc \
	beq bge bgeu blt bltu bne \
	jal jalr \
	lb lbu lh lhu lw \
	lui \
	or ori \
	sb sh sw \
	sll slli \
	slt slti sltiu sltu \
	sra srai \
	srl srli \
	sub \
	xor xori \
	TestDataHazard \
    (add Newasm name here) \

rv32ui__tests = $(addprefix rv32ui-, $(rv32ui_sc_tests))
```
4. **clean up**, and **make** again.
```shell
## in the $(Lab9_FOLDER)/riscv-test folder, run the following commands
$ make clean
$ make all
```

#### Use Emulator to Create Vector Extension Program
- Build the Emulator First
```shell=
## In $(Lab9_FOLDER)Emulator
$ make
```
- Write asm file in `$(LAB09_FOLDER)/Emulator/test_code/<NewFile.S>`
- Run the Emulator and dump Files
```shell=
## In $(LAB09_FOLDER)/Emulator
$ ./obj/emulator ./test_code/<NewFile.S>

## For example, let's run emulator on the test_vadd_vv.S example
$ ./obj/emulator ./test_code/test_vadd_vv.S 
Parsing input file
Reached an unimplemented instruction!
translation done!

Next: lui x02, 0x00000002
[inst:      1 pc:      0, src line    3]
>>

## When you see the emulator prompt `>>`, type 'c'. 
## It will run to the end. List the files in the folder, 
## you will find two output hex files, data.hex and inst.hex. 
## A new inst.asm file is also created 
...
Execution done!
weifen@c23e293eb0b9  /workspace/projects/lab09/Emulator[69]$  ls
data.hex      emulator.h  linenoise.hpp  obj        test_code      translate.h
emulator.cpp  inst.asm    Makefile       README.md  translate.cpp

```
- More information about Emulator is included in the `$(Lab9_FOLDER)/Emulator/README.md` file
#### Copy the required files to the hardware folder
- copy **inst.hex, data.hex, and inst.asm** to **$(Lab9_FOLDER)/Hardware/Lab/src/main/resource/**
```shell=
## In $(Lab9_FOLDER)/Emulator
$ cp inst.hex ../Hardware/Lab/src/main/resource/
$ cp inst.asm ../Hardware/Lab/src/main/resource/
$ cp data.hex ../Hardware/Lab/src/main/resource/

## 或是你可以用下面的 script, 幫你copy 檔案
$ cd ../Hardware/Lab ## (or Hardware/HW)
$ bash ./load_test_data.sh Emulator
## 如果要copy Risc-V test 中 rv32ui-FullTest 裡的指令測試, 也可以用這個scriipt 加上 instruction name
$ bash ./load_test_data.sh add 
## 如果要copy Risc-V test 中 rv32ui-SingleTest 裡的指令測試, 也可以用這個scriipt 加上 -s instruction name
$ bash ./load_test_data.sh -s add 
```
#### Run the program on CPU
```shell=
$ sbt 'Test/runMain acal_lab09.topTest [-tbn verilator]'   # ( CPU <-> Data Memory )
$ sbt 'Test/runMain acal_lab09.topTest [-tbn verilator]' # ( CPU <-> AXI bus <-> Data Memory )
## [-tbn verilator] <-- add if need wavefile.
```
## Lab 9-1 Five-stage pipelined CPU
  In Lab8, you designed a single-cycle CPU and learned the basic design concept to implement the RV32I instruction set. In this lab, we want to implement a higher-performance version of an RV32I RISC-V CPU. Pipelining is a common technique to improve CPU peformance. You may modify your Lab7 design for this lab assignment or design from scratch. The example code from TA is also an alternative starting point. Whichever option is easier for you, you may start with your own option as long as you can pass the testbenches required in each lab.
  
### Pipelining a single-cycle CPU into a 5-staged pipeline
- Pipelining keeps all portions of the processor occupied and increases the amount of useful work the processor can do in a given time. Pipelining typically reduces the processor's cycle time and increases the throughput of instructions.
-  When we pipeline a design, it is important to balance the work partitioned in each pipe stage. In this lab, we follow the conventional 5-staged pipeline in the text book. 
    ![](https://course.playlab.tw/md/uploads/18824482-01fc-4a23-bd16-2685e21834c4.png)
- The five stages include **Instuction Fetch**, **Instuction Decode**, **Execute**, **Memory Access**, and **Write Back**. 

| Sign | Stage             | Hadware (Critical Path) |
|:----:|:----------------- |:----------------------- |
|  IF  | Instuction Fetch  | Instruction Memory      |
|  ID  | Instuction Decode | Register File           |
| EXE  | Execute           | ALU / Branch Comp.      |
| MEM  | Memory Access     | Data Memory             |
|  WB  | Write Back        | Register File           |

- You may refer to the Section 4.6 in [Computer Organization and Design 5th Edition](http://home.ustc.edu.cn/~louwenqi/reference_books_tools/Computer%20Organization%20and%20Design%20RISC-V%20edition.pdf) for more details. 

### CPU Block Diagram with Pipeline Register
![](https://course.playlab.tw/md/uploads/2e5dab57-a0b6-4c0f-b45a-a8b481f5795c.png)
### Pipeline Registers
- Once the datapath is pipelined, an instruction will be carried out in multiple cycles. It means that the datapath design can only complete partial work of an instruction in a single cycle. So we need a storage to store temporary information for an instruction to move from one cycle to the next cycle. We call the storage **stage registers**.
- The pipeline registers are used to store the data passed from one stage to the next stage. In normal cases, the pipeline register passes the instruction data from one stage to another. Sometimes, we might want to stall an instruction or flush an instruction in a particular stage. We need to control the pipeline register update accordingly for each scenario. Here are two examples.
- Example #1 - Pipeline might stall due to long memory accesses 

| Pipeline register Stall signal | IF  | ID  | EXE | MEM | WB  |
| ------------------------------ | --- | --- | --- | --- | --- |
| Stall - Memory Access          | v   | v   | v   | v   |     |

- Example #2 - Need to flush partial pipeline due to branch mispredictions 

| Pipeline register flush signal | IF               | ID  | EXE | MEM | WB  | WBD |
| ------------------------------ | ---------------- | --- | --- | --- | --- | --- |
| Flush - Branch Mispredition    | update to new PC | v   | v   |     |     |     |

### Instruction Implementation Examples

  To demonstrate how to get started, TA has implemented the following instructions in the Lab9-1 example code, including:

- I-Type addi
- Memory request - load
- branch - beq
- lui

In the homework, your assignment is to implement the full set of RV32I instruction set. 

### Top-Level Design

The testbenches are based on the following top-level design module. If you do not use TA's code, please follow the top-level design as much as possible to minimize your efforts to pass the testbenches. 

- PipelinedCPU.scala
```scala=
// PipelinedCPU.scala
/*****  Pipeline Stages Registers Module for holding data *****/
// Pipeline Registers
val stage_IF  = Module(new Reg_IF(memAddrWidth))
val stage_ID  = Module(new Reg_ID(memAddrWidth))
val stage_EXE = Module(new Reg_EXE(memAddrWidth))
val stage_MEM = Module(new Reg_MEM(memAddrWidth))
val stage_WB  = Module(new Reg_WB(memAddrWidth))
// 5 pipe stage datapath modules
val datapath_IF  = Module(new Path_IF(memAddrWidth))
val datapath_ID  = Module(new Path_ID(memAddrWidth))
val datapath_EXE = Module(new Path_EXE(memAddrWidth))
val datapath_MEM = Module(new Path_MEM(memAddrWidth))
val datapath_WB  = Module(new Path_WB(memAddrWidth))
// 1 contorller module
val contorller = Module(new Controller(memAddrWidth))
```

:::warning
**Homework 9-1** 
- Finish the all others rv32i instrunctions，and pass the **rv32ui_SingleTest/TestALL.S**
:::

## Lab 9-2 Hazards

- When we pipeline a CPU design, we expect that multiple datapath modules can execute in parallel to increase the overall throughput. However, we might not be able to achieve the expected throughput due to different types of hazards. **Hazards** that arise in the pipeline prevent the next instruction from executing during its designated clock cycle. There are three types of hazards:
    - **Structural hazards**: Hardware cannot support certain combinations of instructions (two instructions in the pipeline require the same resource).
    - **Data hazards**: Instruction depends on the result of prior instruction still in the pipeline.
    - **Control hazards**: Caused by delay between the fetching of instructions and decisions about changes in the control flow (branches and jumps).

### Structrual Hazard
Structural hazards occur when more than one instruction needs to use the same datapath resource at the same time. There are two main causes of structural hazards:
- **Register File**: The register file is accessed both during ID, when it is read, and during WB, when it is written to. We can solve this by having separate read and write ports. To account for reads and writes to the same register, processors usually write to the register during the first half of the clock cycle, and read from it during in the second half.
- **Memory**: Memory is accessed for both instructions and data. Having a separate instruction memory (abbreviated IMEM) and data memory (abbreviated DMEM) solves this hazard.

![](https://course.playlab.tw/md/uploads/49ab51b8-6f0f-4f5b-b9ec-e3a543b96da4.png)![](https://course.playlab.tw/md/uploads/6a3615c6-60c0-4390-b4f7-fcd952c9dead.png)

In this lab, our example design reads and writes registers at different clock edges. In addition, it has separate memories for instruction fetch and data memory accesses. No structural hazard exists in the lab design. 

### Data Hazard
- Data hazards occur if an instruction reads a register that an older instruction will overwrite in the future cycle. We can tell whether a data hazard exists in a pipeline by inspecting the data dependencies in the source and destination registers. 

- There are three types of data dependencies, but only **Read After Write** is true data dependency. No WAW and WAR hazards exist in a 5-staged pipelined CPU. 
    :::info
    **Data dependency**
    - True dependence: read-after-write (RAW)
    - Anti dependence: write-after-read (WAR)
    - Output dependence: write-after-write (WAW)
    :::

- The following example depicts several RAW hazards. 
```asm=
add s6,s4,s5 
add s2,s5,s6  // RAW : s6
add s3,s6,s4  // RAW : s6
add s2,s2,s3  // RAW : s2, s3
```
- Two common approaches to resolve RAW hazards include
    - Stalling the CPU pipeline - let the older instructions keep  going and the instruction that has dependency stall
    - Data forwarding 
:::success
- **Lab Solution: Stall** - in this lab, we will demonstrate how to use the **basic method - stall** to resolve the data hazard problem. If you are interested in **data forwarding** , you can implement it to get Lab9 **Bonus** points.
:::
- UC Berkeley CS61C Summer 2018 Discussion 6 on stalling pipeline
    ![](https://course.playlab.tw/md/uploads/3150d5e2-144b-4a30-930c-c7ce28ac9b34.png)
- UC Berkeley CS61C Summer 2018 Discussion 6 on data forwarding
    ![](https://course.playlab.tw/md/uploads/91117d80-3935-4bbd-b6df-f086aa6e20ec.png)
- How to implement **Data Hazard Detection** - In a 5-staged pipelined CPU, only RAW hazards exists. The source registers are read in the **ID stage**. If a RAW hazard exist in the pipeline, we might read incorrect data since the destination register is not updated yet. The destination register is written in the **WB stage**. 
    - Possible data hazards scenarios
        1. **Rd of instruction A in the WB stage** is the same as **Rs of instruction B in ID stage**
        2. **Rd of instruction A in MEM stage** is the same as **Rs of instruction B in ID stage**
        3. **Rd of instruction A in EXE stage** is the same as **Rs of instruction B in ID stage** 
    - RegFile usage table
        | Inst type (opcode) | R   | I   | B   | AUPIC | LUI | JAL | JALR | LOAD | STORE |
        | ------------------ | --- | --- | --- | ----- | --- | --- | ---- | ---- | ----- |
        | Use rs1            | v   | v   | v   |       |     |     | v    | v    | v     |
        | Use rs2            | v   |     | v   |       |     |     |      |      | v     |
        | Use rd             | v   | v   |     | v     | v   | v   | v    | v    |       |
#### Stall Implementation for Data Hazards 
- Example - **Rd of instruction A in the WB stage** is the same as **Rs of instrunction B in ID stage**
    - In this case, we need to stall **ID and IF stages** and let the rest of the stages keep moving. The instruction in the **EXE** stage will be invalidated (or so-called insert a bubble) next cycle. In the lab, we create a stall and a flush signal respectively as shown in the following code snippet.
    ```scala=
    // PipelinedCPU.scala
    /* Wire Connect */
    // === IF stage reg (PC reg) ========================================
    stage_IF.io.Stall := (contorller.io.Hcf||contorller.io.Stall_WB_ID_DH)
    ...
    // === ID stage reg========================================
    stage_ID.io.Stall := (contorller.io.Hcf||contorller.io.Stall_WB_ID_DH)
    ...
    // === EXE stage reg========================================
    stage_EXE.io.Flush := (contorller.io.Flush_BH||contorller.io.Flush_WB_ID_DH)
    ```
    - How to create the Stall_DH signal in **Controller.scala** - Line 24~29 shows how the signal is created. It compares the destination register index of the instruction in the WB stage with and source register index of the instruction in the ID stage.  
    ```scala=
    // Controller.scala

      /****************** Data Hazard ******************/
      // Use rs in ID stage 
      val is_D_use_rs1 = Wire(Bool()) 
      val is_D_use_rs2 = Wire(Bool())
      is_D_use_rs1 := MuxLookup(ID_opcode,false.B,Seq(
        BRANCH -> true.B,
      ))   // To Be Modified
      is_D_use_rs2 := MuxLookup(ID_opcode,false.B,Seq(
        BRANCH -> true.B,
      ))   // To Be Modified

      // Use rd in WB stage
      val is_W_use_rd = Wire(Bool())
      is_W_use_rd := MuxLookup(WB_opcode,false.B,Seq(
        OP_IMM -> true.B,
      ))   // To Be Modified

      // Hazard condition (rd, rs overlap)
      val is_D_rs1_W_rd_overlap = Wire(Bool())
      val is_D_rs2_W_rd_overlap = Wire(Bool())

      is_D_rs1_W_rd_overlap := is_D_use_rs1 && is_W_use_rd && (ID_rs1 === WB_rd) && (WB_rd =/= 0.U(5.W))
      is_D_rs2_W_rd_overlap := is_D_use_rs2 && is_W_use_rd && (ID_rs2 === WB_rd) && (WB_rd =/= 0.U(5.W))

      // Control signal - Stall
      // Stall for Data Hazard
      io.Stall_WB_ID_DH := (is_D_rs1_W_rd_overlap || is_D_rs2_W_rd_overlap)
      ...
      // Control signal - Flush
      io.Flush_WB_ID_DH := (is_D_rs1_W_rd_overlap || is_D_rs2_W_rd_overlap)
      // Control signal - Data Forwarding (Bonus)
      /****************** Data Hazard End******************/
    ```
### Control Hazard (Branch Hazard)
- Control Hazard are also called **Branch Hazard**.
- In general, the next value of PC is usually the instruction PC plus 4. However, in a pipelined CPU, if the current branch is taken, the next instruction won't be the subsequent instruction of the current instruction in the memory. It will jump to another instruction whose address is not current PC plus 4. In a five-stage pipelined CPU, the decision of jump or not will be made in the **EXE stage** rather than the **IF stage**. In addition, the jump address will be calculated in the **EXE stage** as well. 
- When a branch is taken, the instruction in the ID and IF stages are in the wrong execution path, so they must be flushed. The solution will be adding two bubbles in the pipeline, and we say that the branch misprediction penalty is 2 cycles.
![](https://course.playlab.tw/md/uploads/69f50907-57cc-45de-9f9f-e8b4a12f898d.png)
![](https://course.playlab.tw/md/uploads/692d71c7-f0ec-4d03-aa46-82c0ec7144e0.png)
- UC Berkeley CS61C Summer 2018 Discussion 6 on Control Hazard
![](https://course.playlab.tw/md/uploads/c8d3a4ca-1e3d-4e1d-8623-44610ef721d5.png)

#### Implementation of Control Hazard Detection
- PC selection logics
    - Signal definition
        - **EXE_target_pc** - Target pc in current EXE stage.
        - **EXE_pc** - PC in the EXE stage.
        - **IF_pc** - PC in the IF stage.
    - In **RiscvDefs.scala** - the pc_sel_map is defined for the PC selection Mux
        ```scala=
        object pc_sel_map {
          val IF_PC_PLUS_4 = 0.U    // IF stage PC + 4
          val EXE_PC_PLUS_4 = 1.U   // EXE stage PC + 4
          val EXE_T_PC = 2.U        // EXE stage Target PC
        }
        ```
    ![](https://course.playlab.tw/md/uploads/2dd97912-98bb-447d-b6ec-32e255ea5b51.png)
    1. For the non-taken-branch case, next_pc is equal to **IF_PC + 4** by default.
    2. For the taken-branch case, next_pc is updated with  **EXE_target_pc**
    ![](https://course.playlab.tw/md/uploads/53e02c72-8dac-4f3f-baf8-35978014ffd3.png)
- Branch Misprediction Handling - By default, we assume that a branch is non-taken in the design. When a taken branch occurs, the default branch prediction is wrong. We call it a branch misprediction. In this case, we need to update the next PC with the target PC of the branch instruction. 
    ```scala=
      // In Controller.scala
      // pc predict miss signal
      val Predict_Miss = Wire(Bool())
      Predict_Miss := (E_En && E_Branch_taken && io.ID_pc=/=io.EXE_target_pc)
      // Control signal - PC
      when(Predict_Miss){
        io.PCSel := EXE_T_PC
      }.otherwise{
        io.PCSel := IF_PC_PLUS_4
      }
    ```
    ```scala=
    // In Path_IF.scala
    package lab9.PiplinedCPU.DatapathModule

    import chisel3._
    import chisel3.util._
    import lab9.PiplinedCPU.pc_sel_map._

    class Path_IF(addrWidth:Int) extends Module {
        val io = IO(new Bundle{
            ...
        })

        // Next PC combinational circuit
        io.next_pc := MuxLookup(io.PCSel, (io.IF_pc_in + 4.U(addrWidth.W)), Seq(
            IF_PC_PLUS_4 -> (io.IF_pc_in + 4.U(addrWidth.W)),
            EXE_PC_PLUS_4 -> (io.EXE_pc_in + 4.U(addrWidth.W)),
            EXE_T_PC -> io.EXE_target_pc_in
        ))
        ...
    }
    ```
- Pipe Stage Flushing Handling - On misprediction, we need to flush the instructions in the ID & EXE stages
    ```scala=
      // In Controller.scala
      // Control signal - Flush
      io.Flush_BH := Predict_Miss
    ```
    ```scala=
      // In PiplinedCPU.scala
      ...
      /* Wire Connect */
      // === IF stage reg (PC reg) ==============================
      ...
      stage_IF.io.next_pc_in := datapath_IF.io.next_pc
      // IF Block Datapath
      datapath_IF.io.PCSel     := contorller.io.PCSel
      datapath_IF.io.IF_pc_in  := stage_IF.io.pc
      datapath_IF.io.EXE_pc_in := stage_EXE.io.pc
      datapath_IF.io.EXE_target_pc_in := datapath_EXE.io.EXE_target_pc_out
      ...
      // === ID stage reg========================================
      stage_ID.io.Flush := contorller.io.Flush_BH
      ...
      // ===EXE stage reg========================================
      stage_EXE.io.Flush := (contorller.io.Flush_BH||contorller.io.Flush_WB_ID_DH)
      ...
    ```
### Pipelined 5-stage CPU Block Diagram
- The following diagram depicts the top-level blocks in TA's five-stage pipelined CPU design.
- The Original file is in `Lab9/Hardware/CPU Block Diagram.pptx`
![](https://course.playlab.tw/md/uploads/d12967a7-3c38-4b6f-b9a6-8aa89c18acc4.png)

:::warning
**Homework 9-2** 
- Finish the all others RV32I instrunctions，and pass the **rv32ui_SingleTest/TestDataHazard.S** test
:::
### Debug Tips - How to inspect CPU infomation when running simulations
- In order to allow the CPU to have more information to be visualized, we can monitor CPU pipeline information from the top-level module.

    :::spoiler PipelinedCPU.scala
    ```scala=
    class PiplinedCPU(memAddrWidth: Int, memDataWidth: Int) extends Module {
        val io = IO(new Bundle{
            //InstMem
            val InstMem = new MemIF_CPU(memAddrWidth) 
            //DataMem
            val DataMem = new MemIF_CPU(memAddrWidth) 
            //System
            val regs = Output(Vec(32,UInt(32.W)))
            val vector_regs = Output(Vec(32,UInt(512.W)))
            val Hcf = Output(Bool())
            // Test
            val E_Branch_taken = Output(Bool())
            val Flush = Output(Bool())
            val Stall_MA = Output(Bool())
            val Stall_DH = Output(Bool())
            val IF_PC = Output(UInt(memAddrWidth.W))
            val ID_PC = Output(UInt(memAddrWidth.W))
            val EXE_PC = Output(UInt(memAddrWidth.W))
            val MEM_PC = Output(UInt(memAddrWidth.W))
            val WB_PC = Output(UInt(memAddrWidth.W))
            val EXE_src1 = Output(UInt(32.W))
            val EXE_src2 = Output(UInt(32.W))
            val ALU_src1 = Output(UInt(32.W))
            val ALU_src2 = Output(UInt(32.W))
            val EXE_alu_out = Output(UInt(32.W))
            val WB_rd = Output(UInt(5.W))
            val WB_wdata = Output(UInt(32.W))
            val EXE_Jump = Output(Bool())
            val EXE_Branch = Output(Bool())
        })
        ...
        /* System */
        io.regs := datapath_ID.io.regs
        io.vector_regs := datapath_ID.io.vector_regs
        io.Hcf := contorller.io.Hcf
        /* Test */
        io.E_Branch_taken := contorller.io.E_Branch_taken
        io.Flush := contorller.io.Flush
        io.Stall_DH := contorller.io.Stall_DH
        io.Stall_MA := contorller.io.Stall_MA
        io.IF_PC := stage_IF.io.pc
        io.ID_PC := stage_ID.io.pc
        io.EXE_PC := stage_EXE.io.pc
        io.MEM_PC := stage_MEM.io.pc
        io.WB_PC := Mux(stage_WB.io.pc_plus4 > 0.U ,stage_WB.io.pc_plus4 - 4.U,stage_WB.io.pc_plus4)
        io.EXE_alu_out := datapath_EXE.io.EXE_alu_out
        io.EXE_src1 := datapath_EXE.io.EXE_src1
        io.EXE_src2 := datapath_EXE.io.EXE_src2
        io.ALU_src1 := datapath_EXE.io.alu_src1
        io.ALU_src2 := datapath_EXE.io.alu_src2
        io.WB_wdata := datapath_WB.io.WB_wdata
        io.WB_rd := stage_WB.io.inst(11,7)
        io.EXE_Jump := (stage_EXE.io.inst(6, 0)===JAL) || (stage_EXE.io.inst(6, 0)===JALR)
        io.EXE_Branch := (stage_EXE.io.inst(6, 0)===BRANCH)
    }
    ```
    :::
    :::spoiler top.scala
    ```scala=
    class top extends Module {
        val io = IO(new Bundle{
            val pc = Output(UInt(15.W))
            val regs = Output(Vec(32,UInt(32.W)))
            val Hcf = Output(Bool())
            //for sure that IM and DM will be synthesized
            val inst = Output(UInt(32.W))
            val rdata = Output(UInt(32.W))
            // Test
            val E_Branch_taken = Output(Bool())
            val Flush = Output(Bool())
            val Stall_MA = Output(Bool())
            val Stall_DH = Output(Bool())
            val IF_PC = Output(UInt(32.W))
            val ID_PC = Output(UInt(32.W))
            val EXE_PC = Output(UInt(32.W))
            val MEM_PC = Output(UInt(32.W))
            val WB_PC = Output(UInt(32.W))
            val EXE_alu_out = Output(UInt(32.W))
            val EXE_src1 = Output(UInt(32.W))
            val EXE_src2 = Output(UInt(32.W))
            val ALU_src1 = Output(UInt(32.W))
            val ALU_src2 = Output(UInt(32.W))
            val raddr = Output(UInt(32.W))
            val WB_rd = Output(UInt(5.W))
            val WB_wdata = Output(UInt(32.W))
            val EXE_Jump = Output(Bool())
            val EXE_Branch = Output(Bool())
        })
        ...
        //System
        io.regs := cpu.io.regs
        io.Hcf := cpu.io.Hcf
        io.inst := im.io.inst
        io.rdata := cpu.io.DataMem.rdata(data_width-1,0)
        // Test
        io.E_Branch_taken := cpu.io.E_Branch_taken
        io.Flush := cpu.io.Flush
        io.Stall_MA := cpu.io.Stall_MA
        io.Stall_DH := cpu.io.Stall_DH
        io.IF_PC := cpu.io.IF_PC
        io.ID_PC := cpu.io.ID_PC
        io.EXE_PC := cpu.io.EXE_PC
        io.MEM_PC := cpu.io.MEM_PC
        io.WB_PC := cpu.io.WB_PC
        io.EXE_alu_out := cpu.io.EXE_alu_out
        io.EXE_src1 := cpu.io.EXE_src1
        io.EXE_src2 := cpu.io.EXE_src2
        io.ALU_src1 := cpu.io.ALU_src1
        io.ALU_src2 := cpu.io.ALU_src2
        io.raddr := cpu.io.DataMem.raddr
        io.WB_rd := cpu.io.WB_rd
        io.WB_wdata := cpu.io.WB_wdata
        io.EXE_Jump := cpu.io.EXE_Jump
        io.EXE_Branch := cpu.io.EXE_Branch
    }
    ```
    :::
- **topTest.scala**
```scala=
class topTest(dut:top) extends PeekPokeTester(dut){
    implicit def bigint2boolean(b:BigInt):Boolean = if (b>0) true else false
    val filename = "./src/main/resource/inst.asm"
    val lines = Source.fromFile(filename).getLines.toList
    ...
    while(!peek(dut.io.Hcf)){
        var PC_IF = peek(dut.io.IF_PC).toInt
        var PC_ID = peek(dut.io.ID_PC).toInt
        var PC_EXE = peek(dut.io.EXE_PC).toInt
        var PC_MEM = peek(dut.io.MEM_PC).toInt
        var PC_WB = peek(dut.io.WB_PC).toInt
        var E_BT = peek(dut.io.E_Branch_taken).toInt
        var Flush = peek(dut.io.Flush).toInt
        var Stall_MA = peek(dut.io.Stall_MA).toInt
        var Stall_DH = peek(dut.io.Stall_DH).toInt
        var alu_out = (peek(dut.io.EXE_alu_out).toInt.toHexString).replace(' ', '0')
        var EXE_src1 = (peek(dut.io.EXE_src1).toInt.toHexString).replace(' ', '0')
        var EXE_src2 = (peek(dut.io.EXE_src2).toInt.toHexString).replace(' ', '0')
        var ALU_src1 = (peek(dut.io.ALU_src1).toInt.toHexString).replace(' ', '0')
        var ALU_src2 = (peek(dut.io.ALU_src2).toInt.toHexString).replace(' ', '0')
        var DM_rdata = (peek(dut.io.rdata).toInt.toHexString).replace(' ', '0')
        var DM_raddr = (peek(dut.io.raddr).toInt.toHexString).replace(' ', '0')
        var WB_reg = peek(dut.io.WB_rd).toInt
        var WB_wdata = (peek(dut.io.WB_wdata).toInt.toHexString).replace(' ', '0')

        var EXE_Jump = peek(dut.io.EXE_Jump).toInt
        var EXE_Branch = peek(dut.io.EXE_Branch).toInt

        println(s"[PC_IF ]${"%8d".format(PC_IF)} [Inst] ${"%-25s".format(lines(PC_IF>>2))} ")
        println(s"[PC_ID ]${"%8d".format(PC_ID)} [Inst] ${"%-25s".format(lines(PC_ID>>2))} ")
        println(s"[PC_EXE]${"%8d".format(PC_EXE)} [Inst] ${"%-25s".format(lines(PC_EXE>>2))} "+ 
                s"[EXE src1]${"%8s".format(EXE_src1)} [EXE src2]${"%8s".format(EXE_src2)} "+
                s"[Br taken] ${"%1d".format(E_BT)} ")
        println(s"                                                  "+ 
                s"[ALU src1]${"%8s".format(ALU_src1)} [ALU src2]${"%8s".format(ALU_src2)} "+
                s"[ALU Out]${"%8s".format(alu_out)}")
        println(s"[PC_MEM]${"%8d".format(PC_MEM)} [Inst] ${"%-25s".format(lines(PC_MEM>>2))} "+
                s"[DM Raddr]${"%8s".format(DM_raddr)} [DM Rdata]${"%8s".format(DM_rdata)}")
        println(s"[PC_WB ]${"%8d".format(PC_WB)} [Inst] ${"%-25s".format(lines(PC_WB>>2))} "+
                s"[ WB reg ]${"%8d".format(WB_reg)} [WB  data]${"%8s".format(WB_wdata)}")
        println(s"[Flush ] ${"%1d".format(Flush)} [Stall_MA ] ${"%1d".format(Stall_MA)} [Stall_DH ] ${"%1d".format(Stall_DH)} ")
        println("==============================================")
        
        step(1)
    }
```

- **Run rv32ui-SingleTest/addi.s**
```bash=
$ bash ./load_test_data.sh -s addi
$ sbt 'Test/runMain acal_lab09.topTest'
Elaborating design...
Done elaborating.
[info] [0.005] SEED 1673842120768
[info] [0.014] [PC_IF ]       0 [Inst] li ra,291
[info] [0.014] [PC_ID ]       0 [Inst] li ra,291
[info] [0.014] [PC_EXE]       0 [Inst] li ra,291                 [EXE src1]       0 [EXE src2]       0 [Br taken] 0
[info] [0.014]                                                   [ALU src1]       0 [ALU src2]       0 [ALU Out]       0
[info] [0.014] [PC_MEM]       0 [Inst] li ra,291                 [DM Raddr]       0 [DM Rdata]       0
[info] [0.015] [PC_WB ]       0 [Inst] li ra,291                 [ WB reg ]       0 [WB  data]       0
[info] [0.015] [Flush ] 0 [Stall_MA ] 0 [Stall_DH ] 0
[info] [0.015] ==============================================
[info] [0.017] [PC_IF ]       4 [Inst] nop
[info] [0.018] [PC_ID ]       0 [Inst] li ra,291
[info] [0.018] [PC_EXE]       0 [Inst] li ra,291                 [EXE src1]       0 [EXE src2]       0 [Br taken] 0
[info] [0.018]                                                   [ALU src1]       0 [ALU src2]       0 [ALU Out]       0
[info] [0.018] [PC_MEM]       0 [Inst] li ra,291                 [DM Raddr]       0 [DM Rdata]       0
[info] [0.018] [PC_WB ]       0 [Inst] li ra,291                 [ WB reg ]       0 [WB  data]       0
[info] [0.018] [Flush ] 0 [Stall_MA ] 0 [Stall_DH ] 0
[info] [0.018] ==============================================
[info] [0.021] [PC_IF ]       8 [Inst] nop
[info] [0.021] [PC_ID ]       4 [Inst] nop
[info] [0.021] [PC_EXE]       0 [Inst] li ra,291                 [EXE src1]       0 [EXE src2]       0 [Br taken] 0
[info] [0.021]                                                   [ALU src1]       0 [ALU src2]     123 [ALU Out]     123
[info] [0.021] [PC_MEM]       0 [Inst] li ra,291                 [DM Raddr]       0 [DM Rdata]       0
[info] [0.022] [PC_WB ]       0 [Inst] li ra,291                 [ WB reg ]       0 [WB  data]       0
[info] [0.022] [Flush ] 0 [Stall_MA ] 0 [Stall_DH ] 0
[info] [0.022] ==============================================
[info] [0.026] [PC_IF ]      12 [Inst] nop
[info] [0.026] [PC_ID ]       8 [Inst] nop
[info] [0.026] [PC_EXE]       4 [Inst] nop                       [EXE src1]       0 [EXE src2]       0 [Br taken] 0
[info] [0.026]                                                   [ALU src1]       0 [ALU src2]       0 [ALU Out]       0
[info] [0.026] [PC_MEM]       0 [Inst] li ra,291                 [DM Raddr]     123 [DM Rdata]       0
[info] [0.026] [PC_WB ]       0 [Inst] li ra,291                 [ WB reg ]       0 [WB  data]       0
[info] [0.027] [Flush ] 0 [Stall_MA ] 0 [Stall_DH ] 0
[info] [0.027] ==============================================
[info] [0.030] [PC_IF ]      16 [Inst] nop
[info] [0.030] [PC_ID ]      12 [Inst] nop
[info] [0.030] [PC_EXE]       8 [Inst] nop                       [EXE src1]       0 [EXE src2]       0 [Br taken] 0
[info] [0.030]                                                   [ALU src1]       0 [ALU src2]       0 [ALU Out]       0
[info] [0.030] [PC_MEM]       4 [Inst] nop                       [DM Raddr]       0 [DM Rdata]       0
[info] [0.030] [PC_WB ]       0 [Inst] li ra,291                 [ WB reg ]       1 [WB  data]     123
[info] [0.030] [Flush ] 0 [Stall_MA ] 0 [Stall_DH ] 0
[info] [0.030] ==============================================
[info] [0.032] [PC_IF ]      20 [Inst] nop
[info] [0.032] [PC_ID ]      16 [Inst] nop
[info] [0.032] [PC_EXE]      12 [Inst] nop                       [EXE src1]       0 [EXE src2]       0 [Br taken] 0
[info] [0.032]                                                   [ALU src1]       0 [ALU src2]       0 [ALU Out]       0
[info] [0.032] [PC_MEM]       8 [Inst] nop                       [DM Raddr]       0 [DM Rdata]       0
[info] [0.032] [PC_WB ]       4 [Inst] nop                       [ WB reg ]       0 [WB  data]       0
[info] [0.032] [Flush ] 0 [Stall_MA ] 0 [Stall_DH ] 0
[info] [0.033] ==============================================
[info] [0.034] Inst:Hcf
[info] [0.035] This is the end of the program!!
[info] [0.035] ==============================================
[info] [0.035] Value in the RegFile
[info] [0.037] reg[00]：00000000 reg[01]：00000123 reg[02]：00000000 reg[03]：00000000 reg[04]：00000000 reg[05]：00000000 reg[06]：00000000 reg[07]：00000000
[info] [0.038] reg[08]：00000000 reg[09]：00000000 reg[10]：00000000 reg[11]：00000000 reg[12]：00000000 reg[13]：00000000 reg[14]：00000000 reg[15]：00000000
[info] [0.039] reg[16]：00000000 reg[17]：00000000 reg[18]：00000000 reg[19]：00000000 reg[20]：00000000 reg[21]：00000000 reg[22]：00000000 reg[23]：00000000
[info] [0.040] reg[24]：00000000 reg[25]：00000000 reg[26]：00000000 reg[27]：00000000 reg[28]：00000000 reg[29]：00000000 reg[30]：00000000 reg[31]：00000000
test top Success: 0 tests passed in 12 cycles in 0.091398 seconds 131.29 Hz
[info] [0.041] RAN 7 CYCLES PASSED

```

- If the infomation is too long that CLI can't trace back, you can export to a text file.
```bash=
$ sbt 'Test/runMain acal_lab09.topTest > ./out.txt
```
- Then open **out.txt** in Text Editor.

## Lab 9-3 Performance counter and performance analysis
- When you are done with your CPU design, you might need to run large test program to verify the following two things, including:
    - Functional Correctness - confirm the design is doing the right thing functionally
    - Performance 

- As the design gets more complicated, each design might show significant difference in performance. It is a common practice to add performance counters in your design to gather statistics for major events. To understand the performance limitation of the design and how to improve the performance, we prepare a list of popular performance counters in the next subsection. In the lab, TA demonstrates how to implement 1~7 and leaves 8~13 for you to implement in the homework. 

### Performance counter
In this section, 
1. **Cycle Count**
    Count cycles passed.
2. **Fetched Instruction Count**
    Count instruction fetched in IF-stage.
3. **Conditional Branch(Bxx) Count**
    Count B-type instructions executed in EXE-stage.
4. **Conditional Branch(Bxx) hit count**
    Count B-type instructions predict hit in EXE-stage.
5. **Unconditional Branch(Jxx) Count**
    Count J-type instructions executed in EXE-stage.
6. **Unconditional Branch(Jxx) hit count**
    Count J-type instructions predict hit in EXE-stage.
7. **Flush Count**
    Count instructions flushed due to prediction miss.
8. **Mem Read Stall Cycle Count**
    Count cycles stalled due to memory read.
9. **Mem Write Stall Cycle Count**
    Count cycles stalled due to memory write.
10. **Mem Read Request Count**
    Count Load-type instruction.
11. **Mem Write Request Count**
    Count Store-type instruction.
12. **Mem Read Bytes Count**
    Count bytes read in Load-type instruction(lw/lh/lb - all 4 bytes are occupied).
13. **Mem Write Bytes Count**
    Count bytes write in Store-type instruction(sw/sh/sb - all 4 bytes are occupied).
14. **Committed Instruction Count**
    Count the instructions finished by the CPU.


Pull some signal and information up to topTest.scala
- topTest.scala
```scala=
package acal_lab09

import scala.io.Source
import chisel3.iotesters.{PeekPokeTester,Driver}
import scala.language.implicitConversions

class topTest(dut:top) extends PeekPokeTester(dut){

    implicit def bigint2boolean(b:BigInt):Boolean = if (b>0) true else false

    val filename = "./src/main/resource/inst.asm"
    val lines = Source.fromFile(filename).getLines.toList

    /* Lab 9_3 performance counter */
    var Cycle_Count = 0
    var Inst_Count = 0
    var Conditional_Branch_Count = 0
    var Unconditional_Branch_Count = 0
    var Conditional_Branch_Hit_Count = 0
    var Unconditional_Branch_Hit_Count = 0
    var Flush_Count = 0
    /* Lab 9_3 performance counter */
    
    while(!peek(dut.io.Hcf)){
        
        ...
        
        /* Lab 9_3 performance counter */
        Cycle_Count += 1 //Cycle
        if(Stall_MA==0 && Stall_DH==0){
            Inst_Count += 1   // Not Stall, read inst

            if(EXE_Branch==1){
                Conditional_Branch_Count += 1
                if(Flush == 0){
                    Conditional_Branch_Hit_Count += 1
                }else{
                    Flush_Count += 1
                }
            }
            if(EXE_Jump==1){
                Unconditional_Branch_Count += 1
                if(Flush == 0){
                    Unconditional_Branch_Hit_Count += 1
                }else{
                    Flush_Count += 1
                }
            }
        }
        /* Lab 9_3 performance counter */
        
        step(1)
    }
    step(1)
    println("Inst:Hcf")
    println("This is the end of the program!!")
    println("==============================================")
    println("Value in the RegFile")
    ...
    /* Lab 9_3 performance counter */
    // Performance Counter
    println("==============================================================")
    println("Performance Counter:")
    println(s"[Cycle Count                    ] ${"%8d".format(Cycle_Count)}")
    println(s"[Inst Count                     ] ${"%8d".format(Inst_Count)}")
    println(s"[Conditional Branch Count       ] ${"%8d".format(Conditional_Branch_Count)}")
    println(s"[Unconditional Branch Count     ] ${"%8d".format(Unconditional_Branch_Count)}")
    println(s"[Conditional Branch Hit Count   ] ${"%8d".format(Conditional_Branch_Hit_Count)}")
    println(s"[Unconditional Branch Hit Count ] ${"%8d".format(Unconditional_Branch_Hit_Count)}")
    println(s"[Flush Count                    ] ${"%8d".format(Flush_Count)}")
    
    ...
    /* Lab 9_3 performance counter */
}
    
```
:::warning
**Homework 9-3.1**
- Add performance counter **8. ~13.** above
:::
### Performance analysis
- Once you gather statistics by adding the performance counters, you may run the simulation, gather statistics, and do post-processing on the statistics data to analyze design performance. We demonstrate how to calculate CPI in the lab. You need to finish 2~4 in the homework section. 
1. **CPI** (Cycles per instruction)
    - Cycle Count/Instruction Count 
2. **Average Mem Read Request Stall Cycle**
    - Mem Read Stall Cycle Count/Mem Read Request Count
3. **Average Mem Write Request Stall Cycle**
    - Mem Write Stall Cycle Count/Mem Write Request Count
4. **Total Bus bandwidth requiement** (Read + Write, data)
    - Mem Read Bytes Count + Mem Write Bytes Count
- topTest.scala
```scala=
    /* Lab 9_3 performance counter */
    // Performance Counter
    ...
    // Performance Analysis
    println("==============================================================")
    println("Performance Analysis:")
    println(s"[CPI                            ] ${"%8f".format(Cycle_Count.toFloat/Inst_Count.toFloat)}")
    println("==============================================================")
    /* Lab 9_3 performance counter */
}
```

:::warning
**Homework 9-3.2** 
- Complete **2.~ 4.** Performance analysis above.
:::

- **Example: Run rv32ui-SingleTest/addi.s**
```bash=
$ bash ./load_test_data.sh -s addi
$ sbt 'Test/runMain acal_lab09.topTest'

...

[info] [0.158] Inst:Hcf
[info] [0.158] This is the end of the program!!
[info] [0.158] ==============================================
[info] [0.158] Value in the RegFile
[info] [0.159] reg[00]：00000000 reg[01]：00000000 reg[02]：00000003 reg[03]：00000003 reg[04]：00000000 reg[05]：00000000 reg[06]：00000003 reg[07]：00000003
[info] [0.160] reg[08]：00000000 reg[09]：00000000 reg[10]：00000000 reg[11]：00000000 reg[12]：00000000 reg[13]：00000000 reg[14]：00000000 reg[15]：00000000
[info] [0.160] reg[16]：00000000 reg[17]：00000000 reg[18]：00000000 reg[19]：00000000 reg[20]：00000000 reg[21]：00000000 reg[22]：00000000 reg[23]：00000000
[info] [0.160] reg[24]：00000000 reg[25]：00000000 reg[26]：00000000 reg[27]：00000000 reg[28]：00000000 reg[29]：00000000 reg[30]：00000000 reg[31]：00000000
[info] [0.160] ==============================================================
[info] [0.161] Performance Counter:
[info] [0.161] [Cycle Count                    ]       57
[info] [0.161] [Inst Count                     ]       55
[info] [0.161] [Conditional Branch Count       ]        4
[info] [0.162] [Unconditional Branch Count     ]        0
[info] [0.162] [Conditional Branch Hit Count   ]        1
[info] [0.162] [Unconditional Branch Hit Count ]        0
[info] [0.162] [Flush Count                    ]        3
[info] [0.162] ==============================================================
[info] [0.162] Performance Analysis:
[info] [0.162] [CPI                            ] 1.036364
[info] [0.162] ==============================================================
test top Success: 0 tests passed in 63 cycles in 0.201233 seconds 313.07 Hz
[info] [0.163] RAN 58 CYCLES PASSED
```


# Homework 9

- You can create a new branch for each subsequent homework tasks for submission. Remember to specify which branch contains your implementation in the submitted document. 

```shell=
## list all the brances available in the repository
$ git branch -a
* master
  remotes/origin/HEAD -> origin/master
  remotes/origin/lab1
  remotes/origin/lab2
  remotes/origin/lab3
  remotes/origin/master

## checkout the remote branch, lab1, and track
$ git checkout --track origin/lab1
Branch 'lab1' set up to track remote branch 'lab1' from 'origin'.
Switched to a new branch 'lab1'

$ git branch -a
* lab1
  master
  remotes/origin/HEAD -> origin/master
  remotes/origin/lab1
  remotes/origin/lab2
  remotes/origin/lab3
  remotes/origin/master

## create a new branch, hw1, based on the `lab1` branch
$ git checkout -b hw1
Switched to a new branch 'hw1'

$ git branch -a
* hw1
  lab1
  master
  remotes/origin/HEAD -> origin/master
  remotes/origin/lab1
  remotes/origin/lab2
  remotes/origin/lab3
```

## Homework 9-1 5-stage pipelined CPU Implementation
---
- Please design a 5-stage pipelined CPU that supports all RV32I instrunctions，and pass all the single-instruction tests in the **rv32ui_SingleTest/TestALL.S** file.
- You can use any single test，and the golden output is provided in the **rv32ui_SingleTest/golden.txt** file

- **rv32ui_SingleTest/TestALL.S** Result
    ![](https://course.playlab.tw/md/uploads/5e583bbb-b6aa-4479-85c7-04c1ab6945dc.png)


Homework 9-2 Data and Control Hazards  
---
- Please resolves all the possible data and control hazards in your design，and pass the **rv32ui_SingleTest/TestDataHazard.S** program. This test program include different types of hazards for you to resolve. 
- In the result of register file，if sp(x2) is **zero**，it means you passed the test，otherwise，the value of sp(x2) is the test case that you did not passed.

:::info 
- Bonus:  forwarding
also use **rv32ui_SingleTest/TestDataHazard.S** to test the Hardware.
:::

Homework 9-3 Performance Counters and Performance Analysis
---

1. Complete **8.~13.** Performance counter listed in the lab.
2. Complete **2.~4.** Performance analysis liasted in the lab.
3. Run the **mergesort.S (no vector instruction)** and post the **Performance count and analysis** result.
4. Explain How pipelined 5-stage CPU improves performance compared to the single-cycle CPU in Lab 7


## Homework 9-4 Bitmanip Extension (Group Assignment)
### pre-work (Gitlab 共同開發Document)
- [gitlab 多人協同工作教學文件 - 以HW7-2 為例 (computing.ncku.edu.tw)](https://course.playlab.tw/md/Qqyg9O2JQYauvm_nNOr8Mw)
### Introduction
- 此項作業和Lab8同樣都需要同學合作擴增小組的CPU指令功能。不同的地方是emulator是以**軟體思維**下去做指令功能的擴充，但此項作業則是要從**硬體設計的面向**開始著手。
- 需要Implement的指令，除了在``execute()``中進行擴充，在此次作業的``translate_to_machine_code()``也需要完成，才能產生mch code好測試CPU。
- 需要完成的指令請參考下方表格。在 HW7-4中各組同學需要合力完成 BitManip Extension中TA所挑選的指令。
### Requirement
- reference : [RISC-V bitmanip Extension](https://github.com/riscv/riscv-bitmanip/releases)
:::warning
- 建議小組在討論分配之前，每一個人能夠理解並討論好...
    - 每條指令的行為
    - 適合歸類的type(會影響Datapath的樣子喔!)
    - ISA (Instruction Set Architecture)
    - 討論好Module的IO port設計
- 每一個需要實作的指令都要做...
    - Emulator執行功能的擴充(部分指令上次沒有擴充...)
    - 產生machine code 的 case的新增，學習如何拼湊回原始指令
    - Chisel hardware support設計
- 在 Lab7 的時候, 同學多半是每人分一些指令, 但是這樣的分工在硬體設計合適嗎？ 如果你覺得合適, 你們還是可以一個人分幾條指令去做, 並且填下面的指令表格, 但是你如果覺得不合適, 你會想怎麼分工呢？比如說每個人寫一部分的chisel component, 有人寫testbench, 有人寫硬體設計, 有人寫emulator產生machine code, 如果你不是用指令來分, 而是用其他方式分工, 你們可以揚棄下面的指令分工表, 把你們的分工方式說明。
:::
- 指令表格，注意最後一欄的owner，請每一組的組長要協調分工認領指令, 把這個表放在你們的group repo code 裡面的README.md 裡：
### HW 9-4-1
- 需要做的有：
    - dump出machine code
    - 完成指令相關硬體設計
    - 兩邊模擬結果相同

- have been implement in Lab4
  | Num | Mnemonic            | Owner |
  | --- | ------------------- | ----- |
  | 1   | clz rd, rs          |       |
  | 2   | ctz rd, rs          |       |
  | 3   | cpop rd, rs         |       |
  | 4   | andn rd, rs1, rs2   |       |
  | 5   | orn rd, rs1, rs2    |       |
  | 6   | xnor rd, rs1, rs2   |       |
  | 7   | min rd, rs1, rs2    |       |
  | 8   | max rd, rs1, rs2    |       |
  | 9   | minu rd, rs1, rs2   |       |
  | 10  | maxu rd, rs1, rs2   |       |
  | 11  | sext.b rd, rs       |       |
  | 12  | sext.h rd, rs       |       |
  | 13  | bset rd, rs1, rs2   |       |
  | 14  | bclr rd, rs1, rs2   |       |
  | 15  | binv rd, rs1, rs2   |       |
  | 16  | bext rd, rs1, rs2   |       | 
  | 17  | bseti rd, rs1, imm  |       |
  | 18  | bclri rd, rs1, imm  |       |
  | 19  | binvi rd, rs1, imm  |       |
  | 20  | bexti rd, rs1, imm  |       |
  | 21  | ror rd, rs1, rs2    |       |
  | 22  | rol rd, rs1, rs2    |       |
  | 23  | rori rd, rs1, imm   |       |
  | 24  | sh1add rd, rs1, rs2 |       |
  | 25  | sh2add rd, rs1, rs2 |       |
  | 26  | sh3add rd, rs1, rs2 |       |
  | 27  | rev8 rd, rs         |       |
  | 28  | zext.h rd, rs       |       |
  | 29  | orc.b  rd, rs       |       |

- 測試檔案（位於 Lab 8 repo）
    - `emulator/example_code/Hw4_inst.asm`
- 答案參考
    ![](https://course.playlab.tw/md/uploads/a58cbf53-4138-45c7-a26a-09c1e4006a8b.png)
    - **學生補充:** x04應該要是0xfffffff1，因為0xfffffff1(-15)和0x00000098(152)比較的話，應該要是0xfffffff1(-15)比較小


## Homework Submission Rule
- **Step 1**
    - 請在自己的 GitLab內建立 `lab09` repo，並將本次 Lab 撰寫的程式碼放入這個repo。另外記得開權限給助教還有老師。
- **Step 2**
    - 請參考 [(校名_學號_姓名) ACAL 2024 Spring Lab 9 HW Submission Template](https://course.playlab.tw/md/6D7Ql140T0SIhEamFsQhGg?view)，建立(複製一份)並自行撰寫 CodiMD 作業說明文件。請勿更動template裡的內容。
    - 關於 gitlab 開權限給助教群組的方式可以參照以下連結
        - [ACAL 2024 Curriculum GitLab 作業繳交方式說明 : Manage Permission](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Manage-Permission)
- **Step 3**
    - When you are done, please submit your homework document link to the Playlab 作業中心, <font style="color:blue"> 清華大學與陽明交通大學的同學請注意選擇對的作業中心鏈結</font>
        - [清華大學Playlab 作業中心](https://nthu-homework.playlab.tw/course?id=2)
        - [陽明交通大學作業繳交中心](https://course.playlab.tw/homework/course?id=2)
    
    
# References
- [SCR1 RISC-V Core](https://github.com/syntacore/scr1)
- [Lecture 08: RISC-V Pipeline Implementation](https://passlab.github.io/CSCE513/notes/lecture08_RISCV_Impl_pipeline.pdf)
- [What is AXI (Youtube)](https://www.youtube.com/watch?v=1zw1HBsjDH8&list=PLaSdxhHqai2_7WZIhCszu5PLSbZURmibN)
- [AXI - Arm Developer](https://developer.arm.com/documentation/102202/0300/AXI-protocol-overview)
- [CS61C Summer 2018 Discussion 6 – Single Cycle Datapath](https://inst.eecs.berkeley.edu/~cs61c/su18/disc/07/Disc7Sol.pdf)

