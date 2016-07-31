package cn.ac.iscas.cloudeploy.v2.model.dao.host;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

public interface DHostDAO extends PagingAndSortingRepository<DHost, Long> {

	@Query("select host from DHost as host where host.id in ?1")
	List<DHost> findByIds(List<Long> ids);

	@Query("select host from DHost as host where host.id in ?1 and host.user.id=?2")
	List<DHost> findByIdsAndUser(List<Long> ids, Long userId);

	@Query("select host from DHost as host where host.user.id=?1")
	List<DHost> findByUser(Long userId);

	@Query("select host from DHost as host where host.id=?1 and host.user.id=?2")
	DHost findByIdAndUser(Long hostId, Long userId);
	
	DHost findByHostName(String name);
	
	DHost findByHostIP(String hostIp);
}
