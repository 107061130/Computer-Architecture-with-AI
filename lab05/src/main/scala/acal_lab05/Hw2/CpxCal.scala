package acal_lab05.Hw2

import chisel3._
import chisel3.util._

class CpxCal extends Module{
    val io = IO(new Bundle{
    val key_in = Input(UInt(4.W))
    val value = Output(Valid(UInt(128.W)))
    })

    // store input string
    val store = RegInit(VecInit(Seq.fill(160)(0.U(4.W))))
    val length = RegInit(0.U(8.W))
    val index = RegInit(0.U(8.W))

    // number stack and it's pointer
    val num_stk = RegInit(VecInit(Seq.fill(16)(0.U(128.W))))
    val num_sp = RegInit(0.U(4.W))

    // operator stack and it's pointer
    val op_stk = RegInit(VecInit(Seq.fill(64)(0.U(2.W))))
    val op_sp = RegInit(0.U(6.W))

    // State and State Transition Variable
    val sStack :: sCaculate1 :: sCaculate2 :: sFinish :: Nil = Enum(4)
    val state = RegInit(sStack)

    val store_end = WireDefault(false.B)
    store_end := io.key_in === 15.U
    
    val stack_end = WireDefault(false.B)
    stack_end := index > length

    // to ensure store has already read two element, (|| store_end) to prevent (input length == 1) case 
    val ready = WireDefault(false.B)
    ready := (length > 1.U || store_end)

    val go_caculate1 = WireDefault(false.B)
    val go_caculate2 = WireDefault(false.B)
    val caculate1_end = WireDefault(false.B)
    val caculate2_end = WireDefault(false.B)

    go_caculate1 := false.B
    go_caculate2 := false.B
    caculate1_end := false.B
    caculate2_end := false.B

    // Constant Declaration
    val add = 0.U
    val sub = 1.U
    val mul = 2.U
    val l_paren = 3.U

    val priority = VecInit(Seq.fill(4)(0.U(2.W)))
    priority(add) := 1.U
    priority(sub) := 1.U
    priority(mul) := 2.U
    priority(l_paren) := 0.U

    // Next State Decoder
    switch(state){
        // read store input
        is(sStack) {
            when(stack_end) {state := sFinish}
            .elsewhen(go_caculate1) {state := sCaculate1}
            .elsewhen(go_caculate2) {state := sCaculate2}
        }
        // right parentheses case 
        is(sCaculate1){
            when(caculate1_end) {state := sStack}
        }
        // operator priority case 
        is(sCaculate2){
            when(caculate2_end) {state := sStack}
        }
        is(sFinish){
            state := sStack
        }
    }
    //==================================================
    val neg = RegInit(false.B)
    val cur_op = RegInit(0.U(2.W))

    // record input string to store 
    when(!store_end){
        store(length) := io.key_in
        length := length + 1.U
    }
    
    when(ready && state === sStack){
        // traverse to store end, caculate remain number in stack 
        when (index === length) {
            cur_op := l_paren
            go_caculate2 := true.B
        }
        // digits
        .elsewhen (store(index) < 10.U) {
            val src = (num_stk(num_sp)<<3.U) + (num_stk(num_sp)<<1.U) + store(index)
            num_stk(num_sp) := src
            // if it is last bit of number
            when(index+1.U === length || store(index+1.U) >= 10.U) { 
                when(neg) {num_stk(num_sp) := ~src + 1.U}
                num_sp := num_sp + 1.U
            }
        }
        // operators
        .otherwise {
            // negative case 
            when(store(index) === 13.U && store(index + 1.U) === 11.U) {
                neg := true.B
            }
            // left parentheses
            .elsewhen(store(index) === 13.U) {
                op_stk(op_sp) := l_paren
                op_sp := op_sp + 1.U
            }
            // right parentheses
            .elsewhen(store(index) === 14.U) {
                // if negative, only reset neg
                when(neg) {neg := false.B}
                // else go caculate first
                .otherwise { go_caculate1 := true.B}
            }
            // +, -, x but not negative
            .elsewhen (!neg) {
                val op = MuxLookup(store(index),0.U,Seq(
                    10.U -> add,
                    11.U -> sub,
                    12.U -> mul
                ))
                val opstk_top = op_stk(op_sp - 1.U)
                // if priority pass, go caculate first
                when (op_sp > 0.U && priority(opstk_top) >= priority(op)) {
                    go_caculate2 := true.B
                    cur_op := op
                }
                .otherwise {
                    op_stk(op_sp) := op
                    op_sp := op_sp + 1.U
                }
            }
        }
        index := index + 1.U
    }

    // right parentheses case
    when(state === sCaculate1){
        val opstk_top = op_stk(op_sp - 1.U)
        when(opstk_top =/= l_paren) {
            val src2 = num_stk(num_sp - 1.U)
            val src1 = num_stk(num_sp - 2.U)
            num_stk(num_sp - 2.U) := MuxLookup(opstk_top,0.U,Seq(
                add -> (src1 + src2),
                sub -> (src1 - src2),
                mul -> (src1 * src2)
            ))
            num_stk(num_sp - 1.U) := 0.U
            num_sp := num_sp - 1.U
        }
        .otherwise {
            caculate1_end := true.B
        }
        op_sp := op_sp - 1.U
    }

    // operator priority case
    when(state === sCaculate2){
        val opstk_top = op_stk(op_sp - 1.U)
        when(op_sp > 0.U && priority(opstk_top) >= priority(cur_op)) {
            val src2 = num_stk(num_sp - 1.U)
            val src1 = num_stk(num_sp - 2.U)
            num_stk(num_sp - 2.U) := MuxLookup(opstk_top,0.U,Seq(
                add -> (src1 + src2),
                sub -> (src1 - src2),
                mul -> (src1 * src2)
            ))
            num_stk(num_sp - 1.U) := 0.U
            num_sp := num_sp - 1.U
            op_sp := op_sp - 1.U
        }
        .otherwise {
            op_stk(op_sp) := cur_op
            op_sp := op_sp + 1.U
            caculate2_end := true.B
        }
    }

    // Finish and Reset
    when(state === sFinish){
        for (i <- 0 until 16) { num_stk(i) := 0.U }
        neg := false.B
        num_sp := 0.U
        op_sp := 0.U
        index := 0.U
        length := 0.U
    }
    
    io.value.valid := Mux(state === sFinish,true.B,false.B)
    io.value.bits := num_stk(0)
}
