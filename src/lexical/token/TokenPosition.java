package lexical.token;

public record TokenPosition (Integer line, Integer characterPositionInLine) {
	@Override
	public String toString() {
		return "[ line " + this.line.toString() + ", character number " + this.characterPositionInLine.toString() + " ]";
	}
	
}
