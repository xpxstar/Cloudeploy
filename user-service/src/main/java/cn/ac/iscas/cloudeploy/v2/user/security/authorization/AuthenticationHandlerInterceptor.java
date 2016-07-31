package cn.ac.iscas.cloudeploy.v2.user.security.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cn.ac.iscas.cloudeploy.v2.exception.NotFoundException;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;
import cn.ac.iscas.cloudeploy.v2.user.security.SecurityUtils;
import cn.ac.iscas.cloudeploy.v2.user.security.Subject;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

public class AuthenticationHandlerInterceptor extends
		AnonymousHandlerInterceptor {
	private String headerToken = Constants.HTTP_HEADERS_TOKEN;

	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (super.preHandle(request, response, handler)) {
			return true;
		}
		String token = getToken(request);
		if (StringUtils.isEmpty(token)) {
			throw new NotFoundException("unknown user account");
		}
		User user = userService.findUserByToken(token);
		if (user == null) {
			throw new NotFoundException("unknown user account");
		}
		SecurityUtils.setSubject(new Subject(user));
		return true;
	}

	protected String getToken(HttpServletRequest httpRequest) {
		String content = httpRequest.getHeader(headerToken);
		if (StringUtils.isEmpty(content)) {
			return httpRequest.getParameter(headerToken);
		}
		return content;
	}
}
