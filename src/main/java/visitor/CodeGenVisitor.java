package visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.ObjectUtils.Null;

import ast.Node;
import ast.SemanticAction;
import lexicalanalyzer.Constants.LA_TYPE;
import symboltable.SymbolTable;

/*
stack based code generation visitor
*/
/**
 * tempVar is created for following nodes
 * 
 * addOp
 * mulOp
 * intnum
 * floatnum
 * relExpr
 */
public class CodeGenVisitor implements Visitor {

    private Stack<String> registerPool = new Stack<>();
    StringBuilder execCodeBuilder = new StringBuilder();
    StringBuilder dataCodeBuilder = new StringBuilder();
    BufferedWriter out;
    // register
    private final String ZERO_REG = "r0";
    private final String STACK_REG = "r14";
    private final String JUMP_REG = "r15";
    // data access instructions
    private final String STORE_WORD = "sw";
    private final String LOAD_WORD = "lw";
    private final String LOAD_BYTE = "lb";
    // arithmetic instruction
    private final String ADD = "add";
    private final String ADD_I = "addi";
    private final String SUB = "sub";
    private final String SUB_I = "subi";
    private final String MUL = "mul";
    private final String DIV = "div";
    private final String EQUAL = "ceq";
    private final String NOT_EQUAL = "cne";
    private final String LESS = "clt";
    private final String LESS_OR_EQUAL = "cle";
    private final String GREATER = "cgt";
    private final String GREATER_OR_EQUAL = "cge";
    //
    private final String PUT_C = "putc";
    // control instructions
    private final String BRANCH_IF_ZERO = "bz";
    private final String BRANCH_IF_NON_ZERO = "bnz";
    private final String JUMP = "j";
    private final String JUMP_AND_LINK = "jl";
    private final String JUMP_TO_REGISTER = "jr";
    //
    private final String RESERVE = "res";

    private final String COM_LOAD_INT = "% load intnum: ";
    // fields for symbol table offset
    private final String RETURN_OFFSET = "0";
    private final String JUMP_OFFSET = "-4";

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
        writeDataCode("align", null, null, null);
    }

    private int tagNum = 0;

    public String getNewTag() {
        tagNum++;
        return "tag" + tagNum;
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
                writeExecCode(null, "align", null, null);
                writeExecCode(null, null, null, "% begin of main function");
                writeExecCode("main", "entry", "", "% start program");
                writeExecCode("", ADD_I, buildOperation(null, STACK_REG, null, ZERO_REG, null, "topaddr"), "");
            } else {
                writeExecCode(null, null, null, "% begin of function definition: " + node.symbolTableEntry.name);
                writeExecCode(node.symbolTableEntry.name, null, null, null);
                // put r15 on stack frame
                writeExecCode(null, STORE_WORD, buildOperation(JUMP_OFFSET, STACK_REG, null, JUMP_REG, null, null),
                        null);
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
            /**
             * lw r1,offset(r14)
             * lw r2,offset(r14)
             * bnz r1,tag1
             * bnz r2,tag1
             * addi r3,r0,0
             * j tag2
             * tag1 addi r3,r0,1
             * tag2 sw offset(r14),r3
             */
            else if (node.getToken().getType().equals(LA_TYPE.OR)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String offset1 = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(0).symbolTableEntry.name);
                String offset2 = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(1).symbolTableEntry.name);
                String offset3 = node.symbolTableEntry.getOffsetAsString();
                String tag1 = getNewTag();
                String tag2 = getNewTag();

                writeExecCode("", LOAD_WORD, buildOperation(null, r1, offset1, STACK_REG, null, null),
                        "% and operation " + node.children.get(0).symbolTableEntry.name + " and "
                                + node.children.get(1).symbolTableEntry.name);
                writeExecCode("", LOAD_WORD, buildOperation(null, r2, offset2, STACK_REG, null, null), null);
                writeExecCode("", BRANCH_IF_NON_ZERO, buildOperation(null, r1, null, tag1, null, null), null);
                writeExecCode("", BRANCH_IF_NON_ZERO, buildOperation(null, r2, null, tag1, null, null), null);
                writeExecCode("", ADD_I, buildOperation(null, r3, null, ZERO_REG, null, "0"), null);
                writeExecCode("", JUMP, buildOperation(null, tag2, null, null, null, null), null);
                writeExecCode(tag1, ADD_I, buildOperation(null, r3, null, ZERO_REG, null, "1"), null);
                writeExecCode(tag2, STORE_WORD, buildOperation(offset3, STACK_REG, null, r3, null, null), null);

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
            /**
             * lw r1,offset(r14)
             * lw r2,offset(r14)
             * bz r1,tag1
             * bz r2,tag1
             * addi r3,r0,1
             * j tag2
             * tag1 addi r3,r0,0
             * tag2 sw offset(r14),r0
             */
            else if (node.getToken().getType().equals(LA_TYPE.AND)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String offset1 = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(0).symbolTableEntry.name);
                String offset2 = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(1).symbolTableEntry.name);
                String offset3 = node.symbolTableEntry.getOffsetAsString();
                String tag1 = getNewTag();
                String tag2 = getNewTag();

                writeExecCode("", LOAD_WORD, buildOperation(null, r1, offset1, STACK_REG, null, null),
                        "% and operation " + node.children.get(0).symbolTableEntry.name + " and "
                                + node.children.get(1).symbolTableEntry.name);
                writeExecCode("", LOAD_WORD, buildOperation(null, r2, offset2, STACK_REG, null, null), null);
                writeExecCode("", BRANCH_IF_ZERO, buildOperation(null, r1, null, tag1, null, null), null);
                writeExecCode("", BRANCH_IF_ZERO, buildOperation(null, r2, null, tag1, null, null), null);
                writeExecCode("", ADD_I, buildOperation(null, r3, null, ZERO_REG, null, "1"), null);
                writeExecCode("", JUMP, buildOperation(null, tag2, null, null, null, null), null);
                writeExecCode(tag1, ADD_I, buildOperation(null, r3, null, ZERO_REG, null, "0"), null);
                writeExecCode(tag2, STORE_WORD, buildOperation(offset3, STACK_REG, null, r3, null, null), null);

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
            String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                    node.children.get(1).symbolTableEntry.name);
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
            printNewLine();
        }
        /**
         * read(a)
         * 
         * jl r15,getint
         * sw offset(r14),r1
         */
        else if (node.getName().equals(SemanticAction.READ_STAT)) {
            passVisitorToChildren(node);
            String varOffset = SymbolTable.getOffsetByName(node.symbolTable,
                    node.children.get(0).symbolTableEntry.name);
            writeExecCode("", JUMP_AND_LINK, buildOperation(null, JUMP_REG, null, "getint", null, null),
                    "% read integer");
            writeExecCode("", STORE_WORD, buildOperation(varOffset, STACK_REG, null, "r1", null, null),
                    "");
            // printNewLine();
        }
        /**
         * relExpr
         * a == b
         * 
         * lw r1,offset(r14)
         * lw r2,offset(r14)
         * ceq r3,r1,r2
         * sw offset(r14),r3
         */
        else if (node.getName().equals(SemanticAction.REL_EXPR)) {
            passVisitorToChildren(node);
            String r1 = this.registerPool.pop();
            String r2 = this.registerPool.pop();
            String r3 = this.registerPool.pop();

            String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                    node.children.get(0).symbolTableEntry.name);
            String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                    node.children.get(2).symbolTableEntry.name);
            String resultOffset = node.symbolTableEntry.getOffsetAsString();
            Node operator = node.children.get(1);

            writeExecCode(null, LOAD_WORD, buildOperation(null, r1, leftOffset, STACK_REG, null, null),
                    "% relExpr " + node.children.get(0).symbolTableEntry.name + " "
                            + node.children.get(1).getToken().getType() + " "
                            + node.children.get(2).symbolTableEntry.name);
            writeExecCode(null, LOAD_WORD, buildOperation(null, r2, rightOffset, STACK_REG, null, null), null);
            switch (operator.getToken().getType()) {
                case LA_TYPE.EQ:
                    writeExecCode(null, EQUAL, buildOperation(null, r3, null, r1, null, r2), null);
                    break;
                case LA_TYPE.NOTEQ:
                    writeExecCode(null, NOT_EQUAL, buildOperation(null, r3, null, r1, null, r2), null);
                    break;
                case LA_TYPE.LT:
                    writeExecCode(null, LESS, buildOperation(null, r3, null, r1, null, r2), null);
                    break;
                case LA_TYPE.LEQ:
                    writeExecCode(null, LESS_OR_EQUAL, buildOperation(null, r3, null, r1, null, r2), null);
                    break;
                case LA_TYPE.GT:
                    writeExecCode(null, GREATER, buildOperation(null, r3, null, r1, null, r2), null);
                    break;
                case LA_TYPE.GEQ:
                    writeExecCode(null, GREATER_OR_EQUAL, buildOperation(null, r3, null, r1, null, r2), null);
                    break;
                default:
                    writeExecCode(null, null, null, "% unknown operator " + operator.getToken().getType());
                    break;
            }
            writeExecCode(null, STORE_WORD, buildOperation(resultOffset, STACK_REG, null, r3, null, null), null);

            this.registerPool.push(r1);
            this.registerPool.push(r2);
            this.registerPool.push(r3);
        }
        /**
         * ifStat
         */
        else if (node.getName().equals(SemanticAction.IF_STAT)) {
            writeExecCode(null, null, null, "% if statement");
            // visit relExpr
            visit(node.children.get(0));

            String r1 = this.registerPool.pop();
            String offset = SymbolTable.getOffsetByName(node.symbolTable, node.children.get(0).symbolTableEntry.name);
            String tag1 = getNewTag();
            String tag2 = getNewTag();

            writeExecCode(null, LOAD_WORD, buildOperation(null, r1, offset, STACK_REG, null, null), null);
            writeExecCode(null, BRANCH_IF_ZERO, buildOperation(null, r1, null, tag1, null, null), null);
            // code for statBlock
            visit(node.children.get(1));
            writeExecCode(null, JUMP, tag2, null);
            // code for statBlock
            writeExecCode(tag1, null, null, null);
            visit(node.children.get(2));
            writeExecCode(tag2, null, null, null);
            this.registerPool.push(r1);
        }
        /**
         * whileStat
         */
        else if (node.getName().equals(SemanticAction.WHILE_STAT)) {
            String tag1 = getNewTag();
            String tag2 = getNewTag();
            String offset = SymbolTable.getOffsetByName(node.symbolTable, node.children.get(0).symbolTableEntry.name);

            writeExecCode(tag1, null, null, "% while statement");
            // visit relExpr
            visit(node.children.get(0));
            String r1 = this.registerPool.pop();
            writeExecCode(null, LOAD_WORD, buildOperation(null, r1, offset, STACK_REG, null, null), null);
            writeExecCode(null, BRANCH_IF_ZERO, buildOperation(null, r1, null, tag2, null, null), null);
            // visit statBlock
            visit(node.children.get(1));
            writeExecCode(null, JUMP, tag1, null);
            writeExecCode(tag2, null, null, null);
        }
        /**
         * For basic type int/float, don't need to defined tag, but array and object
         * needs tag to allocate memory
         */
        else if (node.getName().equals(SemanticAction.VAR_DECL)) {
            // array can only be 1D
            if (node.children.get(2).children.size() == 1) {
                String size = String.valueOf(Util.getTypeSize(node.children.get(1).getName())
                        * Integer.parseInt(node.children.get(2).children.get(0).getToken().getLexeme()));
                writeDataCode(varResName(node), RESERVE, size, "");
            }
        }
        /**
         * pass parameters
         * 
         * increment stack reg
         * jump to function
         * decrement stack reg
         * get return value from f and store
         */
        else if (node.getName().equals(SemanticAction.FCALL)) {
            passVisitorToChildren(node);
            String fCallName = node.symbolTableEntry.link.getName();
            writeExecCode(null, null, null, "% function call to " + fCallName);
            String r1 = this.registerPool.pop();
            List<Node> aParams = node.children.get(1).children;
            String currentScopeSize = String.valueOf(node.symbolTable.scopeSize);
            for (int i = 0; i < aParams.size(); i++) {
                String aParamOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        aParams.get(i).symbolTableEntry.name);
                String fParamOffset = SymbolTable.getParamOffsetByIndex(node.symbolTableEntry.link, i + 1);
                fParamOffset = combineOffset(fParamOffset, currentScopeSize);
                writeExecCode(null, LOAD_WORD, buildOperation(null, r1, aParamOffset, STACK_REG, null, null),
                        "% pass " + aParams.get(i).symbolTableEntry.name + " into parameter");
                writeExecCode(null, STORE_WORD, buildOperation(fParamOffset, STACK_REG, null, r1, null, null), null);
            }
            writeExecCode(null, SUB_I, buildOperation(null, STACK_REG, null, STACK_REG, null, currentScopeSize),
                    "% increment stack frame");
            writeExecCode(null, JUMP_AND_LINK, buildOperation(null, JUMP_REG, null, fCallName, null, null),
                    "% jump to funciton: " + fCallName);
            writeExecCode(null, ADD_I, buildOperation(null, STACK_REG, null, STACK_REG, null, currentScopeSize),
                    "% decrement stack frame");
            writeExecCode(null, LOAD_WORD, buildOperation(null, r1, "-" + currentScopeSize, STACK_REG, null, null),
                    "% get return value from " + fCallName);
            String fCallTempVarOffset = SymbolTable.getOffsetByName(node.symbolTable, node.symbolTableEntry.name);
            writeExecCode(null, STORE_WORD, buildOperation(fCallTempVarOffset, STACK_REG, null, r1, null, null), null);
            this.registerPool.push(r1);
        }
        /**
         * returnStat, e.g return(retval)
         * 
         * lw r1,offset(r14)
         * sw 0(r14),r1
         * lw r15,-4(r14)
         * jr r15
         */
        else if (node.getName().equals(SemanticAction.RETURN_STAT)) {
            passVisitorToChildren(node);
            String returnValueOffset = SymbolTable.getOffsetByName(node.symbolTable,
                    node.children.get(0).symbolTableEntry.name);
            String r1 = this.registerPool.pop();
            writeExecCode(null, null, null, "% return");
            writeExecCode(null, LOAD_WORD, buildOperation(null, r1, returnValueOffset, STACK_REG, null, null),
                    "% load returned value from mem");
            writeExecCode(null, STORE_WORD, buildOperation(RETURN_OFFSET, STACK_REG, null, r1, null, null), null);
            writeExecCode(null, LOAD_WORD, buildOperation(null, JUMP_REG, JUMP_OFFSET, STACK_REG, null, null),
                    "% retrieve r15 from stack");
            writeExecCode(null, JUMP_TO_REGISTER, JUMP_REG, "% jump back to calling function");
            this.registerPool.push(r1);
        }
        /**
         * default
         */
        else {
            passVisitorToChildren(node);
        }
    }

    private String varResName(Node node) {
        return node.symbolTable.getName() + "_" + node.symbolTableEntry.name;
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

    public void printNewLine() {
        String r1 = this.registerPool.pop();
        String r2 = this.registerPool.pop();

        writeExecCode("", ADD_I, buildOperation(null, r1, null, ZERO_REG, null, "0"), "% print a newline");
        writeExecCode("", LOAD_BYTE, buildOperation(null, r2, "newline", r1, null, null), "");
        writeExecCode("", PUT_C, r2, "");
        writeExecCode("", ADD_I, buildOperation(null, r1, null, r1, null, "1"), "");
        writeExecCode("", LOAD_BYTE, buildOperation(null, r2, "newline", r1, null, null), "");
        writeExecCode("", PUT_C, r2, "");

        this.registerPool.push(r2);
        this.registerPool.push(r1);
    }

    /**
     * Only return negative offset
     * 
     * @param offset1
     * @param offset2
     * @return
     */
    public String combineOffset(String offset1, String offset2) {
        int o1 = Integer.parseInt(offset1);
        int o2 = Integer.parseInt(offset2);

        if (o1 > 0) {
            o1 = o1 * -1;
        }
        if (o2 > 0) {
            o2 = o2 * -1;
        }

        return String.valueOf(o1 + o2);
    }
}
