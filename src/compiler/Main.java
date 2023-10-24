package compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import lexical.LexicalAnalyzer;
import syntatic.SyntacticAnalyzer;

public class Main {

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
			throw new IllegalArgumentException("Nenhum arquivo encontrado. É necessário informar o arquivo desejado para a execução do compilador");
		} else {
			File file = new File(args[0]);
			if (!file.isFile()) {
				throw new FileNotFoundException("O caminho informado não existe ou não corresponde a um arquivo. Informe um arquivo válido.");
			}
		}
		SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer(new LexicalAnalyzer(args[0]));
		syntacticAnalyzer.run();
	}
}
