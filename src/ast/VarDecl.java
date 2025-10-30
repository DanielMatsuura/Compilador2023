package ast;

public class VarDecl extends Node {
	public Type t;
	public Identifier i;
	public Node preNode;

	public VarDecl(Type at, Identifier ai, int ln) {
		super(ln);
		t = at;
		i = ai;
	}

	public Node getPreNode() {
		return preNode;
	}

	public void setPreNode(Node preNode) {
		this.preNode = preNode;
	}

}
