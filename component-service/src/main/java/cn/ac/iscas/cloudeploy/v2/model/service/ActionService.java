package cn.ac.iscas.cloudeploy.v2.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;

@Service
public class ActionService {

	@Autowired
	private ActionDAO actionDAO;
	@Autowired
	private ActionParamDAO paramDAO;

	public List<Action> getActionsByComponent(Long componentId) {
		
		return actionDAO.getByComponent(new Component(componentId));
	}
	public Action changeDisplayname(Long id,String newname) {
		Action ac = actionDAO.findOne(id);
		if (null == ac) {
			return null;
		}
		ac.setDisplayName(newname);
		return actionDAO.save(ac);
	}
	public void delAction(Long id) {
		actionDAO.delete(id);
	}

	public ActionParam changeParam(Long id,String value,String desc) {
		ActionParam ap = paramDAO.findOne(id);
		if (null == ap) {
			return null;
		}
		ap.setDefaultValue(value);
		ap.setDescription(desc);
		return paramDAO.save(ap);
	}
	
}
