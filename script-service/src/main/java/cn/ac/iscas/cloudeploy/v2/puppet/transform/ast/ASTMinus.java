package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTMinus extends ASTBlockExpression{
	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
}
