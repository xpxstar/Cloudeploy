package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;


public class ASTSelector extends ASTBlockExpression{
	private ASTVariable param;
	private ASTASTArray values;
	
	public ASTVariable getParam() {
		return param;
	}
	public void setParam(ASTVariable param) {
		this.param = param;
	}
	public ASTASTArray getValues() {
		return values;
	}
	public void setValues(ASTASTArray values) {
		this.values = values;
	}
}
