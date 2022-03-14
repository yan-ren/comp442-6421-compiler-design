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
                    node.symbolTableEntry = new SymbolTableEntry(varEntry.name, Kind.varRef, null);
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
         * | | | ├──fCall
         * | | | | ├──id
         * | | | | └──aParams
         * | | | | | ├──var
         * | | | | | | ├──id
         * | | | | | | └──indiceList
         * | | | | | └──intnum
         * 
         */
        else if (node.getName().equals(SemanticAction.FCALL)) {
            // if fCall is under dot, don't check free function declaretion
            if (!node.parent.getName().equals(SemanticAction.DOT)) {
                SymbolTable table = node.symbolTable;
                while (table != null
                        && table.getEntryByNameKind(node.children.get(0).getToken().getLexeme(),
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
                    SymbolTableEntry funcDef = table.getEntryByNameKind(node.children.get(0).getToken().getLexeme(),
                            Kind.function);
                    // funcDef.funcInputType
                }
            }

            for (Node child : node.children) {
                child.accept(this);
            }
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
