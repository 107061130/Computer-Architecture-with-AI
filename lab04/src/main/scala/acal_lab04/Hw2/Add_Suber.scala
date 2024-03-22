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


