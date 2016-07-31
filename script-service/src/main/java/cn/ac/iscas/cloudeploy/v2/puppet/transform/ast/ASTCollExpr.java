package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTCollExpr extends ASTBase{
	private Object test1;
	private Object test2;
	private String oper;
	private List<Object> children;
	private String form;
	private String type;
	public Object getTest1() {
		return test1;
	}
	public void setTest1(Object test1) {
		this.test1 = test1;
	}
	public Object getTest2() {
		return test2;
	}
	public void setTest2(Object test2) {
		this.test2 = test2;
	}
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List<Object> children) {
		this.children = children;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
