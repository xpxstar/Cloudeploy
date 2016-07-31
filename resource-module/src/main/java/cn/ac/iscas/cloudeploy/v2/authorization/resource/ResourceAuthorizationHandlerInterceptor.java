package cn.ac.iscas.cloudeploy.v2.authorization.resource;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.software.SoftwareService;
import cn.ac.iscas.cloudeploy.v2.user.security.authorization.AuthorizationHandlerInterceptor;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

public class ResourceAuthorizationHandlerInterceptor extends
		AuthorizationHandlerInterceptor {

	@Autowired
	private HostService hostService;

	@Autowired
	private SoftwareService softwareService;

	@Override
	public boolean checkResourcePermission(HttpServletRequest request,
			String[] resources) {
		for (String resource : resources) {
			if (!checkResourcePermission(request, resource)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkResourcePermission(HttpServletRequest request,
			String resource) {
		switch (resource) {
		case DHost.PERMISSION_KEY:
			String hostId = checkParameter(request,
					Constants.HTTP_REQUEST_PARAM_HOST_ID);
			return checkHostPermissionOfUser(Long.valueOf(hostId),
					currentUser());
		case SoftwareInstance.PERMISSION_KEY:
			String instanceId = checkParameter(request,
					Constants.HTTP_REQUEST_PARAM_INSTANCE_ID);
			return checkInstancePermissionOfUser(Long.valueOf(instanceId),
					currentUser());
		default:
			return false;
		}
	}

	private boolean checkHostPermissionOfUser(Long hostId, User user) {
		DHost host = hostService.findDHost(hostId);
		return (host != null && host.getUser().getId().equals(user.getId()));
	}

	private boolean checkInstancePermissionOfUser(Long instanceId, User user) {
		SoftwareInstance instance = softwareService.findInstance(instanceId);
		return (instance != null && instance.getHost().getUser().getId()
				.equals(user.getId()));
	}

}
