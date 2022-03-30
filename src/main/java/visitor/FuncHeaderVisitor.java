package visitor;

import java.io.IOException;

import ast.Node;
import ast.SemanticAction;

public class FuncHeaderVisitor implements Visitor {

    @Override
    public void visit(Node node) throws IOException {
        /**
         * funcHead under struct has symbol table entry and symbol table
         * 
         * funcHead under funcDef doesn't have symbol table entry and symbol table
         */
        if (node.getName().equals(SemanticAction.FUNC_HEAD)) {
            for (Node child : node.children) {
                child.accept(this);
            }
            if (node.symbolTableEntry != null) {
                node.symbolTableEntry.size = node.symbolTableEntry.link.scopeSize;
            }
        } else {
            for (Node child : node.children) {
                child.accept(this);
            }
        }
    }
}
