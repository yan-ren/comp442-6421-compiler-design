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

import lexicalanalyzer.LexicalAnalyzer;
import parser.Parser;

public class Driver {
    public static final String DEFAULT_OUTPUT = "./output/";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new Exception("Missing input file");
        }
        String fileDir = args[0];
        Files.createDirectories(Paths.get(DEFAULT_OUTPUT));

        File input = new File(fileDir);
        processFile(input);
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
        Parser parser = new Parser(la);
        parser.parse();

        in.close();
        outlextokens.close();
        outlexerrors.close();
    }
}