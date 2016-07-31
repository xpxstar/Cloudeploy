package cn.ac.iscas.cloudeploy.v2.model.dao.resource.file;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.resource.file.RemoteFile;

public interface RemoteFileDAO extends
		PagingAndSortingRepository<RemoteFile, Long> {
	@Query("select file from RemoteFile as file where file.user.id=?1")
	List<RemoteFile> findByUser(Long userId);
	
	@Query("select file from RemoteFile as file where file.host.id=?1")
	List<RemoteFile> findByHost(Long hostId);
}
