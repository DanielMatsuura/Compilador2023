package ast;

public class Not extends Expr {
	public Expr e;

	public Not(Expr ae, int ln) {
		super(ln);
		e = ae;
	}

}
