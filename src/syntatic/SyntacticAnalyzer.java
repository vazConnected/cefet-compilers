package syntatic;

import java.io.IOException;

import lexical.Lexeme;
import lexical.LexicalAnalyzer;
import lexical.token.TokenType;

public class SyntacticAnalyzer {
	private LexicalAnalyzer lexicalAnalyzer;
	
	public SyntacticAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
		this.lexicalAnalyzer = lexicalAnalyzer;
	}

	private void consumeToken(TokenType tokenType) throws IOException {
		Lexeme lexeme = this.lexicalAnalyzer.nextLexeme();

		if (lexeme.tokenType() != tokenType) {
			throw new SyntacticException("Token esperado: " + tokenType + 
					". Token Obtido: " + lexeme.tokenType() + 
					". Erro encontrado na posicao: " + lexeme.position().toString());
		}
	}
	
	private Lexeme checkNextLexeme() throws IOException {
		Lexeme lexeme = this.lexicalAnalyzer.nextLexeme();
		this.lexicalAnalyzer.unget(lexeme);
		return lexeme;
	}

	public void run() throws IOException {
		this.rule_program();
	}
	
	// program ::= class identifier [decl-list] body
	private void rule_program() throws IOException {
		this.consumeToken(TokenType.CLASS);
		this.consumeToken(TokenType.ID);
		
		TokenType nextToken = this.checkNextLexeme().tokenType();
		if (nextToken == TokenType.INT || nextToken == TokenType.STRING || nextToken == TokenType.FLOAT) {
			this.rule_declList();
		}
		
		this.rule_body();
	}
	
	// decl-list ::= decl ";" { decl ";"}
	private void rule_declList() throws IOException {
		TokenType nextToken = null;
		
		do {
			this.rule_decl();
			this.consumeToken(TokenType.SEMI_COLON);
			
			nextToken = this.checkNextLexeme().tokenType();
		} while (nextToken == TokenType.INT || nextToken == TokenType.STRING || nextToken == TokenType.FLOAT);
	}
	
	// decl ::= type ident-list
	private void rule_decl() throws IOException {
		this.rule_type();
		this.rule_identList();
	}
	
	// ident-list ::= identifier {"," identifier}
	private void rule_identList() throws IOException {
		this.consumeToken(TokenType.ID);
		
		while (this.checkNextLexeme().tokenType() == TokenType.COMMA) {
			this.consumeToken(TokenType.COMMA);
			this.consumeToken(TokenType.ID);
		}
	}
	
	// type ::= int | string | float
	private void rule_type() throws IOException {
		Lexeme lexeme = this.checkNextLexeme();
		TokenType nextToken = lexeme.tokenType();
		
		if (nextToken == TokenType.INT || nextToken == TokenType.STRING || nextToken == TokenType.FLOAT) {
			this.consumeToken(nextToken);
		} else {
			throw new SyntacticException("Token inesperado durante a validacao da regra \"type\" encontrado em: " + 
					lexeme.position().toString());
		}
	}
	
	// body ::= "{" stmt-list "}"
	private void rule_body() throws IOException {
		this.consumeToken(TokenType.OPEN_CURLY_BRACKET);
		this.rule_stmtList();
		this.consumeToken(TokenType.CLOSE_CURLY_BRACKET);
	}
	
	// stmt-list ::= stmt ";" { stmt ";" }
	private void rule_stmtList() throws IOException {
		TokenType nextToken = null;
		
		do {
			this.rule_stmt();
			this.consumeToken(TokenType.SEMI_COLON);
			
			nextToken = this.checkNextLexeme().tokenType();
		} while (nextToken == TokenType.ID || nextToken == TokenType.IF || nextToken == TokenType.DO ||
				nextToken == TokenType.READ || nextToken == TokenType.WRITE);
	}
	
	// stmt ::= assign-stmt | if-stmt | do-stmt | read-stmt | write-stmt
	private void rule_stmt() throws IOException {
		Lexeme lexeme = this.checkNextLexeme();
		TokenType nextToken = lexeme.tokenType();
		
		if (nextToken == TokenType.ID || nextToken == TokenType.IF || nextToken == TokenType.DO ||
				nextToken == TokenType.READ || nextToken == TokenType.WRITE) {
			this.consumeToken(nextToken);
		} else {
			throw new SyntacticException("Token inesperado durante a validacao da regra \"stmt\" encontrado em: " + 
					lexeme.position().toString());
		}
	}
	
	// assign-stmt ::= identifier "=" simple_expr
	private void rule_assignStmt() throws IOException {
		this.consumeToken(TokenType.ID);
		this.consumeToken(TokenType.ASSIGN);
		this.rule_simpleExpr();
	}
	
	// if-stmt ::= if "(" condition ")" "{" stmt-list "}"  | if "(" condition ")" "{" stmt-list "}" else "{" stmt-list "}"
	// condition ::= expression
	// do-stmt ::= do "{" stmt-list "}" do-suffix
	// do-suffix ::= while "(" condition ")"
	// read-stmt ::= read "(" identifier ")"
	// write-stmt ::= write "(" writable ")"
	// writable ::= simple-expr
	// expression ::= simple-expr | simple-expr relop simple-expr
	// simple-expr ::= term | simple-expr addop term
	// term ::= factor-a | term mulop factor-a
	// factor-a ::= factor | "!" factor | "-" factor
	// factor ::= identifier | constant | "(" expression ")"
	// relop ::= ">" | ">=" | "<" | "<=" | "!=" | "=="
	// addop ::= "+" | "-" | "||"
	// mulop ::= "*" | "/" | "&&"
}
