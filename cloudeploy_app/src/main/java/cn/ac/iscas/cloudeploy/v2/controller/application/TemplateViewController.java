package cn.ac.iscas.cloudeploy.v2.controller.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "v2/views/templates")
@Transactional
public class TemplateViewController {

	@RequestMapping(method = RequestMethod.GET)
	public String mainPage() {
		return "tempt/tempt";
	}
	
	@RequestMapping(value = { "/files" }, method = RequestMethod.GET)
	public String customFileListView() {
		return "tempt/custom_file_list";
	}
}
