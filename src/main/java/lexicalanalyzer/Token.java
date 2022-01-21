package lexicalanalyzer;

import org.apache.commons.text.StringEscapeUtils;

public class Token {

	private String type;
	private String lexeme;
	private int location;

	public Token(String type, String lexeme) {
		this.type = type;
		this.lexeme = lexeme;
	}

	public Token(String type, String lexeme, int location) {
		this.type = type;
		this.lexeme = lexeme;
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLexeme() {
		return lexeme;
	}

	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "[" + type + ", " + StringEscapeUtils.escapeJava(lexeme) + ", " + location + ']';
	}
}
