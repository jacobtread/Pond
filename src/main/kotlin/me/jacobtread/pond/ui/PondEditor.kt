package me.jacobtread.pond.ui

import me.jacobtread.pond.syntax.PondCompletionProvider
import me.jacobtread.pond.util.Icons
import me.jacobtread.pond.util.Keyboard
import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.autocomplete.CompletionProvider
import org.fife.ui.rsyntaxtextarea.*
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.io.BufferedInputStream
import java.io.StringReader
import java.nio.file.Path
import javax.swing.JFrame
import javax.swing.JPanel

class PondEditor : JFrame() {

    init {
        Keyboard._load()
    }

    var path: Path? = null
    val textArea: RSyntaxTextArea = RSyntaxTextArea(20, 50).apply {
        val tokenMakerFactory = TokenMakerFactory.getDefaultInstance() as AbstractTokenMakerFactory
        tokenMakerFactory.putMapping("text/pond", "me.jacobtread.pond.syntax.PondTokenConverter")
        syntaxEditingStyle = "text/pond"
        tabSize = 2
        isCodeFoldingEnabled = false
        tabsEmulated = true
        val theme = Theme(this).apply {
            baseFont = baseFont.deriveFont(24f)
            bgColor = Color.decode("#333333")
            caretColor = Color.decode("#C1CBC2")
            selectionBG = Color.decode("#404E51")
            useSelectionFG = false
            selectionRoundedEdges = false
            lineNumberColor = Color.decode("#81969A")
            lineNumberFontSize = 30
            activeLineRangeColor = Color.decode("#3399ff")
            currentLineHighlight = Color.decode("#333333")
            scheme = SyntaxScheme(baseFont, false).apply {
                getStyle(TokenTypes.RESERVED_WORD).apply {
                    foreground = Color.decode("#26B79D"); font = font.deriveFont(Font.BOLD or Font.ITALIC)
                }
                getStyle(TokenTypes.RESERVED_WORD_2).apply {
                    foreground = Color.decode("#2689B7"); font = font.deriveFont(Font.BOLD)
                }
                getStyle(TokenTypes.VARIABLE).apply {
                    foreground = Color.decode("#acc425"); underline = true
                }
                getStyle(TokenTypes.ERROR_IDENTIFIER).apply {
                    foreground = Color.decode("#f25f52"); underline = true
                }
                getStyle(TokenTypes.FUNCTION).foreground = Color.decode("#E0E2E4")
                getStyle(TokenTypes.OPERATOR).foreground = Color.decode("#E8E2B7")
                getStyle(TokenTypes.COMMENT_EOL).foreground = Color.decode("#66747B")
                getStyle(TokenTypes.COMMENT_MULTILINE).foreground = Color.decode("#66747B")
                getStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.decode("#6add83")
                getStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT).foreground = Color.decode("#b7e25f")
                getStyle(TokenTypes.WHITESPACE).foreground = Color.decode("#E0E2E4")
            }
        }
        theme.apply(this)
        val completionProvider = PondCompletionProvider()
        val autoCompletion = AutoCompletion(completionProvider)
        autoCompletion.install(this)
    }

    var text: String = textArea.text
        get() = textArea.text
        set(value) {
            textArea.read(StringReader(value), null)
            textArea.caretPosition = 0
            textArea.discardAllEdits()
            textArea.convertTabsToSpaces()
            field = value
        }

    init {

        val root = JPanel(BorderLayout())
        val scrollPane = RTextScrollPane(textArea)

        root.add(EditorMenuBar(this), BorderLayout.PAGE_START)
        root.add(scrollPane, BorderLayout.CENTER)

        contentPane = root
        iconImage = Icons["logo-x120"]
        title = "Pond Editor"
        defaultCloseOperation = EXIT_ON_CLOSE
        pack()
    }

    fun isEmpty(): Boolean {
        return text.isEmpty()
    }

}