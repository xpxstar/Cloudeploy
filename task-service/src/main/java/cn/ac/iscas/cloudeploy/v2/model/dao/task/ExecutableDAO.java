package cn.ac.iscas.cloudeploy.v2.model.dao.task;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.task.Executable;

public interface ExecutableDAO extends PagingAndSortingRepository<Executable, Long> {
}
