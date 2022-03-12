package visitor;

import java.util.List;

import ast.Node;
import ast.SemanticAction;
import symboltable.Kind;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

public class SymbolTableCreationVisitor implements Visitor {

    @Override
    public void visit(Node node) {

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
         * | | ├──funcBody
         * | | | ├──fCall
         * | | | | ├──aParams
         * | | | | | ├──intnum
         * | | | | | └──var
         * | | | | | | ├──indiceList
         * | | | | | | └──id
         * | | | | └──id
         * | | | ├──fCall
         * | | | | ├──aParams
         * | | | | | ├──intnum
         * | | | | | └──var
         * | | | | | | ├──indiceList
         * | | | | | | └──id
         * | | | | └──id
         * | | | ├──assignStat
         * | | | | ├──intnum
         * | | | | └──var
         * | | | | | ├──indiceList
         * | | | | | | └──intnum
         * | | | | | └──id
         * | | | ├──assignStat
         * | | | | ├──intnum
         * | | | | └──var
         * | | | | | ├──indiceList
         * | | | | | | └──intnum
         * | | | | | └──id
         * | | | └──varDecl
         * | | | | ├──arraySize
         * | | | | | └──intnum
         * | | | | ├──integer
         * | | | | └──id
         * | | └──funcHead
         * | | | ├──void
         * | | | ├──fparamList
         * | | | └──id
         */
        else if (node.getName().equals(SemanticAction.FUNC_DEF)) {
            SymbolTable parent = node.symbolTable;

            // make symbol table entry for the funcDel node
            Node funcHead = node.children.get(1);
            String funcName = funcHead.children.get(2).getToken().getLexeme();
            SymbolTable localSymbolTable = new SymbolTable(funcName, node.symbolTable);
            // assign local table to the node
            node.symbolTable = localSymbolTable;

            // pass visitor to funcHead -> fparamList
            for (Node child : node.children.get(1).children.get(1).children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }

            SymbolTableEntry fEntry = new SymbolTableEntry(funcName, Kind.function, localSymbolTable);
            fEntry.funcOutputType = new SymbolTableEntryType(funcHead.children.get(0).getToken().getLexeme());

            List<Node> fparamList = funcHead.children.get(1).children;
            for (Node fParam : fparamList) {
                fEntry.funcInputType.add(fParam.symbolTableEntry.type);
            }
            node.symbolTableEntry = fEntry;

            // add local entry to parent table
            parent.addEntry(fEntry);

            // propagate accepting the same visitor to all the children
            // this effectively achieves Depth-First AST Traversal
            // pass to funcBody
            node.children.get(0).symbolTable = node.symbolTable;
            node.children.get(0).accept(this);
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
         * | └──structDecl
         * | | ├──funcHead
         * | | | ├──float
         * | | | ├──fparamList
         * | | | | └──fparam
         * | | | | | ├──arraySize
         * | | | | | ├──float
         * | | | | | └──id
         * | | | └──id
         * | | ├──public
         * | | ├──inherlist
         * | | | └──id
         * | | └──id
         */
        else if (node.getName().equals(SemanticAction.STRUCT_DECL)) {
            String structName = node.children.get(node.children.size() - 1).getToken().getLexeme();
            SymbolTable localSymbolTable = new SymbolTable(structName, null);
            node.symbolTableEntry = new SymbolTableEntry(structName, Kind.struct, localSymbolTable);

            // add inherits
            List<Node> inherlist = node.children.get(node.children.size() - 2).children;
            for (Node i : inherlist) {
                node.symbolTableEntry.inherits.add(i.getToken().getLexeme());
            }

            // add current symbol table entry to parent table
            node.symbolTable.addEntry(node.symbolTableEntry);
            node.symbolTable = localSymbolTable;

            for (Node child : node.children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }
        }
        /**
         * | | ├──funcHead
         * | | | ├──float
         * | | | ├──fparamList
         * | | | | └──fparam
         * | | | | | ├──arraySize
         * | | | | | ├──float
         * | | | | | └──id
         * | | | └──id
         */
        else if (node.getName().equals(SemanticAction.FUNC_HEAD)) {
            String funcName = node.children.get(2).getToken().getLexeme();
            List<Node> fparamList = node.children.get(1).children;
            SymbolTableEntry fEntry = new SymbolTableEntry(funcName, Kind.function, null);
            fEntry.funcOutputType = new SymbolTableEntryType(node.children.get(0).getToken().getLexeme());

            // pass visitor to fparamList
            for (Node child : fparamList) {
                // child.symbolTable = node.symbolTable;
                child.accept(this);
            }

            for (Node fParam : fparamList) {
                fEntry.funcInputType.add(fParam.symbolTableEntry.type);
            }
            node.symbolTableEntry = fEntry;
            // add local entry to parent table
            node.symbolTable.addEntry(fEntry);
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
         * 
         * | ├──implDef
         * | | ├──funcDef
         * | | | ├──funcBody
         * | | | | ├──returnStat
         * | | | | | └──var
         * | | | | | | ├──indiceList
         * | | | | | | └──id
         * | | | | ├──assignStat
         * | | | | | ├──var
         * | | | | | | ├──indiceList
         * | | | | | | └──id
         * | | | | | ├──plus
         * | | | | | ├──var
         * | | | | | | ├──indiceList
         * | | | | | | └──id
         * | | | | | ├──mult
         * | | | | | ├──var
         * | | | | | | ├──indiceList
         * | | | | | | └──id
         * | | | | | └──var
         * | | | | | | ├──indiceList
         * | | | | | | └──id
         * | | | | ├──assignStat
         * | | | | | ├──floatnum
         * | | | | | └──var
         * | | | | | | ├──indiceList
         * | | | | | | └──id
         * | | | | └──varDecl
         * | | | | | ├──arraySize
         * | | | | | ├──float
         * | | | | | └──id
         * | | | └──funcHead
         * | | | | ├──float
         * | | | | ├──fparamList
         * | | | | | └──fparam
         * | | | | | | ├──arraySize
         * | | | | | | ├──float
         * | | | | | | └──id
         * | | | | └──id
         */
        else if (node.getName().equals(SemanticAction.IMPL_DEF)) {
            String implName = node.children.get(node.children.size() - 1).getToken().getLexeme();
            SymbolTable localSymbolTable = new SymbolTable(implName, node.symbolTable);
            node.symbolTableEntry = new SymbolTableEntry(implName, Kind.impl, localSymbolTable);

            // add current symbol table entry to parent table
            node.symbolTable.addEntry(node.symbolTableEntry);
            node.symbolTable = localSymbolTable;

            for (Node child : node.children) {
                child.symbolTable = node.symbolTable;
                child.accept(this);
            }
        }
        /**
         * ├──varDecl
         * | ├──arraySize
         * | ├──integer
         * | └──id
         */
        else if (node.getName().equals(SemanticAction.VAR_DECL)) {
            String entryName = node.children.get(2).getToken().getLexeme();
            SymbolTableEntryType type = new SymbolTableEntryType(node.children.get(1).getToken().getLexeme());
            Node arraySize = node.children.get(0);
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
            node.symbolTable.addEntry(node.symbolTableEntry);
        }
        /**
         * | ├──fparam
         * | | ├──arraySize
         * | | ├──integer
         * | | └──id
         */
        else if (node.getName().equals(SemanticAction.FPARAM)) {
            String entryName = node.children.get(2).getToken().getLexeme();
            SymbolTableEntryType type = new SymbolTableEntryType(node.children.get(1).getToken().getLexeme());
            Node arraySize = node.children.get(0);
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
                node.symbolTable.addEntry(node.symbolTableEntry);
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
