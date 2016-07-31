package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTIfStatement extends ASTBlockExpression{
	private String doc;
	private Object test;
	private ASTBlockExpression statements;
	private ASTElse ifStatementElse;
	
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public Object getTest() {
		return test;
	}
	public void setTest(Object test) {
		this.test = test;
	}
	public ASTBlockExpression getStatements() {
		return statements;
	}
	public void setStatements(ASTBlockExpression statements) {
		this.statements = statements;
	}
	public ASTElse getIfStatementElse() {
		return ifStatementElse;
	}
	public void setIfStatementElse(ASTElse ifStatementElse) {
		this.ifStatementElse = ifStatementElse;
	}
	
}
