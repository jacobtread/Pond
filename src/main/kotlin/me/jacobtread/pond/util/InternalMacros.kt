package me.jacobtread.pond.util

import me.jacobtread.pond.instr.ComboInstruction
import me.jacobtread.pond.instr.Instruction
import me.jacobtread.pond.instr.StringInstruction
import me.jacobtread.pond.instr.WaitInstruction
import me.jacobtread.pond.util.RobotTranslation

object InternalMacros {

    private val VALUES: Map<String, List<Instruction>> = mapOf(
        "cmd" to listOf(
            WaitInstruction(500),
            ComboInstruction(listOf("GUI", "R")),
            WaitInstruction(1000),
            StringInstruction("cmd.exe"),
            WaitInstruction(500),
            ComboInstruction(listOf("ENTER")),
            WaitInstruction(1000),
        )
    )
    operator fun get(key: String): List<Instruction> {
        return VALUES[key]!!
    }

    operator fun contains(key: String): Boolean {
        return VALUES.containsKey(key)
    }

}