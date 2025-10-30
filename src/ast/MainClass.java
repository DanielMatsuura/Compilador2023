package ast;

public class MainClass extends Node {
	public Identifier i1;
	public Identifier i2;
	public Statement s;

	public MainClass(Identifier ai1, Identifier ai2, Statement as, int ln) {
		super(ln);
		i1 = ai1;
		i2 = ai2;
		s = as;
	}

}
