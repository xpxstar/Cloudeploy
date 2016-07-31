package cn.ac.iscas.cloudeploy.v2.model.service.proxy;

import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

public interface ProxyService {
	public void applyExecutable(DHost host, String executableKey);

	public void applyActionOnHost(Action action, Map<String, String> params,
			DHost host);
}
