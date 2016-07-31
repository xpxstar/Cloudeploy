package cn.ac.iscas.cloudeploy.v2.model.entity.resource.software;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_instance_param")
public class InstanceParam extends IdEntity {
	private String paramKey;
	
	@Column(columnDefinition = "varchar(1000)")
	private String paramValue;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "instance_id")
	private SoftwareInstance softwareInstance;

	public InstanceParam() {
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public SoftwareInstance getSoftwareInstance() {
		return softwareInstance;
	}

	public void setSoftwareInstance(SoftwareInstance softwareInstance) {
		this.softwareInstance = softwareInstance;
	}
}
