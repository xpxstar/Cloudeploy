package cn.ac.iscas.cloudeploy.v2.model.service.log;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

@Service
public class DefaultLogServiceImpl implements LogService {

	@Override
	public void printActionLog(Action action, DHost host,
			Map<String, String> params) {
		System.out.println("############doing action##########");
		System.out.println("host:" + host.getHostName());
		System.out.println("action name:" + action.getName());
		System.out.println("action params:");
		for (Entry<String, String> p : params.entrySet()) {
			System.out.println(p.getKey() + ":" + p.getValue());
		}
		System.out.println();
	}

}
