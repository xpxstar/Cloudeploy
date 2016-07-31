package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTResourceParam extends ASTBlockExpression{
	private Object param;
	private Object value;

	public Object getParam() {
		return param;
	}
	public void setParam(Object param) {
		this.param = param;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
}
