package lexicalanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Driver {
	public static void main(String[] args) throws Exception {
//		File f = new File("/Users/yan.ren/github.com/yan.ren/comp6421/src/lexicalanalyzer/input.txt");
//		File src = new File("/Users/yan.ren/github.com/yan.ren/comp6421/handout/A1/lexpositivegrading.src");
		File src = new File("/Users/yan.ren/github.com/yan.ren/comp6421/handout/A1/lexnegativegrading.src");
		String fileName;
		FileReader fr = null;
		BufferedReader in = null;
		BufferedWriter outlextokens = null;
		BufferedWriter outlexerrors = null;

		if (!src.getName().endsWith(".src")) {
			throw new Exception("invalid input file: " + src.getName() + ", should end with .src");
		}
		Pattern pattern = Pattern.compile("(.*)\\.src");
		Matcher matcher = pattern.matcher(src.getName());
		if (matcher.find()) {
			fileName = matcher.group(1);
		} else {
			throw new Exception("invalid input file: " + src.getName() + ", check the file name again");
		}

		try {
			fr = new FileReader(src);
			in = new BufferedReader(fr);
			outlextokens = new BufferedWriter(new FileWriter(fileName + ".outlextokens"));
			outlexerrors = new BufferedWriter(new FileWriter(fileName + ".outlexerrors"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		LexicalAnalyzer la = new LexicalAnalyzer(in);
		Token token = la.nextToken();
		while (token != null) {
			outlextokens.write(token + "\n");
			token = la.nextToken();
		}

		if (la.hasErrors()) {
			for (String error : la.getErrors()) {
				outlexerrors.write(error + "\n");
			}
		}

		in.close();
		outlextokens.close();
		outlexerrors.close();
	}
}
