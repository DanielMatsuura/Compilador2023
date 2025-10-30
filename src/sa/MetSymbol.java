package sa;

import ast.Identifier;
import ast.ParamList;
import ast.Type;

//Clase para representar un objeto del symbol table pero que sea un metodo
public class MetSymbol extends SymbolItem{
	//Metodos    = met 0 , nombre 1, tipo de retorno 2, clase 3, linea 4, parametros
	
	private ParamList parameters;

	public MetSymbol(Identifier _id, Type _type, int _line, ParamList _parameters) {
		this.id= _id;
		this.type = _type;
		this.line = _line;
		this.parameters = _parameters;
		this.clase = null;
	}
	
	public ParamList getParameters() {
		return parameters;
	}

	public void setParameters(ParamList parameters) {
		this.parameters = parameters;
	}
	
	
}
