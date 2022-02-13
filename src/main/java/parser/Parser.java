package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lexicalanalyzer.Constants;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;

public class Parser {

	LexicalAnalyzer lexer;
	HashMap<String, Set<String>> firstSet = new HashMap<>();
	HashMap<String, Set<String>> followSet = new HashMap<>();

	// e.g. key: APARAMS-id, value: EXPR REPTAPARAMS1
	// APARAMS with id transite to grammar EXPR REPTAPARAMS1
	HashMap<String, ArrayList<String>> parseTable = new HashMap<>();

	Stack<String> stack = new Stack<>();

	// constants
	public static String END_OF_STACK = "$";
	public static String STARTING_SYMBOL = "START";

	public Parser(LexicalAnalyzer lexer) throws IOException {
		this.lexer = lexer;
		initFirstFollowSet();
		initParseTable();
	}

	public boolean parse() throws Exception {
		boolean error = false;
		stack.push(END_OF_STACK);
		stack.push(STARTING_SYMBOL);
		Token a = lexer.nextToken();
		while (!stack.peek().equals(END_OF_STACK)) {
			String x = stack.peek();
			if (isTerminal(x)) {
				if (x.equals(a.getType())) {
					stack.pop();
					a = lexer.nextToken();
				} else {
					a = skipErrors(a);
					error = true;
				}
			} else {
				if (parseTableLookup(x, a) != null) {
					stack.pop();
					inverseRHSMultiplePush(parseTableLookup(x, a));
				} else {
					a = skipErrors(a);
					error = true;
				}
			}
		}

		if (!a.getType().equals(END_OF_STACK) || error) {
			return false;
		} else {
			return true;
		}
	}

	private Token skipErrors(Token lookahead) throws Exception {
		System.out.println("syntax error at: " + lookahead.getLocation() + " Token: " + lookahead.toString());
		if (lookahead.getType().equals(END_OF_STACK) || followSet.get(stack.peek()).contains(lookahead.getType())) {
			stack.pop();
		} else {
			while (!firstSet.get(stack.peek()).contains(lookahead.getType())
					|| (firstSet.get(stack.peek()).contains(Constants.UC_TYPE.EPSILON_WORD)
							&& !followSet.get(stack.peek()).contains(lookahead.getType()))) {
				lookahead = lexer.nextToken();
			}
		}

		return lookahead;
	}

	private void inverseRHSMultiplePush(ArrayList<String> terms) {
		if (terms.size() < 1) {
			return;
		}
		ArrayList<String> newTerms = new ArrayList<>(terms);
		Collections.reverse(newTerms);
		for (String s : newTerms) {
			stack.push(s);
		}
	}

	private ArrayList<String> parseTableLookup(String x, Token a) {
		return parseTable.get(x + "::" + a.getType());
	}

	private boolean isTerminal(String x) {
		return Constants.SYNTACTIC_ANALYZER_TERMINAL.contains(x);
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

		// special case: end of program
		parseTable.put("REPTPROG0::$", new ArrayList<>());
	}
}
