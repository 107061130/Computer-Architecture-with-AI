(NTHU_111064528張瀚)  ACAL 2024 Spring Lab 9 HW 
===

## Gitlab code link

- Gitlab link - https://course.playlab.tw/git/111064528/lab09/-/tree/main

## Homework 9

### Homework 9-1 5-stage pipelined CPU Implementation
- **rv32ui_SingleTest/TestALL.S** 
![](https://course.playlab.tw/md/uploads/4ca7a433-b1d0-45c5-af23-cd44fa27f923.png)


- **rv32ui_SingleTest/rv32ui_SingleTest-srai.S** 
* golden.txt : srai    x1 = 0xffffff12, x3 = 0xfffffff1
![](https://course.playlab.tw/md/uploads/cb76e0bf-6d9e-4766-97ea-e8600938a139.png)


### Homework 9-2 Data and Controll Hazards  
- List possible data hazard scenarios and describe how to resolve the hazard in your design.
    :::info
    1. **Rd of instruction A in EXE stage is the same as Rs of instruction B in ID stage**  
    2. **Rd of instruction A in MEM stage is the same as Rs of instruction B in ID stage**
    3. **Rd of instruction A in WB stage is the same as Rs of instruction B in ID stage**
    :::


- Stall and Flush Control
    :::info
    1. **Data Hazard : Stall IF, ID stage, Flush EXE stage**
    2. **Control Hazard : Flush ID, EXE stage**
    3. **Data Hazard & Control Hazard: FLUSH ID, EXE stage, don't Stall IF stage**
    :::
- **rv32ui_SingleTest/TestDataHazard.S** 

![](https://course.playlab.tw/md/uploads/c1e4245d-4bfb-4ad4-88d6-d9bbcbc721fc.png)


### Homework 9-3 Performance Counters and Performance Analysis
1. Complete **8.~13.** performance counters listed below.
:::info
### Performance counter - HW
8. Mem Read Stall Cycle Count
Count cycles stalled due to memory read.
9. Mem Write Stall Cycle Count
Count cycles stalled due to memory write.
10. Mem Read Request Count
Count Load-type instruction.
11. Mem Write Request Count
Count Store-type instruction.
12. Mem Read Bytes Count
Count bytes read in Load-type instruction(lw/lh/lb - all 4bytes are occupied).
13. Mem Write Bytes Count
Count bytes write in Store-type instruction(sw/sh/sb - all 4bytes are occupied).
14. Committed Instruction Count
Count the instructions finished by the CPU.
:::

```scala=
var Mem_Inst = peek(dut.io.MEM_Inst).toString(2)
Mem_Inst = (("0" * (32 - Mem_Inst.length)) + Mem_Inst).reverse
var Mem_op = Mem_Inst.substring(0, 7).reverse
var Mem_fun3 = Mem_Inst.substring(12, 15).reverse

// load
if (Mem_op == "0000011") {
    Mem_Read_Request_Count += 1
    var bytes = Mem_fun3 match {
        case "000" => 1
        case "001" => 2
        case "010" => 4
        case _ => 0
    }
    Mem_Read_Bytes_Count += bytes
}

// store
if (Mem_op == "0100011") {
    Mem_Write_Request_Count += 1
    var bytes = Mem_fun3 match {
        case "000" => 1
        case "001" => 2
        case "010" => 4
        case _ => 0
    }
    Mem_Write_Bytes_Count += bytes
}

Committed_Instruction_Count = Inst_Count - 2 * Flush_Count
```

3. Complete **2.~4.** Performance analysis listed below.
:::info
### Performance analysis - HW
2. Average Mem Read Request Stall Cycle
Mem Read Stall Cycle Count/Mem Read Request Count
3. Average Mem Write Request Stall Cycle
Mem Write Stall Cycle Count/Mem Write Request Count
4. Total Bus bandwidth requiement (Read + Write, data)
Mem Read Bytes Count + Mem Write Bytes Count
:::
```scala=
println("Performance Analysis:")
println(s"[CPI                                   ] ${"%8f".format(Cycle_Count.toFloat/Inst_Count.toFloat)}")
println(s"[Average Mem Read Request Stall Cycle  ] ${"%8f".format(Mem_Read_Stall_Count/Mem_Read_Request_Count.toFloat)}")
println(s"[Average Mem Write Request Stall Cycle ] ${"%8f".format(Mem_Write_Stall_Count/Mem_Write_Request_Count.toFloat)}")
println(s"[Total Bus bandwidth requiement        ] ${"%8d".format(Mem_Read_Bytes_Count + Mem_Write_Bytes_Count)}")
```

4. **mergesort.S**
* Performance count and analysis.
![](https://course.playlab.tw/md/uploads/3d1cb1be-f428-4b40-b89e-954b126b3b4e.png)
* correctness
After sorting, load the 1,2,3,26,27th of data for check, the value is -474, -397, -379, 412, , 443 respectively 
![](https://course.playlab.tw/md/uploads/3276c4b6-aebc-479d-b35a-dd86dc9f9142.png)


5. Explain How a 5-stage pipelined  CPU improves performance compared to a single-cycle CPU

    In single cycle, each stage needs a cycle so the CPI is 5. However, by using pipeline structure, we can reduce CPI to 1.9 as shown in the figure above.


## HW 9-4 Bitmanip Extension (Group Assignment)
### Gitlab code link
- Gitlab link of your branch - 
https://course.playlab.tw/git/funfish111065531/lab09-group/-/tree/111064528?ref_type=heads
- Gitlab link of your group project repo - 
https://course.playlab.tw/git/funfish111065531/lab09-group/-/tree/main?ref_type=heads

### 硬體架構圖：
- 小組選擇的base CPU架構圖，是誰的呢?
我的
![](https://course.playlab.tw/md/uploads/1f370016-3e59-4066-ade2-d1b156295aa8.png)

- Option 2 - 有其他分工方式的組別
![](https://course.playlab.tw/md/uploads/ee60aa33-a958-439f-b352-90bc430fb0b3.png)

    :::success



    ### Emulator functionality
    ```cpp=
    case BEXT:
        // single bit extracted from rs1 at the index specified in rs2.
        index = rf[i.a3.reg] & 31;
        rf[i.a1.reg] = (rf[i.a2.reg] >> index) & 1;
        break;

    case BSETI:
        rf[i.a1.reg] = rf[i.a2.reg] | (1 << i.a3.imm);
        break;
        // case BSETI: rf[i.a1.reg] = rf[i.a2.reg] + i.a3.imm; break;
                
   case BCLRI:
        // rs1 with a single bit cleared at the index of immediate value
        rf[i.a1.reg] = rf[i.a2.reg] & ~(1 << i.a3.imm);
        break;
        
    case BINVI:
        // rs1 with a single bit inverted at the index of immediate value
        rf[i.a1.reg] = rf[i.a2.reg] ^ (1 << i.a3.imm);
        break;

    case BEXTI:
        // single bit extracted from rs1 at the index of immediate value
        rf[i.a1.reg] = (rf[i.a2.reg] >> i.a3.imm) & 1;
        break;
    
    case ROR:
        // get least 5 bit of rs2
        shamt = rf[i.a3.reg] & mask;
        rf[i.a1.reg] = (rf[i.a2.reg] >> shamt) | (rf[i.a2.reg] << (32 - shamt));
        break;
    
    case ROL:
        // get least 5 bit of rs2
        shamt = rf[i.a3.reg] & mask;
        rf[i.a1.reg] = (rf[i.a2.reg] << shamt) | (rf[i.a2.reg] >> (32 - shamt));
        break;
    
    case RORI:
        // rs1 rotate right with immediate value
        rf[i.a1.reg] = (rf[i.a2.reg] >> i.a3.imm) | (rf[i.a2.reg] << (32 - i.a3.imm));
        break;
    
    case SH1ADD:
        rf[i.a1.reg] = rf[i.a3.reg] + (rf[i.a2.reg] << 1);
        break;

    case SH2ADD:
        rf[i.a1.reg] = rf[i.a3.reg] + (rf[i.a2.reg] << 2);
        break;

    case SH3ADD:
        rf[i.a1.reg] = rf[i.a3.reg] + (rf[i.a2.reg] << 3);
        break;
        
    case REV8:
        rev8_partial = rf[i.a2.reg];
        for (int i = 0; i < 4; i++)
        {
            rev8_output |= (rev8_partial & 0xFF) << ((3 - i) * 8);
            rev8_partial >>= 8;
        }
        rf[i.a1.reg] = rev8_output;
        break;

    case ZEXTH:
        rf[i.a1.reg] = (rf[i.a2.reg] << 16) >> 16;
        break;

    case ORC_B:
        // mask
        for (int byte = 0; byte < 4; byte++)
        {
            orc_mask = 0;
            for (int i = 0; i < 4; i++)
            {
                if (i == byte)
                {
                    orc_mask |= 0xFF << (i * 8);
                }
                else
                {
                    orc_mask &= ~(0xFF << (i * 8));
                }
            }
            // partial_intput
            partial_intput = rf[i.a2.reg] & orc_mask;
            if (partial_intput == 0)
            {
                orc_output = orc_output | 0x00 << (byte * 8);
            }
            else
            {
                orc_output = orc_output | 0xFF << (byte * 8);
            }
        }
        rf[i.a1.reg] = orc_output;
        break;
    ```

    ### Assembler translation
    ```cpp=

    // 16 ~ 29
		// ***********************************************************
    case BEXT:
        binary = 0b0110011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b101 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.reg << 20;  // rs2
        binary += 0b0100100 << 25; // funct7
        break;
    case BSETI:
        binary = 0b0010011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b001 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.imm << 20;  // rs2
        binary += 0b0010100 << 25; // funct7
        break;
    case BCLRI:
        binary = 0b0010011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b001 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.imm << 20;  // rs2
        binary += 0b0100100 << 25; // funct7
        break;
    case BINVI:
        binary = 0b0010011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b000 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.imm << 20;  // rs2
        binary += 0b0110100 << 25; // funct7
        break;
    case BEXTI:
        binary = 0b0010011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b101 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.imm << 20;  // rs2
        binary += 0b0100100 << 25; // funct7
        break;
    case ROR:
        binary = 0b0110011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b101 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.reg << 20;  // rs2
        binary += 0b0110000 << 25; // funct7
        break;
    case ROL:
        binary = 0b0110011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b001 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.reg << 20;  // rs2
        binary += 0b0110000 << 25; // funct7
        break;
    case RORI:
        binary = 0b0010011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b101 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.imm << 20;  // rs2
        binary += 0b0110000 << 25; // funct7
        break;
    case SH1ADD:
        binary = 0b0110011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b010 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.reg << 20;  // rs2
        binary += 0b0010000 << 25; // funct7
        break;
    case SH2ADD:
        binary = 0b0110011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b100 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.reg << 20;  // rs2
        binary += 0b0010000 << 25; // funct7
        break;
    case SH3ADD:
        binary = 0b0110011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b110 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += i.a3.reg << 20;  // rs2
        binary += 0b0010000 << 25; // funct7
        break;
    case REV8:
        binary = 0b0010011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b101 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += 0b11000 << 20;   // rs2
        binary += 0b0110100 << 25; // funct7
        break;
    case ZEXTH:
        binary = 0b0110011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b100 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += 0b00000 << 20;   // rs2
        binary += 0b0000100 << 25; // funct7
        break;
    case ORC_B:
        binary = 0b0010011;		   // opcode
        binary += i.a1.reg << 7;   // rd
        binary += 0b101 << 12;	   // funct3
        binary += i.a2.reg << 15;  // rs1
        binary += 0b00111 << 20;   // rs2
        binary += 0b0010100 << 25; // funct7

    // ***********************************************************




    ```

## 測試結果
- 測試檔案
    - ``Lab08/Emulator/example_code/Hw4_inst.asm``
- 測試結果
* Emulator
![](https://course.playlab.tw/md/uploads/a9506127-ea02-42d7-a3b8-137a3a4bdc37.png)

* Hardware
![](https://course.playlab.tw/md/uploads/d455ac38-58f7-4056-a12f-d2aebe27baa4.png)


## 小組最後完成CPU架構圖
![](https://course.playlab.tw/md/uploads/1f370016-3e59-4066-ade2-d1b156295aa8.png)
