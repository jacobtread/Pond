package me.jacobtread.pond.instr

import me.jacobtread.pond.util.EncoderBuffer
import java.awt.Robot

interface Instruction {

    fun execute(roboto: Robot)

    fun encode(output: EncoderBuffer)

    companion object {
        const val UNSET_BYTE: Int = -1
        const val NULL_BYTE: Int = 0x00
    }

}