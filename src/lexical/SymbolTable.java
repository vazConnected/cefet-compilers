package lexical;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lexical.token.TokenType;
import syntatic.SyntacticException;

public class SymbolTable {
	private static Map<String, TokenType> symbolTable = new HashMap<String, TokenType>();
	private static Map<String, TokenType> variables = new HashMap<String, TokenType>();
	
	private SymbolTable() {}
		
	public static String toText() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Tabela de simbolos:\n");
		
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
		return symbolTable.containsKey(symbol) || variables.containsKey(symbol);
	}
	
	public static void addToSymbolTable(String symbol, TokenType tokenType){
		symbolTable.put(symbol, tokenType);
	}

	public static void registerVariableDeclaration(String identifier, TokenType tokenType) {
		if (tokenType != TokenType.INT && tokenType != TokenType.FLOAT && tokenType != TokenType.STRING) {
			throw new SyntacticException("O tipo " + tokenType + " nao e valido para a linguagem");
		}
		
		SymbolTable.variables.put(identifier, tokenType);
	}
	
	public static TokenType getVariableType(String identifier) {
		return SymbolTable.variables.getOrDefault(identifier, null);
	}
}
