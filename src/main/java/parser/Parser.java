package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lexicalanalyzer.LexicalAnalyzer;

public class Parser {

	LexicalAnalyzer lexer;
	HashMap<String, Set<String>> firstSet = new HashMap<>();
	HashMap<String, Set<String>> followSet = new HashMap<>();

	// e.g. key: APARAMS-id, value: EXPR REPTAPARAMS1
	// APARAMS with id transite to grammar EXPR REPTAPARAMS1
	HashMap<String, ArrayList<String>> parseTable = new HashMap<>();

	public Parser() {
		// TODO Auto-generated constructor stub
	}

	public Parser(LexicalAnalyzer lexer) {
		this.lexer = lexer;
	}

	public void parse() {

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
			firstSet.put(nonTerminal, firstElements.stream().map(Element::text).collect(Collectors.toSet()));
			// follow set
			Elements followElements = cols.get(2).children();
			followSet.put(nonTerminal, followElements.stream().map(Element::text).collect(Collectors.toSet()));
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
			terminals.add(th.getElementsByTag("terminal").first().text());
		}

		for (int i = 1; i < rows.size(); i++) {
			Element tr = rows.get(i);
			String nonTermial = tr.select("th").first().child(0).text();
			Elements td = tr.select("td");
			for (int j = 0; j < td.size(); j++) {
				if (td.get(j).childrenSize() > 0) {
					ArrayList<String> terms = new ArrayList<>();
					for (Element term : td.get(j).children()) {
						if (!term.text().equals(nonTermial)) {
							terms.add(term.text());
						}
					}
					parseTable.put(nonTermial + "::" + terminals.get(j), terms);
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new Parser().initFirstFollowSet();
		// new Parser().initParseTable();
	}

}
