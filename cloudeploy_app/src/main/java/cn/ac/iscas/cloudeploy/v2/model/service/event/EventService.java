package cn.ac.iscas.cloudeploy.v2.model.service.event;

import cn.ac.iscas.cloudeploy.v2.model.entity.event.ConfigEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.DeployEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.RemoveEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StartEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StopEvent;

public interface EventService {
	public void pubDeployEvent(DeployEvent deployEvent);
	public void pubRemoveEvent(RemoveEvent removeEvent);
	public void pubStartEvent(StartEvent startEvent);
	public void pubStopEvent(StopEvent stopEvent);
	public void pubConfigEvent(ConfigEvent configEvent);
}
