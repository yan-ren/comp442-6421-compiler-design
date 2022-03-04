package ast;

import java.util.ArrayList;

import lexicalanalyzer.Token;

public class Node {
	private String name;
	private Token token;

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

	public static void printTree(Node root) {
		traverseTree(root, "", "");
	}

	private static void traverseTree(Node root, String padding, String pointer) {
		System.out.println(padding + pointer + root.getName());
		for (int i = root.children.size() - 1; i >= 0; i--) {
			if (i == 0) {
				traverseTree(root.children.get(i), padding + "|   ", "└──");
			} else {
				traverseTree(root.children.get(i), padding + "|   ", "├──");
			}
		}
	}
}
