package SoC

import chisel3._
import chisel3.util._
import PipelinedCPU._
import Instruction_Memory._
import AXI._
import DMA._
import AXILite._
import Memory._

// CPU <-> Bus <-> Memory

class top_SoC(idWidth: Int, addrWidth: Int, dataWidth: Int) extends Module {
    val io = IO(new Bundle{
        val regs = Output(Vec(32,UInt(dataWidth.W)))
        val Hcf = Output(Bool())

        //for sure that IM and DM will be synthesized
        val inst = Output(UInt(32.W))
        val rdata = Output(UInt(32.W))
        val wdata  = Output(UInt(32.W))

        val Dump_Mem = Input(Bool())
        // Test
        val E_Branch_taken = Output(Bool())
        val Flush = Output(Bool())
        val Stall_MA = Output(Bool())
        val Stall_DH = Output(Bool())
        val IF_PC = Output(UInt(32.W))
        val ID_PC = Output(UInt(32.W))
        val EXE_PC = Output(UInt(32.W))
        val MEM_PC = Output(UInt(32.W))
        val WB_PC = Output(UInt(32.W))
        val EXE_alu_out = Output(UInt(32.W))
        val EXE_src1 = Output(UInt(32.W))
        val EXE_src2 = Output(UInt(32.W))
        val ALU_src1 = Output(UInt(32.W))
        val ALU_src2 = Output(UInt(32.W))
        val raddr = Output(UInt(32.W))
        val WB_rd = Output(UInt(5.W))
        val WB_wdata = Output(UInt(32.W))
        val EXE_Jump = Output(Bool())
        val EXE_Branch = Output(Bool())
        val DMA_Hcf = Output(Bool())
    })

    object SystemConfig {
        val nMasters: Int = 2
        val nSlaves: Int = 3
        val DMABaseAddr: Int = 0
        val DMASize: Int = 100
        val LocalMemBaseAddr: Int = 0x8000
        val LocalMemSize: Map[String, Int] = Map(
          "Size" -> 2048, // Height x Width
          "Height" -> 64, // The Number of bytes
          "Width" -> 32 // unit: 32 bits
        )
        val LocalMemLatency: Int = 1
        val LocalMemInitFilePath: String =
          "./src/main/resource/data0.hex" // Provide the file path
        
        val GlobalMemBaseAddr: Int = 0x10000 // Provide the base address
        val GlobalMemSize: Map[String, Int] = Map(
          "Size" -> 2048, // Height x Width
          "Height" -> 64, // The Number of bytes
          "Width" -> 32 // unit: 32 bits
        )
        val GlobalMemLatency: Int = 80
        val GlobalMemInitFilePath: String =
          "./src/main/resource/data1.hex" // Provide the file path
    }
    
    val cpu = Module(new PipelinedCPU(32, 32))
    val im = Module(new InstMem(15))

    val dma = Module(new DMA(idWidth, addrWidth, dataWidth, SystemConfig.DMABaseAddr))
    val bus = Module(
        new AXILiteXBar(
          SystemConfig.nMasters,
          SystemConfig.nSlaves,
          idWidth,
          addrWidth,
          dataWidth,
          Seq(
            (SystemConfig.DMABaseAddr, SystemConfig.DMASize),
            (SystemConfig.LocalMemBaseAddr, SystemConfig.LocalMemSize("Size")),
            (SystemConfig.GlobalMemBaseAddr, SystemConfig.GlobalMemSize("Size")),
          )
        )
      )

    val localMem = Module(
            new DataMem(
              SystemConfig.LocalMemSize("Width"),
              SystemConfig.LocalMemSize("Height"),
              idWidth,
              addrWidth,
              dataWidth,
              SystemConfig.LocalMemBaseAddr,
              SystemConfig.LocalMemLatency,
              SystemConfig.LocalMemInitFilePath
            )
          )
    val globalMem = Module(
        new DataMem(
          SystemConfig.GlobalMemSize("Width"),
          SystemConfig.GlobalMemSize("Height"),
          idWidth,
          addrWidth,
          dataWidth,
          SystemConfig.GlobalMemBaseAddr,
          SystemConfig.GlobalMemLatency,
          SystemConfig.GlobalMemInitFilePath
        )
    )
    
    // Bus Interface
    bus.io.masters(0) <> cpu.io.master
    bus.io.masters(1) <> dma.io.master
    
    bus.io.slaves(0) <> dma.io.slave
    bus.io.slaves(1) <> localMem.io.slave
    bus.io.slaves(2) <> globalMem.io.slave
    
    // Data Memory
    localMem.io.dump := io.Dump_Mem
    globalMem.io.dump := false.B
    cpu.io.DataMem.rdata := localMem.io.slave.r.bits.data 
    
    // Insruction Memory
    im.io.raddr := cpu.io.InstMem.raddr
    cpu.io.InstMem.rdata := im.io.inst
    
    //System
    io.DMA_Hcf := dma.io.Hcf
    io.regs := cpu.io.regs
    io.Hcf := cpu.io.Hcf
    io.inst := im.io.inst
    io.rdata := cpu.io.master.r.bits.data
    io.wdata := cpu.io.master.w.bits.data
    cpu.io.InstMem.Valid := true.B // Direct to Mem
    cpu.io.DataMem.Valid := true.B // Direct to Mem
    cpu.io.DMA_Hcf := dma.io.Hcf
    
    // Test
    io.E_Branch_taken := cpu.io.E_Branch_taken
    io.Flush := cpu.io.Flush
    io.Stall_MA := cpu.io.Stall_MA
    io.Stall_DH := cpu.io.Stall_DH
    io.IF_PC := cpu.io.IF_PC
    io.ID_PC := cpu.io.ID_PC
    io.EXE_PC := cpu.io.EXE_PC
    io.MEM_PC := cpu.io.MEM_PC
    io.WB_PC := cpu.io.WB_PC
    io.EXE_alu_out := cpu.io.EXE_alu_out
    io.EXE_src1 := cpu.io.EXE_src1
    io.EXE_src2 := cpu.io.EXE_src2
    io.ALU_src1 := cpu.io.ALU_src1
    io.ALU_src2 := cpu.io.ALU_src2
    io.raddr := cpu.io.DataMem.raddr
    io.WB_rd := cpu.io.WB_rd
    io.WB_wdata := cpu.io.WB_wdata
    io.EXE_Jump := cpu.io.EXE_Jump
    io.EXE_Branch := cpu.io.EXE_Branch
}

import chisel3.stage.ChiselStage

object top_SoC extends App {
  (
    new chisel3.stage.ChiselStage).emitVerilog(
      new top_SoC(4, 32, 32),
      Array("-td","./generated","-tbn","verilator")
  )
}