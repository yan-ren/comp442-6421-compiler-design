package lexicalanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import parser.Parser;

public class LexicalAnalyzer {

	public static String stateTransitionSeed = "1:0,33:1,2:2,2:3,34:4,3:5,4:6,24:7,21:8,26:9,12:10,14:11,17:12,19:13,25:14,27:15,23:16,1:17,1;"
			+ "2:1,2:2,2:3,2:4,2:5,2;" + "3:4,3:5,3:8,5;" + "4:4,35:5,35:8,5;" + "5:4,6:5,6;" + "6:2,8:4,6:5,7;"
			+ "7:4,6:5,7;" + "8:4,11:5,9:6,10:7,10;" + "10:4,11:5,9;" + "11:4,11:5,11;" + "12:9,13;" + "14:9,16:11,15;"
			+ "17:9,18;" + "19:12,20;" + "21:11,22;" + "27:13,30:14,28;"
			+ "28:0,28:1,28:2,28:3,28:4,28:5,28:6,28:7,28:8,28:9,28:10,28:11,28:12,28:13,28:14,28:15,28:16,28:17,29;"
			+ "34:1,34:2,34:3,34:4,34:5,34;" + "35:4,35:5,35";
	public static int statesNum = 35;
	public static int symbolsNum = 17;
	public static String finalStates = "2:id;3:intnum;4:intnum;5:invalidnum;6:floatnum;7:invalidnum;8:invalidnum;9:floatnum;10:invalidnum;11:floatnum;"
			+ "12:assign;13:eq;14:lt;15:noteq;16:leq;17:gt;18:geq;19:colon;20:coloncolon;21:minus;22:arrow;23:special_char;24:plus;25:mult;26:dot;27:div;"
			+ "29:inlinecmt;30:blockcmt;31:unterminated_block_cmt;33:invalidchar;34:invalidid;35:invalidnum";

	public static String symbols = "1:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ;" + "2:e;" + "3:_;"
			+ "4:123456789;" + "5:0;" + "6:+;" + "7:-;" + "8:.;" + "9:=;" + "10:<;" + "11:>;" + "13:*;" + "14:/;"
			+ "15:|&!(){}[],;" + "16: \t\r;" + "17:\n";

	public static String keywords = "if,then,else,integer,float,void,public,private,func,var,struct,while,read,write,return,self,inherits,let,impl";

	public static String specialChar = "|-or:&-and:!-not:(-openpar:)-closepar:{-opencubr:}-closecubr:[-opensqbr:]-closesqbr:;-semi:,-comma";

	private BufferedReader br;
	private int line;
	// state transition table
	public int[][] tb;
	// map state number to state name
	public HashMap<Integer, String> stateMap;
	// map character to its id
	public HashMap<Character, Integer> symbolMap;
	// map special symbol with name
	public HashMap<String, String> specialSymbolsMap;

	public Set<String> keywordsSet;
	private Queue<Character> charBuffer;

	BufferedWriter tokenLogger;
	BufferedWriter errorLogger;

	// constants
	private final int TRAP_STATE = 0;
	private final int START_STATE = 1;
	private final int ID_STATE = 2;
	private final int SPECIAL_SYMBOL_STATE = 23;
	private final int BLOCK_CMT_STATE = 30;
	private final int UNTERMINATED_BLOCK_CMT_STATE = 31;
	private final Set<Integer> INVALID_CHAR = new HashSet<>(Arrays.asList(33));
	private final Set<Integer> INVALID_ID = new HashSet<>(Arrays.asList(34));
	private final Set<Integer> INVALID_NUM = new HashSet<>(Arrays.asList(5, 7, 8, 10, 35));
	private final char EOF = (char) 0x04;
	private final int UNSUPPORTED_SYMBOL = 0;

	public LexicalAnalyzer(BufferedReader br, BufferedWriter outlextokens, BufferedWriter outlexerrors) {
		this.br = br;
		this.tokenLogger = outlextokens;
		this.errorLogger = outlexerrors;
		init();
	}

	public void init() {
		line = 1;
		charBuffer = new LinkedList<>();
		symbolMap = createSymbolMap();
		stateMap = createStateMap();
		createTb();
		specialSymbolsMap = createSpecialSymbolMap();
		keywordsSet = new HashSet<>(Arrays.asList(keywords.split(",")));
	}

	private HashMap<String, String> createSpecialSymbolMap() {
		HashMap<String, String> result = new HashMap<>();
		String[] symbols = specialChar.split(":");
		for (String symbol : symbols) {
			String[] tmp = symbol.split("-");
			result.put(tmp[0], tmp[1]);
		}
		return result;
	}

	private void createTb() {
		this.tb = new int[LexicalAnalyzer.statesNum + 1][LexicalAnalyzer.symbolsNum + 1];
		String[] trans = stateTransitionSeed.split(";");
		for (String t : trans) {
			String[] items = t.split(":");
			int start = Integer.parseInt(items[0]);
			for (int i = 1; i < items.length; i++) {
				String[] tmp = items[i].split(",");
				this.tb[start][Integer.parseInt(tmp[0])] = Integer.parseInt(tmp[1]);
			}
		}
	}

	private HashMap<Integer, String> createStateMap() {
		HashMap<Integer, String> map = new HashMap<>();
		String[] stateItems = finalStates.split(";");
		for (String state : stateItems) {
			String[] items = state.split(":");
			map.put(Integer.parseInt(items[0]), items[1]);
		}
		return map;
	}

	private HashMap<Character, Integer> createSymbolMap() {
		HashMap<Character, Integer> map = new HashMap<>();
		String[] symbolItems = symbols.split(";");
		for (String symbol : symbolItems) {
			String[] items = symbol.split(":");
			for (int i = 0; i < items[1].length(); i++) {
				map.put(items[1].charAt(i), Integer.parseInt(items[0]));
			}
		}
		// add special case
		map.put(':', 12);
		map.put(';', 15);

		return map;
	}

	public Token nextToken() throws Exception {
		Token token = null;
		int currentState = START_STATE;
		int nextState = START_STATE;
		String lexeme = "";

		do {
			char lookup = nextChar();
			if (lookup == EOF) {
				Token newToken = new Token(Parser.END_OF_STACK, Parser.END_OF_STACK, this.line);
				tokenLogger.write(newToken.toString() + "\n");
				return newToken;
			}
			nextState = tb[currentState][getSymbolNum(lookup)];
			if (nextState == TRAP_STATE) {
				storeChar(lookup);
				token = createToken(currentState, lexeme, line);
			} else {
				currentState = nextState;
				if (currentState != START_STATE) {
					lexeme += lookup;
				}
				if (currentState == BLOCK_CMT_STATE) {
					Token newToken = buildBlockCmt(lexeme);
					tokenLogger.write(newToken.toString() + "\n");
					return newToken;
				}
			}
		} while (token == null);

		tokenLogger.write(token.toString() + "\n");
		return token;
	}

	/**
	 * Special route to handle block comment
	 * 
	 * @param lexeme contains "/*"
	 * @param line   current line number
	 * @return
	 * @throws Exception
	 */
	private Token buildBlockCmt(String lexeme) throws Exception {
		int counter = 1;
		while (counter != 0) {
			char next = nextChar();
			lexeme += next;
			if (next == EOF) {
				return createToken(UNTERMINATED_BLOCK_CMT_STATE, lexeme, this.line);
			}
			if (next == '/') {
				char nextnext = nextChar();
				lexeme += nextnext;
				if (next == EOF) {
					return createToken(UNTERMINATED_BLOCK_CMT_STATE, lexeme, this.line);
				}
				if (nextnext == '*') {
					counter++;
				}
			} else if (next == '*') {
				char nextnext = nextChar();
				lexeme += nextnext;
				if (next == EOF) {
					return createToken(UNTERMINATED_BLOCK_CMT_STATE, lexeme, this.line);
				}
				if (nextnext == '/') {
					counter--;
				}
			}
		}

		return createToken(BLOCK_CMT_STATE, lexeme, this.line);
	}

	private void storeChar(char lookup) throws Exception {
		if (charBuffer.size() != 0) {
			throw new Exception("character buffer has value, cannot store in buffer");
		}

		charBuffer.add(lookup);
	}

	/**
	 * Create token based on state, add error message if exists
	 * 
	 * @param state
	 * @param lexeme
	 * @param line
	 * @return
	 * @throws Exception
	 */
	private Token createToken(int state, String lexeme, int line) throws Exception {
		if (!stateMap.containsKey(state)) {
			throw new Exception("invalid state: " + state + " cannot find state in stateMap");
		}

		int lineCalibrated = calculateLineNumber(line, lexeme);

		addErrorIfExists(state, lexeme, lineCalibrated);

		if (state == ID_STATE && keywordsSet.contains(lexeme)) {
			return new Token(lexeme, lexeme, lineCalibrated);
		} else if (state == SPECIAL_SYMBOL_STATE) {
			return createSymbolToken(state, lexeme, lineCalibrated);
		}

		String stateName = stateMap.get(state);

		return new Token(stateName, lexeme, lineCalibrated);
	}

	/**
	 * Recalculate line number consider \n char presented in char buffer and lexeme
	 * 
	 * @param line2
	 * @param lexeme
	 * @return
	 */
	private int calculateLineNumber(int line, String lexeme) {
		if (charBuffer.size() != 0 && charBuffer.peek() == '\n') {
			line--;
		}
		line -= StringUtils.countMatches(lexeme, "\n");
		return line;
	}

	private void addErrorIfExists(int state, String lexeme, int line) throws IOException {
		if (INVALID_CHAR.contains(state)) {
			errorLogger.write(
					"Lexical error: Invalid character: " + "\"" + StringEscapeUtils.escapeJava(lexeme) + "\": "
							+ "line " + line + "\n");
		}

		if (INVALID_ID.contains(state)) {
			errorLogger
					.write("Lexical error: Invalid identifier: " + "\"" + StringEscapeUtils.escapeJava(lexeme) + "\": "
							+ "line " + line + "\n");
		}

		if (INVALID_NUM.contains(state)) {
			errorLogger.write("Lexical error: Invalid number: " + "\"" + StringEscapeUtils.escapeJava(lexeme) + "\": "
					+ "line " + line + "\n");
		}

		if (state == UNTERMINATED_BLOCK_CMT_STATE) {
			errorLogger.write(
					"Lexical error: Invalid block comments: " + "\"" + StringEscapeUtils.escapeJava(lexeme) + "\": "
							+ "line " + line + "\n");
		}
	}

	private Token createSymbolToken(int state, String lexeme, int line) throws Exception {
		if (specialSymbolsMap.containsKey(lexeme)) {
			return new Token(specialSymbolsMap.get(lexeme), lexeme, line);
		}
		throw new Exception("unknown symbols for state: " + state + " symbol: " + lexeme);
	}

	private int getSymbolNum(char c) {
		return symbolMap.getOrDefault(c, UNSUPPORTED_SYMBOL);
	}

	private char nextChar() {
		if (charBuffer.size() != 0) {
			return charBuffer.remove();
		}

		char result = 0;
		try {
			int c = br.read();
			if (c == -1) {
				return EOF;
			} else if (c == 10) {
				line++;
			}
			result = (char) c;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}
