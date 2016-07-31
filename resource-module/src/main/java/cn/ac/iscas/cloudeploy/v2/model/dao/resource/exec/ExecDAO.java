package cn.ac.iscas.cloudeploy.v2.model.dao.resource.exec;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.resource.exec.Exec;

public interface ExecDAO extends
		PagingAndSortingRepository<Exec, Long> {
	@Query("select exec from Exec as exec where exec.user.id=?1")
	List<Exec> findByUser(Long userId);
}
