package me.jacobtread.pond.util

import java.awt.event.KeyEvent

// TODO: Find a better way to do this
object RobotTranslation {

    operator fun get(key: String): String {
        return if (key in ROBOTO_MAP) ROBOTO_MAP[key] ?: key else key
    }

    val REQUIRES_SHIFT: Regex = Regex("[A-Z~!@#$%^&*()_+{}|:\"<>?]")

    private val ROBOTO_MAP: Map<String, String> = mapOf(
        "GUI" to "WINDOWS",
        "MENU" to "CONTEXT_MENU",
        "APP" to "CONTEXT_MENU",
        "CTRL" to "CONTROL",
        "ESC" to "ESCAPE",
        "BREAK" to "PAUSE",
        "DEL" to "DELETE",
        "RIGHTARROW" to "RIGHT",
        "LEFTARROW" to "LEFT",
        "DOWNARROW" to "DOWN",
        "UPARROW" to "UP",
        "PAGEUP" to "PAGE_UP",
        "PAGEDOWN" to "PAGE_DOWN",
        "SCROLLLOCK" to "SCROLL_LOCK",
        "CAPSLOCK" to "CAPS_LOCK",
        "BACKSPACE" to "BACK_SPACE",
        "`" to "BACK_QUOTE",
        "~" to "BACK_QUOTE",
        "!" to "1",
        "@" to "2",
        "#" to "3",
        "$" to "4",
        "%" to "5",
        "^" to "6",
        "&" to "7",
        "*" to "8",
        "(" to "9",
        ")" to "0",
        "-" to "MINUS" ,
        "_" to "MINUS",
        "=" to "EQUALS",
        "+" to "EQUALS",
        "[" to "OPEN_BRACKET",
        "{" to "OPEN_BRACKET",
        "]" to "CLOSE_BRACKET",
        "}" to "CLOSE_BRACKET",
        "\\" to "BACK_SLASH",
        "|" to "BACK_SLASH",
        ";" to "SEMICOLON",
        ":" to "SEMICOLON",
        "'" to "QUOTE",
        "\"" to "QUOTE",
        "," to "COMMA",
        "<" to "COMMA",
        "." to "PERIOD",
        ">" to "PERIOD",
        "/" to "SLASH",
        "?" to "SLASH",
        " " to "SPACE"
    )

    private val v = KeyEvent.VK_COMMA

}