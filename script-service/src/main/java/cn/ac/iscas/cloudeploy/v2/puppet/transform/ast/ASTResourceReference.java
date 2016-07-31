package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTResourceReference extends ASTBase{
	private String type;
	private ASTASTArray title;
	private List<Object> children;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ASTASTArray getTitle() {
		return title;
	}
	public void setTitle(ASTASTArray title) {
		this.title = title;
	}
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List<Object> children) {
		this.children = children;
	}
}
