package cn.ac.iscas.cloudeploy.v2.model.dao.task;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.task.NodeHostRelation;

public interface NodeHostRelationDAO extends PagingAndSortingRepository<NodeHostRelation, Long> {
}
