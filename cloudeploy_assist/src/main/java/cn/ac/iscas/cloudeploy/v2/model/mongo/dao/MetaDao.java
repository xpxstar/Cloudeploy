package cn.ac.iscas.cloudeploy.v2.model.mongo.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.Meta;

public interface MetaDao extends MongoRepository<Meta, String> {  
	  
	public List<Meta> findByComponentLike(String component);  
	public List<Meta> findByName(String component);
  
}  