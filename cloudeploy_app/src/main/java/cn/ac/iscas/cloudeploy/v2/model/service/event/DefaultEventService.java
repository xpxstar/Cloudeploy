package cn.ac.iscas.cloudeploy.v2.model.service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orbitz.consul.Consul;
import com.orbitz.consul.option.EventOptions;
import com.orbitz.consul.option.ImmutableEventOptions;

import cn.ac.iscas.cloudeploy.v2.model.entity.event.ConfigEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.DeployEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.RemoveEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StartEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StopEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

@Service
public class DefaultEventService implements EventService{
	private static Logger logger = LoggerFactory.getLogger(DefaultEventService.class);
	@Autowired
	private Consul consul;
	private void fireEvent(String eventName, DHost host, String payload){
		logger.info("fire a event: " + eventName +" host: " + host.getHostName() + "payload: " + payload );
		EventOptions eventOptions = ImmutableEventOptions.builder()
				.nodeFilter(host.getHostName()).build();
		consul.eventClient().fireEvent(eventName, eventOptions, payload);
	}

	@Override
	public void pubDeployEvent(DeployEvent deployEvent) {
		fireEvent(DeployEvent.getEventName(),deployEvent.getDhost(),deployEvent.getPayload());
	}

	@Override
	public void pubRemoveEvent(RemoveEvent removeEvent) {
		fireEvent(RemoveEvent.getEventName(), removeEvent.getDhost(),removeEvent.getPayload());
	}

	@Override
	public void pubStartEvent(StartEvent startEvent) {
		fireEvent(StartEvent.getEventName(), startEvent.getDhost(),startEvent.getPayload());
	}

	@Override
	public void pubStopEvent(StopEvent stopEvent) {
		fireEvent(StopEvent.getEventName(), stopEvent.getDhost(),stopEvent.getPayload());
	}

	@Override
	public void pubConfigEvent(ConfigEvent configEvent) {
		try {
			fireEvent(ConfigEvent.getEventName(), configEvent.getDhost(), configEvent.getPayload());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
