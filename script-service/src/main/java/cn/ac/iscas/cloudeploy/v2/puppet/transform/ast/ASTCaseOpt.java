package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTCaseOpt extends ASTBase{
	private ASTBlockExpression statements;
	private List<Object> children;
	private ASTASTArray value;
	public ASTBlockExpression getStatements() {
		return statements;
	}
	public void setStatements(ASTBlockExpression statements) {
		this.statements = statements;
	}
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List<Object> children) {
		this.children = children;
	}
	public ASTASTArray getValue() {
		return value;
	}
	public void setValue(ASTASTArray value) {
		this.value = value;
	}
	
}
