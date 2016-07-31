package cn.ac.iscas.cloudeploy.v2.model.dao.resource.software;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance;

public interface SoftwareInstanceDAO extends
		PagingAndSortingRepository<SoftwareInstance, Long> {

	@Query("select instance from SoftwareInstance as instance where instance.component.id=?1 and instance.host.id=?2")
	List<SoftwareInstance> findByComponentAndHost(Long componentId, Long hostId);
	
	@Query("select instance from SoftwareInstance as instance where instance.host.id=?1")
	List<SoftwareInstance> findByHost(Long hostId);
}
