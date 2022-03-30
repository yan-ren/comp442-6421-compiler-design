package visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import ast.Node;
import ast.SemanticAction;
import lexicalanalyzer.Constants.LA_TYPE;
import symboltable.Kind;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

public class SymbolTableCreationVisitor implements Visitor {

    BufferedWriter logger;

    public SymbolTableCreationVisitor(BufferedWriter logger) {
        this.logger = logger;
    }

    @Override
    public void visit(Node node) throws IOException {

        /**
         * prog
         * | ├──structDecl
         * | ├──implDef
         * | └──funcDef
         * 
         */
        if (node.getName().equals(SemanticAction.PROG)) {
            node.symbolTable = new SymbolTable("global", null);
            // propagate accepting the same visitor to all the children
            // this effectively achieves Depth-First AST Traversal
            for (Node child : node.children) {
                // pass symbol table to children, so children can add their entry to the parent
                // symbol table
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }
        }
        /**
         * | ├──funcDef
         * | | ├──funcHead
         * | | | ├──id
         * | | | ├──fparamList
         * | | | | ├──fparam
         * | | | | | ├──id
         * | | | | | ├──float
         * | | | | | └──arraySize
         * | | | | └──fparam
         * | | | | | ├──id
         * | | | | | ├──float
         * | | | | | └──arraySize
         * | | | └──id
         * | | └──funcBody
         * | | | ├──varDecl
         * | | | | ├──id
         * | | | | ├──id
         * | | | | └──arraySize
         * | | | ├──assignStat
         * | | | | ├──dot
         * | | | | | ├──var
         * | | | | | | ├──id
         * | | | | | | └──indiceList
         * | | | | | └──var
         * | | | | | | ├──id
         * | | | | | | └──indiceList
         * | | | | └──var
         * | | | | | ├──id
         * | | | | | └──indiceList
         * | | | └──returnStat
         * | | | | └──var
         * | | | | | ├──id
         * | | | | | └──indiceList
         */
        else if (node.getName().equals(SemanticAction.FUNC_DEF)) {
            SymbolTable parentTable = node.symbolTable;

            // make symbol table entry for the funcDel node by using funcHead
            Node funcHead = node.children.get(0);
            String funcName = funcHead.children.get(0).getToken().getLexeme();
            SymbolTable localSymbolTable = new SymbolTable(funcName, parentTable);
            // assign local table to the node
            node.symbolTable = localSymbolTable;

            // pass visitor to funcHead -> fparamList
            for (Node child : funcHead.children.get(1).children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }

            // create current entry
            SymbolTableEntry fEntry = new SymbolTableEntry(funcName, Kind.function, localSymbolTable);
            fEntry.funcOutputType = new SymbolTableEntryType(funcHead.children.get(2).getToken().getLexeme());

            List<Node> fparamList = funcHead.children.get(1).children;
            for (Node fParam : fparamList) {
                fEntry.funcInputType.add(fParam.symbolTableEntry.type);
            }
            node.symbolTableEntry = fEntry;

            // check function multiple defined and overload
            if (parentTable.getEntryByNameKind(node.symbolTableEntry.name, node.symbolTableEntry.kind) != null) {
                // same funcDel found, compare the input field
                if (fEntry.funcInputType.equals(parentTable.getEntryByNameKind(node.symbolTableEntry.name,
                        node.symbolTableEntry.kind).funcInputType)) {
                    logger.write(
                            "[error][semantic] multiple declaration for " + node.symbolTableEntry.kind + ": "
                                    + node.symbolTableEntry.name + " in scope: "
                                    + parentTable.getName() + ", line "
                                    + funcHead.children.get(0).getToken().getLocation() +
                                    "\n");
                }
                // input field is different, overload
                else {
                    logger.write(
                            "[warn][semantic] Overloaded function for " + node.symbolTableEntry.kind + ": "
                                    + node.symbolTableEntry.name + " in scope: "
                                    + parentTable.getName() + ", line "
                                    + funcHead.children.get(0).getToken().getLocation() +
                                    "\n");
                    int index = parentTable.getEntryIndexByNameKind(node.symbolTableEntry.name,
                            node.symbolTableEntry.kind);
                    parentTable.replaceEntry(index, fEntry);
                }
            } else {
                // add current entry to parent table
                parentTable.appendEntry(fEntry);
            }

            // skip funcHead, pass to funcBody
            node.children.get(1).symbolTable = node.symbolTable;
            node.children.get(1).accept(this);
        }
        /**
         * struct QUADRATIC inherits POLYNOMIAL {
         * private let a: float;
         * private let b: float;
         * private let c: float;
         * public func build(A: float, B: float, C: float) -> QUADRATIC;
         * public func evaluate(x: float) -> float;
         * };
         * 
         * | ├──structDecl
         * | | ├──id
         * | | ├──inherlist
         * | | ├──public
         * | | └──funcHead
         * | | | ├──id
         * | | | ├──fparamList
         * | | | | └──fparam
         * | | | | | ├──id
         * | | | | | ├──float
         * | | | | | └──arraySize
         * | | | └──float
         */
        else if (node.getName().equals(SemanticAction.STRUCT_DECL)) {
            SymbolTable parentTable = node.symbolTable;
            String structName = node.children.get(0).getToken().getLexeme();
            SymbolTable localSymbolTable = new SymbolTable(structName, parentTable);
            node.symbolTableEntry = new SymbolTableEntry(structName, Kind.struct, localSymbolTable);

            // add inherits
            List<Node> inherlist = node.children.get(1).children;
            for (Node i : inherlist) {
                node.symbolTableEntry.inherits.add(i.getToken().getLexeme());
            }

            // add current symbol table entry to parent table
            if (parentTable.getEntryByNameKind(node.symbolTableEntry.name, node.symbolTableEntry.kind) != null) {
                logger.write(
                        "[error][semantic] multiple declaration for " + node.symbolTableEntry.kind + ": "
                                + node.symbolTableEntry.name + " in scope: "
                                + node.symbolTable.getName() + ", line "
                                + node.children.get(0).getToken().getLocation() + "\n");
            }
            parentTable.appendEntry(node.symbolTableEntry);
            node.symbolTable = localSymbolTable;

            for (Node child : node.children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }
        }
        /**
         * | | └──funcHead
         * | | | ├──id
         * | | | ├──fparamList
         * | | | | └──fparam
         * | | | | | ├──id
         * | | | | | ├──float
         * | | | | | └──arraySize
         * | | | └──float
         */
        // since funcDef skip funcHead, this funcHead must from struct
        else if (node.getName().equals(SemanticAction.FUNC_HEAD)) {
            String funcName = node.children.get(0).getToken().getLexeme();
            List<Node> fparamList = node.children.get(1).children;
            SymbolTableEntry fEntry = new SymbolTableEntry(funcName, Kind.function, null);
            fEntry.funcOutputType = new SymbolTableEntryType(node.children.get(2).getToken().getLexeme());
            SymbolTable parentTable = node.symbolTable;

            // pass visitor to fparamList
            for (Node child : fparamList) {
                // funcHead in struct does not create new scope, it reuses the funcDef scope in
                // impl
                // do not pass symbol talbe, which is struct symbol table to child

                // child.symbolTable = node.symbolTable;
                child.accept(this);
            }
            // use fparamList to populate current entry
            for (Node fParam : fparamList) {
                fEntry.funcInputType.add(fParam.symbolTableEntry.type);
            }
            node.symbolTableEntry = fEntry;

            // check function multiple defined and overload
            if (parentTable.getEntryByNameKind(node.symbolTableEntry.name, node.symbolTableEntry.kind) != null) {
                // same funcDel found, compare the input field
                if (fEntry.funcInputType.equals(parentTable.getEntryByNameKind(node.symbolTableEntry.name,
                        node.symbolTableEntry.kind).funcInputType)) {
                    logger.write(
                            "[error][semantic] multiple declaration for " + node.symbolTableEntry.kind + ": "
                                    + node.symbolTableEntry.name + " in scope: "
                                    + parentTable.getName() + ", line "
                                    + node.children.get(0).getToken().getLocation() +
                                    "\n");
                }
                // input field is different, overload
                else {
                    logger.write(
                            "[warn][semantic] Overloaded function for " + node.symbolTableEntry.kind + ": "
                                    + node.symbolTableEntry.name + " in scope: "
                                    + parentTable.getName() + ", line "
                                    + node.children.get(0).getToken().getLocation() +
                                    "\n");
                    int index = parentTable.getEntryIndexByNameKind(node.symbolTableEntry.name,
                            node.symbolTableEntry.kind);
                    parentTable.replaceEntry(index, fEntry);
                }
            } else {
                // add current entry to parent table
                parentTable.appendEntry(fEntry);
            }

            // for (Node child : node.children) {
            // child.symbolTable = node.symbolTable;
            // child.accept(this);
            // }
        }
        /**
         * impl QUADRATIC {
         * func evaluate(x: float) -> float
         * {
         * let result: float;
         * //Using Horner's method
         * result = a;
         * result = result * x + b;
         * result = result * x + c;
         * return (result);
         * }
         * func build(A: float, B: float, C: float) -> QUADRATIC
         * {
         * let new_function: QUADRATIC ;
         * new_function.a = A;
         * new_function.b = B;
         * new_function.c = C;
         * return (new_function);
         * }
         * }
         * *
         * | ├──implDef
         * | | ├──id
         * | | ├──funcDef
         * | | | ├──funcHead
         * | | | | ├──id
         * | | | | ├──fparamList
         * | | | | | └──fparam
         * | | | | | | ├──id
         * | | | | | | ├──float
         * | | | | | | └──arraySize
         * | | | | └──float
         * | | | └──funcBody
         * | | | | ├──varDecl
         * | | | | | ├──id
         * | | | | | ├──float
         * | | | | | └──arraySize
         * | | | | ├──assignStat
         * | | | | | ├──var
         * | | | | | | ├──id
         * | | | | | | └──indiceList
         * | | | | | └──var
         * | | | | | | ├──id
         * | | | | | | └──indiceList
         * | | | | └──returnStat
         * | | | | | └──var
         * | | | | | | ├──id
         * | | | | | | └──indiceList
         * | | └──funcDef
         * | | | ├──funcHead
         * | | | | ├──id
         * | | | | ├──fparamList
         * | | | | | ├──fparam
         * | | | | | | ├──id
         * | | | | | | ├──float
         * | | | | | | └──arraySize
         * | | | | | ├──fparam
         * | | | | | | ├──id
         * | | | | | | ├──float
         * | | | | | | └──arraySize
         * | | | | | └──fparam
         * | | | | | | ├──id
         * | | | | | | ├──float
         * | | | | | | └──arraySize
         * | | | | └──id
         * | | | └──funcBody
         * | | | | ├──varDecl
         * | | | | | ├──id
         * | | | | | ├──id
         * | | | | | └──arraySize
         * | | | | ├──assignStat
         * | | | | | ├──dot
         * | | | | | | ├──var
         * | | | | | | | ├──id
         * | | | | | | | └──indiceList
         * | | | | | | └──var
         * | | | | | | | ├──id
         * | | | | | | | └──indiceList
         * | | | | | └──var
         * | | | | | | ├──id
         * | | | | | | └──indiceList
         * | | | | └──returnStat
         * | | | | | └──var
         * | | | | | | ├──id
         * | | | | | | └──indiceList
         */
        else if (node.getName().equals(SemanticAction.IMPL_DEF)) {
            String implName = node.children.get(0).getToken().getLexeme();
            SymbolTable localSymbolTable = new SymbolTable(implName, node.symbolTable);
            node.symbolTableEntry = new SymbolTableEntry(implName, Kind.impl, localSymbolTable);

            // add current symbol table entry to parent table
            node.symbolTable.appendEntry(node.symbolTableEntry);
            node.symbolTable = localSymbolTable;

            for (Node child : node.children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }
        }
        /**
         * ├──varDecl
         * | ├──id
         * | ├──integer
         * | └──arraySize
         */
        else if (node.getName().equals(SemanticAction.VAR_DECL)) {
            String entryName = node.children.get(0).getToken().getLexeme();
            SymbolTableEntryType type = new SymbolTableEntryType(node.children.get(1).getToken().getLexeme());
            Node arraySize = node.children.get(2);
            if (arraySize.children.size() > 0) {
                for (Node n : arraySize.children) {
                    if (n.getName().equals("emptySizeArray")) {
                        type.dimension.add("0");
                    } else {
                        type.dimension.add(n.getToken().getLexeme());
                    }
                }
            }
            // create entry for node
            node.symbolTableEntry = new SymbolTableEntry(entryName, Kind.variable, null);
            node.symbolTableEntry.type = type;
            // add entry to parent table
            if (node.symbolTable.getEntryByNameKind(node.symbolTableEntry.name, node.symbolTableEntry.kind) != null) {
                logger.write(
                        "[error][semantic] multiple declaration for " + node.symbolTableEntry.kind + ": "
                                + node.symbolTableEntry.name + " in scope: "
                                + node.symbolTable.getName() + ", line " + node.children.get(0).getToken().getLocation()
                                + "\n");
            }
            node.symbolTable.appendEntry(node.symbolTableEntry);

            for (Node child : node.children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }
        }
        /**
         * | ├──fparam
         * | | ├──id
         * | | ├──integer
         * | | └──arraySize
         */
        else if (node.getName().equals(SemanticAction.FPARAM)) {
            String entryName = node.children.get(0).getToken().getLexeme();
            SymbolTableEntryType type = new SymbolTableEntryType(node.children.get(1).getToken().getLexeme());
            Node arraySize = node.children.get(2);
            if (arraySize.children.size() > 0) {
                for (Node n : arraySize.children) {
                    if (n.getName().equals("emptySizeArray")) {
                        type.dimension.add("0");
                    } else {
                        type.dimension.add(n.getToken().getLexeme());
                    }
                }
            }
            // create entry for node
            node.symbolTableEntry = new SymbolTableEntry(entryName, Kind.parameter, null);
            node.symbolTableEntry.type = type;
            // add entry to parent table
            if (node.symbolTable != null) {
                node.symbolTable.appendEntry(node.symbolTableEntry);
            }

            // for (Node child : node.children) {
            // child.symbolTable = node.symbolTable;
            // child.accept(this);
            // }
        }
        /**
         * intnum, floatnum
         */
        else if (node.getName().equals(LA_TYPE.INTNUM) || node.getName().equals(LA_TYPE.FLOATNUM)) {
            // create an entry for this node, but entry is not added to symbol table
            // this entry is only used for semantic checking
            node.symbolTableEntry = new SymbolTableEntry(node.getName(), null, null);
            node.symbolTableEntry.type = new SymbolTableEntryType(node.getName());

            for (Node child : node.children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }
        }
        /**
         * All other semantic action, don't need to add symbol table
         */
        else {
            for (Node child : node.children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }
        }
    }

}
