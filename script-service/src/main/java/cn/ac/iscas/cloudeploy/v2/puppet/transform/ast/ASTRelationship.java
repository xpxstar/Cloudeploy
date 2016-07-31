package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTRelationship extends ASTBase{
	private List<Object> children;
	private Object left;
	private Object right;
	private String arrow;
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List<Object> children) {
		this.children = children;
	}
	public Object getLeft() {
		return left;
	}
	public void setLeft(Object left) {
		this.left = left;
	}
	public Object getRight() {
		return right;
	}
	public void setRight(Object right) {
		this.right = right;
	}
	public String getArrow() {
		return arrow;
	}
	public void setArrow(String arrow) {
		this.arrow = arrow;
	}
	
}
