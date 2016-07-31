package cn.ac.iscas.cloudeploy.v2.controller.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController.DResponseBuilder;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ApplicationView;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.service.application.ApplicationService;

@Controller
@RequestMapping(value = "v2/applications")
public class ApplicationController {
	@Autowired
	private ApplicationService appService;

	/**
	 * 获取应用列表
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public List<ApplicationView.DetailedItem> getApplications() {
		return ApplicationView.detailedViewListOf(appService
				.getAllApplications());
	}

	/**
	 * 创建应用
	 * 
	 * @param appView
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public ApplicationView.Item createApplication(
			@RequestBody ApplicationView.DetailedItem appView) {
		return ApplicationView.viewOf(appService.createApplication(appView));
	}

	/**
	 * 部署应用
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public ApplicationView.DetailedItem deployApplication(
			@PathVariable("appId") Long id) {
		return ApplicationView.detailedViewOf(appService.deployApplication(id));
	}

	/**
	 * 获取应用详细信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public ApplicationView.DetailedItem getApplication(
			@PathVariable("appId") Long id) {
		Application app = appService.getApplication(id);
		return ApplicationView.detailedViewOf(app);
	}

	/**
	 * 节容器实例上做动作
	 * 
	 * @param id
	 * @param operation
	 * @return
	 */
	@RequestMapping(value = { "/containers/instances/{instanceId}" }, method = RequestMethod.PUT)
	@ResponseBody
	public Object operationOnContainerInstance(
			@PathVariable("instanceId") Long id,
			@RequestParam("operation") ContainerInstance.Operation operation) {
		boolean success = appService.doOperationOnContainerInstance(id,
				operation);
		return DResponseBuilder.instance()
				.add("result", success ? "success" : "fail").build();
	}

	/**
	 * 修改应用
	 * 
	 * @param appId
	 * @param appView
	 * @return
	 */
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.PUT)
	@ResponseBody
	@Transactional
	public ApplicationView.DetailedItem modifyApplication(
			@PathVariable("appId") Long appId,
			@RequestBody ApplicationView.DetailedItem appView) {
		return ApplicationView.detailedViewOf(appService.modifyApplication(
				appId, appView));
	}

	/**
	 * 移除应用
	 * 
	 * @param appId
	 * @return
	 */
	@Transactional
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.DELETE)
	@ResponseBody
	public Object removeApplication(@PathVariable("appId") Long appId) {
		boolean success = appService.removeApplication(appId);
		return DResponseBuilder.instance()
				.add("result", success ? "success" : "fail").build();
	}
}
