package cn.ac.iscas.cloudeploy.v2.model.dao.application;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;

public interface ContainerInstanceDAO extends PagingAndSortingRepository<ContainerInstance, Long> {
	public ContainerInstance findByName(String name);
}
