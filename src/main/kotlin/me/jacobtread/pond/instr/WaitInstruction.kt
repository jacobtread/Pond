package me.jacobtread.pond.instr

import me.jacobtread.pond.util.EncoderBuffer
import me.jacobtread.pond.instr.Instruction.Companion.NULL_BYTE
import java.awt.Robot

class WaitInstruction(val delay: Int) : Instruction {

    override fun execute(roboto: Robot) {
        roboto.delay(delay)
    }

    override fun encode(output: EncoderBuffer) {
        var unWritten: Int = delay
        while (true) {
            output += NULL_BYTE
            if (unWritten >= 255) {
                output += 0xFF
                unWritten -= 255
            } else {
                output += unWritten
                break
            }
        }
    }

    override fun toString(): String {
        return "WAIT $delay"
    }

}