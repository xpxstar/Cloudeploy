package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;

@AllArgsConstructor
@NoArgsConstructor
public class NodeTemplate extends NodeType {
	private String type;
	private int minInstance;
	private int maxInstance;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMinInstance() {
		return minInstance;
	}

	public void setMinInstance(int minInstance) {
		this.minInstance = minInstance;
	}

	public int getMaxInstance() {
		return maxInstance;
	}

	public void setMaxInstance(int maxInstance) {
		this.maxInstance = maxInstance;
	}
	
	@Builder(builderClassName = "NoteTemplateBuilder", buildMethodName = "build", builderMethodName = "templateBuilder")
	public NodeTemplate(String description, String name, @Singular List<PropertyElement> properties,
			List<CapabilityElement> capabilities, List<RequirementElement> requirements,
			List<InterfaceElement> interfaces, @Singular List<AttributeElement> attributes, @Singular List<ServiceElement> services,
			String type, int minInstance, int maxInstance) {
		super(description, name, properties, capabilities, requirements, interfaces, attributes, services);
		this.type = type;
		this.minInstance = minInstance;
		this.maxInstance = maxInstance;
	}
}
