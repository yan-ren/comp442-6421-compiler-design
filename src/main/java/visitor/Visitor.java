package visitor;

import java.io.IOException;

import ast.Node;

public interface Visitor {
    public void visit(Node node) throws IOException;
}
