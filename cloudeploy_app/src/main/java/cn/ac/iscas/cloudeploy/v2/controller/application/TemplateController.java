package cn.ac.iscas.cloudeploy.v2.controller.application;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.dataview.application.ApplicationView;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.service.application.ApplicationService;

/**
 * @description template Manager 
 * @author xpxstar@gmail.com
 * 2016年4月26日 下午2:16:09
 */
@Controller
@RequestMapping(value = "v2/templates")
@Transactional
public class TemplateController {
	@Autowired
	private ApplicationService appService;

	/**
	 * 获取应用简要信息列表
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<ApplicationView.Item> getApplications() {
		return ApplicationView.viewListOf(appService
				.getAllApplications());
	}

	/**
	 * 获取应用详细信息(主要是container 列表)
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.GET)
	@ResponseBody
	public ApplicationView.DetailedItem getApplication(
			@PathVariable("appId") Long id) {
		Application app = appService.getApplication(id);
		return ApplicationView.detailedViewOf(app);
	}

}
