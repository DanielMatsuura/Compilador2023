package sa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ast.And;
import ast.ArrayAssign;
import ast.ArrayLength;
import ast.ArrayLookup;
import ast.Assign;
import ast.Block;
import ast.BooleanType;
import ast.Call;
import ast.ClassDeclExtends;
import ast.ClassDeclSimple;
import ast.ClassType;
import ast.Expr;
import ast.ExprParen;
import ast.False;
import ast.Goal;
import ast.Identifier;
import ast.IdentifierExpr;
import ast.If;
import ast.IntArrayType;
import ast.IntType;
import ast.IntegerLiteral;
import ast.LessThan;
import ast.MainClass;
import ast.MethodDecl;
import ast.Minus;
import ast.Mult;
import ast.NewArray;
import ast.NewObject;
import ast.Node;
import ast.Not;
import ast.Param;
import ast.ParamList;
import ast.Plus;
import ast.Print;
import ast.Statement;
import ast.This;
import ast.True;
import ast.Type;
import ast.VarDecl;
import ast.While;
import ast.visitor.Visitor;

public class SymbolTableChecking implements Visitor {

	private HashMap<String, SymbolItem> symbolTable;
	private HashMap<String, Type> types;
	private ArrayList<String> errors;

	public SymbolTableChecking(HashMap<String, SymbolItem> _symbolTable, HashMap<String, Type> _types,
			ArrayList<String> _errors) {
		this.symbolTable = _symbolTable;
		this.errors = _errors;
		this.types = _types;

	}

	@Override
	public void visit(Goal n) {
		// Para control del main class
		n.m.setPreNode(n);
		visit(n.m);

		// Asing Variables type checking
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.get(i).setPreNode(n);
			visit(n.cl.get(i));
		}
	}

	@Override
	public void visit(MainClass n) {
		n.s.setPreNode(n);
		n.s.setCurrentClass(n);
		visit(n.s);

	}

	@Override
	public void visit(ClassDeclSimple n) {
		// Asing Variables type checking
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.get(i).setPreNode(n);
			n.ml.get(i).setCurrentClass(n);
			visit(n.ml.get(i));
		}

	}

	@Override
	public void visit(ClassDeclExtends n) {
		// Asing Variables type checking
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.get(i).setPreNode(n);
			n.ml.get(i).setCurrentClass(n);
			visit(n.ml.get(i));
		}

	}

	@Override
	public void visit(VarDecl n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MethodDecl n) {
		// Asign Variables type checking
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).setPreNode(n);
			n.sl.get(i).setCurrentClass(n.getCurrentClass());
			n.sl.get(i).setCurrentMethod(n);
			visit(n.sl.get(i));
		}

		// Type checking retorno de un método
		n.e.setCurrentClass(n.getCurrentClass());
		n.e.setCurrentMethod(n);
		n.e.setPreNode(n);
		visit(n.e);

		// Return type
		Type typeExpr = n.e.getCurrentType();

		// Verificamos que sean iguales
		if (!n.t.getValue().equals(typeExpr.getValue())) {
			addErrorMsg(n.getLine(), ": La expresión de retorno no es del tipo especificado como retorno del método.");
		}

	}

	@Override
	public void visit(Param n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IntArrayType n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BooleanType n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IntType n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ClassType n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Block n) {
		// Para el Statement del MainClass
		for (int i = 0; i < n.sl.size(); i++) {
			Statement nextSt = n.sl.get(i);
			nextSt.setCurrentClass(n.getCurrentClass());
			nextSt.setPreNode(n);
			nextSt.setCurrentMethod(n.getCurrentMethod());
			visit(nextSt);
		}
	}

	@Override
	public void visit(If n) {
		// Para el Statement del MainClass
		Node currClass = n.getCurrentClass();
		n.e.setCurrentClass(currClass);
		n.e.setPreNode(n);
		n.e.setCurrentMethod(n.getCurrentMethod());
		visit(n.e);

		n.s1.setCurrentClass(currClass);
		n.s1.setPreNode(n);
		n.s1.setCurrentMethod(n.getCurrentMethod());
		visit(n.s1);

		n.s2.setCurrentClass(currClass);
		n.s2.setPreNode(n);
		n.s2.setCurrentMethod(n.getCurrentMethod());
		visit(n.s2);

	}

	@Override
	public void visit(While n) {
		// Para el Statement del MainClass
		Node currClass = n.getCurrentClass();
		n.e.setCurrentClass(currClass);
		n.e.setPreNode(n);
		n.e.setCurrentMethod(n.getCurrentMethod());
		visit(n.e);

		n.s.setCurrentClass(currClass);
		n.s.setPreNode(n);
		n.s.setCurrentMethod(n.getCurrentMethod());
		visit(n.s);
	}

	@Override
	public void visit(Print n) {
		// Para el Statement del MainClass
		Node currClass = n.getCurrentClass();
		n.e.setCurrentClass(currClass);
		n.e.setPreNode(n);
		n.e.setCurrentMethod(n.getCurrentMethod());
		visit(n.e);

	}

	@Override
	public void visit(Assign n) {
		// Para el Statement del MainClass
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = n.getCurrentMethod();

		// Asing variables type checking

		// Obtenemos el tipo de dato del identificador
		Type typeId = getTypeOfId(n.i, currClass, currMethod);
		if (typeId == null) {
			addErrorMsg(n.getLine(), ": El tipo de dato del identificador no existe.");
			return;
		}

		// Obtener el tipo de dato de la expresion

		n.e.setCurrentClass(currClass);
		n.e.setCurrentMethod(currMethod);
		n.e.setPreNode(n);
		visit(n.e);

		Type typeExpr = n.e.getCurrentType();
		if (typeExpr == null) {
			addErrorMsg(n.getLine(), ": El tipo de dato de la expresion no existe.");
			return;
		}

		// Comparamos los tipo de datos del identificador y la expresion
		if (!typeId.getValue().equals(typeExpr.getValue())) {
			addErrorMsg(n.getLine(), ": El tipo de dato del identificador no coincide con el de la expresión.");
			return;
		}
	}

	@Override
	public void visit(ArrayAssign n) {
		// Para el Statement del MainClass
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = n.getCurrentMethod();

		// Obtenemos el type del identificador
		Type typeId = getTypeOfId(n.i, currClass, currMethod);
		if (typeId == null) {
			addErrorMsg(n.getLine(), ": El tipo de dato del identificador no existe.");
			return;
		}

		if (typeId.getValue() != "IntArray") {
			addErrorMsg(n.getLine(), ": El tipo de dato del identificador debe ser Int Array.");
			return;
		}

		// Obtener el tipo de dato de la expresion
		n.e1.setCurrentClass(currClass);
		n.e1.setPreNode(n);
		n.e1.setCurrentMethod(currMethod);
		visit(n.e1);

		// Obtener el tipo de dato de la expresion 2
		n.e2.setCurrentClass(currClass);
		n.e2.setPreNode(n);
		n.e2.setCurrentMethod(currMethod);
		visit(n.e2);

		Type typeExpr1 = n.e1.getCurrentType();
		Type typeExpr2 = n.e2.getCurrentType();

		// Deben existir los tipos de datos de las expresiones
		if (typeExpr2 == null || typeExpr1 == null) {
			addErrorMsg(n.getLine(), ": El tipo de dato de la expresion no existe.");
			return;
		}

		// Los tipos de datos de las dos expresiones deben ser Int
		if (typeExpr1.getValue() != "Int") {
			addErrorMsg(n.getLine(), ": El tipo de dato de la expresion 1 debe ser Int.");
		}

		if (typeExpr2.getValue() != "Int") {
			addErrorMsg(n.getLine(), ": El tipo de dato de la expresion 2 debe ser Int.");
			return;
		}
	}

	@Override
	public void visit(And n) {
		// Controlamos el tipo de dato de las expresiones del lessthan
		operationsControl(n, n.e1, n.e2, "Boolean");
	}

	@Override
	public void visit(LessThan n) {
		// Controlamos el tipo de dato de las expresiones del lessthan
		operationsControl(n, n.e1, n.e2, "Int");
	}

	@Override
	public void visit(Plus n) {
		// Controlamos el tipo de dato de las expresiones del plus
		operationsControl(n, n.e1, n.e2, "Int");
	}

	@Override
	public void visit(Minus n) {
		// Controlamos el tipo de dato de las expresiones del minus
		operationsControl(n, n.e1, n.e2, "Int");
	}

	@Override
	public void visit(Mult n) {
		// Controlamos el tipo de dato de las expresiones del mult
		operationsControl(n, n.e1, n.e2, "Int");
	}

	@Override
	public void visit(ArrayLookup n) {
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = (MethodDecl) n.getCurrentMethod();

		n.e1.setCurrentClass(currClass);
		n.e1.setPreNode(n);
		n.e1.setCurrentMethod(currMethod);
		visit(n.e1);

		n.e2.setCurrentClass(currClass);
		n.e2.setPreNode(n);
		n.e2.setCurrentMethod(currMethod);
		visit(n.e2);

		// Verificamos que la expresión 1 sea de tipo int array
		Type typeExpr = n.e1.getCurrentType();
		if (!(typeExpr.getValue() == "IntArray")) {
			addErrorMsg(n.getLine(), ": La expresión 1 debe ser de tipo Int Array.");
			n.setCurrentType(null);
			return;
		}

		// Verificamos que la expresion 2 sea de tipo int
		Type typeExpr2 = n.e2.getCurrentType();
		if (!(typeExpr2.getValue() == "Int")) {
			addErrorMsg(n.getLine(), ": La expresión 2 debe ser de tipo Int.");
			n.setCurrentType(null);
			return;
		}

		IntType typeResult = new IntType(n.getLine());
		typeResult.setValue("Int");
		n.setCurrentType(typeResult);

	}

	@Override
	public void visit(ArrayLength n) {
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = (MethodDecl) n.getCurrentMethod();

		n.e.setCurrentClass(currClass);
		n.e.setCurrentMethod(currMethod);
		n.e.setPreNode(n);
		visit(n.e);

		// Verificamos que la expresión sea de tipo int array
		Type typeExpr = n.e.getCurrentType();
		if (!(typeExpr instanceof IntArrayType)) {
			addErrorMsg(n.getLine(), ": La expresion debe ser de tipo Int Array.");
			n.setCurrentType(null);
			return;
		}
		IntType typeResult = new IntType(n.getLine());
		typeResult.setValue("Int");
		n.setCurrentType(typeResult);
	}

	@Override
	public void visit(Call n) {
		// Realizamos los controles debidos para una llamada de metodo
		callControl(n);
	}

	@Override
	public void visit(IntegerLiteral n) {
		//Retornamos el tipo de dato Int
		IntType typeExpr = new IntType(n.getLine());
		typeExpr.setValue("Int");
		n.setCurrentType(typeExpr);
	}

	@Override
	public void visit(True n) {
		//Retornamos el tipo de dato Boolean
		BooleanType typeExpr = new BooleanType(n.getLine());
		typeExpr.setValue("Boolean");
		n.setCurrentType(typeExpr);
	}

	@Override
	public void visit(False n) {
		//Retornamos el tipo de dato Boolean
		BooleanType typeExpr = new BooleanType(n.getLine());
		typeExpr.setValue("Boolean");
		n.setCurrentType(typeExpr);
	}

	@Override
	public void visit(IdentifierExpr n) {
		//Para retornar el tipo de dato del identificador
		Node currClass = n.getCurrentClass();
		Node preNode = n.getPreNode();
		MethodDecl currMethod = (MethodDecl) n.getCurrentMethod();

		Identifier id = new Identifier(n.s, n.getLine());
		id.setCurrentClass(n.getCurrentClass());
		id.setCurrentMethod(currMethod);
		id.setPreNode(preNode);

		Type typeExpr = getTypeOfId(id, currClass, currMethod);
		n.setCurrentType(typeExpr);
	}

	@Override
	public void visit(This n) {
		//Para retornar el tipo de dato de la clase actual (la clase misma)
		Node currClass = n.getCurrentClass();
		
		Type typeExpr = getTypeOfClass(currClass);
		n.setCurrentType(typeExpr);
	}

	@Override
	public void visit(NewArray n) {
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = n.getCurrentMethod();

		n.e.setCurrentClass(currClass);
		n.e.setPreNode(n);
		n.e.setCurrentMethod(currMethod);
		visit(n.e);

		// Verificamos que la expresión 1 sea de tipo int array
		Type typeExpr = n.e.getCurrentType();
		if (!(typeExpr.getValue() == "Int")) {
			addErrorMsg(n.getLine(), ": La expresión debe ser de tipo Int.");
			n.setCurrentType(null);
			return;
		}

		IntArrayType typeResult = new IntArrayType(n.getLine());
		typeResult.setValue("IntArray");
		n.setCurrentType(typeResult);
	}

	@Override
	public void visit(NewObject n) {
		//Para retornar el tipo de dato del identificador del llamada
		Type typeExpr = getTypeOfClass(n.i);
		n.setCurrentType(typeExpr);
	}

	@Override
	public void visit(Not n) {
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = (MethodDecl) n.getCurrentMethod();

		n.e.setCurrentMethod(currMethod);
		n.e.setCurrentClass(currClass);
		n.e.setPreNode(n);
		visit(n.e);

		n.setCurrentType(n.e.getCurrentType());
		
	}

	@Override
	public void visit(Identifier n) {
		// Retorna el tipo de dato del identificador
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = (MethodDecl) n.getCurrentMethod();

		Type typeExpr = getTypeOfId(n, currClass, currMethod);
		n.setCurrentType(typeExpr);
	}

	@Override
	public void visit(ExprParen n) {
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = (MethodDecl) n.getCurrentMethod();

		n.e.setCurrentClass(currClass);
		n.e.setCurrentMethod(currMethod);
		n.e.setPreNode(n);
		visit(n.e);

		n.setCurrentType(n.e.getCurrentType());
	}

	/******************************************
	 * Metodos Auxiliares
	 ******************************************/

	// Se agrega los errores de las variables no utilizadas
	public void noUsedVars() {
		for (String key : symbolTable.keySet()) {
			if (symbolTable.get(key) instanceof VarSymbol) {
				VarSymbol vs = (VarSymbol) symbolTable.get(key);

				// En el caso de que no se utilizo la variable
				if (!vs.getUsed()) {
					addWarningMsg(vs.getLine(), ": La variable \"" + vs.getId().toString()
							+ "\" nunca es utilizada después de su declaración.");
				}
			}
		}
	}

	// Se realiaza la comprobacion de tipo de dato de retorno de una llamada
	private void callControl(Call n) {
		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = (MethodDecl) n.getCurrentMethod();

		// Obtenemos el tipo de dato de la Expresion 1

		n.e.setCurrentClass(currClass);
		n.e.setCurrentMethod(currMethod);
		n.e.setPreNode(n);
		visit(n.e);

		if (n.e.getCurrentType() == null) {
			n.setCurrentType(null);
			return;
		}
		String className = n.e.getCurrentType().getValue();

		// Obtenemos los tipos de datos de los parametros dados
		List<Type> typesParams = new ArrayList<Type>();
		for (int i = 0; i < n.el.size(); i++) {
			n.el.get(i).setCurrentClass(currClass);
			n.el.get(i).setCurrentMethod(currMethod);
			n.el.get(i).setPreNode(n);
			visit(n.el.get(i));
			// Sacamos luego
			if (n.el.get(i).getCurrentType() != null)
				typesParams.add(n.el.get(i).getCurrentType());
		}

		// Obtenemos el tipo de dato que retorna el metodo

		// En caso de que no exista el metodo
		if (!existMethod(className, n.i.toString())) {
			addErrorMsg(n.getLine(),
					": El método \"" + n.i.toString() + "\" no fue declarado para la clase \"" + className + "\".");
			n.setCurrentType(null);
			return;
		}

		Type tyMethod = getMethodReturnType(className, n.i.toString(), typesParams);

		if (tyMethod == null) {
			addErrorMsg(n.getLine(), ": Los parámetros proporcionados a la llamada del método \"" + n.i.toString()
					+ "\" no coinciden con ninguna declaración existente del método.");
		}

		// Retornamos el tipo de dato
		n.setCurrentType(tyMethod);
	}

	// Controlamos que el tipo de dato sea el correcto para las operaciones (+, -,
	// *, &&, LessThan)
	private void operationsControl(Node n, Expr e1, Expr e2, String type) {

		Node currClass = n.getCurrentClass();
		MethodDecl currMethod = (MethodDecl) n.getCurrentMethod();

		e1.setCurrentClass(currClass);
		e1.setCurrentMethod(currMethod);
		e1.setPreNode(n);
		visit(e1);

		e2.setCurrentClass(currClass);
		e2.setCurrentMethod(currMethod);
		e2.setPreNode(n);
		visit(e2);

		Type typeExpr1 = e1.getCurrentType();
		Type typeExpr2 = e2.getCurrentType();

		// Comprobacion de que existan los tipos de datos
		if (typeExpr1 == null || typeExpr2 == null) {
			n.setCurrentType(null);
			return;
		}

		// Comprobacion de que los tipos de datos sean del especificado
		if (typeExpr1.getValue() != type || typeExpr2.getValue() != type) {
			addErrorMsg(n.getLine(), ": Los tipos de datos de la expresión debe ser de tipo \""+type+"\"");
			n.setCurrentType(null);
			return;
		}

		// Comprobacion de que los tipos de datos coincidadn
		if (!typeExpr1.getValue().equals(typeExpr2.getValue())) {
			addErrorMsg(n.getLine(), ": Los tipos de datos de las expresiones no coinciden.");
			n.setCurrentType(null);
		} else {
			n.setCurrentType(typeExpr1);
		}
	}

	private void addErrorMsg(int line, String msg) {
		String error = "Error en la linea " + line + msg;
		errors.add(error);
	}

	private void addWarningMsg(int line, String msg) {
		String warning = "Warning en la linea " + line + msg;
		errors.add(warning);
	}

	// Retorna el tipo de dato del id
	private Type getTypeOfId(Identifier varId, Node clase, MethodDecl met) {
		// Retorna el tipo de dato de las declaraciones de variables del metodo
		Type typeOfMethod = getVarTypeOfMethod(clase, met, varId);
		if (typeOfMethod != null)
			return typeOfMethod;

		// Retorna el tipo de dato de los parametros del metodo
		Type typeOfParamMethod = getTypeOfVarInMethodParams(clase, met, varId);

		if (typeOfParamMethod != null) {
			return typeOfParamMethod;
		}

		// Retorna el tipo de dato de las declaraciones de variables de la clase
		Type typeOfClass = getVarTypeOfClass(clase, varId);
		if (typeOfClass != null)
			return typeOfClass;

		// Retorna el tipo de dato de las declaraciones de variables de la clase padre o
		// mas

		Type typeOfDadClass = getVarTypeOfDadClass(clase, varId);
		if (typeOfDadClass != null) {
			return typeOfDadClass;
		}

		return null;
	}

	/******************************************
	 * Metodos para manipular los metodos
	 ******************************************/

	private Type getMethodReturnType(String className, String methodName, List<Type> params) {

		// Obtenemos el tipo de retorno de un metodo de la clase actual
		Type typeClass = getMethodReturnTypeOfAClass(className, methodName, params);
		if (typeClass != null) {
			return typeClass;
		}
		// Obtenemos el tipo de retorno de un metodo de un clase ancestro
		Type typeDadClass = getMethodReturnTypeOfDadClass(className, methodName, params);
		if (typeDadClass != null) {
			return typeDadClass;
		}
		return null;
	}

	// Se obtiene el tipo de dato de retorno de un metodo
	private Type getMethodReturnTypeOfAClass(String className, String methodName, List<Type> params) {
		for (String key : symbolTable.keySet()) {
			SymbolItem si = symbolTable.get(key);
			if (si instanceof MetSymbol) {
				MetSymbol met = (MetSymbol) si;

				String classNameInTable = getClassName(met.getClase());
				// Comparamos que coincida las clases
				if (classNameInTable.equals(className)) {

					// Comparamos que coincida el nombre del metodo y los parametros
					if (met.getId().toString().equals(methodName) && isEqualParams(met.getParameters(), params))
						return met.type;
				}
			}
		}
		return null;
	}

	// Se obtiene el tipo de dato de retorno de un metodo de una clase ancestro
	private Type getMethodReturnTypeOfDadClass(String className, String methodName, List<Type> params) {

		// Verificamos que la clase tenga una clase padre
		ClassDeclExtends cde = getClassExtByName(className);
		if (cde != null) {

			// Verificamos que no extiende de si mismo
			if (!cde.i.toString().equals(cde.j.toString())) {

				// Verificamos que exista la clase padre
				if (existClass(cde.j.toString())) {

					// Verificamos si existe la variable en la clase padre
					Type typeDad = getMethodReturnTypeOfAClass(cde.j.toString(), methodName, params);

					// Encontro el method return type
					if (typeDad != null) {
						return typeDad;
					}

					// No encontro
					// Verificamos si existe clase abuelo
					return getMethodReturnTypeOfDadClass(cde.j.toString(), methodName, params);
				}
			}
		}
		return null;
	}

	// Se obtiene el tipo de dato de una variable de un metodo
	private Type getVarTypeOfMethod(Node clase, MethodDecl met, Identifier varId) {
		// Control de nulls
		if (met == null || clase == null || varId == null) {
			return null;
		}
		// System.out.println("Symbol Table size: " + symbolTable.size());
		for (String key : symbolTable.keySet()) {
			SymbolItem si = symbolTable.get(key);

			if (si instanceof VarSymbol) {
				VarSymbol siVar = (VarSymbol) si;

				// Comparamos que concidan la clase
				String classNameInTable = getClassName(siVar.getClase());
				String classNameVar = getClassName(clase);
				
				if (classNameInTable.equals(classNameVar)) {

					// Comparamos que coincida el metodo
					if (siVar.getMetodo() != null) {
						String meNameInTable = siVar.getMetodo().i.toString();
						String meNameVar = met.i.toString();

						if (meNameInTable.equals(meNameVar) && isEqualParams(siVar.getMetodo().fl, met.fl)) {

							// Comparamos que concida el identificador
							String varNameInTable = siVar.getId().toString();
							String varNameVar = varId.toString();

							if (varNameInTable.equals(varNameVar)) {
								siVar.setUsed(true);
								return siVar.type;
							}
						}
					}
				}
			}
		}
		return null;

	}

	// Para obtener el tipo de dato de una variable que se paso como parametro de un
	// metodo
	private Type getTypeOfVarInMethodParams(Node clase, MethodDecl met, Identifier varId) {
		// Control de nulls
		if (met == null || clase == null || varId == null) {
			return null;
		}

		// Iteramos sobre todo el symbol table
		for (String key : symbolTable.keySet()) {
			SymbolItem si = symbolTable.get(key);

			// Si el symbol es una variable
			if (si instanceof MetSymbol) {
				MetSymbol siVar = (MetSymbol) si;

				// Comparamos que concidan la clase
				String classNameInTable = getClassName(siVar.getClase());
				String classNameVar = getClassName(clase);

				if (classNameInTable.equals(classNameVar)) {

					// Comparamos que coincida el identificador del metodo
					if (siVar.getId().toString().equals(met.i.toString())) {

						// Buscamos el tipo del identificador
						for (int i = 0; i < siVar.getParameters().size(); i++) {

							// Comparamos los identificadores
							Param pa = siVar.getParameters().get(i);
							if (pa.i.toString().equals(varId.toString())) {
								return pa.t;
							}
						}
					}
				}

			}
		}
		return null;
	}

	// Comparamos la igualdad de los parametros
	private Boolean isEqualParams(ParamList pl1, ParamList pl2) {
		// Comparamos la cantidad de parametros
		if (pl1.size() == pl2.size()) {

			for (int i = 0; i < pl1.size(); i++) {

				// Verificamos si el type es un classtype
				if (pl2.get(i).t instanceof ClassType) {
					if (!isEqualParamClassType(pl1.get(i).t.getValue(), pl2.get(i).t.getValue())) {
						return false;
					}
					// Comparamos los types
				} else if (!pl1.get(i).t.getValue().equals(pl2.get(i).t.getValue())) {
					return false;
				}

			}
			return true;
		}
		return false;
	}

	private Boolean isEqualParams(ParamList pl1, List<Type> pl2) {

		// Comparamos la cantidad de parametros
		if (pl1.size() == pl2.size()) {

			// Comparamos los types
			for (int i = 0; i < pl1.size(); i++) {

				// Verificamos si el type es un classtype
				if (pl2.get(i) instanceof ClassType) {
					if (isEqualParamClassType(pl1.get(i).t.getValue(), pl2.get(i).getValue())) {
						return false;
					}
					// Comparamos los types
				} else if (!pl1.get(i).t.getValue().equals(pl2.get(i).getValue())) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	// Metodo que busca igualar tipos de datos entre clases iguales o entre las
	// clases hijo y padre
	private Boolean isEqualParamClassType(String t1, String t2) {

		// En el caso de que sean iguales
		if (t1 == t2) {
			return true;
		}

		// Obtengo la clase padre del type 2
		ClassDeclExtends cde = getClassExtByName(t2);
		if (cde == null) {
			return false;
		}

		return isEqualParamClassType(t1, cde.j.toString());

	}

	// Retorna true si encuenta el metodo de la clase con los parametros
	private Boolean existMethod(String className, String methodName) {

		// Buscamos en la clase actual
		for (String key : symbolTable.keySet()) {
			// Obtenemos un symbol
			SymbolItem sym = symbolTable.get(key);
			if (sym instanceof MetSymbol) {
				MetSymbol metSym = (MetSymbol) sym;

				// Obtenemos el nombre de la clase del symbol metodo guardado
				String classNameInTable = getClassName(metSym.getClase());
				// Comparamos el className y el methodName
				if ((classNameInTable.equals(className)) && (methodName.equals(metSym.id.toString()))) {
					return true;
				}
			}
		}

		// Buscamos ahora en la clase padre
		ClassDeclExtends clase = getClassExtByName(className);
		if (clase != null) {
			return existMethod(clase.j.toString(), methodName);
		}

		return false;
	}

	/******************************************
	 * Metodos para manipular los nodos classes
	 ******************************************/

	// Metodo para obtener una ClassDeclExtends por medio un className
	private ClassDeclExtends getClassExtByName(String className) {
		for (String key : symbolTable.keySet()) {
			if (symbolTable.get(key) instanceof ClassSymbol) {
				ClassSymbol sy = (ClassSymbol) symbolTable.get(key);
				// Comparamos si son iguales los identificadores de la clase
				if (sy.getId().toString().equals(className)) {

					// Verificamoes que el nodo sea una instancia de una clase extends
					if (sy.getClase() instanceof ClassDeclExtends)
						return (ClassDeclExtends) sy.getClase();
				}
			}
		}
		return null;
	}

	// Funcion recursiva que busca el tipo de datos entre los ancestros de la clase
	// actual
	private Type getVarTypeOfDadClass(Node clase, Identifier varId) {

		// Verificamos que extiende de otra clase
		if (clase instanceof ClassDeclExtends) {
			ClassDeclExtends cde = (ClassDeclExtends) clase;

			// Verificamos que no extiende de si mismo
			if (!cde.i.toString().equals(cde.j.toString())) {

				// Verificamos que exista la clase padre
				if (existClass(cde.j.toString())) {

					// Verificamos si existe la variable en la clase padre
					Type varType = getVarTypeOfClass(cde.j, varId);

					// Encontro el type
					if (varType != null)
						return varType;

					// No encontro
					// Verificamos si existe clase abuelo
					ClassDeclExtends classAbuelo = getClassExtOfId(cde.j);
					if (classAbuelo != null) {
						return getVarTypeOfDadClass(classAbuelo, varId);
					}
				}
			}
		}

		return null;
	}

	// Se obtiene el tipo de dato de una clase
	private Type getTypeOfClass(Node clase) {

		// Obtenemos el identificador de la clase
		Identifier classId = null;
		if (clase instanceof ClassDeclSimple) {
			ClassDeclSimple cds = (ClassDeclSimple) clase;
			classId = cds.i;
		} else if (clase instanceof ClassDeclExtends) {
			ClassDeclExtends cde = (ClassDeclExtends) clase;
			classId = cde.i;
		}

		// Buscamos el type en la lista de tipos de datos
		for (String key : types.keySet()) {
			Type ty = types.get(key);
			if (ty instanceof ClassType) {
				ClassType ct = (ClassType) ty;

				// Si el identificador del type es igual al identificador de la clase dada
				if (ct.className.equals(classId.toString())) {
					return ct;
				}
			}
		}

		return null;
	}

	// Se obtiene el className de un nodo que puede ser ClassDeclSimple o
	// ClassDeclExtends
	private String getClassName(Node clase) {
		String className = "";
		if (clase instanceof ClassDeclSimple) {
			ClassDeclSimple cds = (ClassDeclSimple) clase;
			className = cds.i.toString();
			return className;
		} else if (clase instanceof ClassDeclExtends) {
			ClassDeclExtends cde = (ClassDeclExtends) clase;
			className = cde.i.toString();
			return className;
		} else if (clase instanceof Identifier) {
			Identifier id = (Identifier) clase;
			className = id.toString();
			return className;
		}

		return null;
	}

	// Obtenemos el tipo de dato de una variable de clase
	private Type getVarTypeOfClass(Node clase, Identifier idVar) {

		for (String key : symbolTable.keySet()) {
			SymbolItem si = symbolTable.get(key);
			if (si instanceof VarSymbol) {
				VarSymbol siVar = (VarSymbol) si;

				// Comparamos que coincida la clase
				String classNameInTable = getClassName(siVar.getClase());
				String classNameVar = getClassName(clase);
				if (classNameInTable.equals(classNameVar)) {

					// Verificamos que la variable no sea perteneciente a un metodo
					if (siVar.getMetodo() == null) {

						// Verificamos que el identificador coincida
						String varNameInTable = siVar.getId().toString();
						String varNameVar = idVar.toString();
						if (varNameInTable.equals(varNameVar)) {
							siVar.setUsed(true);
							return siVar.type;
						}
					}
				}
			}
		}
		return null;
	}

	// Busca en la lista de tipos de datos si existe la clase con el identificador
	// dado
	private ClassType getTypeOfClass(Identifier classId) {
		for (String key : types.keySet()) {
			Type ty = types.get(key);
			if (ty instanceof ClassType) {
				ClassType tyC = (ClassType) ty;
				if (tyC.getValue().equals(classId.toString())) {
					return tyC;
				}
			}
		}

		return null;
	}

	// Retorna true si encuentra la clase en los tipos de datos
	private Boolean existClass(String className) {
		for (String key : types.keySet()) {
			Type ty = types.get(key);
			if (ty instanceof ClassType) {
				ClassType cty = (ClassType) ty;
				if (cty.className.equals(className)) {
					return true;
				}
			} else {
				if (ty.getValue() == className) {
					return true;
				}
			}
		}
		return false;
	}

	// Retorna un ClassDeclExtends si el classId pertenece a un classDeclExtends
	private ClassDeclExtends getClassExtOfId(Identifier classId) {

		for (String key : symbolTable.keySet()) {
			if (symbolTable.get(key) instanceof ClassSymbol) {
				ClassSymbol sy = (ClassSymbol) symbolTable.get(key);
				// Comparamos si son iguales los identificadores de la clase
				if (sy.getId().toString().equals(classId.toString())) {

					// Verificamoes que el nodo sea una instancia de una clase extends
					if (sy.getClase() instanceof ClassDeclExtends)
						return (ClassDeclExtends) sy.getClase();
				}
			}
		}
		return null;
	}
}
