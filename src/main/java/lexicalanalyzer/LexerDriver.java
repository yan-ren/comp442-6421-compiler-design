package lexicalanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.Parser;

/**
 * This Driver only executes Lexical Analyzer part
 */
public class LexerDriver {

	public static final String DEFAULT_INPUT = "./input/lexer";
	public static final String DEFAULT_OUTPUT = "./output/lexer/";

	public static void main(String[] args) throws Exception {
		String fileDir = DEFAULT_INPUT;
		if (args != null && args.length != 0) {
			fileDir = args[0];
		}
		Files.createDirectories(Paths.get(DEFAULT_OUTPUT));

		if (fileDir.equals(DEFAULT_INPUT)) {
			File folder = new File(fileDir);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				processFile(listOfFiles[i]);
			}
		} else {
			File input = new File(fileDir);
			processFile(input);
		}
	}

	public static void processFile(File src) throws Exception {
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
			outlextokens = new BufferedWriter(new FileWriter(DEFAULT_OUTPUT + fileName + ".outlextokens"));
			outlexerrors = new BufferedWriter(new FileWriter(DEFAULT_OUTPUT + fileName + ".outlexerrors"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		LexicalAnalyzer la = new LexicalAnalyzer(in, outlextokens, outlexerrors);
		Token token = la.nextToken();
		while (token.getType() != Parser.END_OF_STACK) {
			token = la.nextToken();
		}

		in.close();
		outlextokens.close();
		outlexerrors.close();
	}
}
