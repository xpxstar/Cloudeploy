package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;

@AllArgsConstructor
@NoArgsConstructor
public class NodeType extends RootElement{
	/**
	 * An optional list of property definitions for the Node.
	 */
	private List<PropertyElement> properties;
	/**
	 * An optional list of capability definitions for the Node.
	 */
	private List<CapabilityElement> capabilities;
	/**
	 * An optional sequenced list of requirement definitions for the Node.
	 */
	private List<RequirementElement> requirements;
	/**
	 * An optional list of named interfaces for the Node.
	 */
	private List<InterfaceElement> interfaces;
	/**
	 * An optional list of attribute definitions for Node.
	 */
	private List<AttributeElement> attributes;
	/**
	 * An optional list of service definitions for Node
	 */
	private List<ServiceElement> services;
	
	public List<PropertyElement> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyElement> properties) {
		this.properties = properties;
	}
	public List<CapabilityElement> getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(List<CapabilityElement> capabilities) {
		this.capabilities = capabilities;
	}
	public List<RequirementElement> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<RequirementElement> requirements) {
		this.requirements = requirements;
	}
	public List<InterfaceElement> getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(List<InterfaceElement> interfaces) {
		this.interfaces = interfaces;
	}
	public List<AttributeElement> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<AttributeElement> attributes) {
		this.attributes = attributes;
	}
	public List<ServiceElement> getServices() {
		return services;
	}
	public void setServices(List<ServiceElement> services) {
		this.services = services;
	}
	
	@Builder(builderMethodName= "typeBuilder")
	public NodeType(String description, String name, @Singular List<PropertyElement> properties,
			@Singular List<CapabilityElement> capabilities, @Singular List<RequirementElement> requirements,
			@Singular List<InterfaceElement> interfaces, @Singular List<AttributeElement> attributes, @Singular List<ServiceElement> services) {
		super(description, name);
		this.properties = properties;
		this.capabilities = capabilities;
		this.requirements = requirements;
		this.interfaces = interfaces;
		this.attributes = attributes;
		this.services = services;
	}
}
