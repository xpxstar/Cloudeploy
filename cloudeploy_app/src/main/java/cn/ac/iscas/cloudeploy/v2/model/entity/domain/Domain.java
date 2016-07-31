package cn.ac.iscas.cloudeploy.v2.model.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_domain")
public class Domain extends IdEntity {
	@Column
	private String name;

	@Column
	private String ip;

	@SuppressWarnings("unused")
	private Domain() {

	}

	public Domain(String name, String ip) {
		this.name = name;
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
