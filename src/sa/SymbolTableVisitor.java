package sa;

import java.util.ArrayList;
import java.util.HashMap;

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
import ast.Not;
import ast.Param;
import ast.Plus;
import ast.Print;
import ast.This;
import ast.True;
import ast.Type;
import ast.VarDecl;
import ast.While;
import ast.visitor.Visitor;

//Para obtener los simbolos ya sea los metodos las variables y las clases
public class SymbolTableVisitor implements Visitor {

	private HashMap<String, Type> dataTypes;
	private ArrayList<String> errors;
	// Metodos = met 0 , nombre 1, tipo de retorno 2, clase 3, linea 4,
	// Variables = var 0 , nombre 1, tipo de dato 2, clase si es una variable de
	// clase 3 , metodo si es una variable de metodo 4, linea 5
	// Parametros = par 0 , nombre 1, tipo de dato 2, clase name 3, metodo name 4,
	// linea 5.
	private HashMap<String, SymbolItem> symbolTable;

	public SymbolTableVisitor(HashMap<String, Type> _types, ArrayList<String> _errors,
			HashMap<String, SymbolItem> _symbolTable) {
		this.dataTypes = _types;
		this.errors = _errors;
		this.symbolTable = _symbolTable;
	}

	@Override
	public void visit(Goal n) {
		for (int i = 0; i < n.cl.size(); i++) {
			visit(n.cl.get(i));
		}

	}

	@Override
	public void visit(MainClass n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ClassDeclSimple n) {
	
		// Para obtener los metodos
		for (int i = 0; i < n.ml.size(); i++) {
			MethodDecl met = n.ml.get(i);
			met.setPreNode(n);
			visit(met);
		}

		// Para obtener las variables
		for (int i = 0; i < n.vl.size(); i++) {
			VarDecl var = n.vl.get(i);
			var.setPreNode(n);
			visit(var);
		}

	}

	@Override
	public void visit(ClassDeclExtends n) {
		// Para obtener los metodos
		for (int i = 0; i < n.ml.size(); i++) {
			MethodDecl met = n.ml.get(i);
			met.setPreNode(n);
			visit(met);
		}

		// Para obtener las variables
		for (int i = 0; i < n.vl.size(); i++) {
			VarDecl var = n.vl.get(i);
			var.setPreNode(n);
			visit(var);
		}

	}

	@Override
	public void visit(VarDecl n) {
		VarSymbol varSym = new VarSymbol(n.i, n.t, n.getLine());

		// Controlamos que el tipo de la variable sea correcto
		if (!isTypeAccept(varSym.type)) {
			addErrorMsg(varSym.getLine(), ": El tipo de dato no es valido.");
			return;
		}
		// En caso de que la variable sea del ambito de un metodo
		if (n.getPreNode() instanceof MethodDecl) {
			// Se obtiene el metodo de la variable
			MethodDecl preNodeMethod = (MethodDecl) n.getPreNode();
			varSym.setMetodo(preNodeMethod);

			// Se obtiene la clase de la variable
			String className = "";
			if (preNodeMethod.getPreNode() instanceof ClassDeclSimple) {
				ClassDeclSimple preNodeClass = (ClassDeclSimple) preNodeMethod.getPreNode();
				varSym.setClase(preNodeClass);
				className = preNodeClass.i.toString();
			} else if (preNodeMethod.getPreNode() instanceof ClassDeclExtends) {
				ClassDeclExtends preNodeClass = (ClassDeclExtends) preNodeMethod.getPreNode();
				varSym.setClase(preNodeClass);
				className = preNodeClass.i.toString();
			}
			// Se verifica que no sea una variable repetida en el metodo de una determinada
			// clase
			String keyVarClass = varSym.getId().toString() + className + varSym.getMetodo().i.toString();

			// Verificación de metodo diferenciando la posicion de las variables
			for (int i = 0; i < varSym.getMetodo().fl.size(); i++) {
				keyVarClass += varSym.getMetodo().fl.get(i).i.toString();
			}

			// Si ya existe en la clase el mismo identificador de la variable
			// No metemos la variable repetida
			if (symbolTable.containsKey(keyVarClass)) {
				String error = "Error en la linea " + varSym.getLine()
						+ ": Identificador variable de metodo repetido para \"" + varSym.getId().toString()+"\"" ;
				errors.add(error);

			} else if (isInParams(varSym, preNodeMethod)) {
				addErrorMsg(varSym.getLine(),
						": El identificador de la variable \"" + varSym.id.toString() + "\" ya fue declarada como parametro del metodo.");
			} else {
				symbolTable.put(keyVarClass, varSym);
			}

			// En caso de que la variable sea del ambito de una clase
		} else {

			String className = "";
			if (n.getPreNode() instanceof ClassDeclSimple) {
				ClassDeclSimple preNode = (ClassDeclSimple) n.getPreNode();
				varSym.setClase(preNode);
				className = preNode.i.toString();
			} else if (n.getPreNode() instanceof ClassDeclExtends) {
				ClassDeclExtends preNode = (ClassDeclExtends) n.getPreNode();
				varSym.setClase(preNode);
				className = preNode.i.toString();
			}

			// Verficar que no salte error de varible repetida por clase
			String keyVarClass = varSym.getId().toString() + className;

			// Si ya existe en la clase el mismo identificador de la variable
			// No metemos la variable repetida
			if (symbolTable.containsKey(keyVarClass)) {
				String error = "Error en la linea " + varSym.getLine()
						+ ": Identificador variable de clase repetido para \"" + varSym.getId().toString()+"\"";
				errors.add(error);
			} else {
				symbolTable.put(keyVarClass, varSym);
			}
		}

	}

	@Override
	public void visit(MethodDecl n) {
		MetSymbol metSym = new MetSymbol(n.i, n.t, n.getLine(), n.fl);

		// Se obtiene la clase del metodo
		String className = "";
		if (n.getPreNode() instanceof ClassDeclSimple) {
			ClassDeclSimple preNode = (ClassDeclSimple) n.getPreNode();
			metSym.setClase(preNode);
			className = preNode.i.toString();
		} else if (n.getPreNode() instanceof ClassDeclExtends) {
			ClassDeclExtends preNode = (ClassDeclExtends) n.getPreNode();
			metSym.setClase(preNode);
			className = preNode.i.toString();
		}

		// Si el metodo ya existe
		// Verifica en el caso de que acepte metodos con nombres repetidos pero la
		// cantidad y el orden de los parametros sea diferente

		String keyMetClass = metSym.id.toString() + className;
		for (int j = 0; j < metSym.getParameters().size(); j++) {
			keyMetClass += metSym.getParameters().get(j).t.getValue();
		}

		if (symbolTable.containsKey(keyMetClass)) {
			String error = "Error en la linea " + metSym.getLine() + ": identificador del metodo repetido para "
					+ metSym.getId().toString();
			errors.add(error);
		} else {
			symbolTable.put(keyMetClass, metSym);
			// Ojo solamente voy a controlar las variables de los metodos que se acepto

			for (int i = 0; i < n.vl.size(); i++) {
				VarDecl var = n.vl.get(i);
				var.setPreNode(n);
				visit(var);
			}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(If n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(While n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Print n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Assign n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ArrayAssign n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(And n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(LessThan n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Plus n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Minus n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Mult n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ArrayLookup n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ArrayLength n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Call n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IntegerLiteral n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(True n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(False n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IdentifierExpr n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(This n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NewArray n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NewObject n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Not n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Identifier n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExprParen n) {
		// TODO Auto-generated method stub

	}

	/* Metodos Auxiliares */
	// Se verifica que el tipo de dato sea valido
	private Boolean isTypeAccept(Type t) {
		if (t instanceof IntType || t instanceof IntArrayType || t instanceof BooleanType) {
			return true;
		} else if (t instanceof ClassType) {
			ClassType ct = (ClassType) t;
			if (dataTypes.containsKey(ct.className)) {
				return true;
			}
		}
		return false;
	}

	private void addErrorMsg(int line, String msg) {
		String error = "Error en la linea " + line + msg;
		errors.add(error);
	}

	// Verificamos si el identificador de la variable esta como parametro
	private Boolean isInParams(VarSymbol var, MethodDecl met) {
		for (int i = 0; i < met.fl.size(); i++) {

			// Verificamos el identificador
			if (var.id.toString().equals(met.fl.get(i).i.toString()))
				return true;
		}
		return false;
	}
}
