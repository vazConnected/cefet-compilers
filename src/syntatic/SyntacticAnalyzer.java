package syntatic;

import java.io.IOException;

import lexical.Lexeme;
import lexical.LexicalAnalyzer;
import lexical.LexicalException;
import lexical.token.TokenType;

public class SyntacticAnalyzer {
	private LexicalAnalyzer lexicalAnalyzer;
	
	public SyntacticAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
		this.lexicalAnalyzer = lexicalAnalyzer;
	}
	
	public void run() {
		throw new RuntimeException("todo: implementar regras");
	}
	
	private void consumeToken(TokenType tokenType) throws IOException, LexicalException, SyntacticException {
		Lexeme lexeme = this.lexicalAnalyzer.nextLexeme();
		if (lexeme.tokenType() != tokenType) {
			throw new SyntacticException("Token esperado: " + tokenType + 
					". Token Obtido: " + lexeme.tokenType() + 
					". Erro encontrado na posicao: " + lexeme.position().toString());
		}
	}
	
}
