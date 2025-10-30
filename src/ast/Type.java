package ast;

public abstract class Type extends Node {
	
	private String value;
	
	public Type(int line) {
		super(line);
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
