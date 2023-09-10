package lexical;

public class Tag 
{
	public final static int
    // SYMBOLS
	SEMI_COLON = 256,		// ;
  	COLON = 257,			// ,
  	OPEN_PAR = 258,			// (
  	CLOSE_PAR = 259,		// )
    OPEN_CUR = 260,			// {
    CLOSE_CUR = 261,		// }
    DOT = 262,				// .
    QUOTATION = 263,		// "

    // OPERATORS
	GREATER_THAN = 264, 	// >
    GREATER_EQUAL = 265,	// >=
    LOWER_THAN = 266,		// <
	LOWER_EQUAL = 267, 		// <=
  	NOT_EQUAL = 268,		// !=
    EQUAL = 269,			// ==
    NOT = 270,				// !
    ADD = 271, 				// +
	SUB = 272,				// -
    OR = 273, 				// ||
	MUL = 274,				// *
	DIV = 275, 				// /
  	AND = 276,				// &&
	ASSIGN = 277, 			// =
    COMPARE = 278,          // ==

    // TYPES
    ID = 279,               // identifier
    INT_CONST = 280,        // integer_const
    REAL_CONST = 281,       // real_const
    LITERAL = 282,         	// literal
  
    // KEYWORDS
	CLASS = 283,
    INT = 284,
    STRING = 285,
    FLOAT = 286,
    IF = 287,
    ELSE = 288,
    DO = 289,
    WHILE = 290,
    READ = 291,
    WRITE = 292,

    //OTHERS
    UNEXPECTED_EOF = 293,
    INVALID_TOKEN = 294,
    EOF = 295;
}