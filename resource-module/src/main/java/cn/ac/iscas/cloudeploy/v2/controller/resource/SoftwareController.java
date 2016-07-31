package cn.ac.iscas.cloudeploy.v2.controller.resource;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.SoftwareViews;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.SoftwareViews.BasicInstanceParams;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.SoftwareViews.BasicParamItem;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.SoftwareViews.InstanceItem;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.SoftwareViews.InstanceParams;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.SoftwareViews.ParameteredInstanceItem;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.InstanceParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance.InstanceStatus;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.software.SoftwareService;
import cn.ac.iscas.cloudeploy.v2.user.security.authorization.CheckPermission;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

@Controller
@Transactional
@RequestMapping(value = "v2/resources/softwares/instances")
public class SoftwareController extends BasicController {

	@Autowired
	private SoftwareService softwareService;

	/**
	 * 获取某台主机上的软件列表
	 * 
	 * @param hostId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@CheckPermission(resources = { DHost.PERMISSION_KEY })
	public List<InstanceItem> getSoftwareInstanceListOnHost(
			@RequestParam(Constants.HTTP_REQUEST_PARAM_HOST_ID) Long hostId) {
		return SoftwareViews.instanceListViewOf(softwareService
				.findInstancesByHost(hostId));
	}

	/**
	 * 安装软件
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public SoftwareViews.InstanceItem installSoftware(
			@RequestBody InstanceParams params) {
		return SoftwareViews.instanceViewOf(softwareService
				.installSoftwareOnRemote(currentUser(), params.alias,
						params.actionId, params.hostId,
						convertToInstanceParams(params.params)));
	}

	/**
	 * 获取软件实例
	 * 
	 * @param instanceId
	 * @param actionId
	 * @return
	 */
	@RequestMapping(value = "/{" + Constants.HTTP_REQUEST_PARAM_INSTANCE_ID
			+ "}", method = RequestMethod.GET)
	@ResponseBody
	@CheckPermission(resources = { SoftwareInstance.PERMISSION_KEY })
	public ParameteredInstanceItem getSoftwareInstanceById(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_INSTANCE_ID) Long instanceId,
			@RequestParam("actionId") Long actionId) {
		SoftwareInstance instance = softwareService.findInstance(instanceId);
		for (Action action : instance.getComponent().getActions()) {
			if (action.getId().equals(actionId)) {
				return new ParameteredInstanceItem(instance, action);
			}
		}
		return null;
	}

	/**
	 * 配置软件
	 * 
	 * @param instanceId
	 * @param params
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/{" + Constants.HTTP_REQUEST_PARAM_INSTANCE_ID
			+ "}", method = RequestMethod.PUT)
	@ResponseBody
	@CheckPermission(resources = { SoftwareInstance.PERMISSION_KEY })
	public Object configSoftwareInstance(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_INSTANCE_ID) Long instanceId,
			@RequestBody BasicInstanceParams params) {
		SoftwareInstance instance = softwareService.findInstance(instanceId);

		softwareService.configInstanceOnRemote(instance,
				convertToInstanceParams(params.params));
		return SUCCESS;
	}

	/**
	 * 卸载软件
	 * 
	 * @param instanceId
	 * @return
	 */
	@RequestMapping(value = "/{" + Constants.HTTP_REQUEST_PARAM_INSTANCE_ID
			+ "}", method = RequestMethod.DELETE)
	@ResponseBody
	@CheckPermission(resources = { SoftwareInstance.PERMISSION_KEY })
	public Object removeSoftwareInstance(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_INSTANCE_ID) Long instanceId) {
		softwareService.uninstallInstanceOnRemote(instanceId);
		return SUCCESS;
	}

	/**
	 * 启动/停止 软件
	 * 
	 * @param instanceId
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "/{" + Constants.HTTP_REQUEST_PARAM_INSTANCE_ID
			+ "}/service", method = RequestMethod.PUT)
	@ResponseBody
	@CheckPermission(resources = { SoftwareInstance.PERMISSION_KEY })
	public Object changeSoftwareInstanceStatus(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_INSTANCE_ID) Long instanceId,
			@RequestParam("status") InstanceStatus status) {
		softwareService.changeInstanceToStatusOnRemote(
				softwareService.findInstance(instanceId), status);
		return SUCCESS;
	}

	/**
	 * 在软件实例上执行操作
	 * 
	 * @param instanceId
	 * @param actionId
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/{" + Constants.HTTP_REQUEST_PARAM_INSTANCE_ID
			+ "}/actions/{actionId}", method = RequestMethod.POST)
	@ResponseBody
	@CheckPermission(resources = { SoftwareInstance.PERMISSION_KEY })
	public Object applyActionToInstance(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_INSTANCE_ID) Long instanceId,
			@PathVariable("actionId") Long actionId,
			@RequestBody BasicInstanceParams params) {

		softwareService.applyActionOnInstance(instanceId, actionId,
				convertToInstanceParams(params.params));
		return SUCCESS;
	}

	private List<InstanceParam> convertToInstanceParams(
			List<BasicParamItem> params) {
		List<InstanceParam> instanceParams = new ArrayList<>();
		for (BasicParamItem p : params) {
			InstanceParam param = new InstanceParam();
			param.setParamKey(p.paramKey);
			param.setParamValue(p.paramValue);
			instanceParams.add(param);
		}
		return instanceParams;
	}
}
