package lexical;

public class Lexeme {

    public String token;
    public int type;

    public Lexeme(String token, int type) 
    {
        this.token = token;
        this.type = type;
    }
}
