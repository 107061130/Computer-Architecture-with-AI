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
