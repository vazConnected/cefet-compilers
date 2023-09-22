package lexical;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lexical.token.TokenType;

public class SymbolTable {
	private static final Hashtable<String, TokenType> symbolTable = new Hashtable<String, TokenType>();
	private static final Hashtable<String, TokenType> languageElements = SymbolTable.getLanguageElements();
	
	private SymbolTable() {}
	
	public static boolean contains(String symbol) {
		return languageElements.contains(symbol);
	}
	
	public static TokenType getTokenType(String symbol) {
		//System.out.println("get token type: " + symbol + "     " + languageElements.get(symbol));
		return languageElements.containsKey(symbol) ? languageElements.get(symbol) : TokenType.ID;
	}
	
	public static String toText() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Symbol Table:\n");
		
		Set<String> keys =  symbolTable.keySet();
		Iterator<String> keysIterator = keys.iterator();
		while( keysIterator.hasNext() ) {
			String currentKey = keysIterator.next();
			
			stringBuilder.append("\t" + currentKey + ":\t\t\t");
			stringBuilder.append(symbolTable.get(currentKey) + "\n");
		}
			
		return stringBuilder.toString();
	}
	
	public static TokenType get(String symbol) {
		return SymbolTable.languageElements.getOrDefault(symbol, null);
	}
	
	public static String get(TokenType symbol) {
		for (Map.Entry<String, TokenType> entry : SymbolTable.languageElements.entrySet()) {
	        if (entry.getValue().equals(symbol)) {
	            return entry.getKey();
	        }
	    }
		
	    return null;
	}
	
	public static boolean contains(TokenType tokenType) {
		return SymbolTable.languageElements.containsValue(tokenType);
	}
	
	public static boolean languageContainsToken(String tokenValue) {
		return SymbolTable.languageElements.containsKey(tokenValue);
	}
	
	public static void addToSymbolTable(String symbol, TokenType tokenType){
		//System.out.println("add symbol: " + symbol + "    value: " + tokenType);
		symbolTable.put(symbol, tokenType);
	}
	
	public static Hashtable<String, TokenType> getSymbolTable() {
		Hashtable<String, TokenType> st = symbolTable;
		return st;
	}
	
	private static Hashtable<String, TokenType> getTableElements() {
		Hashtable<String, TokenType> st = new Hashtable<String, TokenType>();
		
		/*st.put(";", TokenType.SEMI_COLON);
      	st.put(",", TokenType.COMMA);
        st.put("(", TokenType.OPEN_PARENTHESIS);
      	st.put(")", TokenType.CLOSE_PARENTHESIS);	
        st.put("{", TokenType.OPEN_CURLY_BRACKET);
      	st.put("}", TokenType.CLOSE_CURLY_BRACKET);	
      	st.put("[", TokenType.OPEN_BRACKET);
      	st.put("]", TokenType.CLOSE_BRACKET);	
        st.put("\"", TokenType.QUOTATION);
				
        // OPERATORS
        st.put(">", TokenType.GREATER_THAN);
      	st.put(">=", TokenType.GREATER_EQUAL);
      	st.put("<", TokenType.LOWER_THAN);
      	st.put("<=", TokenType.LOWER_EQUAL);
        st.put("!=", TokenType.NOT_EQUAL);
        st.put("==", TokenType.EQUAL);
        st.put("!", TokenType.NOT);
		st.put("+", TokenType.ADD);
		st.put("-", TokenType.SUB);
        st.put("||", TokenType.OR);
        st.put("*", TokenType.MUL);
		st.put("/", TokenType.DIV);
        st.put("&&", TokenType.AND);
		st.put("=", TokenType.ASSIGN);
        st.put("==", TokenType.COMPARE);

        // KEYWORDS
		st.put("class", TokenType.CLASS);
		st.put("INT", TokenType.INT);
        st.put("string", TokenType.STRING);
        st.put("float", TokenType.FLOAT);
      	st.put("if", TokenType.IF);
      	st.put("else", TokenType.ELSE);
      	st.put("do", TokenType.DO);
      	st.put("while", TokenType.WHILE);
      	st.put("read", TokenType.READ);
		st.put("write", TokenType.WRITE);*/
		
		return st;
	}
	
	public static Hashtable<String, TokenType> getLanguageElements() {
		Hashtable<String, TokenType> st = new Hashtable<String, TokenType>();
		
		st.put(";", TokenType.SEMI_COLON);
      	st.put(",", TokenType.COMMA);
        st.put("(", TokenType.OPEN_PARENTHESIS);
      	st.put(")", TokenType.CLOSE_PARENTHESIS);	
        st.put("{", TokenType.OPEN_CURLY_BRACKET);
      	st.put("}", TokenType.CLOSE_CURLY_BRACKET);	
      	st.put("[", TokenType.OPEN_BRACKET);
      	st.put("]", TokenType.CLOSE_BRACKET);	
        st.put("\"", TokenType.QUOTATION);
				
        // OPERATORS
        st.put(">", TokenType.GREATER_THAN);
      	st.put(">=", TokenType.GREATER_EQUAL);
      	st.put("<", TokenType.LOWER_THAN);
      	st.put("<=", TokenType.LOWER_EQUAL);
        st.put("!=", TokenType.NOT_EQUAL);
        st.put("==", TokenType.EQUAL);
        st.put("!", TokenType.NOT);
		st.put("+", TokenType.ADD);
		st.put("-", TokenType.SUB);
        st.put("||", TokenType.OR);
        st.put("*", TokenType.MUL);
		st.put("/", TokenType.DIV);
        st.put("&&", TokenType.AND);
		st.put("=", TokenType.ASSIGN);
        st.put("==", TokenType.COMPARE);

        // KEYWORDS
		st.put("class", TokenType.CLASS);
		st.put("INT", TokenType.INT);
        st.put("string", TokenType.STRING);
        st.put("float", TokenType.FLOAT);
      	st.put("if", TokenType.IF);
      	st.put("else", TokenType.ELSE);
      	st.put("do", TokenType.DO);
      	st.put("while", TokenType.WHILE);
      	st.put("read", TokenType.READ);
		st.put("write", TokenType.WRITE);
		
		
		return st;
	}
}
