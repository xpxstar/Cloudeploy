package cn.ac.iscas.cloudeploy.v2.model.dao.file;

import org.springframework.data.jpa.repository.JpaRepository;
import cn.ac.iscas.cloudeploy.v2.model.entity.file.FileSource;

public interface FileSourceDAO extends
		JpaRepository<FileSource, Long> {
	FileSource findByMd5(String md5);
}
