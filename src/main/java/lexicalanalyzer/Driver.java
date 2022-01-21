package lexicalanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Driver {
	public static void main(String[] args) throws Exception {
//		File f = new File("/Users/yan.ren/github.com/yan.ren/comp6421/src/lexicalanalyzer/input.txt");
		File f = new File("/Users/yan.ren/github.com/yan.ren/comp6421/handout/A1/lexpositivegrading.src");
		FileReader fr = null;
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);

		LexicalAnalyzer la = new LexicalAnalyzer(br);
		Token token = la.nextToken();
		while (token != null) {
			System.out.println(token);
			token = la.nextToken();
		}
	}
}
