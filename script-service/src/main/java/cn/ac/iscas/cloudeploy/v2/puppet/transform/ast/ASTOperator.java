package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTOperator extends ASTBase{
	private String operator;
	private Object lval;
	private Object rval;
	private List<Object> children;
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Object getLval() {
		return lval;
	}
	public void setLval(Object lval) {
		this.lval = lval;
	}
	public Object getRval() {
		return rval;
	}
	public void setRval(Object rval) {
		this.rval = rval;
	}
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List<Object> children) {
		this.children = children;
	}
}
