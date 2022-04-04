package visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import ast.Node;
import ast.SemanticAction;
import lexicalanalyzer.Constants.LA_TYPE;
import symboltable.Kind;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;

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
    private final String MUL_I = "muli";
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
    private final String HALT = "hlt";
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
                        "% push r15 on stack frame");
            }
            // process funcBody
            passVisitorToChildren(node.children.get(1));
            writeExecCode(null, HALT, null, "% end of function definition: " + node.symbolTableEntry.name);
        }
        /**
         * intnum
         * 
         * addi r1,r0,intnum
         * sw offset(r14), r1
         */
        else if (node.getName().equals(LA_TYPE.INTNUM)) {
            passVisitorToChildren(node);

            String r1 = this.registerPool.pop();
            String offset = node.symbolTableEntry.getOffsetAsString();
            writeExecCode("", ADD_I,
                    buildOperation(null, r1, null, ZERO_REG, null, node.getToken().getLexeme()),
                    COM_LOAD_INT + node.getToken().getLexeme());
            writeExecCode("", STORE_WORD, buildOperation(offset, STACK_REG, null, r1, null, null), null);
            this.registerPool.push(r1);
        }
        /**
         * <addOp> ::= '+' | '-' | 'or'
         */
        else if (node.getName().equals(SemanticAction.ADD_OP)) {
            passVisitorToChildren(node);
            Node firstNode = node.children.get(0);
            String firstNodeName = firstNode.symbolTableEntry.name;
            Node secondNode = node.children.get(1);
            String secondNodeName = secondNode.symbolTableEntry.name;
            String addOpOffset = node.symbolTableEntry.getOffsetAsString();

            if (node.getToken().getType().equals(LA_TYPE.PLUS) || node.getToken().getType().equals(LA_TYPE.MINUS)) {
                /**
                 * first
                 * lw r1,first_off(r14)
                 * muli r2,r1,4
                 * lw r3,first_name(r2)
                 * 
                 * second
                 * lw r4,second_off(r14)
                 * muli r5,r4,4
                 * lw r6,second_name(r5)
                 * 
                 * add
                 * add r7,r3,r6
                 * sw addOp_offset(r14),r7
                 */
                if (isArrayVar(firstNode) && isArrayVar(secondNode)) {
                    String r1 = this.registerPool.pop();
                    String r2 = this.registerPool.pop();
                    String r3 = this.registerPool.pop();
                    String r4 = this.registerPool.pop();
                    String r5 = this.registerPool.pop();
                    String r6 = this.registerPool.pop();
                    String r7 = this.registerPool.pop();
                    String firstOffset = SymbolTable.getOffsetByName(node.symbolTable,
                            firstNode.children.get(1).children.get(0).symbolTableEntry.name);
                    String secondOffset = SymbolTable.getOffsetByName(node.symbolTable,
                            secondNode.children.get(1).children.get(0).symbolTableEntry.name);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r1, firstOffset, STACK_REG, null, null),
                            "% add operation for both array operands");
                    writeExecCode(null, MUL_I, buildOperation(null, r2, null, r1, null, "4"), null);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r3, firstNodeName, r2, null, null), null);

                    writeExecCode(null, LOAD_WORD, buildOperation(null, r4, secondOffset, STACK_REG, null, null), null);
                    writeExecCode(null, MUL_I, buildOperation(null, r5, null, r4, null, "4"), null);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r6, secondNodeName, r5, null, null), null);

                    if (node.getToken().getType().equals(LA_TYPE.PLUS)) {
                        writeExecCode(null, ADD, buildOperation(null, r7, null, r3, null, r6), null);
                    } else {
                        writeExecCode(null, SUB, buildOperation(null, r7, null, r3, null, r6), null);
                    }
                    writeExecCode(null, STORE_WORD, buildOperation(addOpOffset, STACK_REG, null, r7, null, null), null);
                    registerPool.push(r7);
                    registerPool.push(r6);
                    registerPool.push(r5);
                    registerPool.push(r4);
                    registerPool.push(r3);
                    registerPool.push(r2);
                    registerPool.push(r1);
                }
                /**
                 * first
                 * lw r1,first_off(r14)
                 * muli r2,r1,4
                 * lw r3,first_name(r2)
                 * 
                 * second
                 * lw r4,b_offset(r14)
                 * 
                 * add
                 * add r5,r3,r4
                 * sw addOp_offset(r14),r5
                 */
                else if (isArrayVar(firstNode)) {
                    String r1 = this.registerPool.pop();
                    String r2 = this.registerPool.pop();
                    String r3 = this.registerPool.pop();
                    String r4 = this.registerPool.pop();
                    String r5 = this.registerPool.pop();

                    String firstOffset = SymbolTable.getOffsetByName(node.symbolTable,
                            firstNode.children.get(1).children.get(0).symbolTableEntry.name);
                    String secondOffset = SymbolTable.getOffsetByName(node.symbolTable,
                            secondNode.symbolTableEntry.name);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r1, firstOffset, STACK_REG, null, null),
                            "% add operation for first operand is array");
                    writeExecCode(null, MUL_I, buildOperation(null, r2, null, r1, null, "4"), null);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r3, firstNodeName, r2, null, null), null);

                    writeExecCode(null, LOAD_WORD, buildOperation(null, r4, secondOffset, STACK_REG, null, null), null);

                    if (node.getToken().getType().equals(LA_TYPE.PLUS)) {
                        writeExecCode(null, ADD, buildOperation(null, r5, null, r3, null, r4), null);
                    } else {
                        writeExecCode(null, SUB, buildOperation(null, r5, null, r3, null, r4), null);
                    }
                    writeExecCode(null, STORE_WORD, buildOperation(addOpOffset, STACK_REG, null, r5, null, null), null);

                    registerPool.push(r5);
                    registerPool.push(r4);
                    registerPool.push(r3);
                    registerPool.push(r2);
                    registerPool.push(r1);
                }
                /**
                 * first
                 * lw r1,a_offset(r14)
                 * 
                 * second
                 * lw r2,second_off(r14)
                 * muli r3,r2,4
                 * lw r4,second_name(r3)
                 * 
                 * add
                 * add r5,r3,r4
                 * sw addOp_offset(r14),r5
                 */
                else if (isArrayVar(secondNode)) {
                    String r1 = this.registerPool.pop();
                    String r2 = this.registerPool.pop();
                    String r3 = this.registerPool.pop();
                    String r4 = this.registerPool.pop();
                    String r5 = this.registerPool.pop();

                    String firstOffset = SymbolTable.getOffsetByName(node.symbolTable,
                            firstNode.symbolTableEntry.name);
                    String secondOffset = SymbolTable.getOffsetByName(node.symbolTable,
                            secondNode.children.get(1).children.get(0).symbolTableEntry.name);

                    writeExecCode("", LOAD_WORD, buildOperation(null, r1, firstOffset, STACK_REG, null, null),
                            "% addOp operation for second operand is array");

                    writeExecCode(null, LOAD_WORD, buildOperation(null, r2, secondOffset, STACK_REG, null, null), null);
                    writeExecCode(null, MUL_I, buildOperation(null, r3, null, r2, null, "4"), null);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r4, secondNodeName, r3, null, null), null);
                    if (node.getToken().getType().equals(LA_TYPE.PLUS)) {
                        writeExecCode(null, ADD, buildOperation(null, r5, null, r1, null, r4), null);
                    } else {
                        writeExecCode(null, SUB, buildOperation(null, r5, null, r1, null, r4), null);
                    }
                    writeExecCode(null, STORE_WORD, buildOperation(addOpOffset, STACK_REG, null, r5, null, null), null);

                    registerPool.push(r5);
                    registerPool.push(r4);
                    registerPool.push(r3);
                    registerPool.push(r2);
                    registerPool.push(r1);
                }
                /**
                 * l1.b+l1.a
                 * 
                 * first
                 * addi r1,r0,first_offset
                 * lw r2,l1(r1)
                 * 
                 * second
                 * addi r3,r0,second_offset
                 * lw r4,l1(r3)
                 * 
                 * add
                 * add r5,r2,r4
                 * sw addOp_offset(r14),r5
                 */
                else if (Util.isDotNode(firstNode) && Util.isDotNode(secondNode)) {
                    String r1 = this.registerPool.pop();
                    String r2 = this.registerPool.pop();
                    String r3 = this.registerPool.pop();
                    String r4 = this.registerPool.pop();
                    String r5 = this.registerPool.pop();

                    String firstOffset = SymbolTable.getFieldSize(node.symbolTable,
                            firstNode.symbolTableEntry.type.name,
                            firstNode.children.get(1).children.get(0).getToken().getLexeme());
                    String secondOffset = SymbolTable.getFieldSize(node.symbolTable,
                            secondNode.symbolTableEntry.type.name,
                            secondNode.children.get(1).children.get(0).getToken().getLexeme());
                    // first
                    writeExecCode(null, ADD_I, buildOperation(null, r1, null, ZERO_REG, null, firstOffset),
                            "% load " + firstNodeName);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r2, firstNodeName, r1, null, null), null);
                    // second
                    writeExecCode(null, ADD_I, buildOperation(null, r3, null, ZERO_REG, null, secondOffset),
                            "% load value for " + secondNodeName);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r4, secondNodeName, r3, null, null), null);

                    if (node.getToken().getType().equals(LA_TYPE.PLUS)) {
                        writeExecCode(null, ADD, buildOperation(null, r5, null, r2, null, r4), null);
                    } else {
                        writeExecCode(null, SUB, buildOperation(null, r5, null, r2, null, r4), null);
                    }
                    writeExecCode(null, STORE_WORD, buildOperation(addOpOffset, STACK_REG, null, r5, null, null), null);

                    registerPool.push(r5);
                    registerPool.push(r4);
                    registerPool.push(r3);
                    registerPool.push(r2);
                    registerPool.push(r1);
                }
                /**
                 * l1.b+1
                 * 
                 * first
                 * addi r1,r0,first_offset
                 * lw r2,l1(r1)
                 * 
                 * second
                 * lw r3,b_offset(r14)
                 * 
                 * add
                 * add r4,r2,r3
                 * sw addOp_offset(r14),r4
                 */
                else if (Util.isDotNode(firstNode)) {
                    String r1 = this.registerPool.pop();
                    String r2 = this.registerPool.pop();
                    String r3 = this.registerPool.pop();
                    String r4 = this.registerPool.pop();

                    String firstOffset = SymbolTable.getFieldSize(node.symbolTable,
                            firstNode.symbolTableEntry.type.name,
                            firstNode.children.get(1).children.get(0).getToken().getLexeme());
                    String secondOffset = SymbolTable.getOffsetByName(node.symbolTable,
                            node.children.get(1).symbolTableEntry.name);
                    // first
                    writeExecCode(null, ADD_I, buildOperation(null, r1, null, ZERO_REG, null, firstOffset),
                            "% load " + firstNodeName);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r2, firstNodeName, r1, null, null), null);
                    // second
                    writeExecCode("", LOAD_WORD, buildOperation(null, r3, secondOffset, STACK_REG, null, null), null);

                    if (node.getToken().getType().equals(LA_TYPE.PLUS)) {
                        writeExecCode(null, ADD, buildOperation(null, r4, null, r2, null, r3), null);
                    } else {
                        writeExecCode(null, SUB, buildOperation(null, r4, null, r2, null, r3), null);
                    }
                    writeExecCode(null, STORE_WORD, buildOperation(addOpOffset, STACK_REG, null, r4, null, null), null);

                    registerPool.push(r4);
                    registerPool.push(r3);
                    registerPool.push(r2);
                    registerPool.push(r1);
                }
                /**
                 * 1+l1.b
                 * 
                 * second
                 * addi r1,r0,second_offset
                 * lw r2,l1(r1)
                 * 
                 * first
                 * lw r3,b_offset(r14)
                 * 
                 * add
                 * add r4,r2,r3
                 * sw addOp_offset(r14),r4
                 */
                else if (Util.isDotNode(secondNode)) {
                    String r1 = this.registerPool.pop();
                    String r2 = this.registerPool.pop();
                    String r3 = this.registerPool.pop();
                    String r4 = this.registerPool.pop();

                    String secondOffset = SymbolTable.getFieldSize(node.symbolTable,
                            firstNode.symbolTableEntry.type.name,
                            firstNode.children.get(1).children.get(0).getToken().getLexeme());
                    String firstOffset = SymbolTable.getOffsetByName(node.symbolTable,
                            node.children.get(1).symbolTableEntry.name);
                    // second
                    writeExecCode(null, ADD_I, buildOperation(null, r1, null, ZERO_REG, null, secondOffset),
                            "% load " + secondNodeName);
                    writeExecCode(null, LOAD_WORD, buildOperation(null, r2, secondNodeName, r1, null, null), null);
                    // first
                    writeExecCode("", LOAD_WORD, buildOperation(null, r3, firstOffset, STACK_REG, null, null), null);

                    if (node.getToken().getType().equals(LA_TYPE.PLUS)) {
                        writeExecCode(null, ADD, buildOperation(null, r4, null, r2, null, r3), null);
                    } else {
                        writeExecCode(null, SUB, buildOperation(null, r4, null, r2, null, r3), null);
                    }
                    writeExecCode(null, STORE_WORD, buildOperation(addOpOffset, STACK_REG, null, r4, null, null), null);

                    registerPool.push(r4);
                    registerPool.push(r3);
                    registerPool.push(r2);
                    registerPool.push(r1);
                }
                /**
                 * addOp: a+b
                 * 
                 * lw r1,a_offset(r14)
                 * lw r2,b_offset(r14)
                 * add r3,r1,r2
                 * sw addOp_offset(r14),r3
                 */
                else {
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
         * assign
         */
        else if (node.getName().equals(SemanticAction.ASSIGN_STAT)) {
            passVisitorToChildren(node);
            Node leftNode = node.children.get(0);
            String leftNodeName = leftNode.symbolTableEntry.name;
            Node rightNode = node.children.get(1);
            String rightNodeName = rightNode.symbolTableEntry.name;
            /**
             * x[1] = x[2]
             * 
             * right
             * lw r1,offset(r14)
             * muli r2,r1,4
             * lw r3,arr(r2)
             * left
             * lw r4,offset(r14)
             * muli r5,r4,4
             * sw arr(r5),r3
             */
            if (isArrayVar(leftNode) && isArrayVar(rightNode)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String r4 = this.registerPool.pop();
                String r5 = this.registerPool.pop();
                String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        rightNode.children.get(1).children.get(0).symbolTableEntry.name);
                String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        leftNode.children.get(1).children.get(0).symbolTableEntry.name);
                // right
                writeExecCode(null, LOAD_WORD, buildOperation(null, r1, rightOffset, STACK_REG, null, null),
                        "% load value for " + leftNodeName);
                writeExecCode(null, MUL_I, buildOperation(null, r2, null, r1, null, "4"), null);
                writeExecCode(null, LOAD_WORD, buildOperation(null, r3, rightNodeName, r2, null, null), null);
                // left
                writeExecCode(null, LOAD_WORD, buildOperation(null, r4, leftOffset, STACK_REG, null, null), null);
                writeExecCode(null, MUL_I, buildOperation(null, r5, null, r4, null, "4"), null);
                writeExecCode(null, STORE_WORD, buildOperation(leftNodeName, r5, null, r3, null, null),
                        "% assign " + leftNodeName);

                this.registerPool.push(r5);
                this.registerPool.push(r4);
                this.registerPool.push(r3);
                this.registerPool.push(r2);
                this.registerPool.push(r1);
            }
            /**
             * lw r1, right_off(r14)
             * left
             * lw r2,offset(r14)
             * muli r3,r2,4
             * sw arr(r3),r1
             */
            else if (isArrayVar(leftNode)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        leftNode.children.get(1).children.get(0).symbolTableEntry.name);
                String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        rightNodeName);
                // right
                writeExecCode("", LOAD_WORD, buildOperation(null, r1, rightOffset, STACK_REG, null, null),
                        "% load value for " + leftNodeName);
                // left
                writeExecCode(null, LOAD_WORD, buildOperation(null, r2, leftOffset, STACK_REG, null, null), null);
                writeExecCode(null, MUL_I, buildOperation(null, r3, null, r2, null, "4"), null);
                writeExecCode(null, STORE_WORD, buildOperation(leftNodeName, r3, null, r1, null, null),
                        "% assign " + leftNodeName);
                this.registerPool.push(r3);
                this.registerPool.push(r2);
                this.registerPool.push(r1);
            }
            /**
             * 
             * right
             * lw r1,offset(r14)
             * muli r2,r1,4
             * lw r3,arr(r2)
             * left
             * sw left_off(r14), r3
             */
            else if (isArrayVar(rightNode)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        rightNode.children.get(1).children.get(0).symbolTableEntry.name);
                String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        leftNodeName);
                // right
                writeExecCode(null, LOAD_WORD, buildOperation(null, r1, rightOffset, STACK_REG, null, null),
                        "% load value for " + leftNodeName);
                writeExecCode(null, MUL_I, buildOperation(null, r2, null, r1, null, "4"), null);
                writeExecCode(null, LOAD_WORD, buildOperation(null, r3, rightNodeName, r2, null, null), null);
                // left
                writeExecCode("", STORE_WORD, buildOperation(leftOffset, STACK_REG, null, r3, null, null),
                        "% assign " + leftNodeName);
                this.registerPool.push(r3);
                this.registerPool.push(r2);
                this.registerPool.push(r1);
            }
            /**
             * l1.b = l1.a
             * 
             * right
             * addi r1,r0,right_offset
             * lw r2,l1(r1)
             * 
             * left
             * addi r3,r0,left_offset
             * sw l1(r3),r2
             */
            else if (Util.isDotNode(leftNode) && Util.isDotNode(rightNode)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String rightOffset = SymbolTable.getFieldSize(node.symbolTable,
                        rightNode.symbolTableEntry.type.name,
                        rightNode.children.get(1).children.get(0).getToken().getLexeme());
                String leftOffset = SymbolTable.getFieldSize(node.symbolTable,
                        leftNode.symbolTableEntry.type.name,
                        leftNode.children.get(1).children.get(0).getToken().getLexeme());
                // right
                writeExecCode(null, ADD_I, buildOperation(null, r1, null, ZERO_REG, null, rightOffset),
                        "% load value for " + leftNodeName);
                writeExecCode(null, LOAD_WORD, buildOperation(null, r2, rightNodeName, r1, null, null), null);
                // left
                writeExecCode(null, ADD_I, buildOperation(null, r3, null, ZERO_REG, null, leftOffset), null);
                writeExecCode(null, STORE_WORD, buildOperation(leftNodeName, r3, null, r2, null, null),
                        "% assign " + leftNodeName);
                this.registerPool.push(r3);
                this.registerPool.push(r2);
                this.registerPool.push(r1);
            }
            /**
             * l1.b = x
             * 
             * right
             * lw r1, b_offset(r14)
             * 
             * left
             * addi r2,r0,left_offset
             * sw l1(r2),r1
             */
            else if (Util.isDotNode(leftNode)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        rightNodeName);
                String leftOffset = SymbolTable.getFieldSize(node.symbolTable,
                        leftNode.symbolTableEntry.type.name,
                        leftNode.children.get(1).children.get(0).getToken().getLexeme());
                // right
                writeExecCode("", LOAD_WORD, buildOperation(null, r1, rightOffset, STACK_REG, null, null),
                        "% load value for " + leftNodeName);
                // left
                writeExecCode(null, ADD_I, buildOperation(null, r2, null, ZERO_REG, null, leftOffset), null);
                writeExecCode(null, STORE_WORD, buildOperation(leftNodeName, r2, null, r1, null, null),
                        "% assign " + leftNodeName);
                this.registerPool.push(r2);
                this.registerPool.push(r1);
            }
            /**
             * x = l1.a
             * 
             * right
             * addi r1,r0,right_offset
             * lw r2,l1(r1)
             * 
             * left
             * sw a_offset(r14), r2
             */
            else if (Util.isDotNode(rightNode)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();

                String rightOffset = SymbolTable.getFieldSize(node.symbolTable,
                        rightNode.symbolTableEntry.type.name,
                        rightNode.children.get(1).children.get(0).getToken().getLexeme());
                String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        leftNodeName);
                // right
                writeExecCode(null, ADD_I, buildOperation(null, r1, null, ZERO_REG, null, rightOffset),
                        "% load value for " + leftNodeName);
                writeExecCode(null, LOAD_WORD, buildOperation(null, r2, rightNodeName, r1, null, null), null);
                // left
                writeExecCode("", STORE_WORD, buildOperation(leftOffset, STACK_REG, null, r2, null, null),
                        "% assign " + leftNodeName);

                this.registerPool.push(r2);
                this.registerPool.push(r1);
            }
            /**
             * a = b a = 1
             * 
             * lw r1, b_offset(r14)
             * sw a_offset(r14), r1
             */
            else {
                String r1 = this.registerPool.pop();
                String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        rightNodeName);
                String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        leftNodeName);

                writeExecCode("", LOAD_WORD, buildOperation(null, r1, rightOffset, STACK_REG, null, null),
                        "% load value for " + leftNodeName);
                writeExecCode("", STORE_WORD, buildOperation(leftOffset, STACK_REG, null, r1, null, null),
                        "% assign " + leftNodeName);

                registerPool.push(r1);
            }
        }
        /**
         * write(1)
         */
        else if (node.getName().equals(SemanticAction.WRITE_STAT)) {
            passVisitorToChildren(node);
            /**
             * lw r1, index_off(r14)
             * muli r2,r1,4
             * lw "r1",arr(r2)
             * 
             */
            if (isArrayVar(node.children.get(0))) {
                String indexOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(0).children.get(1).children.get(0).symbolTableEntry.name);
                String arrayName = node.children.get(0).symbolTableEntry.name;
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                writeExecCode(null, LOAD_WORD, buildOperation(null, r1, indexOffset, STACK_REG, null, null), null);
                writeExecCode(null, MUL_I, buildOperation(null, r2, null, r1, null, "4"), null);
                writeExecCode("", LOAD_WORD, buildOperation(null, "r1", arrayName, r2, null, null),
                        "% print integer array: " + arrayName);
                writeExecCode("", JUMP_AND_LINK, buildOperation(null, JUMP_REG, null, "putint", null, null), "");

                this.registerPool.push(r2);
                this.registerPool.push(r1);
            }
            /**
             * write(l1.a)
             * 
             * addi r1,r0,right_offset
             * lw r1,l1(r1)
             * 
             */
            else if (Util.isDotNode(node.children.get(0))) {
                String dotName = node.children.get(0).symbolTableEntry.name;
                String objectTypeName = node.children.get(0).symbolTableEntry.type.name;
                String objectFieldName = node.children.get(0).children.get(1).children.get(0).getToken().getLexeme();
                String size = SymbolTable.getFieldSize(node.symbolTable, objectTypeName, objectFieldName);
                String r1 = this.registerPool.pop();

                writeExecCode(null, ADD_I, buildOperation(null, r1, null, ZERO_REG, null, size),
                        "% print object: " + dotName + " field: " + objectFieldName);
                writeExecCode("", LOAD_WORD, buildOperation(null, "r1", dotName, r1, null, null), null);
                writeExecCode("", JUMP_AND_LINK, buildOperation(null, JUMP_REG, null, "putint", null, null), "");

                this.registerPool.push(r1);
            } else {
                String valueOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(0).symbolTableEntry.name);

                writeExecCode("", LOAD_WORD, buildOperation(null, "r1", valueOffset, STACK_REG, null, null),
                        "% print var: " + node.children.get(0).symbolTableEntry.name);
                writeExecCode("", JUMP_AND_LINK, buildOperation(null, JUMP_REG, null, "putint", null, null), "");
            }
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
         */
        else if (node.getName().equals(SemanticAction.REL_EXPR)) {
            passVisitorToChildren(node);

            Node leftNode = node.children.get(0);
            String leftNodeName = leftNode.symbolTableEntry.name;
            Node rightNode = node.children.get(2);
            String rightNodeName = rightNode.symbolTableEntry.name;
            String relExprOffset = node.symbolTableEntry.getOffsetAsString();
            Node operator = node.children.get(1);

            /**
             * left
             * lw r1,left_off(r14)
             * muli r2,r1,4
             * lw r3,left_name(r2)
             * 
             * right
             * lw r4,right_off(r14)
             * muli r5,r4,4
             * lw r6,right_name(r5)
             * 
             * compare
             * ceq r7,r3,r6
             * sw addOp_offset(r14),r7
             */
            if (isArrayVar(leftNode) && isArrayVar(rightNode)) {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();
                String r4 = this.registerPool.pop();
                String r5 = this.registerPool.pop();
                String r6 = this.registerPool.pop();
                String r7 = this.registerPool.pop();
                String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        leftNode.children.get(1).children.get(0).symbolTableEntry.name);
                String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        rightNode.children.get(1).children.get(0).symbolTableEntry.name);
                writeExecCode(null, LOAD_WORD, buildOperation(null, r1, leftOffset, STACK_REG, null, null),
                        "% relExpr for both array operands");
                writeExecCode(null, MUL_I, buildOperation(null, r2, null, r1, null, "4"), null);
                writeExecCode(null, LOAD_WORD, buildOperation(null, r3, leftNodeName, r2, null, null), null);

                writeExecCode(null, LOAD_WORD, buildOperation(null, r4, rightOffset, STACK_REG, null, null), null);
                writeExecCode(null, MUL_I, buildOperation(null, r5, null, r4, null, "4"), null);
                writeExecCode(null, LOAD_WORD, buildOperation(null, r6, rightNodeName, r5, null, null), null);

                switch (operator.getToken().getType()) {
                    case LA_TYPE.EQ:
                        writeExecCode(null, EQUAL, buildOperation(null, r7, null, r3, null, r6), null);
                        break;
                    case LA_TYPE.NOTEQ:
                        writeExecCode(null, NOT_EQUAL, buildOperation(null, r7, null, r3, null, r6), null);
                        break;
                    case LA_TYPE.LT:
                        writeExecCode(null, LESS, buildOperation(null, r7, null, r3, null, r6), null);
                        break;
                    case LA_TYPE.LEQ:
                        writeExecCode(null, LESS_OR_EQUAL, buildOperation(null, r7, null, r3, null, r6), null);
                        break;
                    case LA_TYPE.GT:
                        writeExecCode(null, GREATER, buildOperation(null, r7, null, r3, null, r6), null);
                        break;
                    case LA_TYPE.GEQ:
                        writeExecCode(null, GREATER_OR_EQUAL, buildOperation(null, r7, null, r3, null, r6), null);
                        break;
                    default:
                        writeExecCode(null, null, null, "% unknown operator " + operator.getToken().getType());
                        break;
                }

                writeExecCode(null, STORE_WORD, buildOperation(relExprOffset, STACK_REG, null, r7, null, null), null);
                registerPool.push(r7);
                registerPool.push(r6);
                registerPool.push(r5);
                registerPool.push(r4);
                registerPool.push(r3);
                registerPool.push(r2);
                registerPool.push(r1);
            }
            /**
             * a == b
             * 
             * lw r1,offset(r14)
             * lw r2,offset(r14)
             * 
             * ceq r3,r1,r2
             * sw offset(r14),r3
             */
            else {
                String r1 = this.registerPool.pop();
                String r2 = this.registerPool.pop();
                String r3 = this.registerPool.pop();

                String leftOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(0).symbolTableEntry.name);
                String rightOffset = SymbolTable.getOffsetByName(node.symbolTable,
                        node.children.get(2).symbolTableEntry.name);
                String resultOffset = node.symbolTableEntry.getOffsetAsString();

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
            this.registerPool.push(r1);
            // code for statBlock
            visit(node.children.get(1));
            writeExecCode(null, JUMP, tag2, null);
            // code for statBlock
            writeExecCode(tag1, null, null, null);
            visit(node.children.get(2));
            writeExecCode(tag2, null, null, null);
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
            this.registerPool.push(r1);

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
            // array can only be 1D and has to be integer
            if (node.children.get(1).getToken().getLexeme().equals(LA_TYPE.INTEGER)
                    && node.children.get(2).children.size() == 1) {
                String size = String.valueOf(Util.getTypeSize(node.children.get(1).getName())
                        * Integer.parseInt(node.children.get(2).children.get(0).getToken().getLexeme()));
                writeDataCode(node.symbolTableEntry.name, RESERVE, size, "");
            } else if (!Util.isPrimaryType(node.children.get(1).getToken().getLexeme())) {
                SymbolTableEntry structEntry = SymbolTable.lookupEntryInTableAndUpperTable(node.symbolTable,
                        node.symbolTableEntry.type.name,
                        Kind.struct);
                writeDataCode(node.symbolTableEntry.name, RESERVE, String.valueOf(structEntry.link.scopeSize), "");
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
        out.write(HALT + "\n");
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

    public boolean isArrayVar(Node node) {
        if (node.getName().equals(SemanticAction.VAR) && node.children.get(1).children.size() > 0) {
            return true;
        }

        return false;
    }
}
