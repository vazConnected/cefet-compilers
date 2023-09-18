package lexical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lexical.token.TokenPosition;
import lexical.token.TokenType;

public class LexicalAnalyzer {
	private int lineCounter;
	private int chracterInLineCounter;
	private FileManager fileManager;

	public LexicalAnalyzer(String filename) throws IOException {
		this.fileManager = new FileManager(filename);
		this.lineCounter = 1;
		this.chracterInLineCounter = 0;
	}
	
	private void resetLexicalAnalyzer() throws IOException {
		this.fileManager.reset();
		this.lineCounter = 1;
		this.chracterInLineCounter = 0;
	}
	
	public List<Lexeme> getListOfLexemes() throws IOException {
		List<Lexeme> lexemes = new ArrayList<Lexeme>();
		this.resetLexicalAnalyzer();
		
		Lexeme lexeme = this.nextLexeme();
		while (lexeme != null) {
			lexemes.add(lexeme);
			lexeme = this.nextLexeme();
		}
		
		this.fileManager.close();
		
		return lexemes;
	}
	
	private void newLine() {
		this.lineCounter++;
		this.chracterInLineCounter = 0;
	}
	
	public Lexeme nextLexeme() throws IOException {
		if (this.fileManager.endOfFileReached()) {
			return null;
		}
		
		Lexeme lexeme = null;

		TokenPosition tokenPosition = new TokenPosition(lineCounter, chracterInLineCounter);
		String tokenValueBuffer = "";

		int state = 1;
		while (state != 14 && state != 15) {
			char currentChar = (char) this.fileManager.read();
			chracterInLineCounter++;
			
			switch (state) {
				case 1:
					if (currentChar == ' ' || currentChar == '\t' || currentChar == '\r') {
						state = 1;
					} else if (currentChar == '\n') {
						this.newLine();
						state = 1;
					} else if (currentChar == '>' || currentChar == '<' || currentChar == '!' || currentChar == '=') {
						tokenValueBuffer += (char) currentChar;
						state = 11;
					} else if (currentChar == '|') {
						tokenValueBuffer += (char) currentChar;
						state = 12;
					} else if (currentChar == '&') {
						tokenValueBuffer += (char) currentChar;
						state = 13;
					} else if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == ';' || currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}') {
						tokenValueBuffer += (char) currentChar;
						state = 15;
					} else if (currentChar == '/') {
						state = 2;
					} else if (Character.isLetter(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 10;
					} else if (currentChar == '"') {
						state = 6;
					} else if (Character.isDigit(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 7;
					} else {
						tokenValueBuffer += (char) currentChar;
						lexeme = new Lexeme(TokenType.INVALID_TOKEN, tokenValueBuffer, tokenPosition);
						state = 15;
					}
					break;
					
				case 2:
					if (currentChar == '*') {
						tokenValueBuffer += (char) currentChar;
						state = 3;
					} else if (currentChar == '/') {
						tokenValueBuffer += (char) currentChar;
						state = 5;
					} else {
						this.fileManager.unget(currentChar);
						state = 15;
					}
					break;
	
				case 3:
					if (currentChar == '*') {
						tokenValueBuffer += (char) currentChar;
						state = 4;
					} else if (currentChar == '\n') {
						tokenValueBuffer += (char) currentChar;
						this.newLine();
						//state = 3;
					} else if (currentChar == -1) {
						lexeme = new Lexeme(TokenType.UNEXPECTED_EOF, tokenValueBuffer, tokenPosition);
						state = 15;
					} else {
						tokenValueBuffer += (char) currentChar;
						//state = 3;
					}
					break;
	
				case 4:
					if (currentChar == '/') {
						tokenValueBuffer += (char) currentChar;
						state = 1;
					} else if (currentChar == -1) {
						lexeme = new Lexeme(TokenType.UNEXPECTED_EOF, tokenValueBuffer, tokenPosition);
						state = 15;
					} else if (currentChar == '\n') {
						this.newLine();
						state = 3;
					} else {
						state = 3;
					}
					break;
	
				case 5:
					if (currentChar == '\n') {
						this.newLine();
						state = 1;
					} else {
						state = 5;
					}
					break;
	
				case 6:
					if (currentChar == '"') {
						lexeme = new Lexeme(TokenType.LITERAL, tokenValueBuffer, tokenPosition);
						state = 14;
					} else if (currentChar == -1) {
						lexeme = new Lexeme(TokenType.UNEXPECTED_EOF, tokenValueBuffer, tokenPosition);
						state = 15;
					} else if (currentChar == '\n') {
						lexeme = new Lexeme(TokenType.INVALID_TOKEN, tokenValueBuffer, tokenPosition);
						this.newLine();
						state = 15;
					} else if (currentChar > 31 && currentChar < 127) {
						tokenValueBuffer += (char) currentChar;
						state = 6;
					}
					break;
	
				case 7:
					if (currentChar == '.') {
						tokenValueBuffer += (char) currentChar;
						state = 8;
					} else if (Character.isDigit(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 7;
					} else {
						this.fileManager.unget(currentChar);
						lexeme = new Lexeme(TokenType.INT_CONST, tokenValueBuffer, tokenPosition);
						state = 14;
					}
					break;
	
				case 8:
					if (Character.isDigit(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 9;
					} else {
						lexeme = new Lexeme(TokenType.INVALID_TOKEN, "", tokenPosition);
						state = 14;
					}
					break;
	
				case 9:
					if (Character.isDigit(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 9;
					} else {
						this.fileManager.unget(currentChar);
						lexeme = new Lexeme(TokenType.REAL_CONST, "", tokenPosition);
						state = 14;
					}
					break;
	
				case 10:
					if (currentChar == '_' || Character.isLetter(currentChar) || Character.isDigit(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 10;
					} else {
						this.fileManager.unget(currentChar);
						state = 15;
					}
					break;
	
				case 11:
					if (currentChar == '=') {
						tokenValueBuffer += (char) currentChar;
						state = 15;
					} else {
						this.fileManager.unget(currentChar);
						state = 15;
					}
					break;
	
				case 12:
					if (currentChar == '|') {
						tokenValueBuffer += (char) currentChar;
						state = 15;
					} else {
						lexeme = new Lexeme(TokenType.INVALID_TOKEN, "", tokenPosition);
						state = 14;
					}
					break;
	
				case 14:
					if (currentChar == '&') {
						tokenValueBuffer += (char) currentChar;
						state = 15;
					} else {
						lexeme = new Lexeme(TokenType.INVALID_TOKEN, "", tokenPosition);
						state = 14;
					}
					break;
				default:
					throw new LexicalException("Unreachable");
			}
		}

		if (state == 15) {
			TokenType tokenType = SymbolTable.get(tokenValueBuffer);
			if (tokenType != null) {
				lexeme = new Lexeme(tokenType, tokenValueBuffer, tokenPosition);
			} else {
				lexeme = new Lexeme(TokenType.ID, tokenValueBuffer, tokenPosition);
			}
		}
		
		return lexeme;
	}

	
}