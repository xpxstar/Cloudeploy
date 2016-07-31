package cn.ac.iscas.cloudeploy.v2.model.dao.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.domain.Domain;

public interface DomainDAO extends PagingAndSortingRepository<Domain, Long> {
}
