package cn.ac.iscas.cloudeploy.v2.user.security.authorization;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import cn.ac.iscas.cloudeploy.v2.exception.BadRequestException;
import cn.ac.iscas.cloudeploy.v2.exception.UnauthorizedException;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.user.security.SecurityUtils;

public abstract class AuthorizationHandlerInterceptor extends
		AnonymousHandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (super.preHandle(request, response, handler)) {
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		CheckPermission checkPermissionAnnotation = method
				.getAnnotation(CheckPermission.class);
		if (checkPermissionAnnotation != null) {
			if (!checkResourcePermission(request,
					checkPermissionAnnotation.resources())) {
				throw new UnauthorizedException("permission denied");
			}
		}
		return true;
	}

	public abstract boolean checkResourcePermission(HttpServletRequest request,
			String[] resources);

	protected String checkParameter(HttpServletRequest request, String name) {
		String value = getParamOrHeader(request, name);
		if (StringUtils.isEmpty(value)) {
			throw new BadRequestException("invalid parameter");
		}
		return value;
	}

	protected String getParamOrHeader(HttpServletRequest request, String name) {
		if (StringUtils.isEmpty(name)) {
			return StringUtils.EMPTY;
		}
		String value = request.getParameter(name);
		if (StringUtils.isEmpty(value)) {
			value = request.getHeader(name);
		}
		if (StringUtils.isEmpty(value)) {
			value = getPathVarible(request, name);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	protected String getPathVarible(HttpServletRequest request, String name) {
		if (StringUtils.isEmpty(name)) {
			return StringUtils.EMPTY;
		}
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String value = pathVariables.get(name);
		if (StringUtils.isEmpty(value)) {
			return StringUtils.EMPTY;
		}

		return value;
	}

	protected User currentUser() {
		if (SecurityUtils.getSubject() != null) {
			return SecurityUtils.getSubject().getUser();
		}
		return null;
	}
}
