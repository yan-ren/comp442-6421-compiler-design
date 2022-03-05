package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ast.Node;
import ast.SemanticAction;
import lexicalanalyzer.Constants;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;

public class Parser {

	LexicalAnalyzer lexer;
	BufferedWriter derivationLogger;
	BufferedWriter errorLogger;
	BufferedWriter astLogger;
	BufferedWriter outdot;
	HashMap<String, Set<String>> firstSet = new HashMap<>();
	HashMap<String, Set<String>> followSet = new HashMap<>();

	// e.g. key: APARAMS-id, value: EXPR REPTAPARAMS1
	// APARAMS with id transite to grammar EXPR REPTAPARAMS1
	Map<String, ArrayList<String>> parseTable = new TreeMap<>();

	Stack<String> parsingStack = new Stack<>();
	Stack<Node> semanticStack = new Stack<>();
	Set<String> nonTermials = new HashSet<>();

	String derivation = "";

	// always cache the previous token
	Token tokenCache = null;
	// global variable tracks the token currently being used
	Token currentToken = null;

	// constants
	public static String END_OF_STACK = "$";
	public static String STARTING_SYMBOL = "START";

	public Parser(LexicalAnalyzer lexer, BufferedWriter outderivation, BufferedWriter outsyntaxerrors,
			BufferedWriter outast, BufferedWriter outdot)
			throws IOException {
		this.lexer = lexer;
		this.derivationLogger = outderivation;
		this.errorLogger = outsyntaxerrors;
		this.astLogger = outast;
		this.outdot = outdot;
		initFirstFollowSet();
		// initParseTable();
		initParseTableFromJson();
		// special case: end of program
		this.parseTable.put("REPTPROG0::$", new ArrayList<>());
		createNonTerminalsFromParseTable();
	}

	public boolean parse() throws Exception {
		boolean error = false;
		parsingStack.push(END_OF_STACK);
		parsingStack.push(STARTING_SYMBOL);
		derivation = STARTING_SYMBOL;
		derivationLogger.write(derivation + "\n");

		Token a = nextToken();

		while (!parsingStack.peek().equals(END_OF_STACK)) {
			String x = parsingStack.peek();
			if (isTerminal(x)) {
				if (x.equals(a.getType())) {
					parsingStack.pop();
					a = nextToken();
				} else {
					a = skipErrors(a);
					error = true;
				}
			} else if (isNonTerminal(x)) {
				if (parseTableLookup(x, a) != null) {
					parsingStack.pop();
					inverseRHSMultiplePush(parseTableLookup(x, a));
					// logging
					logDerivation(a, x);
				} else {
					a = skipErrors(a);
					error = true;
				}
			}
			// x ∈ SemanticActions
			else {
				semanticActions(x);
				parsingStack.pop();
			}
		}

		Node.postProcessing(semanticStack.peek());
		Node.printTreeToFile(semanticStack.peek(), astLogger);
		Node.createDotFile(semanticStack.peek(), outdot);
		if (!a.getType().equals(END_OF_STACK) || error) {
			return false;
		} else {
			return true;
		}
	}

	private void semanticActions(String action) throws Exception {
		if (!SemanticAction.SEMANTIC_ACTION_TABLE.containsKey(action)) {
			throw new Exception("invalid semantic action symbol: " + action);
		}
		switch (SemanticAction.SEMANTIC_ACTION_TABLE.get(action).get(0)) {
			case "makeNode": {
				SemanticAction.makeNode(this.semanticStack, this.tokenCache);
				break;
			}
			case "pushNull": {
				this.semanticStack.push(null);
				break;
			}
			case "makeFamilyUntil": {
				SemanticAction.makeFamilyUntil(this.semanticStack,
						SemanticAction.SEMANTIC_ACTION_TABLE.get(action).get(1));
				break;
			}
			case "makeFamily": {
				SemanticAction.makeFamily(this.semanticStack,
						SemanticAction.SEMANTIC_ACTION_TABLE.get(action).get(1),
						Integer.parseInt(SemanticAction.SEMANTIC_ACTION_TABLE.get(action).get(2)));
				break;
			}
			case "makeNodeEmptySizeArray": {
				SemanticAction.makeNodeEmptySizeArray(this.semanticStack, "emptySizeArray");
				break;
			}
			default:
				throw new Exception("invalid semantic action symbol-function map: " + action + "->"
						+ SemanticAction.SEMANTIC_ACTION_TABLE.get(action).get(0));
		}
	}

	private void logDerivation(Token a, String x) throws IOException {
		String d = parseTableLookup(x, a).stream().collect(Collectors.joining(" "));
		derivation = derivation.replaceFirst(x, d);
		// formatting
		derivation = derivation.replaceAll("  ", " ");
		derivationLogger.write("START => " + derivation + "\n");
	}

	private Token skipErrors(Token lookahead) throws Exception {
		String errorMsg = "syntax error at: " + lookahead.getLocation() + " Token: " + lookahead.toString();
		System.out.println(errorMsg);
		errorLogger.write(errorMsg + "\n");
		/*
		 * if ( lookahead is $ or in FOLLOW( top() ) )
		 * pop()
		 * pop - equivalent to A → ε
		 */
		if (lookahead.getType().equals(END_OF_STACK)
				|| (followSet.get(parsingStack.peek()) != null
						&& followSet.get(parsingStack.peek()).contains(lookahead.getType()))) {
			parsingStack.pop();
		} else {
			/*
			 * while ( lookahead ∉ FIRST( top() ) or
			 * ε ∈ FIRST( top() ) and lookahead ∉ FOLLOW( top() ) )
			 * 
			 * lookahead = nextToken()
			 */
			while ((firstSet.get(parsingStack.peek()) == null
					|| !firstSet.get(parsingStack.peek()).contains(lookahead.getType()))
					&& !((firstSet.get(parsingStack.peek()) != null
							&& firstSet.get(parsingStack.peek()).contains(Constants.UC_TYPE.EPSILON_WORD))
							&& (followSet.get(parsingStack.peek()) != null
									&& followSet.get(parsingStack.peek()).contains(lookahead.getType())))) {
				lookahead = nextToken();
				// edge case: skipping error until end of file
				if (lookahead.getType().equals(END_OF_STACK)) {
					return lookahead;
				}
			}
		}

		return lookahead;
	}

	/**
	 * Get next token from lexical analyzer by ignoring commet token
	 * 
	 * @return
	 * @throws Exception
	 */
	private Token nextToken() throws Exception {
		Token t = lexer.nextToken();
		while (t.getType().equals(Constants.LA_TYPE.INLINECMT) || t.getType().equals(Constants.LA_TYPE.BLOCKCMT)) {
			t = lexer.nextToken();
		}
		if (currentToken != null) {
			tokenCache = (Token) currentToken.clone();
		}
		currentToken = t;
		return t;
	}

	private void inverseRHSMultiplePush(ArrayList<String> terms) {
		if (terms.size() < 1) {
			return;
		}
		// Semantic Special Case
		/**
		 * (id sa1 sa2 sa4 sa11 dot id sa1 sa2 sa4 sa11 <<special>> ASSIGNOP EXPR sa22
		 * sa17 semi sa12)
		 */
		if (terms.get(0).equals("SPECIAL_ASSIGN")) {
			int count = 0;

			if (parsingStack.peek().equals("sa12")) {
				while (parsingStack.peek().equals("sa12")) {
					count++;
					parsingStack.pop();
				}
			}
			ArrayList<String> newTerms = new ArrayList<>(terms.subList(1, terms.size()));
			Collections.reverse(newTerms);
			for (String s : newTerms) {
				parsingStack.push(s);
			}

			while (count > 0) {
				parsingStack.push("sa12");
				count--;
			}

			return;
		}
		// Normal case, reverse push
		ArrayList<String> newTerms = new ArrayList<>(terms);
		Collections.reverse(newTerms);
		for (String s : newTerms) {
			parsingStack.push(s);
		}
	}

	private ArrayList<String> parseTableLookup(String x, Token a) {
		return parseTable.get(x + "::" + a.getType());
	}

	private boolean isTerminal(String x) {
		return Constants.SYNTACTIC_ANALYZER_TERMINAL.contains(x);
	}

	private boolean isNonTerminal(String x) {
		return this.nonTermials.contains(x);
	}

	public void initFirstFollowSet() throws IOException {
		File input = new File("./input/first-follow-set.html");
		Document doc = Jsoup.parse(input, "UTF-8", "https://smlweb.cpsc.ucalgary.ca/start.html");
		Element table = doc.select("table[class=stats]").first();
		Elements rows = table.select("tr");
		for (int i = 1; i < rows.size(); i++) { // first row is the col names, skip it
			Element row = rows.get(i);
			Elements cols = row.select("td");
			// nonterminal
			String nonTerminal = cols.get(0).child(0).text();
			// first set
			Elements firstElements = cols.get(1).children();
			firstSet.put(nonTerminal,
					firstElements.stream().map(Element::text)
							.map(key -> Constants.UC_TYPE_TO_LA_TYPE.getOrDefault(key, key))
							.collect(Collectors.toSet()));
			// follow set
			Elements followElements = cols.get(2).children();
			followSet.put(nonTerminal,
					followElements.stream().map(Element::text)
							.map(key -> Constants.UC_TYPE_TO_LA_TYPE.getOrDefault(key, key))
							.collect(Collectors.toSet()));
		}
	}

	public void initParseTable() throws IOException {
		File input = new File("./input/parse-table.html");
		Document doc = Jsoup.parse(input, "UTF-8", "https://smlweb.cpsc.ucalgary.ca/start.html");
		Element table = doc.select("table[class=parse_table]").first();
		Elements rows = table.select("tr");
		Element firstRow = rows.get(0);
		ArrayList<String> terminals = new ArrayList<>();
		for (Element th : firstRow.select("th")) {
			terminals.add(Constants.UC_TYPE_TO_LA_TYPE.getOrDefault(th.getElementsByTag("terminal").first().text(),
					th.getElementsByTag("terminal").first().text()));
		}

		for (int i = 1; i < rows.size(); i++) {
			Element tr = rows.get(i);
			String nonTermial = tr.select("th").first().child(0).text();
			this.nonTermials.add(nonTermial);
			Elements td = tr.select("td");
			for (int j = 0; j < td.size(); j++) {
				if (td.get(j).childrenSize() > 0) {
					ArrayList<String> terms = new ArrayList<>();
					// skip first one
					/*
					 * <td>
					 * <nonterm>REPTFUNCBODY1</nonterm> → <nonterm>VARDECLORSTAT</nonterm>
					 * <nonterm>REPTFUNCBODY1</nonterm>
					 * </td>
					 */
					for (int z = 1; z < td.get(j).children().size(); z++) {
						Element term = td.get(j).children().get(z);
						terms.add(Constants.UC_TYPE_TO_LA_TYPE.getOrDefault(term.text(), term.text()));
					}
					parseTable.put(nonTermial + "::" + terminals.get(j), terms);
				}
			}
		}
	}

	private void initParseTableFromJson() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String parseTableJson = new String(Files.readAllBytes(Paths.get("./input/parse_table_attribute.json")));
		this.parseTable = mapper.readValue(parseTableJson, Map.class);
	}

	private void createNonTerminalsFromParseTable() {
		for (String key : this.parseTable.keySet()) {
			this.nonTermials.add(key.split("::")[0]);
		}
	}
}
