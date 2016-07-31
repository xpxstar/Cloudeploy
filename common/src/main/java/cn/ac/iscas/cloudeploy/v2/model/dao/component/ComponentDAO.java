package cn.ac.iscas.cloudeploy.v2.model.dao.component;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;

public interface ComponentDAO extends
		PagingAndSortingRepository<Component, Long> {

	Component findByName(String name);

	@Query("select component from Component as component where component.userId = 0")
	List<Component> findSystem();
	
	@Query("select component from Component as component where component.userId = 0 and component.type.name like %?1%")
	List<Component> findSystemByType(String type);
	
	@Query("select component from Component as component where component.userId = ?2 and component.type.name like %?1%")
	List<Component> findByTypeAndUser(String type,Long userId);
	
	@Query("select component from Component as component where component.userId = ?1")
	List<Component> findByUser(Long userId);
}
