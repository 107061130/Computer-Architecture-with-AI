package simd

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object MulOp extends ChiselEnum {
  val NONE                             = Value
  val PMULI8I16S_VV_L, PMULI8I16S_VV_H = Value
  val PMULI8I16S_VX_L, PMULI8I16S_VX_H = Value

  val AMULI8I8S_VV_NQ, AMULI8I8S_VV_AQ = Value
  val AMULI8I8S_VX_NQ, AMULI8I8S_VX_AQ = Value

  val QNT_INFO, QNTI16I8S_VV_NQ, QNTI16I8S_VV_AQ = Value
}

class MulUnit extends Module {
  val io = IO(new Bundle {
    val opSel = Input(MulOp())
    val rs1   = Input(UInt(32.W))
    val rs2   = Input(UInt(32.W))
    val scale = Input(UInt(5.W))
    val zero  = Input(UInt(16.W))
    val rdMsb = Output(UInt(32.W))
    val rd    = Output(UInt(32.W))
  })

  val rs1ByteArray    = Wire(Vec(4, UInt(8.W)))
  val rs2ByteArray    = Wire(Vec(4, UInt(8.W)))
  val rdHalfArray     = Wire(Vec(4, UInt(16.W)))
  val rdHalfArray_Q   = Wire(Vec(4, UInt(16.W)))
  rdHalfArray_Q := VecInit(Seq(0.U, 0.U, 0.U, 0.U))
  val shift_right     = WireDefault(true.B)
  shift_right := io.scale <= 16.U

  val rdLsbByteConcat = Wire(UInt(32.W))
  val rdMsbByteConcat = Wire(UInt(32.W))
  val rdLsbHalfConcat = Wire(UInt(32.W))
  val rdMsbHalfConcat = Wire(UInt(32.W))

  // 8-bit wire assignment
  for (i <- 0 until 4) {
    rs1ByteArray(i) := io.rs1(8 * i + 7, 8 * i)
    rs2ByteArray(i) := io.rs2(8 * i + 7, 8 * i)

    when(io.opSel.isOneOf(MulOp.AMULI8I8S_VV_NQ, MulOp.AMULI8I8S_VV_AQ, MulOp.PMULI8I16S_VV_L, MulOp.PMULI8I16S_VV_H)) {
      rdHalfArray(i) := (rs1ByteArray(i).asSInt * rs2ByteArray(i).asSInt).asUInt
    }
    .elsewhen(io.opSel.isOneOf(MulOp.AMULI8I8S_VX_NQ, MulOp.AMULI8I8S_VX_AQ, MulOp.PMULI8I16S_VX_L, MulOp.PMULI8I16S_VX_H)) {
      rdHalfArray(i) := (rs1ByteArray(i).asSInt * rs2ByteArray(0).asSInt).asUInt
    }
    .otherwise {
      rdHalfArray(i) := DontCare
    }
  }

  when (io.opSel.isOneOf(MulOp.AMULI8I8S_VV_AQ, MulOp.AMULI8I8S_VX_AQ)) {
    for (i <- 0 until 4) {
      rdHalfArray_Q(i) := Mux(shift_right, ((rdHalfArray(i).asSInt >> io.scale) + io.zero.asSInt).asUInt, 
                                           ((rdHalfArray(i) << ~io.scale + 1.U).asSInt + io.zero.asSInt).asUInt)
    }
  }

  rdMsbByteConcat := Seq.range(3, -1, -1).map { i => rdHalfArray(i)(15, 8) }.reduce(_ ## _)
  rdLsbByteConcat := Seq.range(3, -1, -1).map { i => rdHalfArray_Q(i)(7, 0) }.reduce(_ ## _)
  rdLsbHalfConcat := Seq.range(1, -1, -1).map { i => rdHalfArray(i) }.reduce(_ ## _)
  rdMsbHalfConcat := Seq.range(3, 1, -1).map { i => rdHalfArray(i) }.reduce(_ ## _)

  // output assignment
  io.rdMsb := rdMsbHalfConcat
  io.rd    := MuxLookup(
    io.opSel.asUInt,
    DontCare,
    Seq(
      MulOp.AMULI8I8S_VV_NQ.asUInt -> rdMsbByteConcat,
      MulOp.AMULI8I8S_VX_NQ.asUInt -> rdMsbByteConcat,
      MulOp.AMULI8I8S_VV_AQ.asUInt -> rdLsbByteConcat,
      MulOp.AMULI8I8S_VX_AQ.asUInt -> rdLsbByteConcat,

      MulOp.PMULI8I16S_VV_L.asUInt -> rdLsbHalfConcat,
      MulOp.PMULI8I16S_VV_H.asUInt -> rdMsbHalfConcat,
      MulOp.PMULI8I16S_VX_L.asUInt -> rdLsbHalfConcat,
      MulOp.PMULI8I16S_VX_H.asUInt -> rdMsbHalfConcat
    )
  )
}
