# (NTHU_111064528_張瀚) ACAL 2024 Spring Lab4 HW

## Gitlab code link
- Gitlab link - https://course.playlab.tw/git/111064528/lab04/-/tree/main

## Hw4-1 Mix Adder
### Scala Code

```scala=
package acal_lab04.Hw1

import chisel3._
import chisel3.util.Cat
import acal_lab04.Lab._

class MixAdder (n:Int) extends Module{
    val io = IO(new Bundle{
        val Cin = Input(UInt(1.W))
        val in1 = Input(UInt((4*n).W))
        val in2 = Input(UInt((4*n).W))
        val Sum = Output(UInt((4*n).W))
        val Cout = Output(UInt(1.W))
    })

    val CL_Array = Array.fill(n)(Module(new CLAdder()).io)
    val carry = Wire(Vec(n+1, UInt(1.W)))
    val sum   = Wire(Vec(n, UInt(4.W)))

    carry(0) := io.Cin                    
    for (i <- 0 until n) {
        CL_Array(i).in1 := io.in1((i << 2) + 3, i << 2)
        CL_Array(i).in2 := io.in2((i << 2) +3, i << 2)
        CL_Array(i).Cin := carry(i)
        carry(i+1) := CL_Array(i).Cout
        sum(i) := CL_Array(i).Sum
    }
    
    //io.Sum := Cat(sum.reverse).asUInt
    io.Sum := sum.asUInt
    io.Cout := carry(n)
}
```
### Test Result
![](https://course.playlab.tw/md/uploads/746fc3be-620f-4e1b-a5b4-bde81da2029d.png)


## Hw4-2 Add-Suber
### Scala Code
![](https://course.playlab.tw/md/uploads/7d188373-a4e6-408e-bb93-e3877f08d2d7.png)

```scala=
package acal_lab04.Hw2

import chisel3._
import chisel3.util._
import acal_lab04.Lab._

class Add_Suber extends Module{
  val io = IO(new Bundle{
  val in_1 = Input(UInt(4.W))
  val in_2 = Input(UInt(4.W))
  val op = Input(Bool()) // 0:ADD 1:SUB
  val out = Output(UInt(4.W))
  val o_f = Output(Bool())
  })

  val FA_Array = Array.fill(4)(Module(new FullAdder()).io)
  val carry = Wire(Vec(5, UInt(1.W)))
  val sum = Wire(Vec(4, UInt(1.W)))

  carry(0) := io.op

  for (i <- 0 until 4) {
    FA_Array(i).A := io.in_1(i)
    FA_Array(i).B := io.in_2(i) ^ io.op
    FA_Array(i).Cin := carry(i)
    carry(i+1) := FA_Array(i).Cout
    sum(i) := FA_Array(i).Sum
  }

  io.out := sum.asUInt
  io.o_f := sum(3) ^ carry(4)
}

```
### Test Result
![](https://course.playlab.tw/md/uploads/201ea9b5-3f6f-4e93-bdf4-95ecc07d4d19.png)

## Hw4-3 Booth Multiplier
### Scala Code
![](https://course.playlab.tw/md/uploads/83891755-f8ca-44f0-8c4f-ef6509c01687.png)

```scala=
package acal_lab04.Hw3

import chisel3._
import chisel3.util._
import scala.annotation.switch

//------------------Radix 4---------------------
class Booth_Mul(width:Int) extends Module {
  val io = IO(new Bundle{
    val in1 = Input(UInt(width.W))      //Multiplicand
    val in2 = Input(UInt(width.W))      //Multiplier
    val out = Output(UInt((2*width).W)) //product
  })

  // there are width/2 parts in radix 4 representaion
  val parts = Wire(Vec(width >> 1, SInt(32.W)))
  // store multiplier as SInt
  val B = io.in2.asSInt()

  // initialization or compile will fail
  for (i <- 0 until width >> 1) {
    parts(i) := 0.S(32.W)
  }

  // for part 0
  switch(io.in1(1, 0)) { 
    is("b00".U) {
      parts(0) := 0.S(32.W)
    }
    is("b01".U) {
      // 1
      parts(0) := B.asTypeOf(SInt(32.W))
    }
    is("b10".U) {
      // -2
      parts(0) := (B * (-2).S).asTypeOf(SInt(32.W))
    }
    is("b11".U) {
      // -1
      parts(0) := -B.asTypeOf(SInt(32.W))
    }
  }

  // for part 1 ~ 7
  // switch case to decide weight and shift with 2^(i+1) 
  for (i <- 1 until width - 2 by 2) {
    switch(io.in1(i+2, i)) {
      is("b000".U) {
        // 0
        parts(i+1 >> 1) := 0.S(32.W)
      }                    
      is("b001".U) {
        // 1
        parts(i+1 >> 1) := (B << (i+1)).asTypeOf(SInt(32.W))
      }          
      is("b010".U) {
        // 1
        parts(i+1 >> 1) := (B << (i+1)).asTypeOf(SInt(32.W))
      }                       
      is("b011".U) {
        // 2
        parts(i+1 >> 1) := (B << (i+2)).asTypeOf(SInt(32.W))
      }                    
      is("b100".U) {
        // -2
        parts(i+1 >> 1) := (B * (-2).S << (i+1)).asTypeOf(SInt(32.W))       
      }
      is("b101".U) {
        // -1
        parts(i+1 >> 1) := (-B << (i+1)).asTypeOf(SInt(32.W))
      }
      is("b110".U) {
        // -1
        parts(i+1 >> 1) := (-B << (i+1)).asTypeOf(SInt(32.W))
      }
      is("b111".U) {
        // 0
        parts(i+1 >> 1) := 0.S(32.W)
      }
    }
  }
  
  // sum all the parts and take last 32 bits, ignoring overflow bits
  val ans = parts.reduce(_ +& _)(31, 0)
  io.out := ans.asUInt()
}
```
### Test Result
![](https://course.playlab.tw/md/uploads/d0888cf1-3cb8-4f91-b975-2d7d5049f9f4.png)
 

