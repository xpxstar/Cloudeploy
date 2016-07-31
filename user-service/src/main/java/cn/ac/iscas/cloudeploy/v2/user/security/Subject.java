package cn.ac.iscas.cloudeploy.v2.user.security;

import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;

public class Subject {
	private User user;

	public Subject(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
