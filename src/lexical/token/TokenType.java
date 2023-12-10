package lexical.token;

public enum TokenType {
	// KEYWORDS
	CLASS,
    INT,
    STRING,
    FLOAT,
    IF,
    ELSE,
    DO,
    WHILE,
    READ,
    WRITE,
	
	// TYPES
    ID,
    INT_CONST,
    REAL_CONST,
    LITERAL,
    LOGICAL_EXPRESSION,
    
    // OPERATORS
 	GREATER_THAN,	// >
    GREATER_EQUAL,	// >=
    LOWER_THAN,		// <
 	LOWER_EQUAL,	// <=
   	NOT_EQUAL,		// !=
	EQUAL,			// ==
	NOT,			// !
	ADD,			// +
	SUB,			// -
	OR,				// ||
	MUL,			// *
	DIV,			// /
	AND,			// &&
	ASSIGN,			// =
	COMPARE,		// ==
	
	SEMI_COLON,			// ;
  	COMMA,				// ,
    OPEN_PARENTHESIS,	// (
  	CLOSE_PARENTHESIS,	// )
    OPEN_CURLY_BRACKET,	// {
  	CLOSE_CURLY_BRACKET,// }
  	OPEN_BRACKET,		// [
  	CLOSE_BRACKET,		// ]
    QUOTATION,			// "
	
	EOF,				// End of File
	UNEXPECTED_EOF,		// Unexpected End of File
	
	INVALID_TOKEN;
	
}
