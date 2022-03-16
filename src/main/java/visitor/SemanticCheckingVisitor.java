package visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
        /**
         * 10.1 Type error in expression
         */
        if (node.getName().equals(SemanticAction.ADD_OP) || node.getName().equals(SemanticAction.MULT_OP)) {
            // first pass visitor to children
            for (Node child : node.children) {
                child.accept(this);
            }

            String firstOperandTypeName = node.children.get(0).symbolTableEntry.type.name;
            String secondOperandTypeName = node.children.get(1).symbolTableEntry.type.name;
            if (!isSameTypeOperand(firstOperandTypeName, secondOperandTypeName)) {
                logger.write("[error][semantic] type error in expression: " + node.getToken().getLexeme() + ", line: "
                        + node.getToken().getLocation() + "\n");
            } else {
                node.symbolTableEntry = new SymbolTableEntry(node.getName(), null, null);
                if (firstOperandTypeName.equals(LA_TYPE.INTEGER) || firstOperandTypeName.equals(LA_TYPE.INTNUM)) {
                    node.symbolTableEntry.type = new SymbolTableEntryType(LA_TYPE.INTNUM);
                } else if (firstOperandTypeName.equals(LA_TYPE.FLOAT)
                        || firstOperandTypeName.equals(LA_TYPE.FLOATNUM)) {
                    node.symbolTableEntry.type = new SymbolTableEntryType(LA_TYPE.FLOATNUM);
                } else {
                    node.symbolTableEntry.type = node.children.get(0).symbolTableEntry.type;
                }
            }
        }
        /**
         * intnum, floatnum
         */
        else if (node.getName().equals(LA_TYPE.INTNUM) || node.getName().equals(LA_TYPE.FLOATNUM)) {
            node.symbolTableEntry = new SymbolTableEntry(node.getName(), null, null);
            node.symbolTableEntry.type = new SymbolTableEntryType(node.getName());
        }
        /**
         * check var exists
         * 
         * 11.1 Undeclared local variable
         * 13.1 Use of array with wrong number of dimensions
         */
        else if (node.getName().equals(SemanticAction.VAR)) {
            // if var is under dot, don't check var
            if (!node.parent.getName().equals(SemanticAction.DOT)) {
                SymbolTable table = node.symbolTable;
                String varName = node.children.get(0).getToken().getLexeme();
                // var found in table is either varialbe or parameter
                while (table != null
                        && (table.getEntryByNameKind(varName,
                                Kind.variable) == null
                                && table.getEntryByNameKind(varName,
                                        Kind.parameter) == null)) {
                    table = table.upperTable;
                }
                if (table == null) {
                    logger.write(
                            "[error][semantic] Undeclared local variable: "
                                    + varName + ", line: "
                                    + node.children.get(0).getToken().getLocation() + "\n");
                } else {
                    // find var, write type into the var node
                    // var is either a variable or a parameter
                    SymbolTableEntry varEntry = table.getEntryByNameKind(varName,
                            Kind.variable);
                    if (varEntry == null) {
                        varEntry = table.getEntryByNameKind(varName,
                                Kind.parameter);
                    }
                    node.symbolTableEntry = new SymbolTableEntry(varEntry.name, null, null);
                    node.symbolTableEntry.type = varEntry.type;
                }
            }
            // whenever use a variable, need to use with correct array dimension
            if (node.symbolTableEntry != null && node.parent.getName().equals(SemanticAction.ASSIGN_STAT)) {
                if (node.symbolTableEntry.type.dimension.size() != node.children.get(1).children.size()) {
                    logger.write(
                            "[error][semantic] use of array with wrong number of dimensions "
                                    + node.children.get(0).getToken().getLexeme() + ", line: "
                                    + node.children.get(0).getToken().getLocation() + "\n");
                }
            }

            for (Node child : node.children) {
                child.accept(this);
            }
        }
        /**
         * | ├──varDecl
         * | | ├──id
         * | | ├──id
         * | | └──arraySize
         * 
         * | ├──varDecl
         * | | ├──id
         * | | ├──float
         * | | └──arraySize
         * 
         * 11.5 Undeclared class
         */
        else if (node.getName().equals(SemanticAction.VAR_DECL)) {
            // when varDecl type is id
            Node varDeclType = node.children.get(1);
            if (varDeclType.getToken().getType().equals(LA_TYPE.ID)) {
                SymbolTable table = node.symbolTable;
                while (table != null
                        && table.getEntryByNameKind(varDeclType.getToken().getLexeme(),
                                Kind.struct) == null) {
                    table = table.upperTable;
                }
                if (table == null) {
                    logger.write(
                            "[error][semantic] Undeclared class: "
                                    + varDeclType.getToken().getLexeme() + ", line: "
                                    + varDeclType.getToken().getLocation() + "\n");
                }
            }
        }
        /**
         * 11.4 Undeclared free function
         * 
         * 12.1 Function call with wrong number of parameters
         * 12.2 Function call with wrong type of parameters
         * 13.3 Array parameter using wrong number of dimensions
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
                Node fCallId = node.children.get(0);
                node.symbolTableEntry = new SymbolTableEntry(SemanticAction.FCALL, null, null);
                node.symbolTableEntry.funcInputType = node.children.get(1).symbolTableEntry.funcInputType;

                String fCallName = fCallId.getToken().getLexeme();
                while (table != null
                        && table.getEntryByNameKind(fCallName,
                                Kind.function) == null) {
                    table = table.upperTable;
                }
                if (table == null) {
                    logger.write(
                            "[error][semantic] Undeclared free function: "
                                    + node.children.get(0).getToken().getLexeme() + ", line: "
                                    + node.children.get(0).getToken().getLocation() + "\n");
                } else {
                    // fCall is defined, check parameters
                    SymbolTableEntry funcDefEntry = table.getEntryByNameKind(fCallName,
                            Kind.function);
                    // funcDef.funcInputType compares with fCall.aParams
                    if (funcDefEntry.funcInputType.size() != node.symbolTableEntry.funcInputType.size()) {
                        logger.write(
                                "[error][semantic] Function call with wrong number of parameters, expected: "
                                        + funcDefEntry.funcInputType.toString() + " actual: "
                                        + node.symbolTableEntry.funcInputType.toString() + ", line: "
                                        + fCallId.getToken().getLocation() + "\n");
                    } else {
                        boolean inputTypeError = false;
                        // !funcDefEntry.funcInputType.equals(node.symbolTableEntry.funcInputType)
                        // compare funcDefEntry.funcInputType with node.symbolTableEntry.funcInputType
                        for (int i = 0; i < funcDefEntry.funcInputType.size(); i++) {
                            SymbolTableEntryType fparam = funcDefEntry.funcInputType.get(i);
                            SymbolTableEntryType aparam = node.symbolTableEntry.funcInputType.get(i);
                            if (!fparam.name.equals(aparam.name)) {
                                inputTypeError = true;
                                continue;
                            }
                            if (fparam.dimension.size() != aparam.dimension.size()) {
                                inputTypeError = true;
                                continue;
                            }
                            for (int j = 0; j < fparam.dimension.size(); j++) {
                                if (!fparam.dimension.get(j).equals(aparam.dimension.get(j))
                                        && !fparam.dimension.get(j).equals("0")) {
                                    inputTypeError = true;
                                    continue;
                                }
                            }
                        }
                        if (inputTypeError) {
                            logger.write(
                                    "[error][semantic] Function call with wrong type of parameters "
                                            + funcDefEntry.funcInputType.toString() + " actual: "
                                            + node.symbolTableEntry.funcInputType.toString() + " line: "
                                            + fCallId.getToken().getLocation() + "\n");
                        }
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
                if (child.getName().equals(SemanticAction.VAR) || child.getName().equals(SemanticAction.SIGN)) {
                    if (child.symbolTableEntry.type != null) {
                        node.symbolTableEntry.funcInputType.add(child.symbolTableEntry.type);
                    }
                } else if (child.getToken().getType().equals(LA_TYPE.FLOATNUM)) {
                    node.symbolTableEntry.funcInputType.add(new SymbolTableEntryType(LA_TYPE.FLOAT));
                } else if (child.getToken().getType().equals(LA_TYPE.INTNUM)) {
                    node.symbolTableEntry.funcInputType.add(new SymbolTableEntryType(LA_TYPE.INTEGER));
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
            // find dot var type
            SymbolTable table = node.symbolTable;
            // var found in table is either varialbe or parameter
            while (table != null
                    && (table.getEntryByNameKind(varName,
                            Kind.variable) == null
                            && table.getEntryByNameKind(varName,
                                    Kind.parameter) == null)) {
                table = table.upperTable;
            }
            if (table == null) {
                logger.write(
                        "[error][semantic] Undeclared local variable "
                                + varName + " line: "
                                + node.children.get(0).getToken().getLocation() + "\n");
            } else {
                // find var, write type into the dot node
                // var is either a variable or a parameter
                SymbolTableEntry varEntry = table.getEntryByNameKind(varName,
                        Kind.variable);
                if (varEntry == null) {
                    varEntry = table.getEntryByNameKind(varName,
                            Kind.parameter);
                }
                node.symbolTableEntry = new SymbolTableEntry(varEntry.name, null, null);
                node.symbolTableEntry.type = varEntry.type;
                // dot var type needs to be ID, which means struct
                if (node.symbolTableEntry.type.name.equals(LA_TYPE.INTEGER)
                        || node.symbolTableEntry.type.name.equals(LA_TYPE.FLOAT)) {
                    logger.write(
                            "[error][semantic] dot operator used on non-class type "
                                    + varName + " type: " + node.symbolTableEntry.type.toString() + " line: "
                                    + node.children.get(0).children.get(0).getToken().getLocation() + "\n");
                }
            }

            for (Node child : node.children) {
                child.accept(this);
            }
        }
        /**
         * | └──indiceList
         * | | ├──intnum
         * | | └──var
         * | | | ├──id
         * | | | └──indiceList
         * 
         * 13.1 Use of array with wrong number of dimensions
         * 13.2 Array index is not an integer
         */
        else if (node.getName().equals(SemanticAction.INDICE_LIST)) {
            // pass to all children
            for (Node child : node.children) {
                child.accept(this);
            }

            for (Node child : node.children) {
                if (!child.symbolTableEntry.type.name.equals(LA_TYPE.INTEGER)
                        && !child.symbolTableEntry.type.name.equals(LA_TYPE.INTNUM)) {
                    logger.write(
                            "[error][semantic] array index is not an integer, line: "
                                    + node.parent.children.get(0).getToken().getLocation() + "\n");
                }
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

    public static Set<String> intGroup = new HashSet<>() {
        {
            add(LA_TYPE.INTEGER);
            add(LA_TYPE.INTNUM);
        }
    };

    public static Set<String> floatGroup = new HashSet<>() {
        {
            add(LA_TYPE.FLOATNUM);
            add(LA_TYPE.FLOAT);
        }
    };

    public boolean isSameTypeOperand(String firstOperandType, String secondOperandType) {
        if (firstOperandType.equals(secondOperandType)) {
            return true;
        } else if (intGroup.contains(firstOperandType) && intGroup.contains(secondOperandType)) {
            return true;
        } else if (floatGroup.contains(firstOperandType) && floatGroup.contains(secondOperandType)) {
            return true;
        }

        return false;
    }

}
