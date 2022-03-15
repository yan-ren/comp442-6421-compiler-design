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

import ast.Node;
import lexicalanalyzer.LexicalAnalyzer;
import parser.Parser;
import visitor.Util;
import visitor.SemanticCheckingVisitor;
import visitor.SymbolTableCreationVisitor;
import visitor.Visitor;

public class Driver {
    public static final String DEFAULT_OUTPUT = "./output/";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new Exception("Missing input file");
        }
        String inputFilePath = args[0];
        File input = new File(inputFilePath);
        processFile(input);
    }

    public static void processFile(File src) throws Exception {
        String fileName;
        FileReader fr = null;
        BufferedReader in = null;
        BufferedWriter outlextokens = null;
        BufferedWriter outlexerrors = null;
        BufferedWriter outderivation = null;
        BufferedWriter outsyntaxerrors = null;
        BufferedWriter outast = null;
        BufferedWriter outdot = null;
        BufferedWriter outsemanticerrors = null;
        BufferedWriter outsymboltables = null;

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

        String directory = DEFAULT_OUTPUT + fileName + "/";
        Files.createDirectories(Paths.get(directory));

        try {
            fr = new FileReader(src);
            in = new BufferedReader(fr);
            outlextokens = new BufferedWriter(new FileWriter(directory + fileName + ".outlextokens"));
            outlexerrors = new BufferedWriter(new FileWriter(directory + fileName + ".outlexerrors"));
            outderivation = new BufferedWriter(new FileWriter(directory + fileName + ".outderivation"));
            outsyntaxerrors = new BufferedWriter(new FileWriter(directory + fileName + ".outsyntaxerrors"));
            outast = new BufferedWriter(new FileWriter(directory + fileName + ".ast.outsat"));
            outdot = new BufferedWriter(new FileWriter(directory + fileName + ".dot.outsat"));
            outsemanticerrors = new BufferedWriter(new FileWriter(directory + fileName + ".outsemanticerrors"));
            outsymboltables = new BufferedWriter(new FileWriter(directory + fileName + ".outsymboltables"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            LexicalAnalyzer la = new LexicalAnalyzer(in, outlextokens, outlexerrors);
            Parser parser = new Parser(la, outderivation, outsyntaxerrors, outast, outdot);
            System.out.println("[debug] parser success: " + parser.parse());
            Visitor symbolTableCreationVisitor = new SymbolTableCreationVisitor(outsemanticerrors);
            Node astRoot = parser.getASTRoot();
            symbolTableCreationVisitor.visit(astRoot);
            Util.processSymbolTable(astRoot, outsemanticerrors);
            Util.printSymbolTableToFile(astRoot, outsymboltables);

            Visitor semanticCheckingVisitor = new SemanticCheckingVisitor(outsemanticerrors);
            semanticCheckingVisitor.visit(astRoot);
        } catch (Exception e) {
            throw e;
        } finally {
            in.close();
            outlextokens.close();
            outlexerrors.close();
            outderivation.close();
            outsyntaxerrors.close();
            outast.close();
            outdot.close();
            outsemanticerrors.close();
            outsymboltables.close();
        }
    }
}
