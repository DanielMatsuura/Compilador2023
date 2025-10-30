package sa;

import ast.Identifier;
import ast.Node;

// Clase para representar un objeto del symbol table pero que sea una clase
public class ClassSymbol extends SymbolItem{
	public ClassSymbol(Identifier _id, int _line, Node _clase) {
		this.id= _id;
		this.clase = _clase;
		this.line = _line;
	}

}
