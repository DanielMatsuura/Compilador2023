package sa;

import ast.Identifier;
import ast.Node;
import ast.Type;

//Clase para representar un objeto del symbol table
public class SymbolItem {
	protected Identifier id;
	protected Type type;
	protected int line;
	protected Node clase;

	public Identifier getId() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Node getClase() {
		return clase;
	}

	public void setClase(Node clase) {
		this.clase = clase;
	}

}
