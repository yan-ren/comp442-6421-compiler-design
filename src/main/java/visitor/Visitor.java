package visitor;

import ast.Node;

public interface Visitor {
    public void visit(Node node);
}
