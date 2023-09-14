package lexical;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

import lexical.token.TokenPosition;
import lexical.token.TokenType;

public class LexicalAnalyzer implements AutoCloseable {
	private int lineCounter;
	private int chracterInLineCounter;
	private String filePath;
	
	private PushbackInputStream input;

	public LexicalAnalyzer(String filePath) throws FileNotFoundException {
		this.filePath = filePath;
		
		this.input = new PushbackInputStream(new FileInputStream(this.filePath));
		
		this.lineCounter = 1;
		this.chracterInLineCounter = 0;
	}

	public void close() throws IOException {
		input.close();
	}

//	public int getLineNumber() {
//		return this.lineCounter;
//	}
	
	public List<Lexeme> getListOfLexemes() throws FileNotFoundException {
		this.lineCounter = 1;
		this.chracterInLineCounter = 0;
		
		this.input = new PushbackInputStream(new FileInputStream(this.filePath));
		
		List<Lexeme> lexemes = new ArrayList<Lexeme>();
		while (true) {
			try {
				lexemes.add(this.nextToken());
			} catch (IOException e) {
				break; // TODO: melhorar implementação. É necessário parar quando terminar o arquivo
			}
		}
		
		return lexemes;
		
	}
	
	public boolean hasNextToken() {
		// TODO
		throw new RuntimeException("Implementar hasNextToken()");
	}


	public Lexeme nextToken() throws IOException {
		TokenPosition tokenPosition = new TokenPosition(lineCounter, chracterInLineCounter);
		String tokenValueBuffer = "";
		
		Lexeme lexeme = null;

		int state = 1;
		while (state != 14 && state != 15) {
			int currentChar = getc();
			chracterInLineCounter++;
			
			switch (state) {
				case 1:
					if (currentChar == ' ' || currentChar == '\t' || currentChar == '\r') {
						state = 1;
					} else if (currentChar == '\n') {
						lineCounter++;
						chracterInLineCounter = 0;
						state = 1;
					} else if (currentChar == '/') {
						tokenValueBuffer += (char) currentChar;
						state = 2;
					} else if (currentChar == '"') {
						state = 6;
					} else if (Character.isDigit(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 7;
					} else if (Character.isLetter(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 10;
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
					} else {
						tokenValueBuffer += (char) currentChar;
						lexeme = new Lexeme(TokenType.INVALID_TOKEN, tokenValueBuffer, tokenPosition);
						state = 15;
					}
					break;
	
				case 2:
					if (currentChar == '*') {
						state = 3;
					} else if (currentChar == '/') {
						state = 5;
					} else {
						ungetc(currentChar);
						state = 15;
					}
					break;
	
				case 3:
					if (currentChar == '*') {
						state = 4;
					} else if (currentChar == '\n') {
						chracterInLineCounter++;
						state = 3;
					} else if (currentChar == -1) {
						lexeme = new Lexeme(TokenType.UNEXPECTED_EOF, tokenValueBuffer, tokenPosition);
						state = 15;
					} else {
						state = 3;
					}
					break;
	
				case 4:
					if (currentChar == '/') {
						state = 1;
					} else if (currentChar == -1) {
						lexeme = new Lexeme(TokenType.UNEXPECTED_EOF, tokenValueBuffer, tokenPosition);
						state = 15;
					} else if (currentChar == '\n') {
						chracterInLineCounter++;
						state = 3;
					} else {
						state = 3;
					}
					break;
	
				case 5:
					if (currentChar == '\n') {
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
						// lex.tokenValue = "";
						lexeme = new Lexeme(TokenType.UNEXPECTED_EOF, tokenValueBuffer, tokenPosition);
						state = 15;
					} else if (currentChar == '\n') {
						lexeme = new Lexeme(TokenType.INVALID_TOKEN, tokenValueBuffer, tokenPosition);
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
						ungetc(currentChar);
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
						ungetc(currentChar);
						lexeme = new Lexeme(TokenType.REAL_CONST, "", tokenPosition);
						state = 14;
					}
					break;
	
				case 10:
					if (currentChar == '_' || Character.isLetter(currentChar) || Character.isDigit(currentChar)) {
						tokenValueBuffer += (char) currentChar;
						state = 5;
					} else {
						ungetc(currentChar);
						state = 15;
					}
					break;
	
				case 11:
					if (currentChar == '=') {
						tokenValueBuffer += (char) currentChar;
						state = 15;
					} else {
						ungetc(currentChar);
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
			if ( lexeme != null && SymbolTable.contains(lexeme.tokenType())) {
				lexeme = new Lexeme( SymbolTable.get(lexeme.tokenValue()), tokenValueBuffer, tokenPosition);
			} else {
				lexeme = new Lexeme(TokenType.ID, tokenValueBuffer, tokenPosition);
			}
		}
		
		return lexeme;
	}

	private int getc() throws IOException {
		int currentCharCode = input.read();
		this.chracterInLineCounter++;
		
		return currentCharCode;
	}

	private void ungetc(int c) throws IOException {
		if (c != -1) {
			input.unread(c);
			this.chracterInLineCounter--;
		}
	}
}