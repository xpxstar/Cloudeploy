package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTResourceOverride extends ASTBlockExpression{
	private boolean checked;
	private String doc;
	private ASTResourceReference object;
	private ASTASTArray parameters;
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public ASTResourceReference getObject() {
		return object;
	}
	public void setObject(ASTResourceReference object) {
		this.object = object;
	}
	public ASTASTArray getParameters() {
		return parameters;
	}
	public void setParameters(ASTASTArray parameters) {
		this.parameters = parameters;
	}
	
}
