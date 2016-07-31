package cn.ac.iscas.cloudeploy.v2.model.service.domain;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.domain.Domain;

public interface DomainService {
	public List<Domain> getDomainList();

	public Domain bindDomain(String name, String ip);

	public void unbindDomain(Long domainId);
}
