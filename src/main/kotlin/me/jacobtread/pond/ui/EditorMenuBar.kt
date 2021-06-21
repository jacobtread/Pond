package me.jacobtread.pond.ui

import me.jacobtread.pond.instr.*
import me.jacobtread.pond.parse.PondParser
import me.jacobtread.pond.util.EncoderBuffer
import me.jacobtread.pond.util.Icons
import me.jacobtread.pond.util.icon
import java.awt.Color
import java.awt.Robot
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.*
import javax.swing.filechooser.FileFilter

class EditorMenuBar(private val editor: PondEditor) : JMenuBar() {

    private var running: Boolean = false
    private val pondFileChooser = JFileChooser(Paths.get("").toFile()).apply {
        fileFilter = object : FileFilter() {
            override fun accept(f: File?): Boolean {
                f ?: return false
                return if (f.isDirectory) {
                    false
                } else {
                    f.name.lowercase().endsWith(".pond")
                }
            }

            override fun getDescription(): String {
                return "Pond Script files (*.pond)"
            }

        }
    }

    private val binFileChooser = JFileChooser(Paths.get("").toFile()).apply {
        fileFilter = object : FileFilter() {
            override fun accept(f: File?): Boolean {
                f ?: return false
                return if (f.isDirectory) {
                    false
                } else {
                    f.name.lowercase().endsWith(".bin")
                }
            }

            override fun getDescription(): String {
                return "Compiled pond files (*.bin)"
            }

        }
    }

    private val saveItem = JMenuItem("Save", Icons["save"].icon()).apply {
        isEnabled = false // Save button disabled until a file gets opened
        addActionListener { save(editor.path) } // Add a click listener
    }

    private val runButton = JButton(Icons["play"].icon()).apply {
        background = Color(0, 0, 0, 0) // Transparent
        border = BorderFactory.createEmptyBorder(5, 20, 5, 20) // Adding some padding
        isFocusable = false // Never gain focus to prevent typing start loop
        addActionListener { testRun() } // Add click action listener
    }

    init {
        add(JMenu("File").apply {
            add(JMenuItem("New File", Icons["create"].icon()).apply {
                addActionListener {
                    saveIfOpen()
                    saveItem.isEnabled = false
                    editor.path = null
                    editor.text = ""
                }
            })
            add(JMenuItem("Open File", Icons["folder"].icon()).apply {
                addActionListener {
                    saveIfOpen()
                    val result: Int = pondFileChooser.showOpenDialog(this@EditorMenuBar)
                    if (result == JFileChooser.APPROVE_OPTION) {
                        val file: File = pondFileChooser.selectedFile
                        editor.path = file.toPath()
                        saveItem.isEnabled = true
                        editor.path?.let {
                            if (Files.exists(it)) {
                                if (Files.isDirectory(it)) {
                                    JOptionPane.showMessageDialog(
                                        null,
                                        "Cannot open directories",
                                        "Unable to open file",
                                        JOptionPane.ERROR_MESSAGE
                                    )
                                } else {
                                    try {
                                        editor.text = String(Files.readAllBytes(it), StandardCharsets.UTF_8)
                                    } catch (e: IOException) {
                                        JOptionPane.showMessageDialog(
                                            null,
                                            "$e",
                                            "Unable to open file",
                                            JOptionPane.ERROR_MESSAGE
                                        )
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(
                                    null,
                                    "Failed to open file that doesn't exist",
                                    "Unable to open file",
                                    JOptionPane.ERROR_MESSAGE
                                )
                            }
                        }
                    }
                }
            })

            add(saveItem)

            add(JMenuItem("Save As", Icons["save"].icon()).apply {
                addActionListener { save(null) }
            })
            add(JMenuItem("Compile", Icons["export"].icon()).apply {
                addActionListener { export() }
            })
        })
        val sideBox: Box = Box.createHorizontalBox()
        sideBox.add(JMenuItem("Help").apply {
            addActionListener {
                SwingUtilities.invokeLater {
                    HelpFrame().isVisible = true
                }
            }
        })
        sideBox.add(runButton)
        add(Box.createHorizontalGlue())
        add(sideBox)
    }

    private fun saveIfOpen() {
        if (!editor.isEmpty()) {
            val result: Int = JOptionPane.showConfirmDialog(
                null, "Would you like to save the current file?"
            )
            if (result == JOptionPane.OK_OPTION) {
                save(editor.path)
            }
        }
    }

    private fun save(path: Path?) {
        if (path == null) {
            val result: Int = pondFileChooser.showSaveDialog(this@EditorMenuBar)
            if (result == JFileChooser.APPROVE_OPTION) {
                val file: File = pondFileChooser.selectedFile
                var newPath = file.toPath()
                val fileName: String = newPath.fileName.toString()
                if (!fileName.endsWith(".pond")) {
                    newPath = newPath.parent.resolve("$fileName.pond")
                }
                editor.path = newPath
                saveItem.isEnabled = true
                try {
                    Files.newBufferedWriter(newPath).use { writer ->
                        editor.textArea.write(writer)
                    }
                } catch (e: IOException) {
                    JOptionPane.showMessageDialog(
                        null,
                        "$e",
                        "Unable to save file",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        } else {
            saveItem.isEnabled = true
            try {
                Files.newBufferedWriter(editor.path).use {
                    editor.textArea.write(it)
                }
            } catch (e: IOException) {
                JOptionPane.showMessageDialog(
                    null,
                    "$e",
                    "Unable to save file",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun export() {
        val result: Int = binFileChooser.showSaveDialog(this@EditorMenuBar)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file: File = binFileChooser.selectedFile
            var path = file.toPath()
            val fileName: String = path.fileName.toString()
            if (!fileName.endsWith(".bin")) {
                path = path.parent.resolve("$fileName.bin")
            }
            val text: String = editor.text
            Thread {
                val parser = PondParser()
                try {
                    parser.parse(text)
                    val buffer = EncoderBuffer()
                    val instructions: List<Instruction> = parser.result()
                    var defaultWait = 0
                    var stringWait = 0
                    var index = 0
                    while (index < instructions.size) {
                        val inst: Instruction = instructions[index]
                        if (defaultWait > 0) WaitInstruction(defaultWait).encode(buffer)
                        if (stringWait > 0 && inst is StringInstruction) inst.keyWait = stringWait
                        inst.encode(buffer)
                        if (inst is DefaultWaitInstruction) defaultWait = inst.delay
                        if (inst is StringWaitInstruction) stringWait = inst.delay
                        index++
                    }
                    val output: ByteArray = buffer.bytes()
                    Files.write(path, output)
                    SwingUtilities.invokeLater {
                        JOptionPane.showMessageDialog(
                            null,
                            "File exported",
                            "Sucess",
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    }
                } catch (e: Exception) {
                    SwingUtilities.invokeLater {
                        JOptionPane.showMessageDialog(
                            null,
                            e.javaClass.simpleName + ": " + e.message,
                            "Failed to export",
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                    e.printStackTrace()
                }
            }.apply { name = "Export Thread"; start() }
        }
    }

    private fun testRun() {
        if (running) {
            running = false
            runButton.icon = Icons["play"].icon()
        } else {
            running = true
            runButton.icon = Icons["stop"].icon()
            val text: String = editor.text
            Thread {
                val parser = PondParser()
                try {
                    parser.parse(text)
                    val robot = Robot()
                    val instructions: List<Instruction> = parser.result()
                    var defaultWait = 0
                    var stringWait = 0
                    var index = 0
                    while (index < instructions.size && running) {
                        val inst: Instruction = instructions[index]
                        if (defaultWait > 0) robot.delay(defaultWait)
                        if (stringWait > 0 && inst is StringInstruction) inst.keyWait = stringWait
                        inst.execute(robot)
                        if (inst is DefaultWaitInstruction) defaultWait = inst.delay
                        if (inst is StringWaitInstruction) stringWait = inst.delay
                        index++
                    }
                    running = false
                    runButton.icon = Icons["play"].icon()
                } catch (e: Exception) {
                    SwingUtilities.invokeLater {
                        JOptionPane.showMessageDialog(
                            null,
                            e.javaClass.simpleName + ": " + e.message,
                            "Failed to run",
                            JOptionPane.ERROR_MESSAGE
                        )
                        running = false
                        runButton.icon = Icons["play"].icon()
                    }
                    e.printStackTrace()
                }
            }.apply { name = "Robot Thread"; start() }
        }
    }

}