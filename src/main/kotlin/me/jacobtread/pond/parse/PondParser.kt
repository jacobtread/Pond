package me.jacobtread.pond.parse

import me.jacobtread.pond.instr.*
import me.jacobtread.pond.parse.TokenTypes.*
import me.jacobtread.pond.util.*
import java.util.*

class PondParser(tokenConsumer: TokenConsumer? = null, private val parent: PondParser? = null) {

    private val tokenConsumer: TokenConsumer =
        tokenConsumer ?: DefaultTokenConsumer(this) // Use a default token consumer if none provided
    private val lexer: PondLexer = PondLexer() // The lexer
    private val instructions: LinkedList<Instruction> = LinkedList() // The parsed instructions

    private val variables: HashMap<String, String> = HashMap() // The variable names and their values
    private val macroStructs: HashMap<String, MacroStruct> = HashMap() // The macro names and their structs

    init {
        Keyboard._load() // Load the keyboard configuration if it's not loaded
    }

    fun parse(text: String) {
        val tokens: List<Token> = lexer.getTokens(text) // Generate a token list with the lexer
            .filter { it.tokenType != COMMENT } // Ignore any comment tokens
        this.parse(tokens) // Run the parser
    }

    fun parse(tokens: List<Token>) {
        instructions.clear() // Clear the instructions
        variables.clear() // Clear the variables
        macroStructs.clear() // Clear the macros
        tokenConsumer.set(tokens) // Set the current token set in the consumer
        while (!tokenConsumer.eot) { // While no
            val token: Token = tokenConsumer.consume(false)
            val tokenValue: String = token.text.lowercase()
            when (token.tokenType) {
                KEYWORD -> when (tokenValue) {
                    "set" -> parseVariable()
                    "wait", "default_wait", "string_wait" -> parseWait(tokenValue)
                    "string" -> parseString()
                    "macro" -> parseMacro()
                    "repeat" -> parseRepeat()
                }
                KEY_NAME -> parseCombo()
                MACRO_INVOKE -> parseMacroInvoke()
                EOF -> break
            }
        }
    }

    fun result(): LinkedList<Instruction> {
        return instructions
    }


    private fun parseVariable() {
        val nameToken: Token = tokenConsumer.consume(false)
        if (nameToken.tokenType != IDENTIFIER) {
            throw UnexpectedTokenException(nameToken.tokenType, IDENTIFIER, nameToken.start)
        }
        val name: String = nameToken.text
        val value: String = tokenConsumer.consumeString()
        this.variables[name] = value
    }

    private fun parseWait(type: String) {
        val delay: Int = tokenConsumer.consumeInt()
        val instruction: Instruction = when (type) {
            "default_wait" -> DefaultWaitInstruction(delay)
            "string_wait" -> StringWaitInstruction(delay)
            else -> WaitInstruction(delay)
        }
        this.instructions.add(instruction)
    }

    private fun parseString() {
        val value: String = tokenConsumer.consumeString()
        this.instructions.add(StringInstruction(value))
    }

    private fun parseMacro() {
        val macroStruct: MacroStruct = tokenConsumer.consumeMacro()
        this.macroStructs[macroStruct.name] = macroStruct
    }

    private fun parseRepeat() {
        val current: Token = tokenConsumer.current()
        val amount: Int = tokenConsumer.consumeInt()
        val children: LinkedList<Token> = tokenConsumer.consumeIndent(current.indent + 1)
        val parser = PondParser(tokenConsumer, this)
        parser.parse(children)
        val instructions: List<Instruction> = parser.result()
        for (i in 0 until amount) {
            this.instructions.addAll(instructions)
        }
    }

    private fun parseCombo() {
        tokenConsumer.back() // Step back to get the whole combo
        val keys: List<String> = tokenConsumer.consumeCombo()
        this.instructions.add(ComboInstruction(keys))
    }

    private fun hasMacroStruct(name: String): Boolean {
        return name in macroStructs || (parent != null && name in parent.macroStructs)
    }

    private fun getMacroStruct(name: String): MacroStruct {
        return if (name in macroStructs) {
            macroStructs[name]!!
        } else {
            return parent?.macroStructs?.get(name)!!
        }
    }

    private fun parseMacroInvoke() {
        val nameToken: Token = tokenConsumer.consume(false)
        if (nameToken.tokenType != IDENTIFIER) {
            throw UnexpectedTokenException(nameToken.tokenType, IDENTIFIER, nameToken.start)
        }
        val name: String = nameToken.text
        if (hasMacroStruct(name)) {
            val macroStruct: MacroStruct = getMacroStruct(name)
            val macroArguments: List<String> = macroStruct.arguments
            val arguments: List<String> = tokenConsumer.consumeMacroArgs()
            val valueMap: HashMap<String, String> = HashMap()
            if (arguments.size != macroArguments.size) {
                for (it in arguments) {
                    if (it.isBlank()) continue
                    else throw InvalidArgumentsException(arguments.size, macroArguments.size, nameToken.start)
                }
            } else {
                macroArguments.forEachIndexed { index, variableName ->
                    val value: String = arguments[index]
                    valueMap[variableName] = value
                }
            }
            val instructions: List<Instruction> = macroStruct.compile(tokenConsumer, valueMap)
            this.instructions.addAll(instructions)
        } else if (name in InternalMacros) {
            this.instructions.addAll(InternalMacros[name])
        } else {
            throw UndefinedMacroException(name, nameToken.start)
        }
    }

    class DefaultTokenConsumer(private val parser: PondParser) : TokenConsumer() {
        override fun variable(name: String): String {
            return parser.variables[name]!!
        }

        override fun variableSet(name: String): Boolean {
            return name in parser.variables
        }
    }

}