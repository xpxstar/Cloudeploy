package cn.ac.iscas.cloudeploy.v2.model.service;

import cn.ac.iscas.cloudeploy.v2.exception.NotFoundException;

public class BasicService {
	protected Object verifyResourceExists(Object resource) {
		if (resource == null) {
			throw new NotFoundException("resource not found");
		}
		return resource;
	}
}
