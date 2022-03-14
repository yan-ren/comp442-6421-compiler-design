package visitor;

import java.io.BufferedWriter;
import java.io.IOException;

import ast.Node;
import ast.SemanticAction;
import lexicalanalyzer.Constants.LA_TYPE;
import symboltable.Kind;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

public class SemanticCheckingVisitor implements Visitor {
    BufferedWriter logger;

    public SemanticCheckingVisitor(BufferedWriter logger) {
        this.logger = logger;
    }

    @Override
    public void visit(Node node) throws IOException {
        if (node.getName().equals(SemanticAction.ADD_OP) || node.getName().equals(SemanticAction.MULT_OP)) {

        }
        /**
         * check var exists
         * 11.1 Undeclared local variable
         */
        else if (node.getName().equals(SemanticAction.VAR)) {
            // if var is under dot, don't check var
            if (!node.parent.getName().equals(SemanticAction.DOT)) {
                SymbolTable table = node.symbolTable;
                while (table != null
                        && table.getEntryByNameKind(node.children.get(0).getToken().getLexeme(),
                                Kind.variable) == null) {
                    table = table.upperTable;
                }
                if (table == null) {
                    logger.write(
                            "[error][semantic] Undeclared local variable "
                                    + node.children.get(0).getToken().getLexeme() + " line: "
                                    + node.children.get(0).getToken().getLocation() + "\n");
                } else {
                    // find var, write type into the var node
                    SymbolTableEntry varEntry = table.getEntryByNameKind(node.children.get(0).getToken().getLexeme(),
                            Kind.variable);
                    node.symbolTableEntry = new SymbolTableEntry(varEntry.name, null, null);
                    node.symbolTableEntry.type = varEntry.type;
                }
            }

            for (Node child : node.children) {
                child.accept(this);
            }
        }
        /**
         * 11.5 Undeclared class
         */
        else if (node.getName().equals(SemanticAction.VAR_DECL)) {
            if (node.children.get(1).getToken().getType().equals(LA_TYPE.ID)) {
                SymbolTable table = node.symbolTable;
                while (table != null
                        && table.getEntryByNameKind(node.children.get(1).getToken().getLexeme(),
                                Kind.struct) == null) {
                    table = table.upperTable;
                }
                if (table == null) {
                    logger.write(
                            "[error][semantic] Undeclared class "
                                    + node.children.get(1).getToken().getLexeme() + " line: "
                                    + node.children.get(1).getToken().getLocation() + "\n");
                }
            }
        }
        /**
         * 11.4 Undeclared free function
         * 
         * 12.1 Function call with wrong number of parameters
         * 12.2 Function call with wrong type of parameters
         * 
         * 
         * | | | ├──fCall
         * | | | | ├──id
         * | | | | └──aParams
         * | | | | | ├──sign
         * | | | | | | └──floatnum
         * | | | | | ├──floatnum
         * | | | | | └──floatnum
         * 
         */
        else if (node.getName().equals(SemanticAction.FCALL)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            // if fCall is under dot, don't check free function declaretion
            if (!node.parent.getName().equals(SemanticAction.DOT)) {
                SymbolTable table = node.symbolTable;
                Node fCall = node.children.get(0);
                String fCallName = fCall.getToken().getLexeme();
                while (table != null
                        && table.getEntryByNameKind(fCallName,
                                Kind.function) == null) {
                    table = table.upperTable;
                }
                if (table == null) {
                    logger.write(
                            "[error][semantic] Undeclared free function "
                                    + node.children.get(0).getToken().getLexeme() + " line: "
                                    + node.children.get(0).getToken().getLocation() + "\n");
                } else {
                    // fCall is defined, check parameters
                    SymbolTableEntry funcDefEntry = table.getEntryByNameKind(fCallName,
                            Kind.function);
                    // funcDef.funcInputType compares with fCall.aParams
                    if (funcDefEntry.funcInputType.size() != fCall.symbolTableEntry.funcInputType.size()) {
                        logger.write(
                                "[error][semantic] Function call with wrong number of parameters, expected: "
                                        + funcDefEntry.funcInputType.toString() + " actual: "
                                        + fCall.symbolTableEntry.toString() + " line: "
                                        + node.getToken().getLocation() + "\n");
                    } else if (!funcDefEntry.funcInputType.equals(fCall.symbolTableEntry.funcInputType)) {
                        logger.write(
                                "[error][semantic] Function call with wrong type of parameters "
                                        + funcDefEntry.funcInputType.toString() + " actual: "
                                        + fCall.symbolTableEntry.toString() + " line: "
                                        + node.getToken().getLocation() + "\n");
                    }
                }
            }
        }
        /**
         * aParams
         */
        else if (node.getName().equals(SemanticAction.APARAMS)) {
            for (Node child : node.children) {
                child.accept(this);
            }

            node.symbolTableEntry = new SymbolTableEntry(node.getName(), null, null);
            for (Node child : node.children) {
                if (child.getToken().getType().equals(LA_TYPE.FLOATNUM)) {
                    node.symbolTableEntry.funcInputType.add(new SymbolTableEntryType(LA_TYPE.FLOAT));
                } else if (child.getToken().getType().equals(LA_TYPE.INTNUM)) {
                    node.symbolTableEntry.funcInputType.add(new SymbolTableEntryType(LA_TYPE.INTEGER));
                } else {
                    node.symbolTableEntry.funcInputType.add(child.symbolTableEntry.type);
                }
            }
        }
        /**
         * 
         */
        else if (node.getName().equals(SemanticAction.SIGN)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            node.symbolTableEntry = new SymbolTableEntry(node.getName(), null, null);
            node.symbolTableEntry.type = node.children.get(0).symbolTableEntry.type;
        }
        /**
         * 11.2 Undeclared data member
         * 
         * 15.1 "." operator used on non-class type
         */
        else if (node.getName().equals(SemanticAction.DOT)) {
            // first child of DOT should be var with type id
            String varName = node.children.get(0).children.get(0).getToken().getLexeme();
            SymbolTable table = node.symbolTable;
            while (table != null
                    && table.getEntryByNameKind(varName,
                            Kind.struct) == null) {
                table = table.upperTable;
            }
            if (table == null) {
                logger.write(
                        "[error][semantic] dot operator used on non-class type "
                                + varName + " line: "
                                + node.children.get(0).children.get(0).getToken().getLocation() + "\n");
            }

            for (Node child : node.children) {
                child.accept(this);
            }
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

}
