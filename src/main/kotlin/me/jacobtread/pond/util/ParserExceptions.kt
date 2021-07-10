package me.jacobtread.pond.util

import me.jacobtread.pond.parse.TokenTypes
import java.lang.RuntimeException

open class ParserException(message: String) : RuntimeException(message)

class UndefinedVariableException(variable: String, at: Int) : ParserException("Undefined variable '$variable' at $at")
class UnexpectedTokenException(got: Int, want: Int, at: Int) : ParserException("Unexpected token got ${TokenTypes.TOKEN_NAMES[got]} expected ${TokenTypes.TOKEN_NAMES[want]} at $at")
class InvalidArgumentsException(got: Int, want: Int, at: Int) : ParserException("Incorrect amount of arguments for Macro got $got expected $want at $at")
class UndefinedMacroException(name: String, at: Int) : ParserException("Undefined macro '$name' at $at")
class UnknownKeyException(value: String, at: Int) : ParserException("Unknown key '$value' at $at")