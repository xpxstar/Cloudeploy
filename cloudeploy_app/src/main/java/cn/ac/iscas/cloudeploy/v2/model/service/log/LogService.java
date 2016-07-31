package cn.ac.iscas.cloudeploy.v2.model.service.log;

import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

public interface LogService {
	public void printActionLog(Action action, DHost host,Map<String,String> params);
}
