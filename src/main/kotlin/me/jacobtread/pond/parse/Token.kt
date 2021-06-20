package me.jacobtread.pond.parse

data class Token(
    var text: String,
    var start: Int,
    var end: Int,
    var tokenType: Int,
    val indent: Int
) {
    companion object {
        val EOF = Token("", -1, -1, TokenTypes.EOF, 0)
    }

    override operator fun equals(other: Any?): Boolean {
        return if (other is Token) {
            other.tokenType == this.tokenType
        } else {
            false
        }
    }


}