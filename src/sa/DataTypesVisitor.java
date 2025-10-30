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


//Clase para obtener todos los tipos de datos del Arbol
public class DataTypesVisitor implements Visitor {

	private HashMap<String, Type> dataTypes;
	private ArrayList<String> errors;
	private HashMap<String, SymbolItem> symbolTable;

	public DataTypesVisitor(HashMap<String, Type> _types, ArrayList<String> _errors,
			HashMap<String, SymbolItem> _symbolTable) {
		
		this.dataTypes = _types;
		this.errors = _errors;
		this.symbolTable= _symbolTable;
		
		Type intType = new IntType(0);
		intType.setValue("Int");
		Type booleanType = new BooleanType(0);
		booleanType.setValue("Boolean");
		Type intArrayType = new IntArrayType(0);
		intArrayType.setValue("intArray");

		dataTypes.put("Int", intType);
		dataTypes.put("Boolean", booleanType);
		dataTypes.put("IntArray", intArrayType);
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
		if (dataTypes.containsKey(n.i.toString())) {
			String error = "Error en la linea " + n.getLine() + ": identificador de clase repetido para "
					+ n.i.toString();
			errors.add(error);
		} else {
			ClassType _classType = new ClassType(n.getLine(), n.i.toString());
			_classType.setValue(n.i.toString());
			dataTypes.put(n.i.toString(), _classType);

			ClassSymbol cs = new ClassSymbol(n.i, n.getLine(), n);
			symbolTable.put(n.i.toString(), cs);
		}
	}

	@Override
	public void visit(ClassDeclExtends n) {
		if (dataTypes.containsKey(n.i.toString())) {
			String error = "Error en la linea " + n.getLine() + ": identificador de clase repetido para "
					+ n.i.toString();
			errors.add(error);
		} else {
			ClassType _classType = new ClassType(n.getLine(), n.i.toString());
			_classType.setValue(n.i.toString());
			dataTypes.put(n.i.toString(), _classType);

			ClassSymbol cs = new ClassSymbol(n.i, n.getLine(), n);
			symbolTable.put(n.i.toString(), cs);
		}
	}

	@Override
	public void visit(VarDecl n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MethodDecl n) {
		// TODO Auto-generated method stub

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

}
