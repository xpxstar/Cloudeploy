package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTResourceInstance extends ASTBase{
	private Object title;
	private ASTASTArray parameters;
	private List<Object> children;
	public Object getTitle() {
		return title;
	}
	public void setTitle(Object title) {
		this.title = title;
	}
	public ASTASTArray getParameters() {
		return parameters;
	}
	public void setParameters(ASTASTArray parameters) {
		this.parameters = parameters;
	}
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List<Object> children) {
		this.children = children;
	}
}
