package cn.ac.iscas.cloudeploy.v2.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.model.service.task.AdminService;

@Controller
@Transactional
@RequestMapping("v2/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@RequestMapping(value = { "/components" }, method = RequestMethod.POST)
	@ResponseBody
	public void addComponent(@RequestParam("name") String name,
			@RequestParam("displayName") String displayName,
			@RequestParam("typeId") Long typeId) {
		adminService.createComponent(name, displayName, typeId);
	}

	@RequestMapping(value = { "/actions" }, method = RequestMethod.POST)
	@ResponseBody
	public void addAction(@RequestParam("name") String name,
			@RequestParam("displayName") String displayName,
			@RequestParam("params") String params,
			@RequestParam("componentId")Long componentId) {
		adminService.createAction(name, displayName, params,componentId);
	}
}
