package cn.ac.iscas.cloudeploy.v2.model.dao.component;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;

public interface ActionDAO extends
		PagingAndSortingRepository<Action, Long> {

	Action findByName(String name);

	List<Action> getByComponent(Component component);

}
