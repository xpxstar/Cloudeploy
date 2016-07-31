package cn.ac.iscas.cloudeploy.v2.authorization.task;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import cn.ac.iscas.cloudeploy.v2.exception.UnauthorizedException;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.Task;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.task.TaskService;
import cn.ac.iscas.cloudeploy.v2.user.security.authorization.AuthorizationHandlerInterceptor;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

public class TaskAuthorizationHandlerInterceptor extends
		AuthorizationHandlerInterceptor {

	@Autowired 
	private TaskService taskService;

	@Override
	public boolean checkResourcePermission(HttpServletRequest request,
			String[] resources) {
		String taskId = checkParameter(request,
				Constants.HTTP_REQUEST_PARAM_TASK_ID);
		if (!checkHostPermissionOfUser(Long.valueOf(taskId), currentUser())) {
			throw new UnauthorizedException("permission dennied");
		}
		return true;
	}

	private boolean checkHostPermissionOfUser(Long taskId, User user) {
		Task task = taskService.getTaskById(taskId);
		return (task != null && task.getUser().getId().equals(user.getId()));
	}

}
