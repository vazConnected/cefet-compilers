package lexical;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {

  private int line;
  private SymbolTable st;
  private PushbackInputStream input;

  public LexicalAnalysis(String filename) {
    try {
      input = new PushbackInputStream(new FileInputStream(filename));
    } catch (Exception e) {
      throw new LexicalException("Unable to open file");
    }

    st = new SymbolTable();
    line = 1;
  }

  public void close() {
    try {
      input.close();
    } catch (Exception e) {
      throw new LexicalException("Unable to close file");
    }
  }

  public int getLine() {
    return this.line;
  }

  public SymbolTable getSymbolTable() {
    return st;
  }

  public Lexeme nextToken() {
    Lexeme lex = new Lexeme("", tag.EOF);

    int state = 1;
    while (state != 14 && state != 15) {
      int c = getc();
      switch (state) {
        case 1:
          if (c == ' ' || c == '\t' || c == '\r') {
            state = 1;
          } else if (c == '\n') {
            line++;
            state = 1;
          } else if (c == '/') {
            lex.token += (char) c;
            state = 2;
          } else if (c == '"') {
            state = 6;
          } else if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 7;
          } else if (Character.isLetter(c)) {
            lex.token += (char) c;
            state = 10;
          } else if (c == '>' || c == '<' || c == '!' || c == '=') {
            lex.token += (char) c;
            state = 11;
          } else if (c == '|'){
            lex.token += (char) c;
            state = 12;
          } else if (c == '&'){
            lex.token += (char) c;
            state = 13;
          } else if (c == '+' || c == '-' || c == '*' || c == ';' ||
              c == '(' || c == ')' || c == '{' || c == '}') {
            lex.token += (char) c;
            state = 15;
          } else {
            lex.token += (char) c;
            lex.type = tag.INVALID_TOKEN;
            state = 15;
          }
          break;

        case 2:
          if (c == '*') {
            state = 3;
          } else if (c == '/') {
            state = 5;
          } else {
            ungetc(c);
            state = 15;
          }
          break;

        case 3:
          if (c == '*') {
            state = 4;
          } else if (c == '\n') {
            line++;
            state = 3;
          } else if (c == -1) {
            lex.type = tag.UNEXPECTED_EOF;
            state = 15;
          } else {
            state = 3;
          }
          break;
        
        case 4:
          if (c == '/') {
            state = 1;
          } else if (c == -1) {
            lex.type = tag.UNEXPECTED_EOF;
            state = 15;
          } else if (c == '\n') {
            line++;
            state = 3;
          } else {
            state = 3;
          }
          break;

        case 5:
          if (c == '\n') {
            state = 1;
          } else {
            state = 5;
          }
          break;

        case 6:
          if (c == '"') {
            lex.type = tag.LITERAL;
            state = 14;
          } else if (c == -1) {
            lex.token = "";
            lex.type = tag.UNEXPECTED_EOF;
            state = 15;
          } else if (c == '\n') {
            lex.type = tag.INVALID_TOKEN;
            state = 15;
          } else if (c > 31 && c < 127) {
            lex.token += (char) c;
            state = 6;
          }
          break;

        case 7:
          if (c == '.') {
            lex.token += (char) c;
            state = 8;
          } else if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 7;
          } else {
            ungetc(c);
            lex.type = tag.INT_CONST;
            state = 14;
          }
          break;

        case 8:
          if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 9;
          } else {
            lex.token = "";
            lex.type = tag.INVALID_TOKEN;
            state = 14;
          }
          break;

        case 9:
          if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 9;
          } else {
            ungetc(c);
            lex.type = tag.REAL_CONST;
            state = 14;
          }
          break;

        case 10:
          if (c == '_' || Character.isLetter(c) || Character.isDigit(c)) {
            lex.token += (char) c;
            state = 5;
          } else {
            ungetc(c);
            state = 15;
          }
          break;

        case 11:
          if (c == '=') {
            lex.token += (char) c;
            state = 15;
          } else {
            ungetc(c);
            state = 15;
          }
          break;

        case 12:
          if (c == '|') {
            lex.token += (char) c;
            state = 15;
          } else {
            lex.token = "";
            lex.type = tag.INVALID_TOKEN;
            state = 14;
          }
          break;
        
        case 14:
          if (c == '&') {
            lex.token += (char) c;
            state = 15;
          } else {
            lex.type = tag.INVALID_TOKEN;
            state = 14;
          }
          break;
        default:
          throw new LexicalException("Unreachable");
      }
    }

    if (state == 15) {
      if (st.contains(lex.token)) {
        lex.type = st.get(lex.token);
      } else {
        lex.type = tag.ID;
        st.add(lex.token, lex.type);
      }     
    }
    return lex;
  }

  private int getc() {
    try {
      return input.read();
    } catch (Exception e) {
      throw new LexicalException("Unable to read file");
    }
  }

  private void ungetc(int c) {
    if (c != -1) {
      try {
        input.unread(c);
      } catch (Exception e) {
        throw new LexicalException("Unable to ungetc");
      }
    }
  }
}