package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.List;

public class ASTConcat extends ASTBase{
	private List<Object> value;

	public List<Object> getValue() {
		return value;
	}

	public void setValue(List<Object> value) {
		this.value = value;
	}
	
}
