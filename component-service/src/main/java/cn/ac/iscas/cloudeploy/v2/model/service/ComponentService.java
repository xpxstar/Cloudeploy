package cn.ac.iscas.cloudeploy.v2.model.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentTypeDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ComponentType;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;

@Service
public class ComponentService {

	@Autowired
	private ComponentDAO componentDAO;

	@Autowired
	private ComponentTypeDAO componentTypeDAO;

	public List<Component> getDefaultComponents() {
		return (List<Component>) componentDAO.findSystem();
	}

	public List<ComponentType> getComponentTypes() {
		return (List<ComponentType>) componentTypeDAO.findSystem();
	}

	public List<Component> getComponentsByType(String type) {
		if(StringUtils.isEmpty(type)){
			return getDefaultComponents();
		}
		return componentDAO.findSystemByType(type);
	}

	public List<Component> getComponentsByUser(String type, Long userId) {
		if(StringUtils.isEmpty(type)){
			return componentDAO.findByUser(userId);
		}
		return componentDAO.findByTypeAndUser(type, userId);
	}

	public List<ComponentType> getComponentTypesByUser(Long userId) {
		return componentTypeDAO.findByUserId(userId);
	}

	public Component createComponent(Long id, String name, String displayName, Long typeId,User user,boolean repeatable,String identifiers) {
		Component com;
		if (0 != id) {
				com = new Component(id);
			}else {
				com = componentDAO.findByName(name);
				if (null != com) {
					return com;
				}else {
					com = new Component();
				}
			}
			ComponentType comt = componentTypeDAO.findOne(typeId);
			com.setName(name);
			com.setDisplayName(displayName);
			com.setType(comt);
			com.setUserId(user.getId());
			com.setRepeatable(repeatable);
			com.setIdentifiers(identifiers);
		
		return componentDAO.save(com);
	}
	public ComponentType createComponentType(Long id, String name, String displayName, Long userId) {
		ComponentType comt = new ComponentType();
		if (0 != id) {
			comt.setId(id);
		}
		comt.setName(name);
		comt.setDisplayName(displayName);
		comt.setUserId(userId);
		return componentTypeDAO.save(comt);
	}
	public void delComponent(Long id) {
		componentDAO.delete(id);
	}
	public void delComponentType(Long id) {
		componentTypeDAO.delete(id);
	}
	
}
