package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IParameterElement extends RootElement{
	/**
	 * represents the required value to associate with the parameter name.
	 */
	private String value;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
