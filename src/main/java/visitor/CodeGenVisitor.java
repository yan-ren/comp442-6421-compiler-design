package visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Stack;

import ast.Node;
import ast.SemanticAction;
import lexicalanalyzer.Constants.LA_TYPE;
import symboltable.SymbolTable;

/*
stack based code generation visitor
*/
public class CodeGenVisitor implements Visitor {

    private Stack<String> registerPool = new Stack<>();
    StringBuilder execCodeBuilder = new StringBuilder();
    StringBuilder dataCodeBuilder = new StringBuilder();
    BufferedWriter out;
    //
    private final String ZERO_REG = "r0";
    private final String STACK_REG = "r14";
    private final String JUMP_REG = "r15";
    //
    private final String ADD_I = "addi";
    private final String STORE_WORD = "sw";
    private final String LOAD_WORD = "lw";
    private final String ADD = "add";
    private final String SUB = "sub";
    private final String MUL = "mul";
    private final String DIV = "div";
    private final String LOAD_BYTE = "lb";
    private final String JUMP_AND_LINK = "jl";
    private final String PUT_C = "putc";

    private final String COM_LOAD_INT = "% load intnum: ";

    public CodeGenVisitor(BufferedWriter out) {
        this.out = out;
        init();
        // initialize the register pool
        for (int i = 12; i >= 1; i--) {
            registerPool.push("r" + i);
        }
    }

    public void init() {
        writeDataCode("newline", "db", buildOperation(null, "13", null, "10", null, null),
                "% The bytes 13 and 10 are return and linefeed, respectively");
    }

    @Override
    public void visit(Node node) throws IOException {
        if (node.getName().equals(SemanticAction.PROG)) {
            passVisitorToChildren(node);
            generateMoonCode();
        } else if (node.getName().equals(SemanticAction.FUNC_DEF)) {
            /**
             * main function
             * 
             * entry
             * addi r14,r0,topaddr
             */
            if (node.symbolTableEntry.name.equals("main")) {
                writeExecCode(null, null, null, "% begin of main function");
                writeExecCode("", "entry", "", "% start program");
                writeExecCode("", ADD_I, buildOperation(null, STACK_REG, null, ZERO_REG, null, "topaddr"), "");
            }
            // process funcBody
            passVisitorToChildren(node.children.get(1));
        }
        /**
         * intnum
         * 
         * addi r1,r0,intnum
         * sw offset(r14), r1
         */
        else if (node.getName().equals(LA_TYPE.INTNUM)) {
            passVisitorToChildren(node);

            String localReg = this.registerPool.pop();
            String offset = node.symbolTableEntry.getOffsetAsString();
            writeExecCode("", ADD_I,
                    buildOperation(null, localReg, null, ZERO_REG, null, node.getToken().getLexeme()),
                    COM_LOAD_INT + node.getToken().getLexeme());
            writeExecCode("", STORE_WORD, buildOperation(offset, STACK_REG, null, localReg, null, null), null);
            this.registerPool.push(localReg);
        }
        /**
         * <addOp> ::= '+' | '-' | 'or'
         * addOp: a+b
         * 
         * lw r1,offset(r14)
         * lw r2,offset(r14)
         * add r3,r1,r2
         * sw offset(r14),r3
         */
        else if (node.getName().equals(SemanticAction.ADD_OP)) {
            passVisitorToChildren(node);
            if (node.getToken().getType().equals(LA_TYPE.PLUS) || node.getToken().getType().equals(LA_TYPE.MINUS)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String offset1 = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(0).symbolTableEntry.name);
                String offset2 = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(1).symbolTableEntry.name);
                String offset3 = node.symbolTableEntry.getOffsetAsString();

                writeExecCode("", LOAD_WORD, buildOperation(null, r1, offset1, STACK_REG, null, null),
                        "% addOp operation");
                writeExecCode("", LOAD_WORD, buildOperation(null, r2, offset2, STACK_REG, null, null), null);
                if (node.getToken().getType().equals(LA_TYPE.PLUS)) {
                    writeExecCode("", ADD, buildOperation(null, r3, null, r1, null, r2), null);
                } else {
                    writeExecCode("", SUB, buildOperation(null, r3, null, r1, null, r2), null);
                }
                writeExecCode("", STORE_WORD, buildOperation(offset3, STACK_REG, null, r3, null, null), null);

                registerPool.push(r3);
                registerPool.push(r2);
                registerPool.push(r1);
            }
        }
        /**
         * <multOp> ::= '*' | '/' | 'and'
         * 
         */
        else if (node.getName().equals(SemanticAction.MULT_OP)) {
            passVisitorToChildren(node);
            if (node.getToken().getType().equals(LA_TYPE.MULT) || node.getToken().getType().equals(LA_TYPE.DIV)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String offset1 = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(0).symbolTableEntry.name);
                String offset2 = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(1).symbolTableEntry.name);
                String offset3 = node.symbolTableEntry.getOffsetAsString();

                writeExecCode("", LOAD_WORD, buildOperation(null, r1, offset1, STACK_REG, null, null),
                        "% mulOp operation");
                writeExecCode("", LOAD_WORD, buildOperation(null, r2, offset2, STACK_REG, null, null), null);
                if (node.getToken().getType().equals(LA_TYPE.MULT)) {
                    writeExecCode("", MUL, buildOperation(null, r3, null, r1, null, r2), null);
                } else {
                    writeExecCode("", DIV, buildOperation(null, r3, null, r1, null, r2), null);
                }
                writeExecCode("", STORE_WORD, buildOperation(offset3, STACK_REG, null, r3, null, null), null);

                registerPool.push(r3);
                registerPool.push(r2);
                registerPool.push(r1);
            }
        }
        /**
         * assign: a = b a = 1
         * 
         * lw r1,offset(r14)
         * sw offset(r14),r1
         */
        else if (node.getName().equals(SemanticAction.ASSIGN_STAT)) {
            passVisitorToChildren(node);

            String r1 = this.registerPool.pop();
            String rightOffset = node.children.get(1).symbolTableEntry.getOffsetAsString();
            String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                    node.children.get(0).symbolTableEntry.name);

            writeExecCode("", LOAD_WORD, buildOperation(null, r1, rightOffset, STACK_REG, null, null),
                    "% assign " + node.children.get(0).symbolTableEntry.name);
            writeExecCode("", STORE_WORD, buildOperation(leftOffset, STACK_REG, null, r1, null, null), "");

            registerPool.push(r1);
        }
        /**
         * write(1)
         */
        else if (node.getName().equals(SemanticAction.WRITE_STAT)) {
            passVisitorToChildren(node);
            String valueOffset = SymbolTable.getOffsetByName(node.symbolTable,
                    node.children.get(0).symbolTableEntry.name);

            writeExecCode("", LOAD_WORD, buildOperation(null, "r1", valueOffset, STACK_REG, null, null),
                    "% print integer");
            writeExecCode("", JUMP_AND_LINK, buildOperation(null, JUMP_REG, null, "putint", null, null), "");
            nextLine();
        } else {
            passVisitorToChildren(node);
        }
    }

    private void passVisitorToChildren(Node node) throws IOException {
        for (Node child : node.children) {
            child.accept(this);
        }
    }

    public String buildOperation(String offset1, String firstOp, String offset2, String secondOp, String offset3,
            String thirdOp) {
        String res = "";
        if (firstOp != null && !firstOp.isEmpty()) {
            if (offset1 != null && !offset1.isEmpty()) {
                res += offset1 + "(" + firstOp + ")";
            } else {
                res += firstOp;
            }
        }
        if (secondOp != null && !secondOp.isEmpty()) {
            if (offset2 != null && !offset2.isEmpty()) {
                res += "," + offset2 + "(" + secondOp + ")";
            } else {
                res += "," + secondOp;
            }
        }
        if (thirdOp != null && !thirdOp.isEmpty()) {
            if (offset3 != null && !offset3.isEmpty()) {
                res += offset3 + "(" + thirdOp + ")";
            } else {
                res += "," + thirdOp;
            }
        }

        return res;
    }

    public void writeExecCode(String tag, String function, String operation, String comment) {
        if (tag == null) {
            tag = "";
        }
        if (function == null) {
            function = "";
        }
        if (operation == null) {
            operation = "";
        }
        if (comment == null) {
            comment = "";
        }
        execCodeBuilder
                .append(String.format(" %1$-10s %2$-10s %3$-20s %4$s", tag, function, operation, comment) + "\n");
    }

    public void writeDataCode(String tag, String function, String operation, String comment) {
        if (tag == null) {
            tag = "";
        }
        if (function == null) {
            function = "";
        }
        if (operation == null) {
            operation = "";
        }
        if (comment == null) {
            comment = "";
        }
        dataCodeBuilder
                .append(String.format(" %1$-10s %2$-10s %3$-20s %4$s", tag, function, operation, comment) + "\n");
    }

    public void generateMoonCode() throws IOException {
        out.write(execCodeBuilder.toString());
        out.write("hlt\n");
        out.write(dataCodeBuilder.toString());
    }

    public void nextLine() {
        String r1 = this.registerPool.pop();
        String r2 = this.registerPool.pop();

        writeExecCode("", ADD_I, buildOperation(null, r1, null, ZERO_REG, null, "0"), "");
        writeExecCode("", LOAD_BYTE, buildOperation(null, r2, "newline", r1, null, null), "");
        writeExecCode("", PUT_C, r2, "");
        writeExecCode("", ADD_I, buildOperation(null, r1, null, r1, null, "1"), "");
        writeExecCode("", LOAD_BYTE, buildOperation(null, r2, "newline", r1, null, null), "");
        writeExecCode("", PUT_C, r2, "");

        this.registerPool.push(r2);
        this.registerPool.push(r1);
    }
}
