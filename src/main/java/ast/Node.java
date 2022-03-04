package ast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import lexicalanalyzer.Token;

public class Node {
	private String name;
	private Token token;
	// to generate dot file
	private int index;

	public Node parent;
	public ArrayList<Node> children;

	public Node(String name) {
		this.name = name;
		this.children = new ArrayList<>();
	}

	public Node(Token token) {
		this.name = token.getType();
		this.token = token;
		this.children = new ArrayList<>();
	}

	public Node(String name, Token token) {
		this.name = name;
		this.token = token;
		this.children = new ArrayList<>();
	}

	public void addChild(Node child) {
		child.parent = this;
		this.children.add(child);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "Node [name=" + name + ", token=" + token + ", parent=" + parent + ", children=" + children + "]";
	}

	public static void printTreeToFile(Node root, BufferedWriter br) throws IOException {
		traverseTree(br, root, "", "");
	}

	private static void traverseTree(BufferedWriter br, Node root, String padding, String pointer) throws IOException {
		br.write(padding + pointer + root.getName() + "\n");
		for (int i = root.children.size() - 1; i >= 0; i--) {
			if (i == 0) {
				traverseTree(br, root.children.get(i), padding + "|   ", "└──");
			} else {
				traverseTree(br, root.children.get(i), padding + "|   ", "├──");
			}
		}
	}

	static int counter = 0;

	public static void createDotFile(Node root, BufferedWriter br) throws IOException {
		counter = 0;
		br.write(
				"digraph AST { node [shape=record];node [fontname=Sans];charset=\"UTF-8\" splines=true splines=spline rankdir =LR \n");
		traversTree(br, root);
		br.write("}\n");
	}

	private static void traversTree(BufferedWriter br, Node root) throws IOException {
		// String label = "\"" + root.name;
		// if (root.getToken() != null) {
		// label += " | " + root.getToken().getLexeme() + "\"";
		// } else {
		// label += "\"";
		// }
		// br.write(counter + "[label=" + label + "];\n");
		// for (Node child : root.children) {
		// br.write(counter + "->" + (++counter) + ";\n");
		// traversTree(br, child);
		// }
		Queue<Node> queue = new LinkedList<>();
		root.index = counter++;
		queue.add(root);
		while (!queue.isEmpty()) {
			Node node = queue.remove();
			String label = "\"" + node.name;
			if (node.getToken() != null) {
				label += " | " + node.getToken().getLexeme() + "\"";
			} else {
				label += "\"";
			}
			br.write(node.index + "[label=" + label + "];\n");
			if (node.parent != null) {
				br.write(node.parent.index + "->" + node.index + ";\n");
			}
			for (Node child : node.children) {
				child.index = counter++;
				queue.add(child);
			}
		}
	}
}
