package cn.ac.iscas.cloudeploy.v2.model.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.ConfigEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.DeployEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.RemoveEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StartEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StopEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.util.application.HostSelector;

@Service
public class DockerEventFactory implements EventFactory {
	@Autowired
	private HostSelector selector; 
	@Override
	public DeployEvent createDeployEvent(String url) {
		DHost host = selector.getHost();
		return DeployEvent.builder().dhost(host).payload(url).build();
	}

	@Override
	public RemoveEvent createRemoveEvent(ContainerInstance instance) {
		DHost host = instance.getMaster();
		return RemoveEvent.builder().dhost(host).payload(instance.getName()).build();
	}

	@Override
	public StartEvent createStartEvent(ContainerInstance instance) {
		DHost host = instance.getMaster();
		return StartEvent.builder().dhost(host).payload(instance.getName()).build();
	}

	@Override
	public StopEvent createStopEvent(ContainerInstance instance) {
		DHost host = instance.getMaster();
		return StopEvent.builder().dhost(host).payload(instance.getName()).build();
	}

	@Override
	public ConfigEvent createConfigEvent(Application app, Container container, String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
