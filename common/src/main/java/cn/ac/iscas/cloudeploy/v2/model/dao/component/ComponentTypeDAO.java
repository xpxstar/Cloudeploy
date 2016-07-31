package cn.ac.iscas.cloudeploy.v2.model.dao.component;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.ComponentType;

public interface ComponentTypeDAO extends
		PagingAndSortingRepository<ComponentType, Long> {

	ComponentType findByName(String componentTypeString);
	
	List<ComponentType> findByUserId(Long userId);

	@Query("select componentType from ComponentType as componentType where componentType.userId = 0")
	List<ComponentType> findSystem();

}
