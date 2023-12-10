package semantic;

import java.util.List;

import lexical.Lexeme;
import lexical.SymbolTable;
import lexical.token.TokenType;

public class SemanticAnalyzer {
	private SemanticAnalyzer() {}
	
	public static boolean matchType(Lexeme lex, TokenType type) {
		return lex.tokenType() == type;
	}
	
	public static boolean matchType(Lexeme lex, List<TokenType> type) {
		for(int i = 0; i < type.size(); i++) {
			if ( !type.contains(lex.tokenType()) ) return false;
		}
		
		return true;
	}
	
	public static boolean variableHasBeenDeclared(Lexeme lex) {
		return SymbolTable.contains( lex.tokenValue() );
	}
	
	public static TokenType getVariableType(Lexeme lex) {
		boolean tokenIsId = lex.tokenType().equals(TokenType.ID);
		
		if (tokenIsId) {
			TokenType type = SymbolTable.getVariableType(lex.tokenValue());
			if (type != null) return type;
			else {
				throw new SemanticException("A variavel com ID " + lex.tokenValue() + " nao foi declarada anteriormente no script.\n " + 
						"Erro encontrado na posicao: " + lex.position().toString());
			}
		} else {
			throw new SemanticException("O token informado nao e uma variavel.");
		}		
	}

}
