package cn.ac.iscas.cloudeploy.v2.model.service.event;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.ConfigEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.DeployEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.RemoveEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StartEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StopEvent;

public interface EventFactory {
	public DeployEvent createDeployEvent(String url);
	public RemoveEvent createRemoveEvent(ContainerInstance instance);
	public StartEvent createStartEvent(ContainerInstance cInstance);
	public StopEvent createStopEvent(ContainerInstance instance);
	public ConfigEvent createConfigEvent(Application app, Container container, String key, String value);
	
}
