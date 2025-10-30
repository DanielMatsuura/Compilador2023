
/*package de.jflex.example.standalone;*/
import java_cup.runtime.*;
%%

%public
%class Scanner
%cup
%unicode
%line
%column


%{
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }
%}

whitespace=[\r\n]|[ \t]

%%
"System.out.println" { return symbol(sym.PRINT, yytext()); }

/* operators */
"+" { return symbol(sym.PLUS, yytext()); }
"=" { return symbol(sym.EQ, yytext()); }
"-" { return symbol(sym.MINUS, yytext()); }
"*" { return symbol(sym.MULT, yytext()); }
"<" { return symbol(sym.MINOR, yytext()); }
"&&" { return symbol(sym.AND, yytext()); }

/* delimiters */
"(" { return symbol(sym.O_PAREN, yytext()); }
")" { return symbol(sym.C_PAREN, yytext()); }
";" { return symbol(sym.SEMICOLON, yytext()); }
"{" { return symbol(sym.O_CBRACKET, yytext()); }
"}" { return symbol(sym.C_CBRACKET, yytext()); }
"[" { return symbol(sym.O_SBRACKET, yytext()); }
"]" { return symbol(sym.C_SBRACKET, yytext()); }
"." { return symbol(sym.DOT, yytext()); }
"," { return symbol(sym.COMMA, yytext()); }
"!" { return symbol(sym.S_EXCL, yytext()); }

/* reserved words*/
"public" { return symbol(sym.PUBLIC, yytext()); }
"static" { return symbol(sym.STATIC, yytext()); }
"void" { return symbol(sym.VOID, yytext()); }
"main" { return symbol(sym.MAIN, yytext()); }
"class" { return symbol(sym.CLASS, yytext()); }
"extends" { return symbol(sym.EXTENDS, yytext()); }
"this" { return symbol(sym.THIS, yytext()); }
"new" { return symbol(sym.NEW, yytext()); }
"length" { return symbol(sym.LENGTH, yytext()); }
"return" { return symbol(sym.C_RETURN, yytext()); }

"if" { return symbol(sym.IF, yytext()); }
"while" { return symbol(sym.WHILE, yytext()); }
"else" { return symbol(sym.ELSE, yytext()); }

"int" { return symbol(sym.INT, yytext()); }
"boolean" { return symbol(sym.BOOLEAN, yytext()); }
"String" { return symbol(sym.STRING, yytext()); }
"true" { return symbol(sym.TRUE, yytext()); }
"false" { return symbol(sym.FALSE, yytext()); }

/* identifiers */
[a-zA-Z] ([a-zA-Z]|[0-9]|_)* { return symbol(sym.IDENTIFIER, yytext()); }
[0-9]+ { return symbol(sym.INTEGER_LITERAL, yytext()); }

/* Comments*/
\/\/.*|\/\*[\s\S]*?\*\/ {}

{whitespace}+ {/*ignore*/}

. { System.err.println(
	"\nunexpected character in input: '" + yytext() + "' at line " +
	(yyline+1) + " column " + (yycolumn+1));
  }