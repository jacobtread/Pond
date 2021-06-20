package me.jacobtread.pond.instr

import me.jacobtread.pond.util.EncoderBuffer
import java.awt.Robot

open class EmptyInstruction : Instruction {
    override fun execute(roboto: Robot) {
    }

    override fun encode(output: EncoderBuffer) {
    }
}