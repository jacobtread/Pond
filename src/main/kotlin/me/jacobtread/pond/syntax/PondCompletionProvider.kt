package me.jacobtread.pond.syntax

import me.jacobtread.pond.util.Keyboard
import org.fife.ui.autocomplete.*

class PondCompletionProvider : DefaultCompletionProvider() {

    init {
        basic("WAIT", "Wait the provided delay")
        basic("SET", "Set a variable")
        basic("STRING", "Type all the characters")
        basic("STRING_WAIT", "Set the global typing delay")
        basic("DEFAULT_WAIT", "Set the global wait delay")
        addCompletion(ShorthandCompletion(this, "Exec", """
            MACRO EXEC COMMAND
              WAIT 500
              GUI R
              WAIT 1000
              STRING cmd
              WAIT 500
              ENTER
              WAIT 1000
              STRING ${'$'}COMMAND
              WAIT 100
              ENTER
        """.trimIndent()))
        Keyboard.keys().forEach {
            basic(it)
        }
    }

    private fun basic(value: String, desc: String? = null) {
        addCompletion(BasicCompletion(this, value, desc))
    }

}