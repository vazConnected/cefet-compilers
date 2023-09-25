package lexical;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lexical.token.TokenType;

public class SymbolTable {
	private static Map<String, TokenType> symbolTable = new HashMap<String, TokenType>();
	
	private SymbolTable() {}
		
	public static String toText() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Symbol Table:\n");
		
		if (SymbolTable.symbolTable.size() == 0) {
			stringBuilder.append("\tNenhum simbolo na registrado");
			return stringBuilder.toString();
		}
		
		Set<String> keys =  SymbolTable.symbolTable.keySet();
		Iterator<String> keysIterator = keys.iterator();
		while( keysIterator.hasNext() ) {
			String currentKey = keysIterator.next();
			
			stringBuilder.append("\t" + currentKey + ":\t\t\t");
			stringBuilder.append(symbolTable.get(currentKey) + "\n");
		}
			
		return stringBuilder.toString();
	}
	
	public static TokenType get(String symbol) {
		return SymbolTable.symbolTable.getOrDefault(symbol, null);
	}
	
	public static boolean contains(String symbol) {
		return SymbolTable.symbolTable.containsKey(symbol);
	}
	
	public static void addToSymbolTable(String symbol, TokenType tokenType){
		symbolTable.put(symbol, tokenType);
	}

}
