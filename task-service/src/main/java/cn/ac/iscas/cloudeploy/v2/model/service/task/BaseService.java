package cn.ac.iscas.cloudeploy.v2.model.service.task;


public class BaseService {
	protected Object verifyResourceExists(Object resource) {
		if (resource == null) {
			throw new RuntimeException("resource not found");
		}
		return resource;
	}
}
