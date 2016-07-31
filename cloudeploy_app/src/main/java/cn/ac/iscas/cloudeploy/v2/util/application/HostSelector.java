package cn.ac.iscas.cloudeploy.v2.util.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;

@Component
public class HostSelector {
	@Autowired
	private HostService hostService;
	@Autowired
	private UserService userService;
	
	private int i = 1;
	
	public DHost getHost(){
//		User user = userService.getCurrentUser();
//		List<DHost> hosts = hostService.findDHostsByUser(user.getId());
//		int randHost = (int) (Math.random() * hosts.size());
//		return hosts.get(randHost);
//		int[] arrs = new int[]{11};
//		int index = (int) ((i++) % 3);
//		System.out.println("select host:" + index);
//		return hostService.findDHost((long) arrs[index]);
		return hostService.findDHost(16L);
	}
}
