package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTHashOrArrayAccess extends ASTBase{
	private Object variable;
	private Object key;
	public Object getVariable() {
		return variable;
	}
	public void setVariable(Object variable) {
		this.variable = variable;
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
}
