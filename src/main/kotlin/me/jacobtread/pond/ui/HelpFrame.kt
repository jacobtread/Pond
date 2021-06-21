package me.jacobtread.pond.ui

import me.jacobtread.pond.util.Icons
import java.awt.Color
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.text.AttributeSet
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext

class HelpFrame : JFrame() {

    init {
        val pane = JTextPane()
        val keywordColor: Color = Color.decode("#26b79d")
        val variableColor: Color = Color.decode("#acc425")
        val stringColor: Color = Color.decode("#6add83")
        val macroColor: Color = Color.decode("#E0E2E4")
        val numberColor: Color = Color.decode("#b7e25f")

        colorText(pane, "STRING\n", keywordColor, 16, true)
        colorText(pane, "The string keyword is used to type out lots of\n", Color.LIGHT_GRAY)
        colorText(pane, "it can be used like so", Color.LIGHT_GRAY)
        colorText(pane, "\nSTRING", keywordColor, bold = true)
        colorText(pane, " {MESSAGE}", Color.LIGHT_GRAY)

        colorText(pane, "\n\nWAIT\n", keywordColor, 16, true)
        colorText(pane, "The wait keyword is used to set the amount of\n", Color.LIGHT_GRAY)
        colorText(pane, "milliseconds to wait before running the next\n", Color.LIGHT_GRAY)
        colorText(pane, "instruction it can be used like so", Color.LIGHT_GRAY)
        colorText(pane, "\nWAIT", keywordColor, bold = true)
        colorText(pane, " {DELAY}", numberColor)

        colorText(pane, "\n\nSET\n", keywordColor, 16, true)
        colorText(pane, "The set keyword is used to set variables that\n", Color.LIGHT_GRAY)
        colorText(pane, "can then be accessed later on in the code\n", Color.LIGHT_GRAY)
        colorText(pane, "this can be done like so", Color.LIGHT_GRAY)
        colorText(pane, "\nSET", keywordColor, bold = true)
        colorText(pane, " {NAME} ", variableColor)
        colorText(pane, "{VALUE}\n", stringColor)
        colorText(pane, "It can then be accessed using\n", Color.LIGHT_GRAY)
        colorText(pane, "${'$'}NAME", variableColor)

        colorText(pane, "\n\nMACRO\n", keywordColor, 16, true)
        colorText(pane, "The macro keyword is used to defined reusable\n", Color.LIGHT_GRAY)
        colorText(pane, "sets of code with arguments that can be used\n", Color.LIGHT_GRAY)
        colorText(pane, "throught the script this can be done like so", Color.LIGHT_GRAY)
        colorText(pane, "\nMACRO", keywordColor, bold = true)
        colorText(pane, " {NAME} ", macroColor)
        colorText(pane, " {ARG1} {ARG2}\n", variableColor)
        colorText(pane, "  {INSTRUCTIONS}\n", stringColor)
        colorText(pane, "It can then be accessed using\n", Color.LIGHT_GRAY)
        colorText(pane, ":NAME", macroColor)

        colorText(pane, "\n\nDEFAULT_WAIT\n", keywordColor, 16, true)
        colorText(pane, "The wait keyword is used to set the amount of\n", Color.LIGHT_GRAY)
        colorText(pane, "milliseconds to wait before running the next\n", Color.LIGHT_GRAY)
        colorText(pane, "instruction it can be used like so", Color.LIGHT_GRAY)
        colorText(pane, "\nWAIT", keywordColor, bold = true)
        colorText(pane, " {DELAY}", numberColor)

        colorText(pane, "\n\nSTRING_WAIT\n", keywordColor, 16, true)
        colorText(pane, "The wait keyword is used to set the amount of\n", Color.LIGHT_GRAY)
        colorText(pane, "milliseconds to wait before running the next\n", Color.LIGHT_GRAY)
        colorText(pane, "instruction it can be used like so", Color.LIGHT_GRAY)
        colorText(pane, "\nWAIT", keywordColor, bold = true)
        colorText(pane, " {DELAY}", numberColor)

        colorText(pane, "\n\nREPEAT\n", keywordColor, 16, true)
        colorText(pane, "The repeat keyword is used to reapeat a block\n", Color.LIGHT_GRAY)
        colorText(pane, "of instructions nth times and can be indented this\n", Color.LIGHT_GRAY)
        colorText(pane, "instruction it can be used like so", Color.LIGHT_GRAY)
        colorText(pane, "\nREPEAT", keywordColor, bold = true)
        colorText(pane, " {DELAY}\n", numberColor)
        colorText(pane, "  {INSTRUCTIONS}\n", stringColor)
        pane.isEditable = false
        val scrollPane = JScrollPane(pane)
        scrollPane.preferredSize = Dimension(500, 500)
        pane.caretPosition = 0
        contentPane = scrollPane
        iconImage = Icons["logo-x120"]
        title = "Syntax Help"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        pack()
    }


    private fun colorText(pane: JTextPane, message: String, color: Color, size: Int = 12, bold: Boolean = false) {
        val styleContext: StyleContext = StyleContext.getDefaultStyleContext()
        var attributeSet: AttributeSet =
            styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color)
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontSize, size)
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.Bold, bold)
        val length = pane.document.length
        pane.caretPosition = length
        pane.setCharacterAttributes(attributeSet, false)
        pane.replaceSelection(message)
    }

}