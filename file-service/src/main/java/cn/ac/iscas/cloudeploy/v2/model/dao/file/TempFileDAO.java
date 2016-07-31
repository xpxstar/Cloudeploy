package cn.ac.iscas.cloudeploy.v2.model.dao.file;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.file.TempFile;

public interface TempFileDAO extends PagingAndSortingRepository<TempFile, Long> {
	TempFile findByAccessKey(String accessKey);
}
