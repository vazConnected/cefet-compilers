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

	public void run() throws IOException {
		this.consumeToken(TokenType.CLASS);
		this.consumeToken(TokenType.ID);
		// this.rule_decllist();
		// this.rule.body()
		throw new RuntimeException("todo: implementar demais regras");
	}
}
