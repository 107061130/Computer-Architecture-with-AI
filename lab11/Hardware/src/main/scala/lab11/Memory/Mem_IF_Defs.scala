package acal_lab09.MemIF

import chisel3._
import chisel3.util._

class MemIF_CPU(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val Mem_R = Output(Bool())
  val Mem_W = Output(Bool())
  val Length = Output(UInt(4.W))
	val Valid = Input(Bool())

	val raddr = Output(UInt(addrWidth.W))
	val rdata = Input(UInt(dataWidth.W))

	val waddr = Output(UInt(addrWidth.W))
	val wdata = Output(UInt(dataWidth.W))

  override def clone = { new MemIF_CPU(addrWidth, dataWidth).asInstanceOf[this.type] }
}
