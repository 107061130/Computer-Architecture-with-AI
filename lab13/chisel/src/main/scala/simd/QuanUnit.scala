package simd

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object QuanOp extends ChiselEnum {
  val NONE = Value
  val QNT_INFO, QNTI16I8S_VV_NQ, QNTI16I8S_VV_AQ = Value
}

class QuanUnit extends Module {
  val io = IO(new Bundle {
    val opSel = Input(QuanOp())
    val rs1   = Input(UInt(32.W))
    val rs2   = Input(UInt(32.W))
    val rd    = Output(UInt(32.W))
    val scale = Output(UInt(5.W))
    val zero  = Output(UInt(16.W))
  })

  val rsHalfArray = Wire(Vec(4, UInt(16.W)))
  rsHalfArray(0) := io.rs1(15, 0)
  rsHalfArray(1) := io.rs1(31, 16)
  rsHalfArray(2) := io.rs2(15, 0)
  rsHalfArray(3) := io.rs2(31, 16)

  val rsHalfArray_Q   = Wire(Vec(4, UInt(16.W)))
  rsHalfArray_Q := VecInit(Seq(0.U, 0.U, 0.U, 0.U))

  val rdLsbByteConcat = Wire(UInt(32.W))
  val rdMsbByteConcat = Wire(UInt(32.W))

  // Quantization
  val scaling_factor = RegInit(0.U(5.W))
  val zero_point     = RegInit(0.U(16.W))
  val shift_right    = WireDefault(true.B)
  shift_right := scaling_factor <= 16.U
 
  // Store Quantization Factors
  when (io.opSel === QuanOp.QNT_INFO) {
    scaling_factor := io.rs1(4, 0)
    zero_point     := io.rs2(15, 0)
  }

  io.scale := scaling_factor
  io.zero  := zero_point

  when (io.opSel === QuanOp.QNTI16I8S_VV_AQ) {
    for (i <- 0 until 4) {
      rsHalfArray_Q(i) := Mux(shift_right, ((rsHalfArray(i).asSInt >> scaling_factor) + zero_point.asSInt).asUInt, 
                                           ((rsHalfArray(i) << ~scaling_factor + 1.U).asSInt + zero_point.asSInt).asUInt)
    }
  }

  rdMsbByteConcat := Seq.range(3, -1, -1).map { i => rsHalfArray(i)(15, 8) }.reduce(_ ## _)
  rdLsbByteConcat := Seq.range(3, -1, -1).map { i => rsHalfArray_Q(i)(7, 0) }.reduce(_ ## _)

  io.rd    := MuxLookup(
    io.opSel.asUInt,
    DontCare,
    Seq(
      QuanOp.QNT_INFO.asUInt -> io.rs1(4, 0),
      QuanOp.QNTI16I8S_VV_NQ.asUInt -> rdMsbByteConcat,
      QuanOp.QNTI16I8S_VV_AQ.asUInt -> rdLsbByteConcat
    )
  )
}
