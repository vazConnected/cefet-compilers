package lexical.token;

public record TokenPosition (Integer line, Integer characterPositionInLine) {
	@Override
	public String toString() {
	    return "{" +
	           "linha=" + line +
	           ", caractere=" + characterPositionInLine +
	           '}';
	}
}
