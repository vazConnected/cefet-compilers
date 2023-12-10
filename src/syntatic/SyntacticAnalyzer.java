package syntatic;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import lexical.Lexeme;
import lexical.LexicalAnalyzer;
import lexical.SymbolTable;
import lexical.token.TokenType;
import semantic.SemanticAnalyzer;
import semantic.SemanticException;

public class SyntacticAnalyzer {
	private LexicalAnalyzer lexicalAnalyzer;
	private Lexeme currentLexeme, lookAhead;
	
	public SyntacticAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
		this.lexicalAnalyzer = lexicalAnalyzer;
	}

	private Lexeme consumeToken(TokenType tokenType)  throws IOException {
		Lexeme lexeme = this.currentLexeme;

		if (lexeme.tokenType() != tokenType) {
			throw new SyntacticException("Token esperado: " + tokenType + 
					". Token Obtido: " + lexeme.tokenType() + 
					". Erro encontrado na posicao: " + lexeme.position().toString());
		}
		
		this.updateCurrentLexeme();
		
		return lexeme;
	}
	
	public void run() throws IOException {
		this.currentLexeme = this.lexicalAnalyzer.nextLexeme();
		this.lookAhead = this.lexicalAnalyzer.nextLexeme();
		this.rule_program();

		System.out.println("Análise sintática concluída com sucesso.");
	}
	
	private void updateCurrentLexeme() throws IOException {
		this.currentLexeme = this.lookAhead;
		this.lookAhead = this.lexicalAnalyzer.nextLexeme();
	}
	
	// program ::= class identifier [decl-list] body
	private void rule_program() throws IOException {
		this.consumeToken(TokenType.CLASS);
		this.consumeToken(TokenType.ID);
		TokenType nextType = this.currentLexeme.tokenType();
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
		
		TokenType nextType = this.currentLexeme.tokenType();
		
		if (nextType == TokenType.SEMI_COLON) {
			this.consumeToken(TokenType.SEMI_COLON);
		}
		nextType = this.currentLexeme.tokenType();
		if(nextType == TokenType.INT || nextType == TokenType.STRING || nextType == TokenType.FLOAT) {
			this.rule_decl();
			this.rule_declList_();
		}
	}
	
	// decl ::= type ident-list
	private TokenType rule_decl() throws IOException {
		TokenType type = this.rule_type();
		List<String> identifiers = this.rule_identList();
		
		for (String identifier: identifiers) {
			SymbolTable.registerVariableDeclaration(identifier, type);
		}
		
		return type;
	}
	
	// ident-list ::= identifier ident-list'
	private List<String> rule_identList() throws IOException {
		List<String> identifiers = new LinkedList<String>();
		
		Lexeme lex = this.consumeToken(TokenType.ID);
		identifiers.add(lex.tokenValue());
		
		List<String> moreIdentifiers = this.rule_identList_();
		identifiers.addAll(moreIdentifiers);
		
		return identifiers;
	}
	
	// ident-list' ::= "," identifier ident-list' | ε
	private List<String> rule_identList_() throws IOException {
		List<String> identifiers = new LinkedList<String>();
		TokenType nextType = this.currentLexeme.tokenType();
		
		if (nextType == TokenType.COMMA) {
			this.consumeToken(TokenType.COMMA);
			
			Lexeme lex = this.consumeToken(TokenType.ID);
			identifiers.add(lex.tokenValue());
			
			List<String> moreIdentifiers = this.rule_identList_();
			identifiers.addAll(moreIdentifiers);
		}
		
		return identifiers;
	}
	
	// type ::= int | string | float
	private TokenType rule_type() throws IOException {
		Lexeme lexeme = this.currentLexeme;
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
		
		return nextType;
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
		TokenType nextToken = this.currentLexeme.tokenType();

		if (nextToken == TokenType.SEMI_COLON) {
			this.consumeToken(TokenType.SEMI_COLON);
			this.rule_stmt();
			this.rule_stmtList_();
		}
		this.rule_stmt();
	}
	
	// stmt ::= if-stmt | assign-stmt | do-stmt | read-stmt | write-stmt | ε
	private void rule_stmt() throws IOException {
		Lexeme lexeme = this.currentLexeme;
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
		TokenType nextToken = this.currentLexeme.tokenType();
		
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
	private TokenType rule_readStmt() throws IOException {
		this.consumeToken(TokenType.READ);
		this.consumeToken(TokenType.OPEN_PARENTHESIS);
		Lexeme lex = this.consumeToken(TokenType.ID);
		this.consumeToken(TokenType.CLOSE_PARENTHESIS);
		
		return lex.tokenType();
	}

	// write-stmt ::= write "(" writable ")"
	private TokenType rule_writeStmt() throws IOException {
		this.consumeToken(TokenType.WRITE);
		this.consumeToken(TokenType.OPEN_PARENTHESIS);
		TokenType type = this.rule_writable();
		this.consumeToken(TokenType.CLOSE_PARENTHESIS);
		
		return type;
	}
	
	// writable ::= simple-expr
	private TokenType rule_writable() throws IOException {
		return this.rule_simpleExpr();
	}
	
	// condition ::= expression
	private TokenType rule_condition() throws IOException {
		return this.rule_expression();
	}
	
	// expression ::= simple-expr expression'
	private TokenType rule_expression() throws IOException {
		TokenType simpleExprType = this.rule_simpleExpr();
		TokenType expressionType = this.rule_expression_();
		
		if (expressionType == null && simpleExprType != null) return simpleExprType;
		else if (expressionType != null && simpleExprType == null) return expressionType;
	
		boolean expressionIsNumeric = (expressionType == TokenType.INT || expressionType == TokenType.FLOAT);
		boolean simpleExprIsNumeric = (simpleExprType == TokenType.INT || simpleExprType == TokenType.FLOAT);
		
		if (expressionIsNumeric && simpleExprIsNumeric) {
			if (simpleExprType == TokenType.FLOAT || expressionType == TokenType.FLOAT) 
				return TokenType.FLOAT;
			else if (simpleExprType == TokenType.INT || expressionType == TokenType.INT) 
				return TokenType.INT;
		} else {
			if (  (expressionType == TokenType.STRING && simpleExprType == null) || (expressionType == null && simpleExprType == TokenType.STRING) )
				return expressionType;
		}

		throw new SemanticException("Tipos incompatíveis na regra expression: " + simpleExprType + " e " + expressionType);
	}
	
	// expression' ::= relop simple-expr expression' | ε
	private TokenType rule_expression_() throws IOException {
		TokenType nextType = this.currentLexeme.tokenType();
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
				
				return TokenType.COMPARE; //TODO verificar
			default: return null;
		}
	}
	
	// simple-expr ::= term simple-expr'
	private TokenType rule_simpleExpr() throws IOException {
		TokenType termType = this.rule_term();
		TokenType simpleExprType = this.rule_simpleExpr_();
		
		if (termType == null && simpleExprType != null) return termType;
		else if (termType == null && simpleExprType != null) return simpleExprType;
		
		boolean termIsNumeric = (termType == TokenType.INT || termType == TokenType.FLOAT);
		boolean simpleExprIsNumeric = (simpleExprType == TokenType.INT || simpleExprType == TokenType.FLOAT);
		
		if (!(termIsNumeric && simpleExprIsNumeric)) {
			// if (simpleExprType == TokenType.STRING && termType == null)
				return TokenType.STRING;
		} else {
			if (simpleExprType == TokenType.FLOAT || termType == TokenType.FLOAT) 
				return TokenType.FLOAT;
			else if (simpleExprType == TokenType.INT || termType == TokenType.INT) 
				return TokenType.INT;
		}
		
		throw new SemanticException("Tipos icompativeis para operacao: " + termType + " e " + simpleExprType);
	}
	
	// simple-expr' ::= addop term simple-expr' | ε
	private TokenType rule_simpleExpr_() throws IOException {
		TokenType nextType = this.currentLexeme.tokenType();
		switch (nextType) {
			case ADD:
			case SUB:
			case OR:
				this.rule_addop();
				
				TokenType typeTerm = this.rule_term();
				TokenType typeSimpleExpr = this.rule_simpleExpr_();
				
				boolean typeTermIsNumerical = (typeTerm == TokenType.INT || typeTerm != TokenType.FLOAT);
				boolean typeSimpleExprIsNumerical = (typeSimpleExpr == TokenType.INT || typeSimpleExpr != TokenType.FLOAT);
				
				if (typeTermIsNumerical && typeSimpleExprIsNumerical) {
					if (typeTerm == TokenType.FLOAT || typeSimpleExpr == TokenType.FLOAT) return TokenType.FLOAT;
					else return TokenType.INT;
				} else {
					throw new SemanticException("Comparacao realizara entre dois valores nao numericos detectada.");
				}
			default: return null;
		}
	}
	
	// term ::= factor-a term'
	private TokenType rule_term() throws IOException {
		TokenType typeFactorA = this.rule_factorA();
		TokenType typeTerm_ = this.rule_term_();
		
		if (typeTerm_ == null) return typeFactorA;
		
		boolean typeFactorAIsNumerical = (typeFactorA == TokenType.INT || typeFactorA != TokenType.FLOAT);
		boolean typeTerm_IsNumerical = (typeTerm_ == TokenType.INT || typeTerm_ != TokenType.FLOAT);
		
		if (typeFactorAIsNumerical && typeTerm_IsNumerical) {
			if (typeFactorA == TokenType.FLOAT || typeTerm_ == TokenType.FLOAT) return TokenType.FLOAT;
			else return TokenType.INT;
		} else {
			throw new SemanticException("Comparacao realizara entre dois valores nao numericos detectada.");
		}
	}
	
	// term' ::= mulop factor-a term' | ε
	private TokenType rule_term_() throws IOException {
		TokenType nextType = this.currentLexeme.tokenType();
		switch (nextType) {
			case MUL:
			case DIV:
			case AND:
				this.rule_mulop();

				TokenType typeFactorA = this.rule_factorA();
				TokenType typeTerm_ = this.rule_term_();
				
				boolean typeFactorAIsNumerical = (typeFactorA == TokenType.INT || typeFactorA != TokenType.FLOAT);
				boolean typeTerm_IsNumerical = (typeTerm_ == TokenType.INT || typeTerm_ != TokenType.FLOAT);
				
				if (typeFactorAIsNumerical && typeTerm_IsNumerical) {
					if (typeFactorA == TokenType.FLOAT || typeTerm_ == TokenType.FLOAT) return TokenType.FLOAT;
					else return TokenType.INT;
				} else {
					throw new SemanticException("Comparacao realizara entre dois valores nao numericos detectada.");
				}
			default: return null;
		}
	}
	
	// factor-a ::= factor | "!" factor | "-" factor
	private TokenType rule_factorA() throws IOException {
		TokenType nextType = this.currentLexeme.tokenType();
		if (nextType == TokenType.NOT || nextType == TokenType.SUB) {
			this.consumeToken(nextType);
		}
		
		return this.rule_factor();
	}
	
	// factor ::= identifier | constant | "(" expression ")"
	private TokenType rule_factor() throws IOException {
		TokenType typeToReurn = null;
		
		Lexeme lexeme = this.currentLexeme;
		TokenType nextType = lexeme.tokenType();
		switch (nextType) {
			case ID:
				typeToReurn = SemanticAnalyzer.getVariableType( this.consumeToken(nextType) );
				break;
			case INT_CONST:
				typeToReurn = TokenType.INT;
				this.consumeToken(nextType);
				break;
			case REAL_CONST:
				typeToReurn = TokenType.FLOAT;
				this.consumeToken(nextType);
				break;
			case LITERAL:
				typeToReurn = TokenType.STRING;
				this.consumeToken(nextType);
				break;
			case OPEN_PARENTHESIS:
				this.consumeToken(TokenType.OPEN_PARENTHESIS);
				typeToReurn = this.rule_expression();
				this.consumeToken(TokenType.CLOSE_PARENTHESIS);
				break;
			default:
				this.unexpectedTokenError("factor", lexeme);
				break;
		}
		
		return typeToReurn;
	}
	
	// relop ::= ">" | ">=" | "<" | "<=" | "!=" | "=="
	private TokenType rule_relop() throws IOException {
		Lexeme lexeme = this.currentLexeme;
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
		
		return nextType;
	}
	
	// addop ::= "+" | "-" | "||"
	private TokenType rule_addop() throws IOException {
		Lexeme lexeme = this.currentLexeme;
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
		
		return nextType;
	}
	
	// mulop ::= "*" | "/" | "&&"
	private TokenType rule_mulop() throws IOException {
		Lexeme lexeme = this.currentLexeme;
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
		
		return nextType;
	}

	private void unexpectedTokenError(String ruleName, Lexeme lexeme) throws SyntacticException {
		throw new SyntacticException("Token inesperado (" + lexeme.tokenType() + ") encontrado durante a validacao da regra \"" + ruleName + "\" encontrado em: " + 
				lexeme.position().toString());
	}
}
