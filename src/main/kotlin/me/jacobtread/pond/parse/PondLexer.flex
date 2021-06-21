package me.jacobtread.pond.parse;

import java.io.*;
import java.util.LinkedList;
import me.jacobtread.pond.util.Keyboard;

%%

%public
%class PondLexer
%unicode
/* Case sensitive */
%type void
%ignorecase



%{

	public PondLexer() {
    }

    public static int INDENT_SPACES = 2;

    private LinkedList<Token> tokens = new LinkedList<>();

    private void addToken(String text, int start, int end, int tokenType, int indent) {
        tokens.add(new Token(text, start, end, tokenType, indent));
        zzStartRead = zzMarkedPos;
    }

    private void addToken(int start, int end, int tokenType) {
        addToken(yytext(), start, end, tokenType, yyindent(start));
    }

    private void addToken(int tokenType) {
        addToken(zzStartRead, zzMarkedPos - 1, tokenType);
    }

    public final String yytext() {
        return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
    }


    public LinkedList<Token> getTokens(String text) {
        tokens.clear();
        try {
            yyreset(text);
            yybegin(0);
            yylex();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private boolean zzRefill() {
        return zzCurrentPos>=zzBuffer.length;
    }

    private int yyindent(int start) {
        if (start == zzBuffer.length) start--;
        int lastCharAt = start;
        for (int i = start; i > 0; i--) {
            char at = zzBuffer[i];
            if (at == '\n') {
                lastCharAt -= i + 1;
                break;
            }
            if (!Character.isWhitespace(at)) {
                lastCharAt = i;
            }
        }
        return lastCharAt / INDENT_SPACES;
    }

    public final void yyreset(String text) {
        zzBuffer = text.toCharArray();
        zzStartRead = 0;
        zzEndRead = zzStartRead + text.length() - 1;
        zzCurrentPos = zzMarkedPos = 0;
        zzLexicalState = YYINITIAL;
        zzAtBOL  = true;
        zzAtEOF  = false;
    }

%}

NonZeroDigit            = [1-9]
Digit                   = ("0"|{NonZeroDigit})
Digits                  = ({Digit}){1, 9}
WhiteSpace				= ([ \t\f]+)
SetKeyword              = "SET"
WaitKeyword             = "WAIT"
DefaultWaitKeyword      = "DEFAULT_WAIT"
StringWaitKeyword       = "STRING_WAIT"
StringKeyword           = "STRING"
MacroKeyword            = "MACRO"
RepeatKeyword           = "REPEAT"
MacroInvoke             = ":"
MacroDivier             = [^\\]"|"
LineTerminator			= (\n)
Identifier              = ([:jletter:]|"-") ([:jletterdigit:]|"-")*
VariableUsage           = "$" {Identifier}
AnyButVar               = ([^\n\t\f ])+

LineComment             = ("#"[^\n]*)
InlineComment           = "/*".*"*/"

%state VARIABLE
%state STRING
%state WAIT
%state DEFAULT_WAIT
%state MACRO
%state MACRO_ARGS
%state MACRO_INVOKE
%state MACRO_ARGS_INVOKE
%state REPEAT

%%

<YYINITIAL> {
    {StringKeyword}                                           { addToken(TokenTypes.KEYWORD); yybegin(STRING); }
    {WaitKeyword}|{DefaultWaitKeyword}|{StringWaitKeyword}    { addToken(TokenTypes.KEYWORD); yybegin(WAIT); }
    {SetKeyword}                                              { addToken(TokenTypes.KEYWORD); yybegin(VARIABLE); }
    {MacroKeyword}                                            { addToken(TokenTypes.KEYWORD); yybegin(MACRO); }
    {RepeatKeyword}                                           { addToken(TokenTypes.KEYWORD); yybegin(REPEAT); }
    {MacroInvoke}                                             { addToken(TokenTypes.MACRO_INVOKE); yybegin(MACRO_INVOKE); }
    {Identifier}                                              {
          if (Keyboard.INSTANCE.has(yytext())) {
              addToken(TokenTypes.KEY_NAME);
          } else { addToken(TokenTypes.KEY_INVALID); }
      }
}

<STRING> {
    {VariableUsage}                           { addToken(TokenTypes.VARIABLE_USAGE); }
    {AnyButVar}                               { addToken(TokenTypes.LITERAL_STRING); }
}

<VARIABLE> {
    {Identifier}                          { addToken(TokenTypes.IDENTIFIER); yybegin(STRING);  }
    {Digits}                              { addToken(TokenTypes.LITERAL_NUMBER); }
}

<WAIT> {
    {Digits}                              { addToken(TokenTypes.LITERAL_NUMBER); yybegin(YYINITIAL); }
}

<MACRO> {
    {Identifier}                          { addToken(TokenTypes.IDENTIFIER); yybegin(MACRO_ARGS);}
}

<REPEAT> {
    {Digits}                              { addToken(TokenTypes.LITERAL_NUMBER); yybegin(YYINITIAL); }
}

<MACRO_ARGS> {
    {Identifier}                          { addToken(TokenTypes.IDENTIFIER); }
}

<MACRO_INVOKE> {
    {Identifier}                          { addToken(TokenTypes.IDENTIFIER); yybegin(MACRO_ARGS_INVOKE); }
}

<MACRO_ARGS_INVOKE> {
    {VariableUsage}                       { addToken(TokenTypes.VARIABLE_USAGE); }
    {AnyButVar}                           { addToken(TokenTypes.LITERAL_STRING); }
    {MacroDivier}                         { addToken(TokenTypes.MACRO_DIVIDER); }
}

{VariableUsage}                           { addToken(TokenTypes.VARIABLE_USAGE); }
{WhiteSpace}                              { addToken(TokenTypes.WHITESPACE); }
{LineComment}|{InlineComment}             { addToken(TokenTypes.COMMENT); }
<<EOF>>						              { addToken(TokenTypes.EOF); return; }
{LineTerminator}                          { addToken(TokenTypes.NEW_LINE); yybegin(YYINITIAL); }
.							              { addToken(TokenTypes.UNEXPECTED); }
