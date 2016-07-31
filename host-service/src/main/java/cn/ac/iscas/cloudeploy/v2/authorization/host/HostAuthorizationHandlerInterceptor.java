package cn.ac.iscas.cloudeploy.v2.authorization.host;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import cn.ac.iscas.cloudeploy.v2.exception.UnauthorizedException;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.user.security.authorization.AuthorizationHandlerInterceptor;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

public class HostAuthorizationHandlerInterceptor extends
		AuthorizationHandlerInterceptor {

	@Autowired
	private HostService hostService;

	@Override
	public boolean checkResourcePermission(HttpServletRequest request,
			String[] resources) {
		String hostId = checkParameter(request,
				Constants.HTTP_REQUEST_PARAM_HOST_ID);
		if (!checkHostPermissionOfUser(Long.valueOf(hostId), currentUser())) {
			throw new UnauthorizedException("permission dennied");
		}
		return true;
	}

	private boolean checkHostPermissionOfUser(Long hostId, User user) {
		DHost host = hostService.findDHost(hostId);
		return (host != null && host.getUser().getId().equals(user.getId()));
	}

}
