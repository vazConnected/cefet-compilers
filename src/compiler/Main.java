package compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import lexical.Lexeme;
import lexical.LexicalAnalyzer;
import lexical.SymbolTable;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		System.out.print(SymbolTable.toText());
		
		if (args.length == 0) {
			throw new IllegalArgumentException("Nenhum arquivo encontrado. É necessário informar o arquivo desejado para a execução do software");
		} else {
			File file = new File(args[0]);
			if (!file.isFile()) {
				throw new FileNotFoundException("O caminho informado não existe ou não corresponde a um arquivo. Informe um arquivo válido.");
			}
		}
		
		try {
			LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(args[0]);
			
			System.out.println("\nTokens:");
			List<Lexeme> lexemes = lexicalAnalyzer.getListOfLexemes();
			for(Lexeme lexeme: lexemes) {
				System.out.println("\t" + lexeme);
			}
			
		} catch (IOException e) {
			System.err.println("Não foi possível executar a análise léxica: \n\t" + e.getMessage());
		}
		

	}

}
