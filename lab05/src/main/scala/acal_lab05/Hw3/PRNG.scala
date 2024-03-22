package acal_lab05.Hw3

import chisel3._
import chisel3.util._

object LfsrTaps {
    def apply(size: Int): Seq[Int] = {
        size match {
            // Seqp[Int] means the taps in LFSR
            case 16 => Seq(14,13,11)      //p(x) = x^16+x^14+x^13+x^11+1
            case _ => throw new Exception("No LFSR taps stored for requested size")
        }
    }
}

class PRNG(seed:Int) extends Module{
    val io = IO(new Bundle{
        val gen = Input(Bool())
        val puzzle = Output(Vec(4,UInt(4.W)))
        val ready = Output(Bool())
    })
    
    // Initialize the shift register with seed
    val binarySeed = seed.toBinaryString.reverse.padTo(16, '0').reverse
    val shiftReg = RegInit(VecInit(binarySeed.map(_ == '1').map(_.B)))
    
    val sIdle :: sGen :: sConvert1 :: sConvert2 :: sCheck :: sEnd :: Nil = Enum(6)
    val state = RegInit(sIdle)

    val result = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))
    val pass = RegInit(1.U(2.W))
    val Combination_Table = RegInit(VecInit(Seq.fill(10000)(false.B)))
    
    switch(state){
        is(sIdle){
            when(io.gen) { state := sGen }
        }
        is(sGen) {
            state := sConvert1
            pass := 1.U
        }
        is(sConvert1) {
            state := sConvert2
        }
        is(sConvert2) {
            state := sCheck
        }
        is(sCheck) {
            when(pass === 0.U) {state := sGen}
            .elsewhen(pass === 2.U) {state := sEnd}
        }
        is(sEnd){
            state := sIdle
        }
    }

    // generate random 16 bits
    when(state === sGen) {
        //Barrel Shift Register
        (shiftReg.zipWithIndex).map{
            case(sr,i) => sr := shiftReg((i+1)%16)
        }
        //Fibonacci LFSR
        shiftReg(15) := (LfsrTaps(16).map(x=>shiftReg(16-x)).reduce(_^_)) ^ shiftReg(0)
    }
    
    // convert it to four 4 bits UInt
    when(state === sConvert1) { 
        for (i <- 0 until 4) {
            result(i) := Cat(shiftReg.slice(i << 2, (i + 1) << 2).reverse)
        }
    }
    
    // handle > 9 situation
    when(state === sConvert2) {
        for (i <- 0 until 4) {
            when(result(i) >= 10.U) { result(i) := result(i) - 10.U }
        }
    }

    // check repeated number and repeated combination
    when(state === sCheck) { 
        pass := 2.U
        // repeated number
        for (i <- 0 until 3) {
            for (j <- i + 1 until 4) {
                when (result(i) === result(j)) { pass := 0.U }
            }
        }
         // repeated combination
        when(Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3))) { pass := 0.U }
        Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3)) := true.B
    }
    
    io.puzzle := result
    io.ready := state === sEnd
}
