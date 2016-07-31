package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import lombok.Builder;
import lombok.NoArgsConstructor;
@NoArgsConstructor
public class AttributeElement extends RootElement{
	/**
	 * The required data type for the attribute.
	 */
	private String type;
	/**
	 * An optional key that may provide a value to be used as a default 
	 * if not provided by another means.
	 * This value SHALL be type compatible with the type declared by
	 * the property definition’s type keyname.
	 */
	private String value;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Builder
	public AttributeElement(String description, String name, String type, String value) {
		super(description, name);
		this.type = type;
		this.value = value;
	}
}
