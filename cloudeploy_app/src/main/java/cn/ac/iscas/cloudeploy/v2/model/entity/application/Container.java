package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.ImmutableList;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import lombok.Setter;

@Entity
@Table(name = "d_app_container")
public class Container extends IdEntity {
	@Column
	@Setter private String name;
	@Column
	private int port;

	@OneToMany(mappedBy = "container")
	private List<ContainerParam> params;
	
	@OneToMany(mappedBy = "container")
	private List<TemplateParam> templates;
	
	@OneToMany(mappedBy = "container")
	private List<ContainerAttribute> attributes;
	
	@Column
	private int maxCount;

	@Column
	private int initCount;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "app_id")
	private Application application;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "component_id")
	private Component component;

	@Enumerated(EnumType.STRING)
	private Status status;

	@OneToMany(mappedBy = "to")
	private List<Relation> inRelations;

	@OneToMany(mappedBy = "from")
	private List<Relation> outRelations;

	@Column(columnDefinition = "INT(11) DEFAULT 0")
	private Integer xPos;
	@Column(columnDefinition = "INT(11) DEFAULT 0")
	private Integer yPos;

	@Column
	private String identifier;

	@OneToMany(mappedBy = "container")
	private List<ContainerInstance> instances;

	public enum Status {
		CREATED, DEPLOYED, REMOVED
	}

	@SuppressWarnings("unused")
	private Container() {

	}

	public Container(String name, int port, int maxCount,
			int initCount, Application application, Component component,
			int xPos, int yPos, String identifier) {
		reset(name, port, maxCount, initCount, application, component,
				xPos, yPos, identifier);
		this.params = new ArrayList<>();
		this.status = Status.CREATED;
		this.inRelations = new ArrayList<>();
		this.outRelations = new ArrayList<>();
		this.instances = new ArrayList<>();
	}

	public void reset(String name, int port, int maxCount,
			int initCount, Application application, Component component,
			int xPos, int yPos, String identifier) {
		this.name = name;
		this.port = port;
		this.maxCount = maxCount;
		this.initCount = initCount;
		this.application = application;
		this.component = component;
		this.xPos = xPos;
		this.yPos = yPos;
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public List<ContainerParam> getParams() {
		return ImmutableList.copyOf(params);
	}

	public int getMaxCount() {
		return maxCount;
	}

	public Application getApplication() {
		return application;
	}

	public Component getComponent() {
		return component;
	}

	public Status getStatus() {
		return status;
	}

	public List<Relation> getInRelations() {
		return ImmutableList.copyOf(inRelations);
	}

	public List<Relation> getOutRelations() {
		return ImmutableList.copyOf(outRelations);
	}

	public Integer getxPos() {
		return xPos;
	}

	public Integer getyPos() {
		return yPos;
	}

	public List<ContainerInstance> getInstances() {
		return ImmutableList.copyOf(instances);
	}

	public int getInitCount() {
		return initCount;
	}

	public List<TemplateParam> getTemplates() {
		if(templates == null) return Collections.emptyList();
		return templates;
	}

	public void setTemplates(List<TemplateParam> templates) {
		this.templates = templates;
	}

	public List<ContainerAttribute> getAttributes() {
		if(attributes == null) return Collections.emptyList();
		return attributes;
	}

	public void setAttributes(List<ContainerAttribute> attributes) {
		this.attributes = attributes;
	}

	public void addInRelation(Relation relation) {
		inRelations.add(relation);
	}

	public void addInRelations(List<Relation> relations) {
		inRelations.addAll(relations);
	}

	public void addOutRelation(Relation relation) {
		outRelations.add(relation);
	}

	public void addOutRelations(List<Relation> relations) {
		outRelations.addAll(relations);
	}

	public void addInstance(ContainerInstance instance) {
		instances.add(instance);
	}

	public void removeInstance(ContainerInstance instance) {
		instances.remove(instance);
	}

	public void addInstances(List<ContainerInstance> instances) {
		this.instances.addAll(instances);
	}

	public void addParam(ContainerParam param) {
		params.add(param);
	}

	public void addParams(List<ContainerParam> params) {
		this.params.addAll(params);
	}

	public void changeStatusToDeployed() {
		status = Status.DEPLOYED;
	}

	public void changeStatusToRemoved() {
		status = Status.REMOVED;
	}

	public void changeStatusToCreated() {
		status = Status.CREATED;
	}

	public String getIdentifier() {
		return identifier;
	}
}
