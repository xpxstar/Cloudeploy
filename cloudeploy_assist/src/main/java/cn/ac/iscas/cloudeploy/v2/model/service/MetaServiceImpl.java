package cn.ac.iscas.cloudeploy.v2.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.entity.Meta;
import cn.ac.iscas.cloudeploy.v2.model.mongo.dao.MetaDao;

@Service
public class MetaServiceImpl {
	@Autowired
	MetaDao metaDao;
	public List<Meta> getByComponent(String component){
		return metaDao.findByComponentLike(component);
	}
	public Meta save(Meta meta){
		return metaDao.save(meta);
	}
}
