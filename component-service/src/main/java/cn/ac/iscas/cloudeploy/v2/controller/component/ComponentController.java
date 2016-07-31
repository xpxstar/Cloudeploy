package cn.ac.iscas.cloudeploy.v2.controller.component;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.component.ComponentTypeViews;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.component.ComponentViews;
import cn.ac.iscas.cloudeploy.v2.model.service.ComponentService;
@Controller
@Transactional
@RequestMapping("v2/components")
public class ComponentController extends BasicController{
	@Autowired
	private ComponentService componentService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<ComponentViews.Item> getComponents(
			@RequestParam(value = "type", required = false, defaultValue = "") String type) {
		return ComponentViews.listViewOf(componentService
				.getComponentsByType(type));
	}

	@RequestMapping(value = { "/types" }, method = RequestMethod.GET)
	@ResponseBody
	public List<ComponentTypeViews.Item> getComponentTypes() {
		return ComponentTypeViews.listViewOf(componentService
				.getComponentTypes());
	}
	@RequestMapping(value = { "/custom" },method = RequestMethod.GET)
	@ResponseBody
	public List<ComponentViews.Item> getCustomComponents(
			@RequestParam(value = "type", required = false, defaultValue = "") String type) {
		return ComponentViews.listViewOf(componentService
				.getComponentsByUser(type,currentUser().getId()));
	}

	@RequestMapping(value = { "/types/custom" }, method = RequestMethod.GET)
	@ResponseBody
	public List<ComponentTypeViews.Item> getCustomComponentTypes() {
		return ComponentTypeViews.listViewOf(componentService
				.getComponentTypesByUser(currentUser().getId()));
	}
	/**
	 * @description add a component by user
	 * @param id
	 * @param name
	 * @param displayName
	 * @param typeId
	 * @param repeatable
	 * @param identifiers
	 * @return Long
	 * @author xpxstar@gmail.com
	 * 2015年11月11日 下午4:42:42
	 */
	@RequestMapping(value = { "/custom" },method = RequestMethod.POST)
	@ResponseBody
	public Long addComponent(@RequestParam(value = "id",defaultValue = "0" ) Long id,
			@RequestParam("name") String name,
			@RequestParam("displayName") String displayName,
			@RequestParam("typeId") Long typeId,
			@RequestParam(value = "repeatable" ,defaultValue = "false") boolean repeatable,
			@RequestParam(value = "identifiers",defaultValue = "") String identifiers
			) {
		return componentService.createComponent(id, name, displayName, typeId,currentUser(),repeatable,identifiers).getId();
	}
	/**
	 * @description  add a component by user
	 * @param id
	 * @param name
	 * @param displayName
	 * @return Long
	 * @author xpxstar@gmail.com
	 * 2015年11月11日 下午5:14:29
	 */
	@RequestMapping(value = { "/types/custom" }, method = RequestMethod.POST)
	@ResponseBody
	public Long addComponentType(@RequestParam("id") Long id,
			@RequestParam("name") String name,
			@RequestParam("displayName") String displayName) {
		return componentService.createComponentType(id, name, displayName, currentUser().getId()).getId();
	}
	
	@RequestMapping(value = { "/del" }, method = RequestMethod.POST)
	@ResponseBody
	public Object delComponent(@RequestParam("id") Long id) {
		currentUser().getId();
		try{
			componentService.delComponent(id);
		}catch(Exception e){
			return "ERROR";
		}
		return SUCCESS;
	}
	@RequestMapping(value = { "/types/del" }, method = RequestMethod.POST)
	@ResponseBody
	public void delComponentType(@RequestParam("id") Long id) {
		currentUser().getId();
		componentService.delComponentType(id);
	}
	
}
