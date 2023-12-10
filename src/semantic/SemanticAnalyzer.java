package semantic;

import lexical.Lexeme;
import lexical.SymbolTable;
import lexical.token.TokenType;

public class SemanticAnalyzer {
	private SemanticAnalyzer() {}
	
	public static boolean validVariableType(TokenType type) {
		switch(type) {
			case INT:
			case FLOAT:
			case STRING:
				return true;
			default: 
				return false;
		}
	}
	
	public static boolean checkTypeCompatibility(TokenType type1, TokenType type2) {
		if (validVariableType(type1) && validVariableType(type2)) {
			if (type1 == TokenType.INT && type2 == TokenType.INT) return true;
			else if (type1 == type2) return true;
			else if (type1 == TokenType.FLOAT && type2 == TokenType.INT) return true;
			else if (type1 == TokenType.INT && type2 == TokenType.FLOAT) return true;
			else return false;
		}
		
		return false;
	}
	
	public static boolean checkVariableDeclaration(String variableIdentifier) {
		return (SymbolTable.getVariableType(variableIdentifier) != null);
	}
	
	public static TokenType getVariableType(Lexeme lex) {
		if ( !(lex.tokenType().equals(TokenType.ID)) ) 
			throw new SemanticException("O Lexema informado nao e uma variavel: " + lex.toString());
		
		TokenType type = SymbolTable.getVariableType(lex.tokenValue());
		if (type != null) return type;
		else {
			throw new SemanticException("A variavel com identificador " + lex.tokenValue() + " nao foi declarada anteriormente no script.\n " + 
					"Erro encontrado na posicao: " + lex.position().toString());
		}
	}
	
	public static TokenType translateType(TokenType type) {
		if (type == null) return null;
		
		switch(type) {
			case INT:
			case FLOAT:
			case STRING:
				return type;
			case INT_CONST:
				return TokenType.INT;
			case REAL_CONST:
				return TokenType.FLOAT;
			case LITERAL:
				return TokenType.STRING;
			default:
				throw new SemanticException("Impossivel traduzir o tipo " + type + ".");
		}
	}
	
	public static TokenType getTheMostComprehensiveType(TokenType type1, TokenType type2) {
		TokenType type1_ = translateType(type1);
		TokenType type2_ = translateType(type2);
		
		if (type1_ == null) return type2_;
		else if (type2_ == null) return type1_;
		else if (type1_ == type2_) return type1_;
		else if ( checkTypeCompatibility(type1_, type2_) ) {
			if (type1_ == TokenType.FLOAT || type2_ == TokenType.FLOAT)
				return TokenType.FLOAT;
			else if (type1_ == TokenType.STRING || type2_ == TokenType.STRING)
				return TokenType.STRING;
			else
				return TokenType.INT;
		} else {
			throw new SemanticException("Os tipos " + type1 + " e " + type2 + " nao sao compativeis");
		}
		
	}
	
}
