package acal_lab05.Hw3

import chisel3._
import chisel3.util._

class NumGuess(seed:Int = 1) extends Module{
    require (seed > 0 , "Seed cannot be 0")

    val io  = IO(new Bundle{
        val gen = Input(Bool())
        val guess = Input(UInt(16.W))
        val puzzle = Output(Vec(4,UInt(4.W)))
        val ready  = Output(Bool())
        val g_valid  = Output(Bool())
        val A      = Output(UInt(4.W))
        val B      = Output(UInt(4.W))

        //don't care at Hw6-3-2 but should be considered at Bonus
        val s_valid = Input(Bool())
    })
    
     // Initialize the shift register with seed
    val binarySeed = seed.toBinaryString.reverse.padTo(16, '0').reverse
    val shiftReg = RegInit(VecInit(binarySeed.map(_ == '1').map(_.B)))
    
    val sIdle :: sGen :: sConvert1 :: sConvert2 :: sCheck :: sGen_End :: sGuess_Read :: sGuess_Out :: Nil = Enum(8)
    val state = RegInit(sIdle)

    val result = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))
    val pass = RegInit(1.U(2.W))
    val Combination_Table = RegInit(VecInit(Seq.fill(10000)(false.B)))
    val GUESS = RegInit(VecInit(Seq.fill(4)(15.U(4.W))))
    
    val NA = RegInit(0.U(3.W))
    val NB = RegInit(0.U(3.W))
    
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
            .elsewhen(pass === 2.U) {state := sGen_End}
        }
        is(sGen_End){
            state := sGuess_Read
        }
        is(sGuess_Read){
            state := sGuess_Out
        }
        is(sGuess_Out){
            state := sGen_End
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

    // handle repeated number & repeated combination
    when(state === sCheck) { 
        pass := 2.U
        for (i <- 0 until 3) {
            for (j <- i + 1 until 4) {
                when (result(i) === result(j)) { pass := 0.U }
            }
        }
        when(Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3))) { pass := 0.U }
        Combination_Table(result(0)*1000.U + result(1)*100.U + result(2)*10.U + result(3)) := true.B
    }
    
    when(state === sGuess_Read) { 
        // count A
        val A_compare = Wire(Vec(4, UInt(1.W)))
        for (i <- 0 until 4) {
            A_compare(i) := result(i) === io.guess((i << 2) + 3, i << 2)
        }
        NA := A_compare.reduce(_ +& _)
        
        // count B
        val B_compare = Wire(Vec(16, UInt(1.W)))
        for (i <- 0 until 4) {
            for (j <- 0 until 4) {
                B_compare((i << 2) + j) := result(i) === io.guess((j << 2) + 3, j << 2)
            }
        }
        NB := B_compare.reduce(_ +& _)
    }
    
    io.puzzle := result
    io.ready := state === sGen_End
    
    io.g_valid  := state === sGuess_Out
    io.A      := NA
    io.B      := NB
}