package lexical;

import lexical.token.TokenPosition;
import lexical.token.TokenType;

//public class Lexeme {
//	public TokenType tokenType;
//	public String tokenValue;
//	public TokenPosition position;
	
public record Lexeme(TokenType tokenType, String tokenValue, TokenPosition position) {
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Token: ");
		stringBuilder.append(this.tokenType);
		
		stringBuilder.append(", value: ");
		stringBuilder.append(this.tokenValue);
		
		stringBuilder.append("position: ");
		stringBuilder.append(position);
		
		return stringBuilder.toString();
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
