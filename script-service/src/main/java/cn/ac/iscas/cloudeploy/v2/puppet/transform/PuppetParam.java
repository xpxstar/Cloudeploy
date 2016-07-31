package cn.ac.iscas.cloudeploy.v2.puppet.transform;

/**
 * param object of class
 * @author RichardLcc
 *
 */
public class PuppetParam {
	private PuppetClass parent;
	private String paramName;
	private Object value;
	private Object type;
	public PuppetParam(String key, Object value2,PuppetClass parent) {
		paramName=key;
		value=value2;
		this.parent=parent;
	}
	public PuppetParam(){}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public PuppetClass getParent() {
		return parent;
	}
	public void setParent(PuppetClass parent) {
		this.parent = parent;
	}
	public Object getType() {
		return type;
	}
	public void setType(Object type) {
		this.type = type;
	}
	
	public ParamType getTypeString() {
		return ParamType.typeOf(type);
	}
	
}
