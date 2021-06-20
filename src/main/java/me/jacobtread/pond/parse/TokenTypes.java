package me.jacobtread.pond.parse;

import java.util.HashMap;
import java.util.Map;

public class TokenTypes {
    public static int UNEXPECTED = 0;
    public static int NEW_LINE = 1;
    public static int COMMENT = 2;
    public static int LITERAL_NUMBER = 3;
    public static int LITERAL_STRING = 4;
    public static int IDENTIFIER = 5;
    public static int WHITESPACE = 6;
    public static int KEYWORD = 7;
    public static int KEY_NAME = 8;
    public static int KEY_INVALID = 9;
    public static int MACRO_INVOKE = 10;
    public static int MACRO_DIVIDER = 11;
    public static int VARIABLE_USAGE = 12;
    public static int EOF = 13;
    public static int ANY = 99;

    public static final Map<Integer, String> TOKEN_NAMES = new HashMap<>();
    static {
        TOKEN_NAMES.put(0, "Unknown");
        TOKEN_NAMES.put(1, "New Line");
        TOKEN_NAMES.put(2, "Comment");
        TOKEN_NAMES.put(3, "Number");
        TOKEN_NAMES.put(4, "String");
        TOKEN_NAMES.put(5, "Identifier");
        TOKEN_NAMES.put(6, "Whitespace");
        TOKEN_NAMES.put(7, "Keyword");
        TOKEN_NAMES.put(8, "Key Name");
        TOKEN_NAMES.put(9, "Invalid Key");
        TOKEN_NAMES.put(10, "Macro Invoke");
        TOKEN_NAMES.put(11, "Macro Divider");
        TOKEN_NAMES.put(12, "Variable Usage");
        TOKEN_NAMES.put(13, "End Of File");
    }
}
