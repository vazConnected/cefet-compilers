package lexical;

import lexical.token.TokenPosition;
import lexical.token.TokenType;

public record Lexeme(TokenType tokenType, String tokenValue, TokenPosition position) {
	@Override
	public String toString() {
	    return "{" +
	           "token=" + tokenType +
	           ", valor='" + tokenValue + '\'' +
	           ", posicao=" + position.toString() +
	           '}';
	}

	@Override
	public boolean equals(Object object) {
		if ( !(object instanceof Lexeme) ) return false;
		
		Lexeme lexeme = (Lexeme) object;
		return lexeme.tokenType.equals(this.tokenType) && lexeme.tokenValue.equals(this.tokenValue) && lexeme.position.equals(this.position);
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((tokenType == null) ? 0 : tokenType.hashCode());
	    result = prime * result + ((tokenValue == null) ? 0 : tokenValue.hashCode());
	    result = prime * result + ((position == null) ? 0 : position.hashCode());
	    return result;
	}
}
