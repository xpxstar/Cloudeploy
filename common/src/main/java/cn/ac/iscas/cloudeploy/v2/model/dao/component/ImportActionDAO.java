package cn.ac.iscas.cloudeploy.v2.model.dao.component;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.ImportAction;

public interface ImportActionDAO extends
	PagingAndSortingRepository<ImportAction, Long>{

}
