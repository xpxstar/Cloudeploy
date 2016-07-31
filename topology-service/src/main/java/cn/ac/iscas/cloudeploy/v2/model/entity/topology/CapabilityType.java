package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import java.util.List;

import lombok.NoArgsConstructor;
@NoArgsConstructor
public class CapabilityType extends RootElement{
	/**
	 * An optional parent capability type name this new capability type derives from.
	 */
	private String derived_from;
	/**
	 * An optional list of property definitions for the capability type.
	 */
	private List<PropertyElement> properties;
	
	public String getDerived_from() {
		return derived_from;
	}
	public void setDerived_from(String derived_from) {
		this.derived_from = derived_from;
	}
	public List<PropertyElement> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyElement> properties) {
		this.properties = properties;
	}
}
