package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_app_container_param")
public class ContainerParam extends IdEntity {
	@Column
	private String paramKey;
	@Column
	private String paramValue;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "container_id")
	private Container container;

	@SuppressWarnings("unused")
	private ContainerParam() {

	}

	public ContainerParam(String paramKey, String paramValue,
			Container container) {
		this.paramKey = paramKey;
		this.paramValue = paramValue;
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}

	public String getParamKey() {
		return paramKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

}
