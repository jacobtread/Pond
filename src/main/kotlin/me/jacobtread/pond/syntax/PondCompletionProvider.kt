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
        basic("REPEAT", "Repeating code block")
        basic("MACRO", "Macro block start")
        Keyboard.keys().forEach {
            basic(it)
        }
    }

    private fun basic(value: String, desc: String? = null) {
        addCompletion(CaselessCompletion(this, value, value, desc))
    }

}