package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTElse extends ASTBlockExpression{
	private String doc;
	private ASTBlockExpression statements;
	
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public ASTBlockExpression getStatements() {
		return statements;
	}
	public void setStatements(ASTBlockExpression statements) {
		this.statements = statements;
	}
	
}
