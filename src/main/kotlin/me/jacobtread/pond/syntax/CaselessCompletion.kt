package me.jacobtread.pond.syntax

import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.Completion
import org.fife.ui.autocomplete.CompletionProvider
import javax.swing.text.JTextComponent

class CaselessCompletion(provider: CompletionProvider, text: String, replacement: String, desc: String?) :
    BasicCompletion(provider, text, replacement, desc) {

    var entered: String = ""

    override fun getReplacementText(): String {
        if (entered.isNotEmpty()) {
            val first = entered[0]
            return if (first.isUpperCase()) {
                super.getReplacementText().uppercase()
            } else {
                super.getReplacementText().lowercase()
            }
        }
        return super.getReplacementText()
    }

    override fun getAlreadyEntered(comp: JTextComponent?): String {
        val entered = super.getAlreadyEntered(comp)
        this.entered = entered
        return entered
    }

}