package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_test")
public class Test extends IdEntity {
	private String name;

	@SuppressWarnings("unused")
	private Test() {

	}

	public Test(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
