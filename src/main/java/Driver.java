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
import visitor.CodeGenVisitor;
import visitor.ComputeMemSizeVisitor;
import visitor.FuncHeaderVisitor;
import visitor.SemanticCheckingVisitor;
import visitor.SymbolTableCreationVisitor;
import visitor.Util;
import visitor.Visitor;

/*
This driver runs the complete compiler, which includes lexical analyzer, parser, AST generation, semantic analysis, code generation
*/
public class Driver {
    public static final String DEFAULT_OUTPUT = "./output/code_gen/";

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
        BufferedWriter outcgsymboltables = null;
        BufferedWriter outmoon = null;

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
            outcgsymboltables = new BufferedWriter(new FileWriter(directory + fileName + ".cg.outsymboltables"));
            outmoon = new BufferedWriter(new FileWriter(directory + fileName + ".m"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            LexicalAnalyzer la = new LexicalAnalyzer(in, outlextokens, outlexerrors);
            Parser parser = new Parser(la, outderivation, outsyntaxerrors, outast, outdot);
            // System.out.println("[debug] parser success: " + parser.parse());
            parser.parse();
            Visitor symbolTableCreationVisitor = new SymbolTableCreationVisitor(outsemanticerrors);
            Node astRoot = parser.getASTRoot();
            symbolTableCreationVisitor.visit(astRoot);
            Util.processSymbolTable(astRoot, outsemanticerrors);
            Util.printSymbolTableToFile(astRoot, outsymboltables);

            Visitor semanticCheckingVisitor = new SemanticCheckingVisitor(outsemanticerrors);
            semanticCheckingVisitor.visit(astRoot);

            Visitor computeMemSizeVisitor = new ComputeMemSizeVisitor();
            computeMemSizeVisitor.visit(astRoot);
            Visitor funcHeaderVisitor = new FuncHeaderVisitor();
            funcHeaderVisitor.visit(astRoot);

            Util.printSymbolTableToFile(astRoot, outcgsymboltables);
            Visitor codeGenVisitor = new CodeGenVisitor(outmoon);
            codeGenVisitor.visit(astRoot);

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
            outcgsymboltables.close();
            outmoon.close();
        }
    }
}
