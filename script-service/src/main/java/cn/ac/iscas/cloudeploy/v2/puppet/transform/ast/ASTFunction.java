package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTFunction extends ASTBase{
	private String ftype;
	private String doc;
	private String name;
	private ASTASTArray arguments;
	private List<Object> children;
	
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ASTASTArray getArguments() {
		return arguments;
	}
	public void setArguments(ASTASTArray arguments) {
		this.arguments = arguments;
	}
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List<Object> children) {
		this.children = children;
	}
	
}
