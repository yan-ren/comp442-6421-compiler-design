package ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import lexicalanalyzer.Token;

public class SemanticAction {

    public static void makeNode(Stack<Node> stack, Token t) {
        if (t == null) {
            stack.push(null);
            return;
        }
        stack.push(new Node(t));
    }

    public static void makeFamily(Stack<Node> stack, String name, int children) {
        Node newNode = new Node(name);
        while (children > 0) {
            newNode.addChild(stack.pop());
            children--;
        }
        stack.push(newNode);
    }

    public static void makeFamilyUntil(Stack<Node> stack, String name) {
        Node newNode = new Node(name);
        while (stack.peek() != null) {
            newNode.addChild(stack.pop());
        }

        stack.pop();
        stack.push(newNode);
    }

    public static void makeNodeEmptySizeArray(Stack<Node> stack, String name) {
        Node newNode = new Node(name);
        stack.push(newNode);
    }

    /**
     * 
     * dataMem -> var
     * rept-idnest -> indiceList
     */
    public static final HashMap<String, List<String>> SEMANTIC_ACTION_TABLE = new HashMap<>() {
        {
            put("sa1", Arrays.asList("makeNode"));
            put("sa2", Arrays.asList("pushNull"));
            put("sa3", Arrays.asList("makeFamilyUntil", "arraySize"));
            put("sa4", Arrays.asList("makeFamilyUntil", "indiceList"));
            put("sa5", Arrays.asList("makeFamilyUntil", "aParams"));
            put("sa6", Arrays.asList("makeFamily", "expr", "1"));
            put("sa7", Arrays.asList("makeFamily", "expr", "3"));
            put("sa8", Arrays.asList("makeFamilyUntil", "arithExpr"));
            put("sa9", Arrays.asList("makeFamily", "relExpr", "3"));
            put("sa10", Arrays.asList("makeFamilyUntil", "term"));
            put("sa11", Arrays.asList("makeFamily", "var", "2"));
            put("sa12", Arrays.asList("makeFamily", "dot", "2"));
            put("sa13", Arrays.asList("makeFamily", "fCall", "2"));
            put("sa14", Arrays.asList("makeFamily", "factor", "1"));
            put("sa15", Arrays.asList("makeFamilyUntil", "factor"));
            put("sa16", Arrays.asList("makeFamily", "readStat", "1"));
            put("sa17", Arrays.asList("makeFamily", "stat", "1"));
            put("sa18", Arrays.asList("makeFamily", "writeStat", "1"));
            put("sa19", Arrays.asList("makeFamily", "returnStat", "1"));
            put("sa20", Arrays.asList("makeFamily", "whileStat", "2"));
            put("sa21", Arrays.asList("makeFamily", "ifStat", "3"));
            put("sa22", Arrays.asList("makeFamily", "assignStat", "2"));
            put("sa23", Arrays.asList("makeFamilyUntil", "statBlock"));
            put("sa24", Arrays.asList("makeFamilyUntil", "prog"));
            put("sa25", Arrays.asList("makeFamilyUntil", "inherlist"));
            put("sa26", Arrays.asList("makeFamily", "varDecl", "3"));
            put("sa27", Arrays.asList("makeFamily", "fparam", "3"));
            put("sa28", Arrays.asList("makeFamilyUntil", "fparamList"));
            put("sa29", Arrays.asList("makeFamily", "funcHead", "3"));
            put("sa30", Arrays.asList("makeFamilyUntil", "funcBody"));
            put("sa31", Arrays.asList("makeFamily", "funcDef", "2"));
            put("sa32", Arrays.asList("makeFamilyUntil", "rept-structDecl"));
            put("sa33", Arrays.asList("makeFamily", "structDecl", "3"));
            put("sa34", Arrays.asList("makeFamilyUntil", "rept-implDef"));
            put("sa35", Arrays.asList("makeFamily", "implDef", "2"));
            put("sa36", Arrays.asList("makeNodeEmptySizeArray"));
        }
    };
}
