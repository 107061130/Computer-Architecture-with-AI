package acal_lab05.Hw1

import chisel3._
import chisel3.util._

class TrafficLight_p(Ytime:Int, Gtime:Int, Ptime:Int) extends Module{
    val io = IO(new Bundle{
        val P_button = Input(Bool())
        val H_traffic = Output(UInt(2.W))
        val V_traffic = Output(UInt(2.W))
        val P_traffic = Output(UInt(2.W))
        val timer     = Output(UInt(5.W))
    })
    //parameter declaration
    val Off = 0.U
    val Red = 1.U
    val Yellow = 2.U
    val Green = 3.U
    
    val sIdle :: sHGVR :: sHYVR :: sHRVG :: sHRVY :: sPG :: Nil = Enum(6)
    
    //State register
    val state = RegInit(sIdle)
    // record previous state
    val pre = RegInit(sHGVR)
    
    //Counter============================
    val cntMode = WireDefault(0.U(2.W))
    val cntReg = RegInit(0.U(5.W))
    val cntDone = Wire(Bool())
    cntDone := cntReg === 0.U
    
    when(cntDone || (io.P_button && state =/= sPG)){
        when(io.P_button || cntMode === 2.U){
            cntReg := (Ptime-1).U
        }
        .elsewhen(cntMode === 0.U){
            cntReg := (Gtime-1).U
        }.elsewhen(cntMode === 1.U){
            cntReg := (Ytime-1).U
        }
    }.otherwise{
        cntReg := cntReg - 1.U
    }
    
    //Next State Decoder
    switch(state){
        is(sIdle){
            state := sHGVR
        }
        is(sHGVR){
            when(io.P_button) {
                pre := sHGVR
                state := sPG
            }
            .elsewhen(cntDone) {state := sHYVR}
        }
        is(sHYVR){
            when(io.P_button) {
                pre := sHYVR
                state := sPG
            }
            .elsewhen(cntDone) {state := sHRVG}
        }
        is(sHRVG){
            when(io.P_button) {
                pre := sHRVG
                state := sPG
            }
            .elsewhen(cntDone) {state := sHRVY}
        }
        is(sHRVY){
            when(io.P_button) {
                pre := sHRVY
                state := sPG
            }
            .elsewhen(cntDone) {state := sPG}
        }
        is(sPG){
            when(cntDone) {
                state := pre
                pre := sHGVR
            }
        }
    }
    
    //Output Decoder
    //Default statement
    cntMode := 0.U
    io.H_traffic := Off
    io.V_traffic := Off
    io.P_traffic := Off
    
    switch(state){
        is(sHGVR){
            cntMode := 1.U
            io.H_traffic := Green
            io.V_traffic := Red
            io.P_traffic := Red
        }
        is(sHYVR){
            cntMode := 0.U
            io.H_traffic := Yellow
            io.V_traffic := Red
            io.P_traffic := Red
        }
        is(sHRVG){
            cntMode := 1.U
            io.H_traffic := Red
            io.V_traffic := Green
            io.P_traffic := Red
        }
        is(sHRVY){
            cntMode := 2.U
            io.H_traffic := Red
            io.V_traffic := Yellow
            io.P_traffic := Red
        }
        is(sPG){
            when (pre === sHGVR || pre === sHRVG) {cntMode := 0.U}
            .otherwise {cntMode := 1.U}
            io.H_traffic := Red
            io.V_traffic := Red
            io.P_traffic := Green
        }
    }
    io.timer := cntReg
}