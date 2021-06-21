package me.jacobtread.pond.ui

import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.KeyStroke

class KeyboardShortcuts(val editor: PondEditor) {

    init {
        val inputMap = editor.textArea.inputMap
        val ctrlShiftS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK)
        inputMap.put(ctrlShiftS, "saveAs")
        val ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)
        inputMap.put(ctrlS, "save")
        val ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK)
        inputMap.put(ctrlO, "open")
        val ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK)
        inputMap.put(ctrlN, "new")
        val ctrlH = KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK)
        inputMap.put(ctrlH, "help")
        val altC = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK)
        inputMap.put(altC, "compile")
        val f5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)
        inputMap.put(f5, "run")

        val actionMap = editor.textArea.actionMap
        actionMap.put("saveAs", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                editor.menuBar.save(null)
            }
        })

        actionMap.put("save", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                editor.menuBar.save(editor.path)
            }
        })

        actionMap.put("new", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                editor.menuBar.new()
            }
        })
        actionMap.put("open", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                editor.menuBar.open()
            }
        })
        actionMap.put("help", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                editor.menuBar.help()
            }
        })
        actionMap.put("compile", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                editor.menuBar.export()
            }
        })

        actionMap.put("run", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                editor.menuBar.testRun()
            }
        })
    }

}