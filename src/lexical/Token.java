package lexical;

import lexical.token.TokenType;

public record Token(String tokenValue, TokenType tokenType) {
	@Override
	public String toString() {
	    return  tokenValue +
	           ": " + tokenType;
	}

	@Override
	public boolean equals(Object object) {
		if ( !(object instanceof Token) ) return false;
		
		Token token = (Token) object;
		return token.tokenValue.equals(this.tokenValue) && token.tokenType.equals(this.tokenType);
	}
}
