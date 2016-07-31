package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTBoolean extends ASTBase{
    private boolean value;
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
}
