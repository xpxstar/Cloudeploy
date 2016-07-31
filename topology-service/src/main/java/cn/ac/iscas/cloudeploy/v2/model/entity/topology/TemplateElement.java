package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import lombok.Builder;
import lombok.NoArgsConstructor;
@NoArgsConstructor
public class TemplateElement extends RootElement{
	private String template;
	private String configurationfile;
	private String command;
	
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getConfigurationfile() {
		return configurationfile;
	}
	public void setConfigurationfile(String configurationfile) {
		this.configurationfile = configurationfile;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	@Builder
	public TemplateElement(String description, String name, String template, String configurationfile, String command) {
		super(description, name);
		this.template = template;
		this.configurationfile = configurationfile;
		this.command = command;
	}
}
