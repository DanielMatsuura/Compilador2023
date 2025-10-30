package ast;

public class ExprParen extends Expr{
	public Expr e;
	public ExprParen(Expr expr, int line) {
		super(line);
		this.e = expr;
	}
}
