package cn.ac.iscas.cloudeploy.v2.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;

import com.google.common.collect.ImmutableMap;

public class BasicController {
	public static final Map<String, String> SUCCESS = ImmutableMap
			.<String, String> of("result", "success");

	public static class DResponseBuilder {
		private Map<String, Object> map;

		public static DResponseBuilder instance() {
			return new DResponseBuilder();
		}

		private DResponseBuilder() {
			map = new HashMap<>();
		}

		public DResponseBuilder add(String key, Object value) {
			map.put(key, value);
			return this;
		}

		public Map<String, Object> build() {
			return map;
		}
	}

	@Autowired
	private UserService userService;

	/**
	 * get the current logged in user
	 * 
	 * @return null if the user was not found
	 */
	public User currentUser() {
		return userService.getCurrentUser();
	}
}
