package AXILite

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import chiseltest.ChiselScalatestTester

import chiseltest.simulator.WriteVcdAnnotation
import chisel3.experimental.BundleLiterals._

import AXI._
import Config._
import Utils.AXITester
class AXILiteXBarBurstTest
    extends AnyFlatSpec
    with ChiselScalatestTester
    with AXITester {

  "Masters" should "send/read data to each Slaves according to addr" in {
    test(
      new AXILiteXBar(
        AXI_Config.master_num,
        AXI_Config.slave_num,
        AXI_Config.s_id_width,
        AXI_Config.addr_width,
        AXI_Config.data_width,
        AXI_Config.addr_map
      )
    ).withAnnotations(
      Seq(
        WriteVcdAnnotation
      )
    ) { dut =>
      /* Initialize IO ports */
      // * masters
      for (i <- 0 until AXI_Config.master_num) {
        // input port
        dut.io.masters(i).ar.initSource().setSourceClock(dut.clock)
        dut.io.masters(i).aw.initSource().setSourceClock(dut.clock)
        dut.io.masters(i).w.initSource().setSourceClock(dut.clock)

        // output ports
        dut.io.masters(i).r.initSink().setSinkClock(dut.clock)
        dut.io.masters(i).b.initSink().setSinkClock(dut.clock)
      }
      // * slaves
      for (i <- 0 until AXI_Config.slave_num) {
        // input port
        dut.io.slaves(i).r.initSource().setSourceClock(dut.clock)
        dut.io.slaves(i).b.initSource().setSourceClock(dut.clock)

        // output ports
        dut.io.slaves(i).ar.initSink().setSinkClock(dut.clock)
        dut.io.slaves(i).aw.initSink().setSinkClock(dut.clock)
        dut.io.slaves(i).w.initSink().setSinkClock(dut.clock)
      }

      println("----START TEST----")

      println("[Test 1]  READ Burst test")
      println("[Test 1]: Master read three datas from slave according to addr")
      println("[Test 1]: Slave respond burst data ")

      dut.io.masters(0).r.ready.poke(true.B)
      dut.io.masters(0).ar.valid.poke(true.B) 
      fork {
        dut.io.masters(0).ar.enqueue(genAXIAddr(BigInt("9000", 16).toInt))
      }.fork {
        // burst mode with last of last data set to true
        dut.io.slaves(0).r.enqueue(genAXIReadData(0, BigInt("00010203", 16).toInt, false))
        dut.io.slaves(0).r.enqueue(genAXIReadData(0, BigInt("00040506", 16).toInt, false))
        dut.io.slaves(0).r.enqueue(genAXIReadData(0, BigInt("00070809", 16).toInt, true))
      }.fork.withRegion(Monitor){
        while(!(dut.io.masters(0).r.valid.peek().litToBoolean)){
          dut.clock.step(1)
        }     
        println("[Test]: Slave observe address 0x" + dut.io.slaves(0).ar.bits.addr.peek().litValue.toString(16))
        println("[Test]: Master observe data 0x" + dut.io.masters(0).r.bits.data.peek().litValue.toString(16)
               + ", Last = " + dut.io.masters(0).r.bits.last.peek().litValue.toString(16))

        dut.clock.step(1)
        // no need to send ar.valid and address again, just poke r.ready
        dut.io.masters(0).r.ready.poke(true.B)
        //dut.io.masters(0).ar.valid.poke(true.B)

        while(!(dut.io.masters(0).r.valid.peek().litToBoolean)){
          dut.clock.step(1)
        }
        println("[Test]: Master observe data 0x" + dut.io.masters(0).r.bits.data.peek().litValue.toString(16)
                + ", Last = " + dut.io.masters(0).r.bits.last.peek().litValue.toString(16))
        dut.clock.step(1)
        dut.io.masters(0).r.ready.poke(true.B)
        //dut.io.masters(0).ar.valid.poke(true.B)

        while(!(dut.io.masters(0).r.valid.peek().litToBoolean)){
          dut.clock.step(1)
        }
        println("[Test]: Master 0 observe data 0x" + dut.io.masters(0).r.bits.data.peek().litValue.toString(16)
                + ", Last = " + dut.io.masters(0).r.bits.last.peek().litValue.toString(16))

      }.joinAndStep(dut.clock)

      dut.clock.step(2)
    }
  }
}
