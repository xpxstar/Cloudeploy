package cn.ac.iscas.cloudeploy.v2.model.service.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.domain.DomainDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.domain.Domain;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.log.LogService;
import cn.ac.iscas.cloudeploy.v2.model.service.proxy.ProxyService;

@Service
public class DefaultDomainServiceImpl implements DomainService {
	private static final String DOCKER_CONF = "docker_component.properties";
	private static final String ACTION_DOMAIN_INSTALL = "domain.action.install";
	private static final String DOMAIN_SERVER = "domain.server.ip";

	@Autowired
	private DomainDAO domainDAO;

	@Autowired
	private ConfigService configService;

	@Autowired
	private ActionDAO actionDAO;

	@Autowired
	private ProxyService proxyService;

	@Autowired
	private HostService hostService;

	@Autowired
	private LogService logService;

	private String domainInstallAction;

	private String domainServer;

	@PostConstruct
	private void init() {
		domainInstallAction = configService.getConfigAsString(DOCKER_CONF,
				ACTION_DOMAIN_INSTALL);
		domainServer = configService.getConfigAsString(DOCKER_CONF,
				DOMAIN_SERVER);
	}

	@Override
	public List<Domain> getDomainList() {
		return (List<Domain>) domainDAO.findAll();
	}

	@Override
	public Domain bindDomain(String name, String ip) {
		Domain domain = new Domain(name, ip);
		Action domainAction = actionDAO.findByName(domainInstallAction);
		DHost host = hostService.findByName(domainServer);
		Map<String, String> params = new HashMap<>();
		params.put("ip", ip);
		params.put("domain", name);
		logService.printActionLog(domainAction, host, params);
		proxyService.applyActionOnHost(domainAction, params, host);
		return domainDAO.save(domain);
	}

	@Override
	public void unbindDomain(Long domainId) {
		domainDAO.delete(domainId);
	}
}
