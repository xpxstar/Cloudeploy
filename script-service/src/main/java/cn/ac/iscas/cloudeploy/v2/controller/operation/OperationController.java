package cn.ac.iscas.cloudeploy.v2.controller.operation;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController;
import cn.ac.iscas.cloudeploy.v2.model.service.script.service.OperationService;
@Controller
@Transactional
@RequestMapping("v2/operations")
public class OperationController extends BasicController{
	@Autowired
	private OperationService operationService;
	
	/**
	 * @description extract module to component and actions
	 * @param filekey
	 * @param componentId
	 * @return Long
	 * @author xpxstar@gmail.com
	 * 2015年11月12日 上午9:18:20
	 */
	@RequestMapping(value = { "/extract" },method = RequestMethod.POST)
	@ResponseBody
	public Long extractComponents(@RequestParam(value = "filekey") String filekey,
			@RequestParam("comid") Long componentId
			
			) {
		return operationService.extractComponent(filekey,componentId);
	}
	
}
