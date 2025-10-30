package ast;

public abstract class Node {

	protected int line;
	protected Node preNode;
	protected Boolean nextNode;
	protected Node currentNode;
	protected Node currentClass;
	protected Type currentType;
	protected MethodDecl currentMethod;
	
	public Type getCurrentType() {
		return currentType;
	}

	public void setCurrentType(Type currentType) {
		this.currentType = currentType;
	}

	public MethodDecl getCurrentMethod() {
		return currentMethod;
	}

	public void setCurrentMethod(MethodDecl currentMethod) {
		this.currentMethod = currentMethod;
	}

	public Node(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public Node getPreNode() {
		return preNode;
	}

	public void setPreNode(Node preNode) {
		this.preNode = preNode;
	}

	public Boolean getNextNode() {
		return nextNode;
	}

	public void setNextNode(Boolean nextNode) {
		this.nextNode = nextNode;
	}

	public Node getCurrentClass() {
		return currentClass;
	}

	public void setCurrentClass(Node currentClass) {
		this.currentClass = currentClass;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}
	
	
	
}
