package cn.ac.iscas.cloudeploy.v2.model.service.resource.software;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.exception.BadRequestException;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.resource.software.InstanceParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.resource.software.SoftwareInstanceDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.InstanceParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance.InstanceStatus;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.BasicService;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.ApplyService;
import cn.ac.iscas.cloudeploy.v2.util.Constants;
import cn.ac.iscas.cloudeploy.v2.util.DeployUtils;

import com.google.common.collect.ImmutableMap;

@Service
public class SoftwareService extends BasicService {
	@Autowired
	private SoftwareInstanceDAO softwareInstanceDAO;

	@Autowired
	private InstanceParamDAO instanceParamDAO;

	@Autowired
	private ActionDAO actionDAO;

	@Autowired
	private HostService hostService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private ApplyService applyService;

	public List<SoftwareInstance> findInstancesByHost(Long hostId) {
		return softwareInstanceDAO.findByHost(hostId);
	}

	public SoftwareInstance findInstance(Long instanceId) {
		return (SoftwareInstance) verifyResourceExists(softwareInstanceDAO
				.findOne(instanceId));
	}

	/**
	 * 在远程机器安装软件的操作
	 * 
	 * @param alias
	 * @param actionId
	 * @param hostId
	 * @param params
	 * @return
	 */
	public SoftwareInstance installSoftwareOnRemote(User user, String alias,
			Long actionId, Long hostId, List<InstanceParam> params) {
		Action action = actionDAO.findOne(actionId);
		DHost host = hostService.findDHostByIdAndUser(hostId, user.getId());
		SoftwareInstance instance = createSoftwareInstance(alias, action, host,
				params);
		applyActionOnHost(action, instance.getParams(), host);
		return instance;
	}

	/**
	 * 创建软件实例，不会实施到远程机器
	 * 
	 * @param alias
	 * @param action
	 * @param host
	 * @param params
	 * @return
	 */
	public SoftwareInstance createSoftwareInstance(String alias, Action action,
			DHost host, List<InstanceParam> params) {
		SoftwareInstance instance = getTargetInstance(action.getComponent(),
				host, getIdentifiersOfInstance(action.getComponent(), params));
		if (instance != null)
			return instance;
		instance = new SoftwareInstance();
		instance.setAlias(alias);
		instance.setComponent(action.getComponent());
		instance.setHost(host);
		softwareInstanceDAO.save(instance);
		instance.setParams(saveInstanceParams(instance, params));
		return instance;
	}

	/**
	 * 在远程机器配置软件的操作;
	 * 已用applyActionOnInstance()方法代替
	 * 
	 * @param instance
	 * @param params
	 */
	@Deprecated
	public void configInstanceOnRemote(SoftwareInstance instance,
			List<InstanceParam> params) {
		instance.setParams(saveInstanceParams(instance, params));
		applyActionOnHost(instance, ConfigKeys.ACTION_NAME_CONFIG);
	}

	/**
	 * 保存实例参数，不会实施到远程机器
	 * 
	 * @param instance
	 * @param params
	 * @return
	 */
	public List<InstanceParam> saveInstanceParams(SoftwareInstance instance,
			List<InstanceParam> params) {
		for (InstanceParam param : params) {
			param.setSoftwareInstance(instance);
		}
		instanceParamDAO.save(params);
		return params;
	}

	/**
	 * 在远程机器删除软件的操作
	 * 
	 * @param instanceId
	 */
	public void uninstallInstanceOnRemote(Long instanceId) {
		applyActionOnHost(softwareInstanceDAO.findOne(instanceId),
				ConfigKeys.ACTION_NAME_DELETE);
		removeInstance(instanceId);
	}

	/**
	 * 移除软件实例，不会实施到远程机器
	 * 
	 * @param instanceId
	 */
	public void removeInstance(Long instanceId) {
		SoftwareInstance instance = softwareInstanceDAO.findOne(instanceId);
		instanceParamDAO.delete(instance.getParams());
		softwareInstanceDAO.delete(instanceId);
	}

	public void removeInstance(SoftwareInstance instance) {
		removeInstance(instance.getId());
	}

	/**
	 * 更改远程机器上的软件状态
	 * 
	 * @param instance
	 * @param status
	 * @return
	 */
	public void changeInstanceToStatusOnRemote(SoftwareInstance instance,
			InstanceStatus status) {
		changeInstanceToStatus(instance, status);
		applyActionOnHost(serviceActionOfComponent(instance.getComponent()),
				instance.getParams(), instance.getHost());
	}

	/**
	 * 更改软件实例状态，不会应用到远程机器
	 * 
	 * @param instance
	 * @param status
	 */
	public void changeInstanceToStatus(SoftwareInstance instance,
			InstanceStatus status) {
		Component component = instance.getComponent();
		// get the software name to get the service parameters from the
		// configuration
		String serviceKey = getLastSegment(component.getName());
		if (StringUtils.isEmpty(serviceKey) || status == null) {
			throw new BadRequestException(
					"invalid parameters for service operation");
		}
		JSONObject config = configService
				.getServiceConfigAsJSONObject(serviceKey);
		JSONArray serviceParams = null;
		switch (status) {
		case RUNNING:
			serviceParams = config
					.getJSONArray(ConfigKeys.SERVICE_START_PARAMS);
			break;
		case STOPPED:
			serviceParams = config.getJSONArray(ConfigKeys.SERVICE_STOP_PARAMS);
			break;
		default:
			break;
		}
		if (serviceParams == null) {
			throw new BadRequestException(
					"invalid parameters for service operation");
		}
		// modify the service parameters in the software instance
		// // list containing parameters of the latest version
		Action action = serviceActionOfComponent(component);
		if (action != null) {
			Map<String, String> newParamMap = new HashMap<>();
			for (int i = 0; i < serviceParams.length(); i++) {
				JSONObject param = serviceParams.getJSONObject(i);
				newParamMap.put(param.getString(ConfigKeys.PARAM_KEY),
						String.valueOf(param.get(ConfigKeys.PARAM_VALUE)));
			}
			saveInstanceParams(instance,
					mergeParams(action, newParamMap, instance.getParams()));
			instance.setStatus(status);
			softwareInstanceDAO.save(instance);
		}
	}

	private Action serviceActionOfComponent(Component component) {
		for (Action action : component.getActions()) {
			String actionKey = getLastSegment(action.getName());
			if (actionKey.equals(Constants.ACTIONKEY_SERVICE)) {
				return action;
			}
		}
		return null;
	}

	/**
	 * 合并action 中的参数，实例的已有参数和用户指定的参数
	 * 
	 * @param action
	 * @param newParamMap
	 * @param original
	 * @return
	 */
	public List<InstanceParam> mergeParams(Action action,
			Map<String, String> newParamMap, List<InstanceParam> original) {
		Map<String, InstanceParam> instanceParamMap = new HashMap<>();
		for (InstanceParam param : original) {
			instanceParamMap.put(param.getParamKey(), param);
		}
		for (ActionParam param : action.getParams()) {
			if (!instanceParamMap.containsKey(param.getParamKey())) {
				InstanceParam instanceParam = new InstanceParam();
				instanceParam.setParamKey(param.getParamKey());
				instanceParam.setParamValue(param.getDefaultValue());
				instanceParamMap.put(param.getParamKey(), instanceParam);
			}
		}
		for (Entry<String, String> newParam : newParamMap.entrySet()) {
			if (instanceParamMap.containsKey(newParam.getKey())) {
				instanceParamMap.get(newParam.getKey()).setParamValue(
						newParam.getValue());
			}
		}
		List<InstanceParam> res = new ArrayList<>();
		res.addAll(instanceParamMap.values());
		return res;
	}

	/**
	 * 根据component,host和identifiers 唯一获取软件实例
	 * 
	 * @param component
	 * @param host
	 * @param identifiers
	 * @return
	 */
	public SoftwareInstance getTargetInstance(Component component, DHost host,
			Map<String, String> identifiers) {
		List<SoftwareInstance> instances = softwareInstanceDAO
				.findByComponentAndHost(component.getId(), host.getId());
		if (!component.isRepeatable()) {
			return instances.size() > 0 ? instances.get(0) : null;
		}
		if (identifiers == null || identifiers.size() == 0) {
			throw new BadRequestException("Invalid identifiers for software");
		}
		SoftwareInstance targetInstance = null;
		for (SoftwareInstance instance : instances) {
			boolean isTarget = true;
			Map<String, String> instanceParamMap = getIdentifiersOfInstance(
					component, instance.getParams());
			for (Entry<String, String> identifier : identifiers.entrySet()) {
				isTarget &= (instanceParamMap.containsKey(identifier.getKey()) && instanceParamMap
						.get(identifier.getKey()).equals(identifier.getValue()));

			}
			if (isTarget) {
				targetInstance = instance;
				break;
			}
		}
		return targetInstance;
	}

	/**
	 * 在软件实例上执行操作
	 * 
	 * @param instanceId
	 * @param actionId
	 * @param params
	 */
	public void applyActionOnInstance(Long instanceId, Long actionId,
			List<InstanceParam> params) {
		SoftwareInstance instance = findInstance(instanceId);
		instance.setParams(saveInstanceParams(instance, params));
		Action action = actionDAO.findOne(actionId);
		applyActionOnHost(action, instance.getParams(), instance.getHost());
	}

	private Map<String, String> getIdentifiersOfInstance(Component component,
			List<InstanceParam> params) {
		if (!component.isRepeatable()) {
			return ImmutableMap.<String, String> of();
		}
		if (params == null || params.size() == 0) {
			throw new BadRequestException("Invalid identifiers for software");
		}
		Set<String> identifierKeys = new HashSet<>(Arrays.asList(StringUtils
				.split(component.getIdentifiers(),
						Component.IDENTIFIER_SEPARATOR)));
		Map<String, String> instanceParamMap = new HashMap<>();
		for (InstanceParam param : params) {
			if (identifierKeys.contains(param.getParamKey())) {
				instanceParamMap
						.put(param.getParamKey(), param.getParamValue());
			}
		}
		return instanceParamMap;
	}

	private String getLastSegment(String str) {
		return DeployUtils.getLastSegmentOfName(str);
	}

	private void applyActionOnHost(SoftwareInstance instance, String actionName) {
		String name = configService.getConfigAsString(actionName);
		for (Action action : instance.getComponent().getActions()) {
			if (getLastSegment(action.getName()).equalsIgnoreCase(name)) {
				applyActionOnHost(action, instance.getParams(),
						instance.getHost());
				break;
			}
		}
	}

	private void applyActionOnHost(Action action, List<InstanceParam> params,
			DHost host) {
		Map<String, String> paramMap = new HashMap<>();
		for (InstanceParam param : params) {
			paramMap.put(param.getParamKey(), param.getParamValue());
		}
		applyService.applyActionOnHost(action, paramMap, host);
	}

}
