package cn.ac.iscas.cloudeploy.v2.model.service.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentTypeDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.util.ParamUtils;

@Service
public class AdminService {
	@Autowired
	private ActionDAO actionDAO;

	@Autowired
	private ActionParamDAO paramDAO;

	@Autowired
	private ComponentDAO componentDAO;
	
	@Autowired
	private ComponentTypeDAO componentTypeDAO;

	public Action createAction(String name, String displayName, String params,
			Long componentId) {
		Action action = new Action();
		action.setName(name);
		action.setDisplayName(displayName);

		action.setComponent(componentDAO.findOne(componentId));
		actionDAO.save(action);
		List<ActionParam> paramList = ParamUtils.listFromParamString(params);
		for (ActionParam p : paramList) {
			p.setAction(action);
		}
		paramDAO.save(paramList);
		action.setParams(paramList);
		return action;
	}

	public Component createComponent(String name, String displayName,
			Long typeId) {
		Component component = new Component();
		component.setName(name);
		component.setDisplayName(displayName);
		component.setType(componentTypeDAO.findOne(typeId));
		return componentDAO.save(component);
	}
}
