package me.jacobtread.pond.syntax

import me.jacobtread.pond.parse.PondLexer
import me.jacobtread.pond.parse.TokenTypes
import org.fife.ui.rsyntaxtextarea.*
import javax.swing.text.Segment
import me.jacobtread.pond.parse.Token as PondToken

@Suppress("unused")
class PondTokenConverter : TokenMakerBase() {

    private var index = 0
    private var tokens: List<PondToken> = emptyList()
    private val lexer = PondLexer()
    private val variables: ArrayList<String> = ArrayList()
    private val macros: ArrayList<String> = ArrayList()
    private var seg: Segment = Segment()
    private var startOffset: Int = 0

    override fun getTokenList(seg: Segment, initialTokenType: Int, startOffset: Int): Token {
        resetTokenList()
        this.startOffset = startOffset
        this.seg = seg
        index = 0
        val value = String(seg.array, seg.offset, seg.count)
        tokens = lexer.getTokens(value)
        while (index >= 0 && index < tokens.size) {
            val token: PondToken = tokens[index]
            // Have to add the segment offset to token start/end
            token.start += seg.offset
            token.end += seg.offset
            when (token.tokenType) {
                TokenTypes.LITERAL_NUMBER -> addToken(Token.LITERAL_NUMBER_DECIMAL_INT)
                TokenTypes.LITERAL_STRING -> addToken(Token.LITERAL_STRING_DOUBLE_QUOTE)
                TokenTypes.VARIABLE_USAGE -> addToken(Token.VARIABLE)
                TokenTypes.KEYWORD -> addToken(Token.RESERVED_WORD)
                TokenTypes.IDENTIFIER -> {
                    val previous: PondToken = lastToken()
                    if (previous.tokenType == TokenTypes.KEYWORD && previous.text.toLowerCase() == "macro") {
                        addToken(Token.FUNCTION)
                    } else if (previous.tokenType == TokenTypes.MACRO_INVOKE) {
                        addToken(Token.FUNCTION)
                    } else {
                        val keywordToken: PondToken = lastToken(TokenTypes.KEYWORD)
                        if (keywordToken.tokenType == TokenTypes.KEYWORD) {
                            val keyword: String = keywordToken.text.toLowerCase()
                            if (keyword == "set" || keyword == "macro") {
                                addToken(Token.VARIABLE)
                                val nextToken: PondToken = nextToken(TokenTypes.IDENTIFIER)
                                if (nextToken.tokenType == TokenTypes.IDENTIFIER) {
                                    val varName = nextToken.text.substring(1)
                                    if (keyword == "macro") {
                                        macros.add(varName)
                                    } else {
                                        variables.add(varName)
                                    }
                                }
                            } else {
                                addNullToken()
                            }
                        } else {
                            addNullToken()
                        }
                    }
                }
                TokenTypes.WHITESPACE -> addToken(Token.WHITESPACE)
                TokenTypes.COMMENT -> addToken(Token.COMMENT_EOL)
                TokenTypes.KEY_NAME -> addToken(Token.RESERVED_WORD_2)
                TokenTypes.MACRO_DIVIDER -> addToken(Token.OPERATOR)
                TokenTypes.KEY_INVALID -> addToken(Token.ERROR_IDENTIFIER)
                TokenTypes.UNEXPECTED -> addToken(Token.ERROR_IDENTIFIER)
                TokenTypes.MACRO_INVOKE -> addToken(Token.OPERATOR)
                TokenTypes.EOF, TokenTypes.NEW_LINE -> {
                    addNullToken()
                    return firstToken
                }
                else -> addNullToken()
            }
            index++
        }
        if (firstToken == null) {
            addNullToken()
        }
        return firstToken
    }

    private fun lastToken(ofType: Int = TokenTypes.ANY): PondToken {
        var index: Int = this.index
        index--
        while (index >= 0 && index < tokens.size) {
            val token: PondToken = tokens[index]
            if (
                (token.tokenType != TokenTypes.WHITESPACE && ofType == TokenTypes.ANY)
                || token.tokenType == ofType
            ) {
                return token
            }
            index--
        }
        return PondToken.EOF
    }

    private fun nextToken(ofType: Int): PondToken {
        var index: Int = this.index
        index++
        while (index >= 0 && index < tokens.size) {
            val token: PondToken = tokens[index]
            if (token.tokenType == ofType) {
                return token
            }
            index++
        }
        return PondToken.EOF
    }

    private fun addToken(newType: Int) {
        val token: PondToken = tokens[index]
        super.addToken(
            seg.array,
            token.start,
            token.end,
            newType,
            (token.start - seg.offset) + startOffset,
            false
        )
    }

    override fun getLineCommentStartAndEnd(languageIndex: Int): Array<String?> {
        return arrayOf("#", null)
    }

}