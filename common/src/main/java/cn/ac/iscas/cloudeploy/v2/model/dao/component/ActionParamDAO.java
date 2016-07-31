package cn.ac.iscas.cloudeploy.v2.model.dao.component;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;

public interface ActionParamDAO extends
		PagingAndSortingRepository<ActionParam, Long> {

	ActionParam findByActionAndParamKey(Action action, String param_key);

}
