package visitor;

import ast.Node;
import ast.SemanticAction;
import symboltable.Kind;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

public class SymbolTableCreationVisitor implements Visitor {

    @Override
    public void visit(Node node) {
        /**
         * ├──varDecl
         * | ├──id
         * | ├──integer
         * | └──arraySize
         */
        if (node.getName().equals(SemanticAction.VAR_DECL)) {
            String entryName = node.children.get(2).getToken().getLexeme();
            Kind kind = Kind.variable;
            SymbolTableEntryType type = new SymbolTableEntryType(node.children.get(1).getToken().getLexeme());
            Node arraySize = node.children.get(0);
            if (arraySize.children.size() > 0) {
                for (Node n : arraySize.children) {
                    if (n.getName().equals("emptySizeArray")) {
                        type.addDimension(0);
                    } else {
                        type.addDimension(Integer.parseInt(n.getToken().getLexeme()));
                    }
                }
            }
            node.symbolTableEntry = new SymbolTableEntry(entryName, kind, type);
        }
    }

}
