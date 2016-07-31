package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTResource extends ASTBase{
	private String type;
	private String doc;
	private ASTASTArray instances;
	private List<Object> children;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public ASTASTArray getInstances() {
		return instances;
	}
	public void setInstances(ASTASTArray instances) {
		this.instances = instances;
	}
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List<Object> children) {
		this.children = children;
	}
}
