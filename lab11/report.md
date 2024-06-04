NTHU 111064528 張瀚 ACAL 2024 Spring Lab 11
===

## Homework 11-1 SoC Integration with CPU

> Please paste the link close to the parentheses
- [Hw 11-1 Branch link](https://course.playlab.tw/git/111064528/lab11/-/tree/main?ref_type=heads)

1. Revise system configuration and design address mapping scheme(Explain how you design)
![](https://course.playlab.tw/md/uploads/736a4c55-078d-4075-9660-bed7d12585e6.png)


2. Integrate CPU, bus and memory 
* SOC_top intergation
```scala=
object SystemConfig {
        val nMasters: Int = 1
        val nSlaves: Int = 1
        val LocalMemBaseAddr: Int = 0x8000
        val LocalMemSize: Map[String, Int] = Map(
          "Size" -> 1024, // Height x Width
          "Height" -> 64, // The Number of bytes
          "Width" -> 32 // unit: 32 bits
        )
        val LocalMemLatency: Int = 1
        val LocalMemInitFilePath: String = "./src/main/resource/data.hex" // Provide the file path
    }
val cpu = Module(new PipelinedCPU(32, 32))
val im = Module(new InstMem(15))

val bus = Module(
    new AXILiteXBar(
      SystemConfig.nMasters,
      SystemConfig.nSlaves,
      idWidth,
      addrWidth,
      dataWidth,
      Seq(
        (SystemConfig.LocalMemBaseAddr, SystemConfig.LocalMemSize("Size")),
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

// Bus Interface
bus.io.masters(0) <> cpu.io.master
bus.io.slaves(0) <> localMem.io.slave
```

* CPU Master IF
```scala=
/* Master FSM */
// write
val wIdle :: wAddr_Data :: wWaitResp :: Nil = Enum(3)
val writeState = RegInit(wIdle)

switch(writeState) {
    is(wIdle) {
        when (io.DataMem.Mem_W) {
            writeState := wAddr_Data
        }
    } 
    is(wAddr_Data) {
        when (io.master.aw.ready && io.master.w.ready) {
            writeState := wWaitResp
        }
    }

    is(wWaitResp) {
        when(io.master.b.valid) {
            writeState := wIdle
        }
    }
}

when(writeState === wAddr_Data) {
    io.master.aw.valid     := true.B
    io.master.w.valid      := true.B
    io.master.aw.bits.addr := io.DataMem.waddr
    io.master.w.bits.data  := io.DataMem.wdata << write_shift_bit
    io.master.b.ready      := true.B
}

when(writeState === wWaitResp) {
    io.master.b.ready      := true.B
}

// read
val rIdle :: rAddr :: rWaitData :: Nil = Enum(3)
val readState = RegInit(rIdle)

switch(readState) {
    is(rIdle) {
        when (io.DataMem.Mem_R) {
            readState := rAddr
        }
    } 
    is(rAddr) {
        when (io.master.ar.ready) {
            readState := rWaitData
        }
    }

    is(rWaitData) {
        when(io.master.r.valid) {
            readState := rIdle
        }
    }
}

when(readState === rAddr) {
    io.master.ar.valid      := true.B
    io.master.ar.bits.addr  := io.DataMem.raddr
    io.master.r.ready       := true.B
}
when(readState === rWaitData) {
    io.master.r.ready       := true.B
}
```
3. Other Modifications
* Read/Write mask and shift
* Add MUL to CPU
* Change Emulator data memory output format(1 byte a row to 4 bytes)

4. Run the **Emulator/test_code/scalar_Convolution_2D.S** and paste the result(Screenshot)

![](https://course.playlab.tw/md/uploads/921c3446-a88d-4121-80b2-c5546b48dbab.png)


## Homework 11-2 Performance Enhancement Using DMA

> Please paste the link close to the parentheses
- [Hw 11-2 Branch link](https://course.playlab.tw/git/111064528/lab11/-/tree/DMA?ref_type=heads)

1. Revise system configuration and design address mapping scheme(Explain how you design)
![](https://course.playlab.tw/md/uploads/ad3cf654-0371-4684-ae78-6226c759d8de.png)

2. Integrate CPU, bus, memory and dma 
* SOC_top intergation
```scala=
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

// Bus Connection
bus.io.masters(0) <> cpu.io.master
bus.io.masters(1) <> dma.io.master

bus.io.slaves(0) <> dma.io.slave
bus.io.slaves(1) <> localMem.io.slave
bus.io.slaves(2) <> globalMem.io.slave

// dma_hcf signal to cpu to check whether dma is done 
cpu.io.DMA_Hcf := dma.io.Hcf
```
* CPU Master IF
```scala=
switch(readState) {
    is(rIdle) {
        // modified to wait DMA done
        when (io.DataMem.Mem_R && io.DMA_Hcf) {
            readState := rAddr
        }
    } 
    is(rAddr) {
        when (io.master.ar.ready) {
            readState := rWaitData
        }
    }

    is(rWaitData) {
        when(io.master.r.valid) {
            readState := rIdle
        }
    }
}

```
3. Revise software program and generate binary files using emulator(Explain how you revise the program)
```mipsasm=
# DMA
########################
# source addr global
li  t0, 0x10000
sw  t0, 4(x0)

# dest addr local
li  t0, 0x8000
sw  t0, 8(x0)

# CFG
li  t0, 0x04040425
sw  t0, 12(x0)

# enable
li  t0, 1
sw  t0, 0(x0)
########################
```
4. Run the **Emulator/test_code/scalar_Convolution_2D.S** and paste the result(Screenshot)
![](https://course.playlab.tw/md/uploads/c6af7534-5dfe-4914-93c1-1f60423e93d2.png)
![](https://course.playlab.tw/md/uploads/cd552522-efa5-4682-a377-7148cd8031b3.png)



## Homework 11-3 Support and AXI Bus Implemention with Burst Mode
- [Hw 11-3 Branch link](https://course.playlab.tw/git/111064528/lab11/-/tree/burst?ref_type=heads)

1. Upgrade your AXI bus design(use Fixed mode so only use len to represent burst mode)
```scala=
//modified
rlength := Mux(io.slave.ar.valid, io.slave.ar.bits.len, rlength)
```


2. Provide your testbench command. Paste the result here (Screenshot).
command - sbt 'testOnly AXILite.AXILiteXBarBurstTest'
![](https://course.playlab.tw/md/uploads/ec571c14-8f2d-42e1-8952-afd62c282889.png)


3. Modify **Data memory**, and **DMA controller (Interface)** to support **AXI Burst Mode**.
* DMA Master State(DMA FSM is already a burst mode that can move multiple data when it's register is setted. Hence, only do a small improvement on mWriteResp state)
```scala=
// Mater State Controller
switch(mState) {
    is(mIdle) {
      // when the Enable register is set, the DMA starts to issue read request
      when(mmio_enable === 1.U) {
        mState := mReadSend
      }
    }
    is(mReadSend) {
      // When the ARREady signal is asserted, the slave accepts the
      // request and the master will move the the mReadResp state
      // and wait for read response
      when(io.master.ar.ready) {
        mState := mReadResp
      }
    }
    is(mReadResp) {
      // whe tne RValid is assert, the data response returns and
      // DMA starts to write the data to the desitnation, issuing
      // write request
      when(io.master.r.valid) {
        mState := mWriteSend
      }
    }
    is(mWriteSend) {
      // when all the write data are sent, wait for write response
      when(mWriteAddrSent && mWriteDataSent) {
        mState := mWriteResp
      }
    }
    is(mWriteResp) {
      // When receiving write response (BValid is assert), complete the DMA operation and return the mIDLE state
      // modified : not last, keep reading
      when(io.master.b.valid && mReadlast) {
        mState := mIdle
      }.elsewhen(io.master.b.valid && !mReadlast){
        mState := mReadResp
      }
    }
}
```

* DataMem Slave State(only modified read state because DMA write burst is not used)
```scala=
val readLast      = WireDefault(rburst_counter === rlength)

switch(readState) {
    is(sRead) {
      when(io.slave.ar.valid) {
        when(latency.U === 1.U) {
          readState := sResp
        }
        .otherwise {
          readState := sRLatency
        }
      }
    }
    is(sRLatency) {
      when(rLatencyCounter === (latency - 1).U) {
        readState := sRSend //modified
      }
    }
    is(sRSend){
      when(io.slave.r.ready){
        readState := sResp
      }
    }
    is(sResp) {
      when(readLast){ //modified
        readState := sRead
      }.otherwise{
        readState := sRSend
      }
    }
}

when(readState === sRead) {
    io.slave.ar.ready := true.B
    io.slave.r.valid := false.B
    rAddrOffset := ((io.slave.ar.bits.addr - baseAddr.U) & ~(3.U(width.W))) >> 2.U
    readID := io.slave.ar.bits.id

    //modified
    rlength := Mux(io.slave.ar.valid, io.slave.ar.bits.len, rlength)
    rburst_counter := 0.U
}
.elsewhen(readState === sRLatency) {
    rLatencyCounter := rLatencyCounter + 1.U
}
.elsewhen(readState === sResp) {
    rLatencyCounter := 0.U
    io.slave.ar.ready := false.B
    io.slave.r.valid := true.B
    io.slave.r.bits.data := memory(rAddrOffset)
    io.slave.r.bits.id := readID
    io.slave.r.bits.resp := 0.U
    //modified====================
    rburst_counter := rburst_counter + 1.U
    // next row of memory
    rAddrOffset := rAddrOffset + 4.U
    //============================
}
```

3. Do performance analysis to explain why the AXI Burst mode helps the performance.
 
![](https://course.playlab.tw/md/uploads/d8ec066d-4603-48f4-86c8-eb433b234386.png)
![](https://course.playlab.tw/md/uploads/5b71f9f0-c787-428a-a398-581219df25cb.png)


## Homework 11-4 Performance Analysis and Comparison

Compare the performance in HW11-1 and HW11-3 and tell us your comments on the comparison.
* Do you think the results are reasonable? Does it match your expectation?
    * 11-3 cost more cycle than 11-1 because it need to access global memory.
    * I expected 11-3(burst mode) will run faster than 11-2 but it doesn't. Maybe the reason is the extra state in Data Memory read state.



