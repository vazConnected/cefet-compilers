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

	private Lexeme consumeToken(TokenType tokenType) throws IOException {
		Lexeme lexeme = this.currentLexeme;

		if (lexeme.tokenType() != tokenType) {
			throw new SyntacticException("Token esperado: " + tokenType + ". Token Obtido: " + lexeme.tokenType()
					+ ". Erro encontrado na posicao: " + lexeme.position().toString());
		}

		this.updateCurrentLexeme();
		return lexeme;
	}

	public void run() throws IOException {
		this.currentLexeme = this.lexicalAnalyzer.nextLexeme();
		this.lookAhead = this.lexicalAnalyzer.nextLexeme();
		this.rule_program();
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
		if (nextType == TokenType.INT || nextType == TokenType.STRING || nextType == TokenType.FLOAT) {
			this.rule_decl();
			this.rule_declList_();
		}
	}

	// decl ::= type ident-list
	private void rule_decl() throws IOException {
		TokenType type = this.rule_type();
		List<String> identifiers = this.rule_identList();

		for (String identifier : identifiers) {
			SymbolTable.registerVariableDeclaration(identifier, type);
		}
	}

	// ident-list ::= identifier ident-list'
	private List<String> rule_identList() throws IOException {
		List<String> identifiers = new LinkedList<String>();

		identifiers.add(this.consumeToken(TokenType.ID).tokenValue());
		identifiers.addAll(this.rule_identList_());

		return identifiers;
	}

	// ident-list' ::= "," identifier ident-list' | ε
	private List<String> rule_identList_() throws IOException {
		List<String> identifiers = new LinkedList<String>();
		TokenType nextType = this.currentLexeme.tokenType();

		if (nextType == TokenType.COMMA) {
			this.consumeToken(TokenType.COMMA);
			identifiers.add(this.consumeToken(TokenType.ID).tokenValue());
			identifiers.addAll(this.rule_identList_());
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
			return nextType;
		default:
			this.unexpectedTokenError("type", lexeme);
			break;
		}

		return null;
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
		switch (nextType) {
		case IF:
			this.rule_ifStmt();
			return;
		case ID:
			this.rule_assignStmt();
			return;
		case DO:
			this.rule_doStmt();
			return;
		case READ:
			this.rule_readStmt();
			return;
		case WRITE:
			this.rule_writeStmt();
			return;
		default:
			return;
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
		Lexeme idLexeme = this.consumeToken(TokenType.ID);

		this.consumeToken(TokenType.ASSIGN);

		TokenType simpleExprType = this.rule_simpleExpr();

		TokenType variableType = SemanticAnalyzer.getVariableType(idLexeme);
		boolean simpleExprIsNumeric = (simpleExprType == TokenType.INT || simpleExprType == TokenType.FLOAT);

		if (variableType == simpleExprType)
			return;
		if (variableType == TokenType.FLOAT && simpleExprIsNumeric)
			return;
		if (variableType == TokenType.INT && simpleExprType == TokenType.INT)
			return;

		throw new SemanticException("A variavel " + idLexeme.tokenValue() + " e imcompativel com o tipo "
				+ simpleExprType + ". \n " + "Erro encontrado na posicao: " + idLexeme.position().toString());

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

		// return SemanticAnalyzer.getVariableType(lex);
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
		Lexeme currentLexeme = this.currentLexeme;
		TokenType type = this.rule_expression();

		if (type != TokenType.LOGICAL_EXPRESSION)
			throw new SemanticException("E necessario que condicoes seja expressoes logicas. \n"
					+ "Erro encontrado na posicao: " + currentLexeme.position().toString());
	}

	// expression ::= simple-expr expression'
	private TokenType rule_expression() throws IOException {
		TokenType simpleExpressionType = this.rule_simpleExpr();
		TokenType expression_Type = this.rule_expression_();

		return SemanticAnalyzer.getTheMostComprehensiveType(simpleExpressionType, expression_Type);
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
			return TokenType.LOGICAL_EXPRESSION;
		default:
			return null;
		}
	}

	// simple-expr ::= term simple-expr'
	private TokenType rule_simpleExpr() throws IOException {
		TokenType termType = this.rule_term();
		TokenType simpleTermType = this.rule_simpleExpr_();

		return SemanticAnalyzer.getTheMostComprehensiveType(termType, simpleTermType);
	}

	// simple-expr' ::= addop term simple-expr' | ε
	private TokenType rule_simpleExpr_() throws IOException {
		Lexeme currentLexeme = this.currentLexeme;
		TokenType nextType = currentLexeme.tokenType();
		switch (nextType) {
		case ADD:
		case SUB:
		case OR:
			this.rule_addop();
			TokenType termType = this.rule_term();
			TokenType simpleExprType = this.rule_simpleExpr_();

			return SemanticAnalyzer.getTheMostComprehensiveType(termType, simpleExprType);
		default:
			return null;
		}
	}

	// term ::= factor-a term'
	private TokenType rule_term() throws IOException {
		TokenType factorAType = this.rule_factorA();
		TokenType termType = this.rule_term_();

		return SemanticAnalyzer.getTheMostComprehensiveType(factorAType, termType);
	}

	// term' ::= mulop factor-a term' | ε
	private TokenType rule_term_() throws IOException {
		TokenType nextType = this.currentLexeme.tokenType();
		switch (nextType) {
		case MUL:
		case DIV:
		case AND:
			this.rule_mulop();
			TokenType factorAType = this.rule_factorA();
			TokenType term_Type = this.rule_term_();

			return SemanticAnalyzer.getTheMostComprehensiveType(factorAType, term_Type);
		default:
			return null;
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
		Lexeme lexeme = this.currentLexeme;
		TokenType nextType = lexeme.tokenType();
		switch (nextType) {
		case ID:
		case INT_CONST:
		case REAL_CONST:
		case LITERAL:
			this.consumeToken(nextType);

			if (nextType == TokenType.ID)
				return SemanticAnalyzer.getVariableType(lexeme);
			else
				return SemanticAnalyzer.translateType(nextType);
		case OPEN_PARENTHESIS:
			this.consumeToken(TokenType.OPEN_PARENTHESIS);
			TokenType expressionType = this.rule_expression();
			this.consumeToken(TokenType.CLOSE_PARENTHESIS);

			return expressionType;
		default:
			this.unexpectedTokenError("factor", lexeme);
			break;
		}

		return null;
	}

	// relop ::= ">" | ">=" | "<" | "<=" | "!=" | "=="
	private void rule_relop() throws IOException {
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
	}

	// addop ::= "+" | "-" | "||"
	private void rule_addop() throws IOException {
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
	}

	// mulop ::= "*" | "/" | "&&"
	private void rule_mulop() throws IOException {
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
	}

	private void unexpectedTokenError(String ruleName, Lexeme lexeme) throws SyntacticException {
		throw new SyntacticException(
				"Token inesperado (" + lexeme.tokenType() + ") encontrado durante a validacao da regra \"" + ruleName
						+ "\" encontrado em: " + lexeme.position().toString());
	}
}