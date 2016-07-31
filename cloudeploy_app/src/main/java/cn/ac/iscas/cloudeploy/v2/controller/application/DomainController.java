package cn.ac.iscas.cloudeploy.v2.controller.application;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController.DResponseBuilder;
import cn.ac.iscas.cloudeploy.v2.dataview.domain.DomainView;
import cn.ac.iscas.cloudeploy.v2.model.service.domain.DomainService;

@Controller
@RequestMapping(value = "v2/domains")
@Transactional
public class DomainController {

	@Autowired
	private DomainService domainService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<DomainView.Item> getDomains() {
		return DomainView.viewListOf(domainService.getDomainList());
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public DomainView.Item addDomain(@RequestParam("name") String name,
			@RequestParam("ip") String ip) {
		return DomainView.viewOf(domainService.bindDomain(name, ip));
	}

	@RequestMapping(value = "/{domainId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object deleteDomain(@PathVariable("domainId") Long domainId) {
		domainService.unbindDomain(domainId);
		return DResponseBuilder.instance().add("result", "success").build();
	}
}
