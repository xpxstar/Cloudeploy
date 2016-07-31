package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
public class RelationshipTemplate extends RelationshipType{
	/**
	 * The required name of the Relationship Type the Relationship Template is based
	 * upon.
	 */
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@Builder(builderMethodName="templateBuilder")
	public RelationshipTemplate(String description, String name, String target, String source,
			@Singular List<TemplateElement> templates, String type) {
		super(description, name, target, source, templates);
		this.type = type;
	}
}
