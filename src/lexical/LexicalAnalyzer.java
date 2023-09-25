package lexical;

import java.io.IOException;
import java.util.HashMap;
import lexical.token.TokenPosition;
import lexical.token.TokenType;

public class LexicalAnalyzer {
	private HashMap<String, TokenType> languageElements;
	private int lineCounter;
	private int chracterInLineCounter;
	private FileManager fileManager;

	public LexicalAnalyzer(String filename) throws IOException {
		this.setLanguageElements();
		this.fileManager = new FileManager(filename);
		this.lineCounter = 1;
		this.chracterInLineCounter = 0;
	}

//	private void resetLexicalAnalyzer() throws IOException {
//		this.fileManager.reset();
//		this.lineCounter = 1;
//		this.chracterInLineCounter = 0;
//	}

	private void newLine() {
		this.lineCounter++;
		this.chracterInLineCounter = 0;
	}

	private void unget(char character) throws IOException {
		this.chracterInLineCounter--;
		this.fileManager.unget(character);
	}

	public Lexeme nextLexeme() throws IOException, LexicalException {
		if (this.fileManager.endOfFileReached())
			return null;

		TokenPosition currentTokenPosition = getTokenPosition();

		String tokenValueBuffer = "";
		State currentState = State.INITIAL;
		while (true) {
			int currentCharAsInt = this.fileManager.read();
//			if (currentCharAsInt == -1) {
//				if (currentState != State.INITIAL) {
//					throw new LexicalException("Fim de arquivo inesperado. Posicao: " + currentTokenPosition + ", Valor: '" + tokenValueBuffer + "'\n");
//				} else {
//					return null;
//				}
//			}
			
			char currentChar = (char) currentCharAsInt;

			chracterInLineCounter++;
			switch (currentState) {
				case INITIAL:
					if (currentChar == ' ' || currentChar == '\t' || currentChar == '\r');

					else if (currentChar == '\n') { // QUEBRA DE LINHA
						this.newLine();
					}

					else if (Character.isDigit(currentChar)) { // NUMERO
						tokenValueBuffer += String.valueOf(currentChar);
						currentState = State.INTEGER_CONST;
					}

					else if (Character.isLetter(currentChar)) { // LETRA
						tokenValueBuffer += String.valueOf(currentChar);
						currentState = State.IDENTIFIER;
					}

					else if (currentChar == '\"') { // ASPAS
						tokenValueBuffer += String.valueOf(currentChar);
						currentState = State.LITERAL;
					}

					else if (currentChar == '/') { // BARRA
						tokenValueBuffer += String.valueOf(currentChar);
						tokenValueBuffer = "";
						currentState = State.SLASH;
					}

					else if ( this.languageElements.containsKey( String.valueOf(currentChar) )) { // ELEMENTO DA LINGUAGEM
						tokenValueBuffer += String.valueOf(currentChar);
						return new Lexeme(this.languageElements.get(String.valueOf(currentChar)), tokenValueBuffer, currentTokenPosition);
					}

					else if (currentChar == -1) { // FIM DE ARQUIVO
						return null;
					}

					else { // PADRAO NAO IDENTIFICADO
						tokenValueBuffer += String.valueOf(currentChar);
						throw new LexicalException("Padrao nao reconhecido pela linguagem. Posicao: " + currentTokenPosition + ", Valor: '" + tokenValueBuffer + "'\n");
					}
					break;

				case INTEGER_CONST:
					if (Character.isDigit(currentChar)) {
						tokenValueBuffer += String.valueOf(currentChar);
					} 
					
					else if (currentChar == '.') {
						tokenValueBuffer += String.valueOf(currentChar);
						currentState = State.REAL_COST;
					} 
					
					else if (Character.isLetter(currentChar)){
						throw new LexicalException("Constantes inteiras nao podem ter letras. Posicao: " + currentTokenPosition + ", Valor: '" + tokenValueBuffer + "'\n");
					} 
					
					else {
						this.unget(currentChar);
						return new Lexeme(TokenType.INT_CONST, tokenValueBuffer, this.getTokenPosition());
					}
					break;

				case REAL_COST:
					if (Character.isDigit(currentChar)) {
						tokenValueBuffer += String.valueOf(currentChar);
					} 
					
					else if (Character.isLetter(currentChar)){
						throw new LexicalException("Constantes reais nao podem ter letras. Posicao: " + currentTokenPosition + ", Valor: '" + tokenValueBuffer + "'\n");
					} 
					
					else {
						this.unget(currentChar);
						return new Lexeme(TokenType.REAL_CONST, tokenValueBuffer, this.getTokenPosition());
					}
					break;

				case LITERAL:
					tokenValueBuffer += String.valueOf(currentChar);
					if (currentChar == '"') {
						return new Lexeme(TokenType.LITERAL, tokenValueBuffer, this.getTokenPosition());
					} 
					
					else if (currentChar == '\n') {
						tokenValueBuffer = tokenValueBuffer.substring(0, tokenValueBuffer.length() - 2); // Remover quebra de linha
						throw new LexicalException("Quebra de linha inesperada na formacao de um literal. Posicao: " + currentTokenPosition + ", Valor: '" + tokenValueBuffer + "'\n");
					}
					break;

				case IDENTIFIER:
					if (Character.isDigit(currentChar) || Character.isLetter(currentChar) || currentChar == '_') {
						tokenValueBuffer += String.valueOf(currentChar);
					} 
					
					else {
						this.unget(currentChar);
						return new Lexeme(this.languageElements.getOrDefault(tokenValueBuffer, TokenType.ID),
								tokenValueBuffer, this.getTokenPosition());
					}
					break;

				case SLASH:
					if (currentChar == '/') {
						tokenValueBuffer = "";
						currentState = State.ONE_LINE_COMMENT;
					} 
					
					else if (currentChar == '*') {
						tokenValueBuffer = "";
						currentState = State.MULTIPLE_LINE_COMMENT;
					} 
					
					else {
						this.unget(currentChar);
						return new Lexeme(TokenType.DIV, "/", this.getTokenPosition());
					}
					break;

				case ONE_LINE_COMMENT:
					if (currentChar == '\n') {
						this.newLine();
						currentState = State.INITIAL;
					}
					break;

				case MULTIPLE_LINE_COMMENT:
					if (currentCharAsInt == -1 ) {
						throw new LexicalException("Comentario de multiplas linhas nao finalizado. Posicao: " + currentTokenPosition + ", Valor: '" + tokenValueBuffer + "'\n");
					} 
					
					else if (currentChar == '*') {
						char nextChar = (char) fileManager.read();

						if (nextChar == '/') {
							currentState = State.INITIAL;
						} else {
							this.unget(nextChar);
						}
					} 
					
					else if (currentChar == '\n') {
						this.newLine();
					}
					break;

				default:
					throw new LexicalException("Padrao nao reconhecido. Posicao: " + currentTokenPosition + ", Valor: '" + tokenValueBuffer + "'\n");
			}
		}
	}

	private TokenPosition getTokenPosition() {
		return new TokenPosition(this.lineCounter, this.chracterInLineCounter);
	}

	private void setLanguageElements() {
		this.languageElements = new HashMap<String, TokenType>();

		this.languageElements.put(";", TokenType.SEMI_COLON);
		this.languageElements.put(",", TokenType.COMMA);
		this.languageElements.put("(", TokenType.OPEN_PARENTHESIS);
		this.languageElements.put(")", TokenType.CLOSE_PARENTHESIS);
		this.languageElements.put("{", TokenType.OPEN_CURLY_BRACKET);
		this.languageElements.put("}", TokenType.CLOSE_CURLY_BRACKET);
		this.languageElements.put("[", TokenType.OPEN_BRACKET);
		this.languageElements.put("]", TokenType.CLOSE_BRACKET);
		this.languageElements.put("\"", TokenType.QUOTATION);

		// OPERATORS
		this.languageElements.put(">", TokenType.GREATER_THAN);
		this.languageElements.put(">=", TokenType.GREATER_EQUAL);
		this.languageElements.put("<", TokenType.LOWER_THAN);
		this.languageElements.put("<=", TokenType.LOWER_EQUAL);
		this.languageElements.put("!=", TokenType.NOT_EQUAL);
		this.languageElements.put("==", TokenType.EQUAL);
		this.languageElements.put("!", TokenType.NOT);
		this.languageElements.put("+", TokenType.ADD);
		this.languageElements.put("-", TokenType.SUB);
		this.languageElements.put("||", TokenType.OR);
		this.languageElements.put("*", TokenType.MUL);
		this.languageElements.put("/", TokenType.DIV);
		this.languageElements.put("&&", TokenType.AND);
		this.languageElements.put("=", TokenType.ASSIGN);
		this.languageElements.put("==", TokenType.COMPARE);

		// KEYWORDS
		this.languageElements.put("class", TokenType.CLASS);
		this.languageElements.put("INT", TokenType.INT);
		this.languageElements.put("string", TokenType.STRING);
		this.languageElements.put("float", TokenType.FLOAT);
		this.languageElements.put("if", TokenType.IF);
		this.languageElements.put("else", TokenType.ELSE);
		this.languageElements.put("do", TokenType.DO);
		this.languageElements.put("while", TokenType.WHILE);
		this.languageElements.put("read", TokenType.READ);
		this.languageElements.put("write", TokenType.WRITE);
	}

	private enum State {
		INITIAL, INTEGER_CONST, REAL_COST, LITERAL, IDENTIFIER, SLASH, ONE_LINE_COMMENT, MULTIPLE_LINE_COMMENT;
	}
}
