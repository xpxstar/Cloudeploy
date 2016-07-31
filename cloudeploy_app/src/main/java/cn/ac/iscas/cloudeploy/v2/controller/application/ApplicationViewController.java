package cn.ac.iscas.cloudeploy.v2.controller.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "v2/views/applications")
@Transactional
public class ApplicationViewController {

	@RequestMapping(method = RequestMethod.GET)
	public String mainPage() {
		return "app/main";
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	public String listPage() {
		return "app/list";
	}

	@RequestMapping(value = { "/panel" }, method = RequestMethod.GET)
	public String panelPage() {
		return "app/panel";
	}
}
