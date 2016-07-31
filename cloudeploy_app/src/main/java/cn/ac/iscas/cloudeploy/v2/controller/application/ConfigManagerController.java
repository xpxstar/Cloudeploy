package cn.ac.iscas.cloudeploy.v2.controller.application;

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
import cn.ac.iscas.cloudeploy.v2.dataview.application.PropertyView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.PropertyView.PropertyItem;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;
import cn.ac.iscas.cloudeploy.v2.model.service.config.RegisterCenterService;

@Controller
@Transactional
@RequestMapping(value = "v2/views/config")
public class ConfigManagerController extends BasicController{
	@Autowired
	private RegisterCenterService configService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String mainPage() {
		return "config/home";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "properties/{appId}/{containerId}")
	@ResponseBody
	public List<PropertyItem> listConfigs(@PathVariable("appId") Long appId, @PathVariable("containerId") Long containerId){
		List<PropertyElement> properties = configService.findProperties(appId, containerId, currentUser());
		return PropertyView.viewListOf(properties, appId, containerId);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "attributes/{appId}/{containerId}")
	@ResponseBody
	public List<PropertyItem> listAttributes(
			@PathVariable("appId") Long appId,
			@PathVariable("containerId") Long containerId){
		List<AttributeElement> attributes = configService.findAttributes(appId, containerId);
		return PropertyView.viewListOfArrtibutes(attributes, appId, containerId);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "properties/{appId}/{containerId}/{propertyName}")
	@ResponseBody
	public boolean updateProperty(@PathVariable("appId") Long appId, 
			@PathVariable("containerId") Long containerId, @PathVariable("propertyName") String propertyName
			, @RequestBody String propertyValue){
		return configService.updateProperty(appId, containerId, propertyName, propertyValue, currentUser());
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "attributes/{appId}/{containerId}")
	@ResponseBody
	public boolean updateAttribute(@PathVariable("appId") Long appId, 
			@PathVariable("containerId") Long containerId, @RequestParam("attributeName") String attributeName
			, @RequestBody String attributeValue){
		return configService.updateAttribute(appId, containerId, attributeName, attributeValue, currentUser());
	}
}
