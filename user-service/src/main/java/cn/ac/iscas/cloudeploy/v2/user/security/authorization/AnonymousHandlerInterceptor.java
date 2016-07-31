package cn.ac.iscas.cloudeploy.v2.user.security.authorization;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AnonymousHandlerInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		Method method = ((HandlerMethod) handler).getMethod();
		Anonymous anonymous = method.getAnnotation(Anonymous.class);
		if (anonymous != null) {
			return true;
		}
		return false;
	}
}
