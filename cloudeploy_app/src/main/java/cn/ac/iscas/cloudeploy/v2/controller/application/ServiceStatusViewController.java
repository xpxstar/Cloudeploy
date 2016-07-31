package cn.ac.iscas.cloudeploy.v2.controller.application;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "v2/views/service")
@Transactional
public class ServiceStatusViewController {
	@Value("#{configs['consul_host']}")
	private String consul_ip;
	@Value("#{configs['consul_port']}")
	private String port;
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView mainPage() {
		Map<String , String> data = new HashMap<String, String>();
		data.put("url", "http://" + consul_ip + ":" +port + "/ui");
		return new ModelAndView("servicestatus/index",data);
	}
	@RequestMapping(value = "/status",method = RequestMethod.GET)
	@ResponseBody
	public void listServiceStatus(){
		
	}
}
