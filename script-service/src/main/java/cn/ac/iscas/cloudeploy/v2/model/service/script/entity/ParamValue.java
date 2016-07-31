package cn.ac.iscas.cloudeploy.v2.model.service.script.entity;

import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;

public class ParamValue{
	private Object value;
	private ParamType type;
	public ParamValue(Object value2, ParamType type) {
		this.value=value2;
		this.type=type;
	}
	public ParamValue(){}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public ParamType getType() {
		return type;
	}
	public void setType(ParamType type) {
		this.type = type;
	}
	public String transformToScript() {
		return type.transform(value) + ",";
	}
	public String store() {
		return type.transform(value);
	}
}
