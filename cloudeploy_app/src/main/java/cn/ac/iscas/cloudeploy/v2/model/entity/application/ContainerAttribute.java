package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_app_container_attribute")
public class ContainerAttribute extends IdEntity {
	@Column
	private String attrKey;
	
	@Column
	private String attrValue;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "container_id")
	private Container container;
	
	public ContainerAttribute() {
	}
	
	public ContainerAttribute(String key, String value, Container container){
		attrKey = key;
		attrValue = value;
		this.container = container;
	}

	public String getAttrKey() {
		return attrKey;
	}

	public void setAttrKey(String attrKey) {
		this.attrKey = attrKey;
	}

	public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}
}
