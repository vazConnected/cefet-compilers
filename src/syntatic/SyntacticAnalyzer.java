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
			this.lexicalAnalyzer.unget(lexeme);
			throw new SyntacticException("Token esperado: " + tokenType + 
					". Token Obtido: " + lexeme.tokenType() + 
					". Erro encontrado na posicao: " + lexeme.position().toString());
		}
	}
	
	public void run() throws IOException {
		this.rule_program();
	}
	
	private Lexeme checkNextLexeme() throws IOException {
		Lexeme lexeme = this.lexicalAnalyzer.nextLexeme();
		this.lexicalAnalyzer.unget(lexeme);
		return lexeme;
	}

	// program ::= class identifier [decl-list] body
	private void rule_program() throws IOException {
		this.consumeToken(TokenType.CLASS);
		this.consumeToken(TokenType.ID);
		
		TokenType nextType = this.checkNextLexeme().tokenType();
		if (nextType == TokenType.INT || nextType == TokenType.STRING || nextType == TokenType.FLOAT) {
			this.rule_declList();
		}
		
		this.rule_body();
	}

	// decl-list ::= decl decl-list'
	private void rule_declList() throws IOException {
		this.rule_decl();
		this.rule_declList_();
	}
	
	// decl-list' ::= ";" decl decl-list' | ε
	private void rule_declList_() throws IOException {
		TokenType nextType = this.checkNextLexeme().tokenType();
		
		if (nextType == TokenType.SEMI_COLON) {
			this.consumeToken(TokenType.SEMI_COLON);
			this.rule_decl();
			this.rule_declList_();
		}
	}
	
	// decl ::= type ident-list
	private void rule_decl() throws IOException {
		this.rule_type();
		this.rule_identList();
	}
	
	// ident-list ::= identifier ident-list'
	private void rule_identList() throws IOException {
		this.consumeToken(TokenType.ID);
		this.rule_identList_();
	}
	
	// ident-list' ::= "," identifier ident-list' | ε
	private void rule_identList_() throws IOException {
		TokenType nextType = this.checkNextLexeme().tokenType();
		
		if (nextType == TokenType.COMMA) {
			this.consumeToken(TokenType.COMMA);
			this.consumeToken(TokenType.ID);
			this.rule_identList_();
		}
	}
	
	// type ::= int | string | float
	private void rule_type() throws IOException {
		Lexeme lexeme = this.checkNextLexeme();
		TokenType nextType = lexeme.tokenType();
		
		switch (nextType) {
			case INT:
			case STRING:
			case FLOAT:
				this.consumeToken(nextType);
				break;
			default:
				this.unexpectedTokenError("type", lexeme);
				break;
		}
	}
	
	// body ::= "{" stmt-list "}"
	private void rule_body() throws IOException {
		this.consumeToken(TokenType.OPEN_CURLY_BRACKET);
		this.rule_stmtList();
		this.consumeToken(TokenType.CLOSE_CURLY_BRACKET);
	}
	
	// stmt-list ::= stmt stmt-list'
	private void rule_stmtList() throws IOException {
		this.rule_stmt();
		this.rule_stmtList_();
	}
	
	// stmt-list' ::= ";" stmt stmt-list' | ε
	private void rule_stmtList_() throws IOException {
		TokenType nextToken = this.checkNextLexeme().tokenType();
		
		if (nextToken == TokenType.SEMI_COLON) {
			this.consumeToken(TokenType.SEMI_COLON);
			this.rule_stmt();
			this.rule_stmtList_();
		}
	}
	
	// stmt ::= if-stmt | assign-stmt | do-stmt | read-stmt | write-stmt
	private void rule_stmt() throws IOException {
		Lexeme lexeme = this.checkNextLexeme();
		TokenType nextType = lexeme.tokenType();
		
		switch(nextType) {
			case IF:
				this.rule_ifStmt();
				break;
			case ID:
				this.rule_assignStmt();
				break;
			case DO:
				this.rule_doStmt();
				break;
			case READ:
				this.rule_readStmt();
				break;
			case WRITE:
				this.rule_writeStmt();
				break;
			default:
				this.unexpectedTokenError("stmt", lexeme);
				break;
		}
	}
	
	// if-stmt ::= if "(" condition ")" "{" stmt-list "}" stmt-else
	private void rule_ifStmt() throws IOException {
		this.consumeToken(TokenType.IF);
		this.consumeToken(TokenType.OPEN_PARENTHESIS);
		this.rule_condition();
		this.consumeToken(TokenType.CLOSE_PARENTHESIS);
		this.consumeToken(TokenType.OPEN_CURLY_BRACKET);
		this.rule_stmtList();
		this.consumeToken(TokenType.CLOSE_CURLY_BRACKET);
		this.rule_stmtElse();
	}
	
	// stmt-else ::= else "{" stmt-list "}" | ε
	private void rule_stmtElse() throws IOException {
		TokenType nextToken = this.checkNextLexeme().tokenType();
		
		if (nextToken == TokenType.ELSE) {
			this.consumeToken(TokenType.ELSE);
			this.consumeToken(TokenType.OPEN_CURLY_BRACKET);
			this.rule_stmtList();
			this.consumeToken(TokenType.CLOSE_CURLY_BRACKET);
		}
	}
	
	// assign-stmt ::= identifier "=" simple-expr
	private void rule_assignStmt() throws IOException {
		this.consumeToken(TokenType.ID);
		this.consumeToken(TokenType.ASSIGN);
		this.rule_simpleExpr();
	}
	
	// do-stmt ::= do "{" stmt-list "}" do-suffix
	private void rule_doStmt() throws IOException {
		this.consumeToken(TokenType.DO);
		this.consumeToken(TokenType.OPEN_CURLY_BRACKET);
		this.rule_stmtList();
		this.consumeToken(TokenType.CLOSE_CURLY_BRACKET);
		this.rule_doSuffix();
	}
	
	// do-suffix ::= while "(" condition ")"
	private void rule_doSuffix() throws IOException {
		this.consumeToken(TokenType.WHILE);
		this.consumeToken(TokenType.OPEN_PARENTHESIS);
		this.rule_condition();
		this.consumeToken(TokenType.CLOSE_PARENTHESIS);
	}

	// read-stmt ::= read "(" identifier ")"
	private void rule_readStmt() throws IOException {
		this.consumeToken(TokenType.READ);
		this.consumeToken(TokenType.OPEN_PARENTHESIS);
		this.consumeToken(TokenType.ID);
		this.consumeToken(TokenType.CLOSE_PARENTHESIS);
	}

	// write-stmt ::= write "(" writable ")"
	private void rule_writeStmt() throws IOException {
		this.consumeToken(TokenType.WRITE);
		this.consumeToken(TokenType.OPEN_PARENTHESIS);
		this.rule_writable();
		this.consumeToken(TokenType.CLOSE_PARENTHESIS);
	}
	
	// writable ::= simple-expr
	private void rule_writable() throws IOException {
		this.rule_simpleExpr();
	}
	
	// condition ::= expression
	private void rule_condition() throws IOException {
		this.rule_expression();
	}
	
	// expression ::= simple-expr expression'
	private void rule_expression() throws IOException {
		this.rule_simpleExpr();
		this.rule_expression_();
	}
	
	// expression' ::= relop simple-expr expression' | ε
	private void rule_expression_() throws IOException {
		TokenType nextType = this.checkNextLexeme().tokenType();
		
		switch (nextType) {
			case GREATER_THAN:
			case GREATER_EQUAL:
			case LOWER_THAN:
			case LOWER_EQUAL:
			case NOT_EQUAL:
			case COMPARE:
				this.rule_relop();
				this.rule_simpleExpr();
				this.rule_expression_();
				break;
			default: return;
		}
	}
	
	// simple-expr ::= term simple-expr'
	private void rule_simpleExpr() throws IOException {
		this.rule_term();
		this.rule_simpleExpr_();
	}
	
	// simple-expr' ::= addop term simple-expr' | ε
	private void rule_simpleExpr_() throws IOException {
		TokenType nextType = this.checkNextLexeme().tokenType();
		
		switch (nextType) {
			case ADD:
			case SUB:
			case OR:
				this.rule_addop();
				this.rule_term();
				this.rule_simpleExpr_();
				break;
			default: return;
		}
	}
	
	// term ::= factor-a term'
	private void rule_term() throws IOException {
		this.rule_factorA();
		this.rule_term_();
	}
	
	// term' ::= mulop factor-a term' | ε
	private void rule_term_() throws IOException {
		TokenType nextType = this.checkNextLexeme().tokenType();
		
		switch (nextType) {
			case MUL:
			case DIV:
			case AND:
				this.rule_mulop();
				this.rule_factorA();
				this.rule_term_();
				break;
			default: return;
		}
	}
	
	// factor-a ::= factor | "!" factor | "-" factor
	private void rule_factorA() throws IOException {
		TokenType nextType = this.checkNextLexeme().tokenType();
		
		if (nextType == TokenType.NOT || nextType == TokenType.SUB) {
			this.consumeToken(nextType);
		}
		
		this.rule_factor();
	}
	
	// factor ::= identifier | constant | "(" expression ")"
	private void rule_factor() throws IOException {
		Lexeme lexeme = this.checkNextLexeme();
		TokenType nextType = lexeme.tokenType();
		
		switch (nextType) {
			case ID:
			case INT_CONST:
			case REAL_CONST:
				this.consumeToken(nextType);
				break;
			case OPEN_PARENTHESIS:
				this.consumeToken(TokenType.OPEN_PARENTHESIS);
				this.rule_expression();
				this.consumeToken(TokenType.CLOSE_PARENTHESIS);
				break;
			default:
				this.unexpectedTokenError("factor", lexeme);
				break;
		}
	}
	
	// relop ::= ">" | ">=" | "<" | "<=" | "!=" | "=="
	private void rule_relop() throws IOException {
		Lexeme lexeme = this.checkNextLexeme();
		TokenType nextType = lexeme.tokenType();
		
		switch (nextType) {
			case GREATER_THAN:
			case GREATER_EQUAL:
			case LOWER_THAN:
			case LOWER_EQUAL:
			case NOT_EQUAL:
			case COMPARE:
				this.consumeToken(nextType);
				break;
			default:
				this.unexpectedTokenError("relop", lexeme);
				break;
		}
	}
	
	// addop ::= "+" | "-" | "||"
	private void rule_addop() throws IOException {
		Lexeme lexeme = this.checkNextLexeme();
		TokenType nextType = lexeme.tokenType();
				
		switch (nextType) {
			case ADD:
			case SUB:
			case OR:
				this.consumeToken(nextType);
				break;
			default:
				this.unexpectedTokenError("addop", lexeme);
				break;
		}
	}
	
	// mulop ::= "*" | "/" | "&&"
	private void rule_mulop() throws IOException {
		Lexeme lexeme = this.checkNextLexeme();
		TokenType nextType = lexeme.tokenType();
		
		switch (nextType) {
			case MUL:
			case DIV:
			case AND:
				this.consumeToken(nextType);
				break;
			default:
				this.unexpectedTokenError("mulop", lexeme);
				break;
		}
	}

	private void unexpectedTokenError(String ruleName, Lexeme lexeme) throws SyntacticException {
		throw new SyntacticException("Token inesperado (" + lexeme.tokenType() + ") encontrado durante a validacao da regra \"" + ruleName + "\" encontrado em: " + 
				lexeme.position().toString());
	}
}
