package sa;

import ast.Identifier;
import ast.MethodDecl;
import ast.Type;
//Clase para representar un objeto del symbol table pero que sea una variable
public class VarSymbol extends SymbolItem{
	
	//Variables  = var 0 , nombre 1, tipo de dato 2, clase si es una variable de clase 3 , metodo si es una variable de metodo 4, linea 5
	private MethodDecl metodo;
	private Boolean used;
	
	public VarSymbol(Identifier _id, Type _type, int _line) {
		this.id= _id;
		this.type = _type;
		this.clase = null;
		this.metodo = null;
		this.line = _line;
		this.used = false;
	}

	public MethodDecl getMetodo() {
		return metodo;
	}

	public void setMetodo(MethodDecl metodo) {
		this.metodo = metodo;
	}

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}
	
	
	

}
