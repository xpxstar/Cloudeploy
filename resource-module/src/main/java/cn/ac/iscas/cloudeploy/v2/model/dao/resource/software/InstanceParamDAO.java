package cn.ac.iscas.cloudeploy.v2.model.dao.resource.software;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.InstanceParam;

public interface InstanceParamDAO extends
		PagingAndSortingRepository<InstanceParam, Long> {
}
