import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import ast.ClassDeclExtends;
import ast.ClassDeclSimple;
import ast.Goal;
import ast.Node;
import ast.Type;
import ast.visitor.MiniJavaPrintVisitor;
import ast.visitor.Visitor;
import java_cup.runtime.Symbol;
import sa.DataTypesVisitor;
import sa.SymbolItem;
import sa.SymbolTableChecking;
import sa.SymbolTableVisitor;
import sa.VarSymbol;

public class Main {

	public static void main(String[] args) {
		InputStreamReader isr = new InputStreamReader(System.in);
		Scanner s = new Scanner(isr);
		parser p = new parser(s);
		try {
			Symbol root = p.parse();
			Visitor mj = new MiniJavaPrintVisitor();
			Goal g = (Goal) root.value;
			mj.visit(g);

			// Paso 1 obtener el hashmap de los tipos de datos
			HashMap<String, Type> dataTypes = new HashMap<String, Type>();
			ArrayList<String> errors = new ArrayList<String>();
			HashMap<String, SymbolItem> symbolTable = new HashMap<String, SymbolItem>();
			
			DataTypesVisitor dtVisitor = new DataTypesVisitor(dataTypes, errors,symbolTable);
			dtVisitor.visit(g);

			// Paso 2 generar la tabla de simbolos

			SymbolTableVisitor stVisitor = new SymbolTableVisitor(dataTypes, errors, symbolTable);
			stVisitor.visit(g);

			// Paso 3 comprobacion de todos los casos
			SymbolTableChecking stChecking = new SymbolTableChecking(symbolTable, dataTypes, errors);
			stChecking.visit(g);
			stChecking.noUsedVars();

			System.out.println("Errors...");
			printErrors(errors);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void report_error(String message, Object info) {
		System.err.print(message);
		System.err.flush();
		if (info instanceof Symbol)
			if (((Symbol) info).left != -1)
				System.err.println(" at line " + ((Symbol) info).left + " of input");
			else
				System.err.println("");
		else
			System.err.println("");
	}

	public static void printErrors(ArrayList<String> errors) {
		for (int i = 0; i < errors.size(); i++) {
			System.out.println(errors.get(i));
		}
	}

	public static void printTypes(HashMap<String, Type> dataTypes) {
		for (String ty : dataTypes.keySet()) {
			System.out.println(dataTypes.get(ty).getValue());
		}
	}

	public static void printSymbols(HashMap<String, SymbolItem> symbolTable) {
		System.out.println("Symbol Table size: "+ symbolTable.size());
		for (String key : symbolTable.keySet()) {
			SymbolItem sy = symbolTable.get(key);

			if (sy instanceof VarSymbol) {
				VarSymbol syVs = (VarSymbol) sy;
				if (syVs.getMetodo() != null) {
					System.out.println(syVs.getId() + ", " + syVs.getType().getValue() + ", "
							+ syVs.getMetodo().i.toString() + ", " + getClassName(syVs.getClase()));
				} else {
					System.out.println(
							syVs.getId() + ", " + syVs.getType().getValue() + ", " + getClassName(syVs.getClase()));
				}

			}
		}

	}

	public static String getClassName(Node clase) {
		String className = "";
		if (clase instanceof ClassDeclSimple) {
			ClassDeclSimple cds = (ClassDeclSimple) clase;
			className = cds.i.toString();
			return className;
		} else if (clase instanceof ClassDeclExtends) {
			ClassDeclExtends cde = (ClassDeclExtends) clase;
			className = cde.i.toString();
			return className;
		}

		return null;
	}

}