package cn.ac.iscas.cloudeploy.v2.model.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskNode;

public interface TaskNodeDAO extends PagingAndSortingRepository<TaskNode, Long> {

	/**
	 * @param identifiers
	 * @return 找到id集合内的对应taskNode
	 */
	@Query("select node from TaskNode as node where node.identifier in ?1")
	List<TaskNode> findByIdentifiers(List<String> identifiers);
}
