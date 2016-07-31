package cn.ac.iscas.cloudeploy.v2.controller.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.ExecViews;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.ExecViews.ExecItem;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.exec.ExecService;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

@Controller
@Transactional
@RequestMapping(value = "v2/resources/execs")
public class ExecController extends BasicController {
	@Autowired
	private ExecService execService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ExecItem createExec(
			@RequestHeader(Constants.HTTP_HEADERS_TOKEN) String token,
			@RequestParam("name") String name,
			@RequestParam("content") String content) {
		return ExecViews.viewOf(execService.createExec(currentUser(), name,
				content));
	}

	@RequestMapping(value = { "executions" }, method = RequestMethod.POST)
	@ResponseBody
	public Object runExec(@RequestBody ExecViews.RunParams params) {
		Map<String, String> paramMap = new HashMap<>();
		for (ExecViews.ParamItem param : params.params) {
			paramMap.put(param.paramKey, param.paramValue);
		}
		execService.runExec(currentUser(), params.hostId, paramMap);
		return SUCCESS;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<ExecItem> getExecs(
			@RequestHeader(Constants.HTTP_HEADERS_TOKEN) String token) {
		return ExecViews.listViewOf(execService.findExecs(currentUser()));
	}

	@RequestMapping(value = { "/{execId}" }, method = RequestMethod.PUT)
	@ResponseBody
	public ExecItem updateExec(@PathVariable("execId") Long execId,
			@RequestParam("content") String content) {
		return ExecViews.viewOf(execService.updateExec(execId, content));
	}

	@RequestMapping(value = { "/{execId}" }, method = RequestMethod.DELETE)
	@ResponseBody
	public Object deleteExec(@PathVariable("execId") Long execId) {
		execService.removeExec(execId);
		return SUCCESS;
	}
}
