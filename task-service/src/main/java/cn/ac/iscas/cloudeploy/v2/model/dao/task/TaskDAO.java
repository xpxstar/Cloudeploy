package cn.ac.iscas.cloudeploy.v2.model.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.task.Task;

public interface TaskDAO extends PagingAndSortingRepository<Task, Long> {

	@Query("select task from Task as task where task.user.id=?1 and task.operation=?2")
	List<Task> findByUser(Long userId,int operation);
}
