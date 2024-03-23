---
title: ACAL 2024 Curriculum Lab 4 - Introduction to Chisel Programming - DataPath Design
robots: noindex, nofollow
---

# <center>ACAL 2024 Curriculum Lab 4 <br /><font color="＃1560bd">Introduction to Chisel Programming<br />DataPath Design</font></center>

###### tags: `AIAS Spring 2024`

[toc]

## Chisel Tutorial
- [ACAL 2024 Curriculum Lab 4-0 Chisel Environment Setup and Template Tutorial](https://course.playlab.tw/md/iV5YvgW6RJiZ3LUxGSdUsw)
這份文件詳述了Chisel 的內容，請大家先看完這份文件，完成工作環境的建置跟測試。

## Introduction
- 在課堂上，介紹了...
    - 邏輯(AND、OR、XOR...)
    - 布林表示式以及如何簡化
    - Combinational Circuit以及Sequential Circuit
- 那在本次Lab中你會學習到：
    - 一些經典的Combinational logics 的練習，體會每個運算元背後的電路原理。
    - chisel的撰寫方式與技巧。
        - Hierarchical Implementation
        - Hardware Generator
    - 使用上周提供的Blocks，加上pc以及decoder，實現能支援單一指令的 datapath 設計。

## Combinational Circuit
- 只由邏輯閘組合而成的電路。
- 沒有記憶功能，不會也不應該產生任何的Flip Flop
- 設計思維(以Half-Adder為例)：
    ![](/uploads/upload_0aac07b45185ae2fd8acf663d585733a.png)
    - Truth Table
        - 將電路所需的I/O ports列舉出來，並根據所有的Input ports的組合，寫出期望的Output值。整理成上方右邊那張表格。
    - Boolean Algebra and Simplification
        - $Sum = A'B + AB' = A\ xor\ B$
        - $Carry = AB$
    -  Gate-Level Circuit for programming
        - 將表示式繪製成電路圖，就會得到上方左邊的電路圖
    - chisel Example：
    ```scala=
    class HalfAdder extends Module{
      val io = IO(new Bundle{
        val A = Input(UInt(1.W))
        val B = Input(UInt(1.W))
        val Sum = Output(UInt(1.W))
        val Carry = Output(UInt(1.W))
      })
      //the behavior of circuit
      io.Sum := io.A ^ io.B
      io.Carry := io.A & io.B
    }
    ```
Lab 4
===
Lab4-0 : Environment and Repo Setup
---
- Build Course docker and bring up a docker container
    - 在開始lab之前必須先將課堂的docker container run起來，並把一些環境建好，可以參考下面的tutorial : [Lab 0 - Course Environment Setup](https://course.playlab.tw/md/33cXunaGSdmYFej1DJNIqQ)

:::warning
- You may setup passwordless ssh login if you like. Please refer to [Use SSH keys to communicate with GitLab](https://docs.gitlab.com/ee/user/ssh.html)
- Also, if you would like to setup the SSH Key in our Container. Please refer to this [document](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Set-up-the-SSH-Key) to set up the SSH Key in acal-curriculum workspace.
:::

```shell=
## bring up the ACAL docker container 
## clone the lab04 files
$  cd ~/projects
$  git clone ssh://git@course.playlab.tw:30022/acal-curriculum/lab04.git
$  cd lab04

## show the remote repositories 
$  git remote -v
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab04.git (fetch)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab04.git (push)

## add your private upstream repositories
## make sure you have create project repo under your gitlab account
$  git remote add gitlab ssh://git@course.playlab.tw:30022/<your ldap name>/lab04.git

$  git remote -v
gitlab	ssh://git@course.playlab.tw:30022/<your ldap name>/lab04.git (fetch)
gitlab	ssh://git@course.playlab.tw:30022/<your ldap name>/lab04.git (push)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab04.git (fetch)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab04.git (push)
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

Lab4-1 : Full Adder
---
### Introduction
- 電路圖&Truth Table
![](https://course.playlab.tw/md/uploads/160c0f8a-5f16-4a1f-af63-de75552a583f.png =40%x) ![](https://course.playlab.tw/md/uploads/6111fb11-8550-4469-8bc9-d14c9fbfc131.png =35%x)

- 設計思維：
    - HalfAdder考慮的輸入只有一個位的bit相加。
    - FullAdder做bit相加時，考慮了前一位進位(Cin)，並將這一位的進位(Cout)傳給下一位元。 
    - Reference：*chisel-tutorial/src/examples/FullAdder.scala*
        - 在chisel-tutorial提供的範例中，sum和cout是直接藉由Boolean Algebra推導寫出。
        ```scala=
        // Generate the sum
        val a_xor_b = io.a ^ io.b
        io.sum := a_xor_b ^ io.cin
        // Generate the carry
        val a_and_b = io.a & io.b
        val b_and_cin = io.b & io.cin
        val a_and_cin = io.a & io.cin
        io.cout := a_and_b | b_and_cin | a_and_cin
        ```
### Module Hierarchy
- 說明：大型的電路模組可以由小塊的模組組合而成。
- 現在我們試著利用剛剛寫出的Half_Adder組合出Full_Adder。
:::info
- 引用的方式：
    - 欲使用的module如果在**同個package**的話，直接宣告即可。
    - 若不是，則必須引入該module所在的package。
    ```scala=
    import {package}._ //在不同package時才需要。
    ...
    val ha = Module(new HalfAdder()) //Module的引入方式。 
    ...
    ```
:::
- Lab-4.1 Code
    ```scala=
    class FullAdder extends Module{
      val io = IO(new Bundle{
        val A = Input(UInt(1.W))
        val B = Input(UInt(1.W))
        val Cin = Input(UInt(1.W))
        val Sum = Output(UInt(1.W))
        val Cout = Output(UInt(1.W))
      })

      //Module Declaration
      val ha1 = Module(new HalfAdder())
      val ha2 = Module(new HalfAdder())

      //Wiring
      ha1.io.A := io.A
      ha1.io.B := io.B

      ha2.io.A := ha1.io.Sum
      ha2.io.B := io.Cin

      io.Sum := ha2.io.Sum
      io.Cout := ha1.io.Carry | ha2.io.Carry
    }
    ```
- Tester Code
    - 撰寫思維：將所有可能的input pairs都做為測試對象，將預期輸出透過expect()或者assert()來比對。
    ```scala=
    class FullAdderTest (fa : FullAdder) extends PeekPokeTester(fa){
      for(a <- 0 until 2){
        for(b <- 0 until 2){
          for(c <- 0 until 2){
            poke(fa.io.A,a)
            poke(fa.io.B,b)
            poke(fa.io.Cin,c)

            var x = c & (a^b)
            var y = a & b

            expect(fa.io.Sum,(a^b^c))
            expect(fa.io.Cout,(x|y))
            step(1)
          }
        }
      }
      println("FullAdder test completed!!!")
    }
    ```
    - 接下來一樣準備入口函式。
    ```scala=
    object FullAdderTest extends App{
      Driver.execute(Array("-td","./generated","-tbn","verilator"),() => new FullAdder()){
        c => new FullAdderTest(c)
      }
    }
    ```
    - 在shell內下指令
    ```shell=
    ## in sbt shell
    $ sbt 'Test/runMain acal_lab04.Lab.FullAdderTest'
    ```
:::warning
- Debugging
    - 重點不是sucess，而是要看倒數第二行是PASSED還是FAILED喔!!!
        - by PeekPokeTester
            - 正確
            ![](https://course.playlab.tw/md/uploads/ff175a4d-427e-4b39-9b53-6da0f958a7be.png)
            - 錯誤
            ![](https://course.playlab.tw/md/uploads/57a4e685-44d6-4092-8bc6-3a76dbad1cce.png)
        - by VCD file
            ![](https://course.playlab.tw/md/uploads/43cbdd09-4b91-45d1-aac4-7c1689985e93.png)
:::
## Lab4-2 : 32-bits Ripple Carry Adder
### Introduction
- 設計思維：利用32個Full-Adder串接，實驗32bits的加法器。
- Example：4-bit RCAdder
    - 圖片擷取自：[Analysis of Basic Adder with Parallel Prefix Adder : Fig.1](10.1109/ICMICA48462.2020.9242842) ![](https://course.playlab.tw/md/uploads/e44fcb03-3278-4045-92a5-47c18c4b78a4.png)

### Hardware Generator
- 只利用Module Hierarchy去完成，會出現以下的問題。
    - 重複出現的電路，接線接得很煩。
    - 規格一變，努力白費。
- 使用時機
    - 電路重複性高、規格不確定時，可以等到synthesis的時候再藉由傳入參數來決定就好。

- Lab4-2 Code
    ```scala=
    class RCAdder (n:Int) extends Module{
      val io = IO(new Bundle{
          val Cin = Input(UInt(1.W))
          val In1 = Input(UInt(n.W))
          val In2 = Input(UInt(n.W))
          val Sum = Output(UInt(n.W))
          val Cout = Output(UInt(1.W))
      })

      //FullAdder ports: A B Cin Sum Cout
      val FA_Array = Array.fill(n)(Module(new FullAdder()).io)
      val carry = Wire(Vec(n+1, UInt(1.W)))
      val sum   = Wire(Vec(n, Bool()))

      carry(0) := io.Cin

      for (i <- 0 until n) {
        FA_Array(i).A := io.In1(i)
        FA_Array(i).B := io.In2(i)
        FA_Array(i).Cin := carry(i)
        carry(i+1) := FA_Array(i).Cout
        sum(i) := FA_Array(i).Sum
      }

      io.Sum := sum.asUInt
      io.Cout := carry(n)
    }
    ```
    :::warning
    Q：**Line 25**...為什麼不用 io.Sum(i)逐一bit進行賦值就好？
    A：Chisel3 does not support **subword assignment**. The reason for this is that subword assignment generally hints at a better abstraction with an aggregate/structured types, i.e., a Bundle or a Vec.     
    ::: 
    - Instruction
        ```shell=
        $ bash build.sh acal_lab04.Lab RCAdder 32
        ## 32為傳入class的參數，意思為指定生成32bits的漣波加法器。
        ```
- Tester code
  ```scala=
  class RCAdderTest (dut:RCAdder) extends PeekPokeTester(dut){
    //另類的作法，只針對某些case去進行測試。
    val in1 = Array(5,32,1,77,34,55,12)
    val in2 = Array(3456,89489,78,5216,4744,8,321)

    //in1.zip(in2).foreach{
    (in1 zip in2).foreach{
      case(i,j)=>
         poke(dut.io.In1,i)
         poke(dut.io.In2,j)
         expect(dut.io.Sum,i+j)
    }
    println("RCAdder test completed!!!!!")
  }
  ```
    - 入口函式 
    ```scala=
    object RCAdderTest extends App{
        Driver.execute(args,()=>new RCAdder(32)){
            c => new RCAdderTest(c)
        }
    }    
    ```
    - Instruction
    ```shell=
    $ sbt 'Test/runMain acal_lab04.Lab.RCAdderTest -tbn verilator -td ./generated'
    ```
## Lab4-3 : Carry Lookahead Adder
### Introduction
- 設計思維：
    - 倚賴RCAdder的缺點就是，從**輸入資料進來**到**答案產生**最後一個FAdder的Cout所花的時間會是最長且和bit數(n)相關。
        - 如果一個FAdder的Cout產生的花費時間是t，那串接後的最後一個Cout會需要n*t時間。
        :::info
        我們會稱呼花費時間最長的路徑叫做 **Critical Path**
        :::
    - 透過提前分析input資料，我們可以提前算出每一位bit的Cin，加快運行速度->讓每一bit的加法能夠同時進行，而不用等前一位的Cout。
    - **Tradeoff**:越後面的Carry它的展開式會越複雜，等於是**用空間去換取時間**的一種做法，不建議做太多bit，你也不會想一直展開。
- Example：4-bit CLAdder
    - 圖片來源：[Gate Vidyalay](https://www.gatevidyalay.com/carry-look-ahead-adder/logic-diagram-of-carry-look-ahead-adder-1/) 
    ![](https://course.playlab.tw/md/uploads/9e27737a-aaf1-4e1d-8b17-5bca6f07ec1d.png  =75%x)

:::success
[**公式推導**]
$$
    Cout=(A．B)+(A．C_{in})+(B．C_{in}) \\
    = (A．B)+(A+B)．C_{in} \\
    = G + P．C_{in} \\
    ------------------- \\
    G_{i}:Generate=A_{i}．B_{i} \\
    P_{i}:Propagate=A_{i}+B_{i} \\
    C_{i+1} = G_{i}+P_{i}．C_{i}
$$
:::
- Lab4-3 Code
    ```scala=
    class CLAdder extends Module{
      val io = IO(new Bundle{
          val in1 = Input(UInt(4.W))
          val in2 = Input(UInt(4.W))
          val Cin = Input(UInt(1.W))
          val Sum = Output(UInt(4.W))
          val Cout = Output(UInt(1.W))
      })

      val P = Wire(Vec(4,UInt()))
      val G = Wire(Vec(4,UInt()))
      val C = Wire(Vec(4,UInt())) 
      val S = Wire(Vec(4,UInt()))

      for(i <- 0 until 4){
          G(i) := io.in1(i) & io.in2(i)
          P(i) := io.in1(i) | io.in2(i)
      }

      C(0) := io.Cin
      C(1) := G(0)|(P(0)&C(0))
      C(2) := G(1)|(P(1)&G(0))|(P(1)&P(0)&C(0))
      C(3) := G(2)|(P(2)&G(1))|(P(2)&P(1)&G(0))|(P(2)&P(1)&P(0)&C(0))

      val FA_Array = Array.fill(4)(Module(new FullAdder).io)

      for(i <- 0 until 4){
          FA_Array(i).A := io.in1(i)
          FA_Array(i).B := io.in2(i)
          FA_Array(i).Cin := C(i)
          S(i) := FA_Array(i).Sum
      }

      io.Sum := S.asUInt
      io.Cout := FA_Array(3).Cout
    }
    ```
- Tester code
    ```scala=
    class CLAdderTest (dut:CLAdder) extends PeekPokeTester(dut){
        for(i <- 0 to  15){
            for(j <- 0 to 15){
                poke(dut.io.in1,i)
                poke(dut.io.in2,j)
                if(peek(dut.io.Cout)*16+peek(dut.io.Sum)!=(i+j)){
                    println("Oh No!!")
                }
            }
        }
        println("CLAdder test completed!!!")
    }
    ```
    - 入口函式
    ```scala=
    object CLAdderTest extends App{
        Driver.execute(args,()=>new CLAdder){
            c => new CLAdderTest(c)
        }
    }
    ```
    - Instruction
    ```shell=
    $ sbt 'Test/runMain acal_lab04.Lab.CLAdderTest -tbn verilator -td ./generated'
    ```
: : : warning
**Chisel 會把未使用的電路直接忽略 synthesis**
因此不管是在dump出的verilog檔，還是波形圖中都不會找到該訊號。範例中的C訊號在合成時，就被省略掉了。
以下為範例：

**halfAdder.scala**
```scala=
    class HalfAdder extends Module{
      val io = IO(new Bundle{
        val A = Input(UInt(1.W))
        val B = Input(UInt(1.W))
        val Sum = Output(UInt(1.W))
        val Carry = Output(UInt(1.W))
      })
      val c = WireDefault(io.A | io.B)
      //the behavior of circuit
      io.Sum := io.A ^ io.B
      io.Carry := io.A & io.B
    }
```
**HalfAdder.v**
![](https://course.playlab.tw/md/uploads/dba4a78e-19c7-4a20-b9c3-5c5446114360.png)

**因此如果需要該訊號，希望在波形圖中可以看到，可以把該訊號接到module的output port。如果是多個module包在Top裡，可以把在module中需要觀察，但被忽略未合成出來的訊號接到Top的output port。**
範例如下：

**halfAdder.Scala**
```scala=
class HalfAdder extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val C = Output(UInt(1.W))
    val Sum = Output(UInt(1.W))
    val Carry = Output(UInt(1.W))
  })
  val c = WireDefault(io.A | io.B)
  // the behavior of circuit
  io.C := c
  io.Sum := io.A ^ io.B
  io.Carry := io.A & io.B
}
```
**halfAdder.v**
![](https://course.playlab.tw/md/uploads/dd18df01-d805-4844-9372-8d854ee97663.png)
:::
Homework 4
===
- **Hw4-1~3**
    - 撇除複雜度較高的除法，透過加、減、乘來了解電路設計的思維相對容易。
        - 加減法器和乘法器都可以用**組合邏輯**實現。
    - 熟練組合邏輯電路的撰寫。
- **Hw4-4**
    - 利用上面的那些Blocks，加上Decoder、PC實現CPU的Datapath。
- 學生補充
    - 如果真的對於一些語法不熟悉，可以去[Chisel的官網文件](https://www.chisel-lang.org/docs/explanations/operators)找找看，例如說類似verilog的bitwise operation、bitwise assignment等等都可以在裡面找到

HW4-1 Mix Adder
---
### Introduction
- 設計思維
    - RCAdder的Critical Path會很長；CLAdder則是會相對地占用面積(位數越高越複雜)。
    - 融合這兩種電路的特色，取得在**面積和時間上的平衡點**。
### Function Declaration
- 利用Lab使用的兩種Adder(Ripple Carry and Carry Look Ahead)，組合出一個由8個4-bit CLAdder組成的32-bit Adder。
    - Hint：用Hardware Generator的方式宣告ClAdder_Array，觀念在Lab4-2的RCAdder。
- port
    ```scala=
    // n為CLAdder個數
    class MixAdder (n:Int) extends Module{
      val io = IO(new Bundle{
          val Cin = Input(UInt(1.W))
          val In1 = Input(UInt((4*n).W))
          val In2 = Input(UInt((4*n).W))
          val Sum = Output(UInt((4*n).W))
          val Cout = Output(UInt(1.W))
      })
      //Implement Here
    }
    ```
    ```shell=
    $ sbt 'Test/runMain acal_lab04.Hw1.MixAdderTest -tbn verilator -td ./generated'
    ```

Hw4-2 Add-Suber
---
### Introduction
- 設計思維
    - 減法可以藉由提前將**加數**做二補數的轉換，讓減法一樣用加法器實現。
### Function Declaration
- 利用Lab完成的Full Adder組合出4-bits加減法器
    - overflow detector
    - 2's complement
- port
    ```scala=
    class Add_Suber extends Module{
        val io = IO(new Bundle{
            val in_1 = Input(UInt(4.W))
            val in_2 = Input(UInt(4.W))
            val op = Input(Bool()) // 0:ADD 1:SUB
            val out = Output(UInt(4.W))
            val o_f = Output(Bool())
        })
    }
    //Implement Here
    ```
    - op：operation，決定該做加法(0)還是減法(1)
    - o_f：overflow，當**正確答案**超過了有號數加法器所能表示的範圍(-8~7)時，為**high**，其餘時間為**low**
    - out：宣告為UInt，目的是為了讓同學練習不以SInt的方式實現此作業，也可以另外去參考tester裡如何將4-bits的值sign-extend成負數的方式。
- [Learning Source](https://www.youtube.com/watch?v=IAkhdYtNjb0)
```shell=
$ sbt 'Test/runMain acal_lab04.Hw2.Add_SuberTest -tbn verilator -td ./generated'
```

Hw4-3 Booth Multiplier
---
### Introduction
- 是計算機中一種利用數的2補數形式來計算乘法的技巧。
- 算法原理：
    - 考慮一個二進位乘數：$m=2'b01111100$
    - 要將其轉換成十進位，直覺來說，理應是每一位的值呈上該位的權重然後相加也就是$$2^6+2^5+2^4+2^3+2^2=124$$，以此例子而言，被乘數和乘數勢必會產生5(m裡面1的數量)個部份積(*partial sum, pp*)等著之後做位移和相加。此時被乘數和乘數有可能產生的部分積不是0就是被乘數自己。
    - 事實上，在二進制表示法之中，連續出現的1，轉而用**頭尾位數各外擴一個位數的減法替代**，是能夠減少部分積的數量進而加快電路運行的速度，以上面的m舉例而言，連續的一出現在(6到3)同樣也能表示成第7位和第2位的相減(外擴，$2'b10000-2'b00001=2'b01111$)。把需要減掉的位數改用-1表示->$2'b10000(-1)00$，一樣我們試著轉為十進制來驗算，$2^7-2^2=128-4=124$，部分積的項數由5項降低至2項，而部分積則由可能是被乘數、0、或者-1倍的自己。而這種技巧我們稱為**Radix-2**。
        - 某些位數以負數表示=>代表部分積也是會有產生負數的可能性。
        - 在Radix-4中，部分積有可能為被乘數乘以(-2)...
            - 實現方式:轉成二補數後往左shift一位。
- 證明與進階推導：
    - ![](https://course.playlab.tw/md/uploads/d69fee54-ea32-4fc2-8f01-06cf6385bbf8.png =80%x)

    - 以bit-wise的方式分析，每一個二補數都能表示成上方的第一行，$y_{-1}$設為0，只是方便演算法進行分析，和前面提到的**外擴**有一些關係。
        - 題外話：Hw4-2的tester的_signed function，就是利用這種方式將bit vector轉譯成signed integer的形式喔!!!
    - 第一個等號為Radix-2，用大小為2的sliding window去掃描連續的1出現的狀況，可能的狀況有00、01、10、11。假設乘數都沒有連續兩個0或1的情況發生，那我的部分積最多還是需要n項，Ex: $2'b010101$
       | 狀況 | 意義        | 輸出 |
       | ---- | ----------- | ---- |
       | 00   | 連續的0中間 | 0    |
       | 01   | 連續的1開始 | 1    |
       | 10   | 連續的1結束 | -1   |
       | 11   | 連續的1中間 | 0    |

    - 第二個等號為Radix-4，為更進階的分析方法，一次看三個bit來進行分析，用大小為3的sliding window，stride=2去掃描，可能的狀況提升到了8種。而部分積的可能性也提升到5種(-2~2)，但這時部分積的項數最多也會降低到$n/2$項。每一位的權重由高至低(-2,1,1)
       | 狀況 | 輸出 |
       | ---- | ---- |
       | 000  | 0    |
       | 001  | 1    |
       | 010  | 1    |
       | 011  | 2    |
       | 100  | -2   |
       | 101  | -1   |
       | 110  | -1   |
       | 111  | 0    | 
- Hint
    - 首先，應該要先將上方推導證明的式子中，依照括號將乘數分成x項。(想一想，x應該會是多少呢?)
    - x項中，最多存在幾種可能性呢?，根據這些可能性，我們需要對被乘數做什麼呢?
    - 得到部分積後，乘上權重做相加即可得到答案!!
    - <font color=#f00>切記!!!不需要宣告任何暫存器喔!!</font>
### Function Declaration
- 請同學，完成一16-bits(width = 16)的Radix-4 Booth 乘法器
- Hardware Generator的寫法，完成後，任意bit數的乘法器都能實現，但這裡width就先暫定是16。
    - port declaration
    ```scala=
    class Booth_MUL(width:Int) extends Module {
        val io = IO(new Bundle{
        val in1 = Input(UInt(width.W))      //multiplicand
        val in2 = Input(UInt(width.W))      //multiplier
        val out = Output(UInt((2*width).W)) //product
    })
    //Implement Here
    ```
```shell=
$ sbt 'Test/runMain acal_lab04.Hw3.Booth_MulTest -tbn verilator -td ./generated'
```  

## Homework Submission Rule
- **Step 1**
    請在自己的 GitLab內建立 `lab04` repo，並將本次 Lab 撰寫的程式碼放入這個repo。另外記得開權限給助教還有老師。
- **Step 2**
    請參考[(校名_學號_姓名) ACAL 2024 Spring Lab 4 HW Submission Template](https://course.playlab.tw/md/CSbf7XBAQbqiP7kr9MqOyg)，建立(複製一份)並自行撰寫 CodiMD 作業說明文件。請勿更動template裡的內容。
     - 關於 gitlab 開權限給助教群組的方式可以參照以下連結
        - [ACAL 2024 Curriculum GitLab 作業繳交方式說明 : Manage Permission](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Manage-Permission)
- **Step 3**
    - When you are done, please submit your homework document link to the Playlab 作業中心, <font style="color:blue"> 清華大學與陽明交通大學的同學請注意選擇對的作業中心鏈結</font>
        - [清華大學Playlab 作業中心](https://nthu-homework.playlab.tw/course?id=2)
        - [陽明交通大學作業繳交中心](https://course.playlab.tw/homework/course?id=2)

