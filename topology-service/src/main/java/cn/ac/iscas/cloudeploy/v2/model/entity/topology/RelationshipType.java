package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import java.util.List;


import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;
@NoArgsConstructor
public class RelationshipType extends RootElement{
	private String target;
	private String source;
	private List<TemplateElement> templates;
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public List<TemplateElement> getTemplates() {
		return templates;
	}
	public void setTemplates(List<TemplateElement> templates) {
		this.templates = templates;
	}
	@Builder(builderMethodName="typeBuilder")
	public RelationshipType(String description, String name, String target, String source,
			@Singular List<TemplateElement> templates) {
		super(description, name);
		this.target = target;
		this.source = source;
		this.templates = templates;
	}
}
