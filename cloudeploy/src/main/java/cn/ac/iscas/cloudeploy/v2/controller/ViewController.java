package cn.ac.iscas.cloudeploy.v2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("views")
public class ViewController {

	@RequestMapping(value = { "/task/home" }, method = RequestMethod.GET)
	public String taskHomeView() {
		return "task/task_home";
	}

	@RequestMapping(value = { "/task/panel" }, method = RequestMethod.GET)
	public String taskPanelView() {
		return "task/task_panel";
	}

	@RequestMapping(value = { "/task/source" }, method = RequestMethod.GET)
	public String taskSourceView() {
		return "task/task_source";
	}

	@RequestMapping(value = { "/resource/home" }, method = RequestMethod.GET)
	public String resourceHomeView() {
		return "resource/resource_home";
	}

	@RequestMapping(value = { "/resource/software/instance/list" }, method = RequestMethod.GET)
	public String softwareInstanceListView() {
		return "resource/software_instance_list";
	}

	@RequestMapping(value = { "/resource/software/instance/install" }, method = RequestMethod.GET)
	public String softwareInstanceInstallView() {
		return "resource/software_instance_install";
	}

	@RequestMapping(value = { "/resource/software/instance/config" }, method = RequestMethod.GET)
	public String softwareInstanceConfigView() {
		return "resource/software_instance_config";
	}

	@RequestMapping(value = { "/resource/software/instance/params" }, method = RequestMethod.GET)
	public String softwareActionParamView() {
		return "resource/software_action_param";
	}

	@RequestMapping(value = { "/resource/remote/file/list" }, method = RequestMethod.GET)
	public String remoteFileListView() {
		return "resource/remote_file_list";
	}

	@RequestMapping(value = { "/resource/remote/file/edit" }, method = RequestMethod.GET)
	public String remoteFileEditView() {
		return "resource/remote_file_edit";
	}

	@RequestMapping(value = { "/resource/custom/file/list" }, method = RequestMethod.GET)
	public String customFileListView() {
		return "resource/custom_file_list";
	}

	@RequestMapping(value = { "/resource/exec" }, method = RequestMethod.GET)
	public String execView() {
		return "resource/exec";
	}

	@RequestMapping(value = { "/resource/exec/list" }, method = RequestMethod.GET)
	public String execListView() {
		return "resource/exec_list";
	}
	@RequestMapping(value = { "/component/home" }, method = RequestMethod.GET)
	public String componentHomeView() {
		return "component/component_home";
	}
	
	@RequestMapping(value = { "/action/home" }, method = RequestMethod.GET)
	public String actionHomeView() {
		return "component/action_home";
	}
}
