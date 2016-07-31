package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_app_container_template")
public class TemplateParam extends IdEntity{
	@Column
	private String source;
	@Column
	private String target;
	@Column
	private String command;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "container_id")
	private Container container;
	
	
	public TemplateParam() {
	}
	
	public TemplateParam(String source, String target, String command, Container container){
		this.source = source;
		this.container = container;
		this.target = target;
		this.command = command;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	
}
