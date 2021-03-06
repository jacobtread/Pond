package me.jacobtread.pond.instr

import me.jacobtread.pond.util.EncoderBuffer
import me.jacobtread.pond.util.KeyReference
import me.jacobtread.pond.util.Keyboard
import me.jacobtread.pond.util.RobotTranslation
import me.jacobtread.pond.instr.Instruction.Companion.NULL_BYTE
import me.jacobtread.pond.instr.Instruction.Companion.UNSET_BYTE
import java.awt.Robot
import javax.swing.KeyStroke

class ComboInstruction(private val keys: List<String>) : Instruction {

    override fun execute(roboto: Robot) {
        val trueKeys: HashMap<String, Int> = HashMap()
        for (it in keys) {
            val stroke: KeyStroke = KeyStroke.getKeyStroke(RobotTranslation[it.uppercase()])
            roboto.keyPress(stroke.keyCode)
            trueKeys[it] = stroke.keyCode
            println("Pressed ${stroke.keyCode}")
        }
        for (it in keys) {
            trueKeys[it]?.let {
                roboto.keyRelease(it)
                println("Released $it")
            }
        }
    }

    override fun encode(output: EncoderBuffer) {
        var modifier: Int = UNSET_BYTE
        var keyCode: Int = UNSET_BYTE
        for (key in keys) {
            val ref: KeyReference = Keyboard.get(key) ?: continue
            modifier = if (modifier == UNSET_BYTE) {
                ref.mod
            } else modifier or ref.mod
            keyCode = ref.keyCode
        }
        if (keyCode == UNSET_BYTE) keyCode = NULL_BYTE
        if (modifier == UNSET_BYTE) modifier = NULL_BYTE
        output += keyCode
        output += modifier
    }

    override fun toString(): String {
        return "COMBO $keys"
    }

}