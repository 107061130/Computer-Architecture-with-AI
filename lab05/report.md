(NTHU_111064528_張瀚)  ACAL 2024 Spring Lab 5 HW 
===


## Gitlab code link


- Gitlab link - https://course.playlab.tw/git/111064528/lab05

## Hw5-1 TrafficLight with Pedestrian button
### Scala Code

```scala=
package acal_lab05.Hw1

import chisel3._
import chisel3.util._

class TrafficLight_p(Ytime:Int, Gtime:Int, Ptime:Int) extends Module{
    val io = IO(new Bundle{
        val P_button = Input(Bool())
        val H_traffic = Output(UInt(2.W))
        val V_traffic = Output(UInt(2.W))
        val P_traffic = Output(UInt(2.W))
        val timer     = Output(UInt(5.W))
    })
    //parameter declaration
    val Off = 0.U
    val Red = 1.U
    val Yellow = 2.U
    val Green = 3.U
    
    val sIdle :: sHGVR :: sHYVR :: sHRVG :: sHRVY :: sPG :: Nil = Enum(6)
    
    //State register
    val state = RegInit(sIdle)
    // record previous state
    val pre = RegInit(sHGVR)
    
    //Counter============================
    val cntMode = WireDefault(0.U(2.W))
    val cntReg = RegInit(0.U(5.W))
    val cntDone = Wire(Bool())
    cntDone := cntReg === 0.U
    
    when(cntDone || (io.P_button && state =/= sPG)){
        when(io.P_button || cntMode === 2.U){
            cntReg := (Ptime-1).U
        }
        .elsewhen(cntMode === 0.U){
            cntReg := (Gtime-1).U
        }.elsewhen(cntMode === 1.U){
            cntReg := (Ytime-1).U
        }
    }.otherwise{
        cntReg := cntReg - 1.U
    }
    
    //Next State Decoder
    switch(state){
        is(sIdle){
            state := sHGVR
        }
        is(sHGVR){
            when(io.P_button) {
                pre := sHGVR
                state := sPG
            }
            .elsewhen(cntDone) {state := sHYVR}
        }
        is(sHYVR){
            when(io.P_button) {
                pre := sHYVR
                state := sPG
            }
            .elsewhen(cntDone) {state := sHRVG}
        }
        is(sHRVG){
            when(io.P_button) {
                pre := sHRVG
                state := sPG
            }
            .elsewhen(cntDone) {state := sHRVY}
        }
        is(sHRVY){
            when(io.P_button) {
                pre := sHRVY
                state := sPG
            }
            .elsewhen(cntDone) {state := sPG}
        }
        is(sPG){
            when(cntDone) {
                state := pre
                pre := sHGVR
            }
        }
    }
    
    //Output Decoder
    //Default statement
    cntMode := 0.U
    io.H_traffic := Off
    io.V_traffic := Off
    io.P_traffic := Off
    
    switch(state){
        is(sHGVR){
            cntMode := 1.U
            io.H_traffic := Green
            io.V_traffic := Red
            io.P_traffic := Red
        }
        is(sHYVR){
            cntMode := 0.U
            io.H_traffic := Yellow
            io.V_traffic := Red
            io.P_traffic := Red
        }
        is(sHRVG){
            cntMode := 1.U
            io.H_traffic := Red
            io.V_traffic := Green
            io.P_traffic := Red
        }
        is(sHRVY){
            cntMode := 2.U
            io.H_traffic := Red
            io.V_traffic := Yellow
            io.P_traffic := Red
        }
        is(sPG){
            when (pre === sHGVR || pre === sHRVG) {cntMode := 0.U}
            .otherwise {cntMode := 1.U}
            io.H_traffic := Red
            io.V_traffic := Red
            io.P_traffic := Green
        }
    }
    io.timer := cntReg
}
```
### Waveform
![](https://course.playlab.tw/md/uploads/66870ee7-a59d-4b96-95ed-85740492135a.png)

![](https://course.playlab.tw/md/uploads/8c27556a-7ccd-4e60-be06-c4ac799c4327.png)




## Hw5-2-1 Negative Integer Generator
### Scala Code

same as 5-2-3

### Test Result
![](https://course.playlab.tw/md/uploads/e4ea599b-9be4-4317-826f-8608f2352a56.png)


## Hw5-2-2 N operands N-1 operators(+、-)
### Scala Code

same as 5-2-3

### Test Result
![](https://course.playlab.tw/md/uploads/bc25f54b-adb1-40c0-84eb-68a3f11d8da4.png)



## Hw5-2-3 Order of Operation (+、-、*、(、))
### FSM
![](https://course.playlab.tw/md/uploads/b6f79780-168c-4278-a1ca-0e4a998c3474.png)


### Pseudo Code
```c=
store = [input sequence]
stack number, op
priority = {'+' : 1, '-' : 1, '*' : 2, '(' : 0}

for element in store
    if(element == 0~9)
        src = src*10 + element
        if(element is last bit of number) number.push(src)
        
    else if(element == '(') 
        op.push(element)
        
    else if(element == ')')
        // caculate1: run caculation until op.top() == '('
        caculate1() 
        
    else if(element == '+' or '-' or '*')
        if (priority(op.top()) >= priority(element))
            // caculate2: run caculation until priority(op.top()) < priority(element)
            caculate2()
        else
           op.push(element)

return number.top()
            
void caculation()
    src1 = number.top(); number.pop()
    src2 = number.top(); number.pop()
    operator = op.top(); op.pop()
    val = src1 (operator) src2
    number.push(val)
```    
### Scala Code

```scala=
package acal_lab05.Hw2

import chisel3._
import chisel3.util._

class CpxCal extends Module{
    val io = IO(new Bundle{
    val key_in = Input(UInt(4.W))
    val value = Output(Valid(UInt(128.W)))
    })

    // store input string
    val store = RegInit(VecInit(Seq.fill(160)(0.U(4.W))))
    val length = RegInit(0.U(8.W))
    val index = RegInit(0.U(8.W))

    // number stack and it's pointer
    val num_stk = RegInit(VecInit(Seq.fill(16)(0.U(128.W))))
    val num_sp = RegInit(0.U(4.W))

    // operator stack and it's pointer
    val op_stk = RegInit(VecInit(Seq.fill(64)(0.U(2.W))))
    val op_sp = RegInit(0.U(6.W))

    // State and State Transition Variable
    val sStack :: sCaculate1 :: sCaculate2 :: sFinish :: Nil = Enum(4)
    val state = RegInit(sStack)
    
    val stack_end = WireDefault(false.B)
    stack_end := index > length

    val go_caculate1 = WireDefault(false.B)
    val go_caculate2 = WireDefault(false.B)
    val caculate1_end = WireDefault(false.B)
    val caculate2_end = WireDefault(false.B)

    // Constant Declaration
    val add = 0.U
    val sub = 1.U
    val mul = 2.U
    val l_paren = 3.U

    val priority = VecInit(Seq.fill(4)(0.U(2.W)))
    priority(add) := 1.U
    priority(sub) := 1.U
    priority(mul) := 2.U
    priority(l_paren) := 0.U

    // Next State Decoder
    switch(state){
        // read store input
        is(sStack) {
            when(stack_end) {state := sFinish}
            .elsewhen(go_caculate1) {state := sCaculate1}
            .elsewhen(go_caculate2) {state := sCaculate2}
        }
        // right parentheses case 
        is(sCaculate1){
            when(caculate1_end) {state := sStack}
        }
        // operator priority case 
        is(sCaculate2){
            when(caculate2_end) {state := sStack}
        }
        is(sFinish){
            state := sStack
        }
    }
    //==================================================
    val neg = RegInit(false.B)
    val cur_op = RegInit(0.U(2.W))
    val store_end = WireDefault(false.B)
    store_end := io.key_in === 15.U
    // to ensure store has already read two element, (|| store_end) to prevent (input length == 1) case 
    val ready = WireDefault(false.B)
    ready := (length > 1.U || store_end)

    // record input string to store 
    when(!store_end){
        store(length) := io.key_in
        length := length + 1.U
    }
    
    when(ready && state === sStack){
        // traverse to store end, caculate remain number in stack 
        when (index === length) {
            cur_op := l_paren
            go_caculate2 := true.B
        }
        // digits
        .elsewhen (store(index) < 10.U) {
            val src = (num_stk(num_sp)<<3.U) + (num_stk(num_sp)<<1.U) + store(index)
            num_stk(num_sp) := src
            // if it is last bit of number
            when(index+1.U === length || store(index+1.U) >= 10.U) { 
                when(neg) {num_stk(num_sp) := ~src + 1.U}
                num_sp := num_sp + 1.U
            }
        }
        // operators
        .otherwise {
            // negative case 
            when(store(index) === 13.U && store(index + 1.U) === 11.U) {
                neg := true.B
            }
            // left parentheses
            .elsewhen(store(index) === 13.U) {
                op_stk(op_sp) := l_paren
                op_sp := op_sp + 1.U
            }
            // right parentheses
            .elsewhen(store(index) === 14.U) {
                // if negative, only reset neg
                when(neg) {neg := false.B}
                // else go caculate first
                .otherwise { go_caculate1 := true.B}
            }
            // +, -, x but not negative
            .elsewhen (!neg) {
                val op = MuxLookup(store(index),0.U,Seq(
                    10.U -> add,
                    11.U -> sub,
                    12.U -> mul
                ))
                val opstk_top = op_stk(op_sp - 1.U)
                // if priority pass, go caculate first
                when (op_sp > 0.U && priority(opstk_top) >= priority(op)) {
                    go_caculate2 := true.B
                    cur_op := op
                }
                .otherwise {
                    op_stk(op_sp) := op
                    op_sp := op_sp + 1.U
                }
            }
        }
        index := index + 1.U
    }

    // right parentheses case
    when(state === sCaculate1){
        val opstk_top = op_stk(op_sp - 1.U)
        when(opstk_top =/= l_paren) {
            val src2 = num_stk(num_sp - 1.U)
            val src1 = num_stk(num_sp - 2.U)
            num_stk(num_sp - 2.U) := MuxLookup(opstk_top,0.U,Seq(
                add -> (src1 + src2),
                sub -> (src1 - src2),
                mul -> (src1 * src2)
            ))
            num_stk(num_sp - 1.U) := 0.U
            num_sp := num_sp - 1.U
        }
        .otherwise {
            caculate1_end := true.B
        }
        op_sp := op_sp - 1.U
    }

    // operator priority case
    when(state === sCaculate2){
        val opstk_top = op_stk(op_sp - 1.U)
        when(op_sp > 0.U && priority(opstk_top) >= priority(cur_op)) {
            val src2 = num_stk(num_sp - 1.U)
            val src1 = num_stk(num_sp - 2.U)
            num_stk(num_sp - 2.U) := MuxLookup(opstk_top,0.U,Seq(
                add -> (src1 + src2),
                sub -> (src1 - src2),
                mul -> (src1 * src2)
            ))
            num_stk(num_sp - 1.U) := 0.U
            num_sp := num_sp - 1.U
            op_sp := op_sp - 1.U
        }
        .otherwise {
            op_stk(op_sp) := cur_op
            op_sp := op_sp + 1.U
            caculate2_end := true.B
        }
    }

    // Finish and Reset
    when(state === sFinish){
        num_stk(0) := 0.U
        neg := false.B
        num_sp := 0.U
        op_sp := 0.U
        index := 0.U
        length := 0.U
    }
    
    io.value.valid := Mux(state === sFinish,true.B,false.B)
    io.value.bits := num_stk(0)
}
```
### Test Result
![](https://course.playlab.tw/md/uploads/3db0075a-6048-459e-92f8-130ea9dd1aa8.png)

![](https://course.playlab.tw/md/uploads/d15fb90d-5589-443a-b967-fe4aac895c64.png)

## Hw5-3-1 Pseudo Random Number Generator

### FSM
![](https://course.playlab.tw/md/uploads/1c3b770f-29f0-4f08-90b6-274180111209.png)

### Scala Code
```scala=
package acal_lab05.Hw3

import chisel3._
import chisel3.util._

object LfsrTaps {
    def apply(size: Int): Seq[Int] = {
        size match {
            // Seqp[Int] means the taps in LFSR
            case 16 => Seq(14,13,11)      //p(x) = x^16+x^14+x^13+x^11+1
            case _ => throw new Exception("No LFSR taps stored for requested size")
        }
    }
}

class PRNG(seed:Int) extends Module{
    val io = IO(new Bundle{
        val gen = Input(Bool())
        val puzzle = Output(Vec(4,UInt(4.W)))
        val ready = Output(Bool())
    })
    
    // Initialize the shift register with seed
    val binarySeed = seed.toBinaryString.reverse.padTo(16, '0').reverse
    val shiftReg = RegInit(VecInit(binarySeed.map(_ == '1').map(_.B)))
    
    val sIdle :: sGen :: sConvert1 :: sConvert2 :: sCheck :: sEnd :: Nil = Enum(6)
    val state = RegInit(sIdle)

    val result = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))
    val pass = RegInit(1.U(2.W))
    val Combination_Table = RegInit(VecInit(Seq.fill(10000)(false.B)))
    
    switch(state){
        is(sIdle){
            when(io.gen) { state := sGen }
        }
        is(sGen) {
            state := sConvert1
            pass := 1.U
        }
        is(sConvert1) {
            state := sConvert2
        }
        is(sConvert2) {
            state := sCheck
        }
        is(sCheck) {
            when(pass === 0.U) {state := sGen}
            .elsewhen(pass === 2.U) {state := sEnd}
        }
        is(sEnd){
            state := sIdle
        }
    }

    // generate random 16 bits
    when(state === sGen) {
        //Barrel Shift Register
        (shiftReg.zipWithIndex).map{
            case(sr,i) => sr := shiftReg((i+1)%16)
        }
        //Fibonacci LFSR
        shiftReg(15) := (LfsrTaps(16).map(x=>shiftReg(16-x)).reduce(_^_)) ^ shiftReg(0)
    }
    
    // convert it to four 4 bits UInt
    when(state === sConvert1) { 
        for (i <- 0 until 4) {
            result(i) := Cat(shiftReg.slice(i << 2, (i + 1) << 2).reverse)
        }
    }
    
    // handle > 9 situation
    when(state === sConvert2) {
        for (i <- 0 until 4) {
            when(result(i) >= 10.U) { result(i) := result(i) - 10.U }
        }
    }

    // handle repeated number & repeated combination
    when(state === sCheck) { 
        pass := 2.U
        for (i <- 0 until 3) {
            for (j <- i + 1 until 4) {
                when (result(i) === result(j)) { pass := 0.U }
            }
        }
        when(Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3))) { pass := 0.U }
        Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3)) := true.B
    }
    
    io.puzzle := result
    io.ready := state === sEnd
}
```
### Test Result
![](https://course.playlab.tw/md/uploads/30a4bc99-bee9-4e54-80e8-7dde9fc181b1.png)


## Hw5-3-2 1A2B game quiz
### FSM
![](https://course.playlab.tw/md/uploads/aeac9778-95a5-48d9-9829-02398f944e90.png)


### Scala Code

```scala=
package acal_lab05.Hw3

import chisel3._
import chisel3.util._

class NumGuess(seed:Int = 1) extends Module{
    require (seed > 0 , "Seed cannot be 0")

    val io  = IO(new Bundle{
        val gen = Input(Bool())
        val guess = Input(UInt(16.W))
        val puzzle = Output(Vec(4,UInt(4.W)))
        val ready  = Output(Bool())
        val g_valid  = Output(Bool())
        val A      = Output(UInt(4.W))
        val B      = Output(UInt(4.W))

        //don't care at Hw6-3-2 but should be considered at Bonus
        val s_valid = Input(Bool())
    })
    
     // Initialize the shift register with seed
    val binarySeed = seed.toBinaryString.reverse.padTo(16, '0').reverse
    val shiftReg = RegInit(VecInit(binarySeed.map(_ == '1').map(_.B)))
    
    val sIdle :: sGen :: sConvert1 :: sConvert2 :: sCheck :: sGen_End :: sGuess_Read :: sGuess_Out :: Nil = Enum(8)
    val state = RegInit(sIdle)

    val result = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))
    val pass = RegInit(1.U(2.W))
    val Combination_Table = RegInit(VecInit(Seq.fill(10000)(false.B)))
    val GUESS = RegInit(VecInit(Seq.fill(4)(15.U(4.W))))
    
    val NA = RegInit(0.U(3.W))
    val NB = RegInit(0.U(3.W))
    
    switch(state){
        is(sIdle){
            when(io.gen) { state := sGen }
        }
        is(sGen) {
            state := sConvert1
            pass := 1.U
        }
        is(sConvert1) {
            state := sConvert2
        }
        is(sConvert2) {
            state := sCheck
        }
        is(sCheck) {
            when(pass === 0.U) {state := sGen}
            .elsewhen(pass === 2.U) {state := sGen_End}
        }
        is(sGen_End){
            state := sGuess_Read
        }
        is(sGuess_Read){
            state := sGuess_Out
        }
        is(sGuess_Out){
            state := sGen_End
        }
    }

    // generate random 16 bits
    when(state === sGen) {
        //Barrel Shift Register
        (shiftReg.zipWithIndex).map{
            case(sr,i) => sr := shiftReg((i+1)%16)
        }
        //Fibonacci LFSR
        shiftReg(15) := (LfsrTaps(16).map(x=>shiftReg(16-x)).reduce(_^_)) ^ shiftReg(0)
    }
    
    // convert it to four 4 bits UInt
    when(state === sConvert1) { 
        for (i <- 0 until 4) {
            result(i) := Cat(shiftReg.slice(i << 2, (i + 1) << 2).reverse)
        }
    }
    
    // handle > 9 situation
    when(state === sConvert2) {
        for (i <- 0 until 4) {
            when(result(i) >= 10.U) { result(i) := result(i) - 10.U }
        }
    }

    // handle repeated number & repeated combination
    when(state === sCheck) { 
        pass := 2.U
        // repeated number
        for (i <- 0 until 3) {
            for (j <- i + 1 until 4) {
                when (result(i) === result(j)) { pass := 0.U }
            }
        }
        // repeated combination
        when(Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3))) { pass := 0.U }
        Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3)) := true.B
    }
    
    when(state === sGuess_Read) { 
        // count A
        val A_compare = Wire(Vec(4, UInt(1.W)))
        for (i <- 0 until 4) {
            A_compare(i) := result(i) === io.guess((i << 2) + 3, i << 2)
        }
        NA := A_compare.reduce(_ +& _)
        
        // count B
        val B_compare = Wire(Vec(16, UInt(1.W)))
        for (i <- 0 until 4) {
            for (j <- 0 until 4) {
                B_compare((i << 2) + j) := result(i) === io.guess((j << 2) + 3, j << 2)
            }
        }
        NB := B_compare.reduce(_ +& _)
    }
    
    io.puzzle := result
    io.ready := state === sGen_End
    
    io.g_valid  := state === sGuess_Out
    io.A      := NA
    io.B      := NB
}
```
### Test Result
![](https://course.playlab.tw/md/uploads/69ff71bc-c4e9-4eb7-8fdf-8e937c8c62cd.png)


## Bonus : 1A2B hardware solver [Optional]
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
## scala code & comment
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)


## 文件中的問答題
- Q1:Hw5-2-2(長算式)以及Lab5-2-2(短算式)，需要的暫存器數量是否有差別？如果有，是差在哪裡呢？
    - Ans1:長算式會需要更多暫存器，但我的stack大小是寫死的
    
- Q2:你是如何處理**Hw5-2-3**有提到的關於**編碼衝突**的問題呢?
    - Ans2:我用兩個stack，一個存operator，另一個存數字，因此在讀取時並無衝突問題
    
- Q3:你是如何處理**Hw5-3-1**1A2B題目產生時**數字重複**的問題呢?
    - Ans3: 如下方這段code，兩層for迴圈去檢查所有組合，至於重複的題目則是用10^4個register去紀錄已出現的題目

```scala=
// handle repeated number & repeated combination
when(state === sCheck) { 
    pass := 2.U
    // repeated number
    for (i <- 0 until 3) {
        for (j <- i + 1 until 4) {
            when (result(i) === result(j)) { pass := 0.U }
        }
    }
    // repeated combination
    when(Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3))) { pass := 0.U }
    Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3)) := true.B
}
```

