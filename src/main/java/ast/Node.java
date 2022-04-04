package ast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import lexicalanalyzer.Token;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import visitor.Visitor;

public class Node {
	// original node data fields
	private String name;
	private Token token;
	public Node parent;
	public ArrayList<Node> children;

	// field only used to generate AST dot file
	private int index;

	// symbol table fields
	public SymbolTableEntry symbolTableEntry;
	public SymbolTable symbolTable;

	public Node(String name) {
		this.name = name;
		this.children = new ArrayList<>();
	}

	// sa1 uses this constructor, token type is node name, e.g. LA_TYPE
	public Node(Token token) {
		this.name = token.getType();
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
		return "Node [name=" + name + ", token=" + token + ", parent=" + parent.name + ", children=" + children.size()
				+ "]";
	}

	public static void printTree(Node root) {
		traverseTree(root, "", "");
	}

	private static void traverseTree(Node root, String padding, String pointer) {
		System.out.println(padding + pointer + root.getName());
		for (int i = 0; i < root.children.size(); i++) {
			if (i == root.children.size() - 1) {
				traverseTree(root.children.get(i), padding + "|   ", "└──");
			} else {
				traverseTree(root.children.get(i), padding + "|   ", "├──");
			}
		}
	}

	public static void printTreeToFile(Node root, BufferedWriter br) throws IOException {
		traverseTree(br, root, "", "");
	}

	private static void traverseTree(BufferedWriter br, Node root, String padding, String pointer) throws IOException {
		br.write(padding + pointer + root.getName() + "\n");
		for (int i = 0; i < root.children.size(); i++) {
			if (i == root.children.size() - 1) {
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
		Queue<Node> queue = new LinkedList<>();
		root.index = counter++;
		queue.add(root);
		while (!queue.isEmpty()) {
			Node node = queue.remove();
			String label = "\"" + node.name;
			if (node.getToken() != null && node.getToken().getLexeme().matches("^[a-zA-Z0-9]+$")) {
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

	public static void postProcess(Node root) {
		removeNodes(root);
		reverseChildren(root);
		adjustPlusMinusSign(root);
	}

	private static void adjustPlusMinusSign(Node root) {
	}

	private static void removeNodes(Node current) {
		if (current == null || current.children == null) {
			return;
		}
		// remove each child in the removal list
		int i = 0;
		while (i < current.children.size()) {
			if (SemanticAction.EXTRA_INTERMEDIATE_NODE.contains(current.children.get(i).getName())) {
				current.children.get(i).parent = null;
				ArrayList<Node> grandchildren = current.children.get(i).children;
				if (grandchildren != null) {
					for (Node n : grandchildren) {
						n.parent = current;
					}
					// level up grandchildren
					current.children = replaceChildren(current.children, i, grandchildren);
				} else {
					current.children = replaceChildren(current.children, i, new ArrayList<>());
				}
				i = 0; // remove one child and place grandchildren in the same place, need to rescan
						// the children
			} else {
				i++;
			}
		}

		for (Node c : current.children) {
			removeNodes(c);
		}
	}

	private static void reverseChildren(Node node) {
		if (node.children != null) {
			Collections.reverse(node.children);
		}

		for (Node c : node.children) {
			reverseChildren(c);
		}
	}

	/**
	 * Insert grandchildren into children at index i and return the new list
	 * 
	 * @param children
	 * @param i
	 * @param grandchildren
	 * @return
	 */
	private static ArrayList<Node> replaceChildren(ArrayList<Node> children, int i, ArrayList<Node> grandchildren) {
		ArrayList<Node> result = new ArrayList<>();
		int index = 0;
		while (index < i) {
			result.add(children.get(index));
			index++;
		}
		index = i + 1;
		for (Node g : grandchildren) {
			result.add(g);
		}
		while (index < children.size()) {
			result.add(children.get(index));
			index++;
		}
		return result;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
}
