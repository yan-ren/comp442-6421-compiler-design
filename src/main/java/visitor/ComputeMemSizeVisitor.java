package visitor;

import java.io.IOException;

import ast.Node;
import ast.SemanticAction;
import lexicalanalyzer.Constants.LA_TYPE;
import symboltable.Kind;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;

public class ComputeMemSizeVisitor implements Visitor {

    public static final int WORD_SIZE = 4;
    public static final int INT_SIZE = WORD_SIZE;
    public static final int FLOAT_SIZE = 2 * WORD_SIZE;

    private int tempVarNum = 0;

    public String getNewTempVarName() {
        tempVarNum++;
        return "t" + tempVarNum;
    }

    @Override
    public void visit(Node node) throws IOException {
        if (node.getName().equals(SemanticAction.PROG)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            // calculate scope size and offset
            updateScopeSizeAndOffset(node);
        } else if (node.getName().equals(SemanticAction.STRUCT_DECL)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            // calculate scope size and offset
            updateScopeSizeAndOffset(node);
            node.symbolTableEntry.size = node.symbolTableEntry.link.scopeSize;
        } else if (node.getName().equals(SemanticAction.FUNC_DEF)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            // always insert return and jump to funcDef symbol table
            SymbolTableEntry jump = new SymbolTableEntry("jump", Kind.jump, null);
            jump.size = WORD_SIZE;
            SymbolTableEntry returnEntry = new SymbolTableEntry("return", Kind.return_stat, null);
            returnEntry.size = WORD_SIZE;
            node.symbolTable.addAtBeginning(jump);
            node.symbolTable.addAtBeginning(returnEntry);
            // calculate scope size and offset
            updateScopeSizeAndOffset(node);
            node.symbolTableEntry.size = node.symbolTableEntry.link.scopeSize;
        } else if (node.getName().equals(SemanticAction.FCALL)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            // store returned value from fcall into a temp var
            node.symbolTableEntry.name = getNewTempVarName();
            node.symbolTableEntry.size = WORD_SIZE;
            node.symbolTableEntry.kind = Kind.tempvar;
            node.symbolTable.appendEntry(node.symbolTableEntry);
        } else if (node.getName().equals(SemanticAction.VAR_DECL)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            node.symbolTableEntry.size = getNodeSize(node);
        } else if (node.getName().equals(SemanticAction.MULT_OP) || node.getName().equals(SemanticAction.ADD_OP)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            node.tempVarName = getNewTempVarName();
            node.symbolTableEntry.name = node.tempVarName;
            node.symbolTableEntry.kind = Kind.tempvar;
            node.symbolTableEntry.size = getTypeSize(node.symbolTableEntry.type.name);
            // this node requires a temp var in symbol table
            node.symbolTable.appendEntry(node.symbolTableEntry);
        } else if (node.getName().equals(SemanticAction.FPARAM)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            node.symbolTableEntry.size = getNodeSize(node);
        } else if (node.getName().equals(LA_TYPE.INTNUM) || node.getName().equals(LA_TYPE.FLOATNUM)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            /*
             * node.tempVarName = getNewTempVarName();
             * node.symbolTableEntry = new SymbolTableEntry(node.tempVarName, Kind.litvar,
             * null);
             * node.symbolTableEntry.size = getTypeSize(node.getName());
             * // this node requires a temp var in symbol table
             * if (node.symbolTable != null) {
             * node.symbolTable.appendEntry(node.symbolTableEntry);
             * }
             */
        }
        /**
         * Default case
         */
        else {
            for (Node child : node.children) {
                child.accept(this);
            }
        }
    }

    private void updateScopeSizeAndOffset(Node node) {
        int scopeSize = 0;
        int offset = 0;
        for (SymbolTableEntry entry : node.symbolTable.getEntries()) {
            entry.offset = offset;
            scopeSize += entry.size;
            offset -= entry.size;
        }

        node.symbolTable.scopeSize = scopeSize;
    }

    // TODO: get struct type size
    private int getTypeSize(String typeName) {
        if (typeName.equals(LA_TYPE.INTEGER) || typeName.equals(LA_TYPE.INTNUM)) {
            return INT_SIZE;
        } else if (typeName.equals(LA_TYPE.FLOAT) || typeName.equals(LA_TYPE.FLOATNUM)) {
            return FLOAT_SIZE;
        }

        return 0;
    }

    // TODO: get struct type size
    private int getNodeSize(Node node) {
        int totalDim = 1;
        for (String dim : node.symbolTableEntry.type.dimension) {
            totalDim += Integer.parseInt(dim);
        }

        return totalDim * getTypeSize(node.symbolTableEntry.type.name);
    }
}
