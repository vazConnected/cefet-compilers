package lexical;

import java.util.Map;
import java.util.HashMap;

public class SymbolTable 
{
    private Map<String, Integer> st;

    public SymbolTable() 
    {
        st = new HashMap<String, Integer>();
				
        // SYMBOLS
      	st.put(";", tag.SEMI_COLON);
      	st.put(",", tag.COLON);
        st.put("(", tag.OPEN_PAR);
      	st.put(")", tag.CLOSE_PAR);	
        st.put("{", tag.OPEN_CUR);
      	st.put("}", tag.CLOSE_CUR);	
      	st.put(".", tag.DOT);
        st.put("\"", tag.QUOTATION);
				
        // OPERATORS
        st.put(">", tag.GREATER_THAN);
      	st.put(">=", tag.GREATER_EQUAL);
      	st.put("<", tag.LOWER_THAN);
      	st.put("<=", tag.LOWER_EQUAL);
        st.put("!=", tag.NOT_EQUAL);
        st.put("==", tag.EQUAL);
        st.put("!", tag.NOT);
		st.put("+", tag.ADD);
		st.put("-", tag.SUB);
        st.put("||", tag.OR);
        st.put("*", tag.MUL);
		st.put("/", tag.DIV);
        st.put("&&", tag.AND);
		st.put("=", tag.ASSIGN);
        st.put("==", tag.COMPARE);

        // KEYWORDS
		st.put("class", tag.CLASS);
		st.put("INT", tag.INT);
        st.put("string", tag.STRING);
        st.put("float", tag.FLOAT);
      	st.put("if", tag.IF);
      	st.put("else", tag.ELSE);
      	st.put("do", tag.DO);
      	st.put("while", tag.WHILE);
      	st.put("read", tag.READ);
		st.put("write", tag.WRITE);        
    }

    public boolean contains(String token) {
        return st.containsKey(token);
    }

    public int find(String token) {
        return this.contains(token) ? st.get(token) : tag.ID;
    }

	public int get(String token) {
        return st.get(token);
    }

	public void add(String token, int tag) {
		st.put(token, tag);
	}	

	public void print() {
		for (Map.Entry<String, Integer> entrada : st.entrySet()) {
			String chave = entrada.getKey();
			Integer valor = entrada.getValue();
			System.out.println("tag: " + valor + " | Symbol: " + chave);
		}
	}

}