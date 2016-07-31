package cn.ac.iscas.cloudeploy.v2.model.dao.resource.file;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.resource.file.CustomFile;

public interface CustomFileDAO extends
		PagingAndSortingRepository<CustomFile, Long> {
	@Query("select file from CustomFile as file where file.user.id=?1")
	List<CustomFile> findByUser(Long userId);
}
