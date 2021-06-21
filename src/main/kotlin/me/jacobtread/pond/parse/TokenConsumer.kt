package me.jacobtread.pond.parse

import me.jacobtread.pond.parse.Token.Companion.EOF
import me.jacobtread.pond.parse.TokenTypes.*
import me.jacobtread.pond.util.UndefinedVariableException
import me.jacobtread.pond.util.UnexpectedTokenException
import me.jacobtread.pond.util.UnknownKeyException
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import me.jacobtread.pond.parse.TokenTypes.EOF as EOF_TYPE

abstract class TokenConsumer {

    private var index: Int = 0 /* Start at the first token */
    private var tokens: List<Token> = emptyList() /* The tokens to parse */
    val eot: Boolean /* Variable for checking if we have no more tokens */
        get() = index < 0 || index >= tokens.size

    /**
     *  Used to get the value of a variable
     *
     *  @param name The name of the variable
     *  @return The value of the variable
     */
    abstract fun variable(name: String): String

    /**
     *  Used to check if a variable isset
     *
     *  @param name The name of the variable
     *  @return Whether the variable isset
     */
    abstract fun variableSet(name: String): Boolean

    /**
     *  Sets the active token list
     *
     *  @param tokens The list of tokens to consume from
     */
    fun set(tokens: List<Token>) {
        this.index = 0
        this.tokens = tokens
    }

    /**
     *  Moves the index back to the previous token
     */
    fun back() {
        index--
    }

    fun consume(allowWhitespace: Boolean = false): Token {
        if (eot) return EOF // Reached end of tokens
        if (allowWhitespace) { // We are allowing whitespace
            val token: Token = tokens[index]
            index++ // Increment index for next token
            return token
        } else {
            while (!eot) { // Loop until end of tokens
                val token: Token = tokens[index]
                index++ // Increment index for next token
                if (token.tokenType != WHITESPACE) { // If the token isn't whitespace
                    return token // Return the token
                }
            }
            return EOF // Reached end without finding non whitespace
        }
    }

    fun current(): Token {
        return tokens[index - 1]
    }

    fun consumeString(): String {
        val output = StringBuilder() // The string buffer for output
        var first = true // Whether this is the first iteration
        while (!eot) { // Loop until end of tokens
            // Consume any tokens (whitespace for spaces)
            val token: Token = consume(true)
            when (token.tokenType) {
                LITERAL_STRING -> output.append(token.text) // Append the text
                WHITESPACE -> if (!first) output.append(token.text) // Only add after the first iteration
                VARIABLE_USAGE -> output.append(consumeVariable()) // Resolve the variable and append the value
                NEW_LINE, EOF_TYPE -> break // line/file ended string is complete
                else -> throw UnexpectedTokenException(token.tokenType, LITERAL_STRING, token.start) // Unexpected token
            }
            first = false // No longer the first iteration
        }
        return output.toString() // Return the output
    }

    fun consumeInt(): Int {
        val token: Token = consume(false)
        val rawText: String = when (token.tokenType) {
            LITERAL_NUMBER -> token.text // Get the text content
            VARIABLE_USAGE -> consumeVariable() // Consume as a variable
            else -> throw UnexpectedTokenException(token.tokenType, LITERAL_NUMBER, token.start) // Unexpected token
        }
        try {
            return rawText.toInt() // Convert the text to an Integer
        } catch (e: NumberFormatException) {
            throw UnexpectedTokenException(LITERAL_STRING, LITERAL_NUMBER, token.start)
        }
    }

    private fun consumeVariable(): String {
        val token: Token = current() // Get the current token
        // Skip the first character of the text to ignore the dollar sign
        val variableName: String = token.text.substring(1)
        if (variableSet(variableName)) { // When the variable isset
            return variable(variableName) // Get the variable value
        } else { // Otherwise
            throw UndefinedVariableException(variableName, token.start) // Undefined variable
        }
    }


    fun consumeIndent(expectedIndent: Int): LinkedList<Token> {
        val children: LinkedList<Token> = LinkedList() // The children tokens of this macro
        val ignoredIndents: IntArray = intArrayOf(WHITESPACE, NEW_LINE) // Tokens to ignore the indent of
        while (!eot) { // Loop until end of tokens
            val token: Token = consume(true) // Consume anything
            if (token.tokenType == EOF_TYPE) break // No more tokens at end of file
            // Ignore tokens that aren't on the same indent level
            if (token.indent < expectedIndent && token.tokenType !in ignoredIndents) {
                back() // Move back a token
                break // Break out of children parsing
            }
            children.add(token) // Add the child token
        }
        return children
    }

    fun consumeMacro(): MacroStruct {
        val nameToken: Token = consume(false) // Get the next non whitespace token
        if (nameToken.tokenType != IDENTIFIER) { // If it's not an identifier
            throw UnexpectedTokenException(nameToken.tokenType, IDENTIFIER, nameToken.start) // Unexpected token
        }
        val name: String = nameToken.text
        val arguments: ArrayList<String> = ArrayList()
        while (!eot) { // Loop until end of tokens
            val token: Token = consume(false)
            when (token.tokenType) {
                IDENTIFIER -> arguments.add(token.text)
                NEW_LINE, EOF_TYPE -> break // End argument parsing on new line or eof
                else -> throw UnexpectedTokenException(token.tokenType, IDENTIFIER, token.start) // Unexpected token
            }
        }
        val children: LinkedList<Token> = consumeIndent(nameToken.indent + 1)
        return MacroStruct(name, arguments, children)
    }

    fun consumeMacroArgs(): List<String> {
        val arguments: ArrayList<String> = ArrayList() // The parsed arguments
        val argumentBuilder = StringBuilder()
        var first = true // If this is the first iteration
        var keepFirst = false // Whether to stay as the first iteration
        while (!eot) { // Loop until end of tokens
            val token: Token = consume(true)
            when (token.tokenType) {
                VARIABLE_USAGE -> argumentBuilder.append(consumeVariable())
                LITERAL_STRING -> argumentBuilder.append(token.text)
                WHITESPACE -> if (!first) {
                    argumentBuilder.append(token.text)
                }
                MACRO_DIVIDER -> {
                    first = true
                    keepFirst = true
                    arguments.add(argumentBuilder.toString())
                    argumentBuilder.clear()
                }
                NEW_LINE, EOF_TYPE -> {
                    arguments.add(argumentBuilder.toString())
                    argumentBuilder.clear()
                    break
                }
                else -> throw UnexpectedTokenException(token.tokenType, IDENTIFIER, token.start) // Unexpected token
            }
            if (!keepFirst) {
                first = false
                keepFirst = false
            }
        }
        return arguments
    }

    fun consumeCombo(): List<String> {
        val keys: ArrayList<String> = ArrayList() // The list of keys in this combo
        while (!eot) { // Loop until end of tokens
            val token: Token = consume(false)
            when (token.tokenType) {
                KEY_NAME -> keys.add(token.text) // Add the key name
                KEY_INVALID -> throw UnknownKeyException(token.text, token.start) // Unknown/invalid key
                NEW_LINE, EOF_TYPE -> break // End key parsing on new line or eof
                else -> throw UnexpectedTokenException(token.tokenType, IDENTIFIER, token.start) // Unexpected token
            }
        }
        return keys
    }

}
