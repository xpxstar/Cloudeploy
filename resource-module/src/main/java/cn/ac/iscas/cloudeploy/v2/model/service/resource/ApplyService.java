package cn.ac.iscas.cloudeploy.v2.model.service.resource;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.service.proxy.ProxyService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.ScriptService;

@Service
public class ApplyService {

	@Autowired
	@Qualifier("edgeTypedGraphScriptService")
	private ScriptService scriptService;

	@Autowired
	private ProxyService proxyService;

	public void applyActionOnHost(Action action, Map<String, String> params,
			DHost host) {
		proxyService.applyActionOnHost(action, params, host);
	}
}
