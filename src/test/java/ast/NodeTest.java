package ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NodeTest {
    @Test
    void test1() throws Exception {
        Node root = new Node("indiceList");
        root.addChild(new Node("arithExpr"));
        root.children.get(0).addChild(new Node("term"));
        assertEquals(1, root.children.size());
        Node.postProcess(root);
        assertEquals(0, root.children.size());
        assertEquals("indiceList", root.getName());
    }
}
