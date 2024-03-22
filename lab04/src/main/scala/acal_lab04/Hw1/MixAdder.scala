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

