package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.util.Map;

public class ASTASTHash extends ASTBase{
	private Map<Object, Object> value;

	public Map<Object, Object> getValue() {
		return value;
	}

	public void setValue(Map<Object, Object> value) {
		this.value = value;
	}
	
}
