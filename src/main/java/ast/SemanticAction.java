package ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import lexicalanalyzer.Token;

public class SemanticAction {

    public static void makeNode(Stack<Node> stack, Token t) {
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

    public static void makeAddOpNode(Stack<Node> semanticStack) {
        Node right = semanticStack.pop();
        Node op = semanticStack.pop();
        Node left = semanticStack.pop();

        Node newNode = new Node(ADD_OP);
        newNode.setToken(op.getToken());
        newNode.addChild(right);
        newNode.addChild(left);
        semanticStack.push(newNode);
    }

    public static void makeMultOpNode(Stack<Node> semanticStack) {
        Node right = semanticStack.pop();
        Node op = semanticStack.pop();
        Node left = semanticStack.pop();

        Node newNode = new Node(MULT_OP);
        newNode.setToken(op.getToken());
        newNode.addChild(right);
        newNode.addChild(left);
        semanticStack.push(newNode);
    }

    public static void makeNotNode(Stack<Node> semanticStack) {
        Node factor = semanticStack.pop();
        semanticStack.pop();

        Node newNode = new Node(NOT);
        newNode.addChild(factor);
        semanticStack.push(newNode);
    }

    public static void makeSignNode(Stack<Node> semanticStack) {
        Node factor = semanticStack.pop();
        Node sign = semanticStack.pop();

        Node newNode = new Node(SIGN);
        newNode.setToken(sign.getToken());
        newNode.addChild(factor);
        semanticStack.push(newNode);
    }

    /**
     * name change
     * 
     * dataMem -> var
     * rept-idnest -> indiceList
     */
    public static final String ARRAY_SIZE = "arraySize";
    public static final String INDICE_LIST = "indiceList";
    public static final String APARAMS = "aParams";
    public static final String EXPR = "expr";
    public static final String ARITH_EXPR = "arithExpr";
    public static final String REL_EXPR = "relExpr";
    public static final String TERM = "term";
    public static final String VAR = "var";
    public static final String DOT = "dot";
    public static final String FCALL = "fCall";
    public static final String FACTOR = "factor";
    public static final String READ_STAT = "readStat";
    public static final String STAT = "stat";
    public static final String WRITE_STAT = "writeStat";
    public static final String RETURN_STAT = "returnStat";
    public static final String WHILE_STAT = "whileStat";
    public static final String IF_STAT = "ifStat";
    public static final String ASSIGN_STAT = "assignStat";
    public static final String STAT_BLOCK = "statBlock";
    public static final String PROG = "prog";
    public static final String INHER_LIST = "inherlist";
    public static final String VAR_DECL = "varDecl";
    public static final String FPARAM = "fparam";
    public static final String FPARAM_LIST = "fparamList";
    public static final String FUNC_HEAD = "funcHead";
    public static final String FUNC_BODY = "funcBody";
    public static final String FUNC_DEF = "funcDef";
    public static final String REPT_STRUCT_DECL_4 = "rept-structDecl4";
    public static final String STRUCT_DECL = "structDecl";
    public static final String REPT_IMPL_DEF_3 = "rept-implDef3";
    public static final String IMPL_DEF = "implDef";
    public static final String ADD_OP = "addOp";
    public static final String MULT_OP = "multOp";
    public static final String SIGN = "sign";
    public static final String NOT = "not";

    public static final HashMap<String, List<String>> SEMANTIC_ACTION_TABLE = new HashMap<>() {
        {
            put("sa1", Arrays.asList("makeNode"));
            put("sa2", Arrays.asList("pushNull"));
            put("sa3", Arrays.asList("makeFamilyUntil", ARRAY_SIZE));
            put("sa4", Arrays.asList("makeFamilyUntil", INDICE_LIST));
            put("sa5", Arrays.asList("makeFamilyUntil", APARAMS));
            put("sa6", Arrays.asList("makeFamily", EXPR, "1"));
            put("sa7", Arrays.asList("makeFamily", EXPR, "3"));
            put("sa8", Arrays.asList("makeAddOpNode", ADD_OP));
            put("sa9", Arrays.asList("makeFamily", REL_EXPR, "3"));
            put("sa10", Arrays.asList("makeMultOpNode", MULT_OP));
            put("sa11", Arrays.asList("makeFamily", VAR, "2"));
            put("sa12", Arrays.asList("makeFamily", DOT, "2"));
            put("sa13", Arrays.asList("makeFamily", FCALL, "2"));
            put("sa14", Arrays.asList("makeFamily", FACTOR, "1"));
            put("sa15", Arrays.asList("makeFamilyUntil", FACTOR));
            put("sa16", Arrays.asList("makeFamily", READ_STAT, "1"));
            put("sa17", Arrays.asList("makeFamily", STAT, "1"));
            put("sa18", Arrays.asList("makeFamily", WRITE_STAT, "1"));
            put("sa19", Arrays.asList("makeFamily", RETURN_STAT, "1"));
            put("sa20", Arrays.asList("makeFamily", WHILE_STAT, "2"));
            put("sa21", Arrays.asList("makeFamily", IF_STAT, "3"));
            put("sa22", Arrays.asList("makeFamily", ASSIGN_STAT, "2"));
            put("sa23", Arrays.asList("makeFamilyUntil", STAT_BLOCK));
            put("sa24", Arrays.asList("makeFamilyUntil", PROG));
            put("sa25", Arrays.asList("makeFamilyUntil", INHER_LIST));
            put("sa26", Arrays.asList("makeFamily", VAR_DECL, "3"));
            put("sa27", Arrays.asList("makeFamily", FPARAM, "3"));
            put("sa28", Arrays.asList("makeFamilyUntil", FPARAM_LIST));
            put("sa29", Arrays.asList("makeFamily", FUNC_HEAD, "3"));
            put("sa30", Arrays.asList("makeFamilyUntil", FUNC_BODY));
            put("sa31", Arrays.asList("makeFamily", FUNC_DEF, "2"));
            put("sa32", Arrays.asList("makeFamilyUntil", REPT_STRUCT_DECL_4));
            put("sa33", Arrays.asList("makeFamily", STRUCT_DECL, "3"));
            put("sa34", Arrays.asList("makeFamilyUntil", REPT_IMPL_DEF_3));
            put("sa35", Arrays.asList("makeFamily", IMPL_DEF, "2"));
            put("sa36", Arrays.asList("makeNodeEmptySizeArray"));
            put("sa37", Arrays.asList("makeNotNode", NOT));
            put("sa38", Arrays.asList("makeSignNode", SIGN));
        }
    };

    public static final Set<String> EXTRA_INTERMEDIATE_NODE = new HashSet<>() {
        {
            add(EXPR);
            add(STAT);
            add(ARITH_EXPR);
            add(TERM);
            add(FACTOR);
            add(REPT_IMPL_DEF_3);
            add(REPT_STRUCT_DECL_4);
        }
    };
}
