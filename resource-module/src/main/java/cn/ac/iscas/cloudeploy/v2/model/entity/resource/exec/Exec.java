package cn.ac.iscas.cloudeploy.v2.model.entity.resource.exec;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;

@Entity
@Table(name = "d_exec")
public class Exec extends IdEntity {
	private String name;
	private String content;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "user_id")
	private User user;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
