package me.jacobtread.pond.instr

import me.jacobtread.pond.util.EncoderBuffer
import me.jacobtread.pond.util.KeyReference
import me.jacobtread.pond.util.Keyboard
import me.jacobtread.pond.util.RobotTranslation
import me.jacobtread.pond.instr.Instruction.Companion.NULL_BYTE
import java.awt.Robot
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

class StringInstruction(private val text: String) : Instruction {

    var keyWait: Int = 0

    override fun execute(roboto: Robot) {
        for (k: Char in text.toCharArray()) {
            val key: String = k.toString()
            val uppercase: Boolean = RobotTranslation.REQUIRES_SHIFT.matches(key)
            println("$k $key $uppercase")
            if (keyWait > 0) {
                roboto.delay(keyWait)
            }
            if (uppercase) roboto.keyPress(KeyEvent.VK_SHIFT)
            val value: String = key.uppercase()
            val stroke: KeyStroke? = KeyStroke.getKeyStroke(RobotTranslation[value])
            if (stroke != null) {
                roboto.keyPress(stroke.keyCode)
                roboto.keyRelease(stroke.keyCode)
            }
            if (uppercase) roboto.keyRelease(KeyEvent.VK_SHIFT)
        }
    }

    override fun encode(output: EncoderBuffer) {
        output.startCount()
        for (char in text.toCharArray()) {
            val ref: KeyReference? = Keyboard.get(char)
            if (ref != null) {
                output += ref.keyCode
                output += ref.mod
                if (keyWait > 0) {
                    WaitInstruction(keyWait).encode(output)
                }
            } else {
                output += NULL_BYTE
            }
        }
        if (output.writes % 2 != 0) {
            output += NULL_BYTE
        }
    }

    override fun toString(): String {
        return "STRING $text"
    }

}