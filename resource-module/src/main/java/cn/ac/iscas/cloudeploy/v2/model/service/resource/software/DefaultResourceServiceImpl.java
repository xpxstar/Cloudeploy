package cn.ac.iscas.cloudeploy.v2.model.service.resource.software;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.dao.resource.software.InstanceParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.resource.software.SoftwareInstanceDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.InstanceParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.ResourceService;
import cn.ac.iscas.cloudeploy.v2.util.RandomUtils;

import com.google.common.collect.ImmutableList;

@Service
public class DefaultResourceServiceImpl implements ResourceService {
	@Autowired
	private SoftwareInstanceDAO softwareInstanceDAO;

	@Autowired
	private InstanceParamDAO instanceParamDAO;

	@Autowired
	private SoftwareService softwareService;

	@Override
	public boolean createSoftwareInstance(Action action, List<DHost> hosts,
			Map<String, String> params) {
		for (DHost host : hosts) {
			softwareService.createSoftwareInstance(randomName(action
					.getComponent().getDisplayName()), action, host,
					softwareService.mergeParams(action, params,
							ImmutableList.<InstanceParam> of()));
		}
		return true;
	}

	@Override
	public boolean configSoftwareInstance(Action action, List<DHost> hosts,
			Map<String, String> identifiers, Map<String, String> params) {
		Component component = action.getComponent();
		for (DHost host : hosts) {
			SoftwareInstance instance = getTargetInstance(component, host,
					identifiers);
			softwareService.saveInstanceParams(
					instance,
					softwareService.mergeParams(action, params,
							instance.getParams()));
		}
		return true;
	}

	@Override
	public boolean removeSoftwareInstance(Action action, List<DHost> hosts,
			Map<String, String> identifiers) {
		for (DHost host : hosts) {
			SoftwareInstance instance = getTargetInstance(
					action.getComponent(), host, identifiers);
			softwareService.removeInstance(instance);
		}
		return true;
	}

	@Override
	public boolean changeSoftwareInstanceStatus(Action action,
			List<DHost> hosts, Map<String, String> identifiers, String status) {
		for (DHost host : hosts) {
			SoftwareInstance instance = getTargetInstance(
					action.getComponent(), host, identifiers);
			softwareService.changeInstanceToStatus(instance,
					SoftwareInstance.InstanceStatus.of(status));
		}
		return true;
	}

	private String randomName(String prefix) {
		return (StringUtils.isEmpty(prefix) ? "" : prefix) + "_"
				+ RandomUtils.randomString();
	}

	private SoftwareInstance getTargetInstance(Component component, DHost host,
			Map<String, String> identifiers) {
		return softwareService.getTargetInstance(component, host, identifiers);
	}

}
