package cn.ac.iscas.cloudeploy.v2.model.service.event;

import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.ConfigEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.DeployEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.RemoveEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StartEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StopEvent;

@Service
public class PuppetEventFactory implements EventFactory {

	@Override
	public DeployEvent createDeployEvent(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoveEvent createRemoveEvent(ContainerInstance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StartEvent createStartEvent(ContainerInstance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StopEvent createStopEvent(ContainerInstance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigEvent createConfigEvent(Application app, Container container, String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
