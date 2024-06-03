package simd

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object OthersOp extends ChiselEnum {
  val NONE = Value
  val RELU, MAXPOOL = Value
}

class OthersUnit extends Module {
  val io = IO(new Bundle {
    val opSel = Input(OthersOp())
    val rs1   = Input(UInt(32.W))
    val rd    = Output(UInt(32.W))
  })

  val rsByteArray = Wire(Vec(4, SInt(8.W)))
  rsByteArray(0) := io.rs1(7, 0).asSInt
  rsByteArray(1) := io.rs1(15, 8).asSInt
  rsByteArray(2) := io.rs1(23, 16).asSInt
  rsByteArray(3) := io.rs1(31, 24).asSInt

  val rdByteArray = Wire(Vec(4, SInt(8.W)))
  rdByteArray := VecInit(Seq(0.S, 0.S, 0.S, 0.S))

  when (io.opSel === OthersOp.RELU) {
    rdByteArray(0) := Mux(rsByteArray(0) >= 0.S, rsByteArray(0), 0.S)
    rdByteArray(1) := Mux(rsByteArray(1) >= 0.S, rsByteArray(1), 0.S)
    rdByteArray(2) := Mux(rsByteArray(2) >= 0.S, rsByteArray(2), 0.S)
    rdByteArray(3) := Mux(rsByteArray(3) >= 0.S, rsByteArray(3), 0.S)
  }
  .elsewhen (io.opSel === OthersOp.MAXPOOL) {
    rdByteArray(0) := rsByteArray.reduce((a, b) => Mux(a > b, a, b))
    rdByteArray(1) := 0.S
    rdByteArray(2) := 0.S
    rdByteArray(3) := 0.S
  }

  io.rd := rdByteArray.asUInt
}
