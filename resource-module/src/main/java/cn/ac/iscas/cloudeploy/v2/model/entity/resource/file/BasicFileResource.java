package cn.ac.iscas.cloudeploy.v2.model.entity.resource.file;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;

@MappedSuperclass
public class BasicFileResource extends IdEntity {
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "user_id")
	private User user;

	@Column(unique = true)
	private String name;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
