package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class PropertyElement extends RootElement{
	/**
	 * The required data type for the property.
	 */
	private DataType type;
	/**
	 * An optional key that declares a property as required (true) or not(false).
	 */
	private boolean required;
	/**
	 * An optional key that may provide a value to be used as a default if 
	 * not provided by another means.
	 */
	private String defaultValue;
	
//	@Getter @Setter private String value;
	
	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	@Builder
	public PropertyElement(String description, String name, DataType type, boolean required, String defaultValue) {
		super(description, name);
		this.type = type;
		this.required = required;
		this.defaultValue = defaultValue;
	}
}
