package cn.ac.iscas.cloudeploy.v2.model.dao.application;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Test;

public interface TestDAO extends PagingAndSortingRepository<Test, Long> {
}
