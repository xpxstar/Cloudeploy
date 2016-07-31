package cn.ac.iscas.cloudeploy.v2.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.Task;
import cn.ac.iscas.cloudeploy.v2.model.service.task.TaskService;
import cn.ac.iscas.cloudeploy.v2.user.security.authorization.CheckPermission;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

@Controller
@Transactional
@RequestMapping("v2/tasks")
public class TaskController extends BasicController {
	@Autowired
	private TaskService taskService;
	
	/**
	 * @description get tasks or operations
	 * @param operation 1/0 if task is(not) an opration. 
	 * @return List<TaskViews.Item> pageView of taskes
	 * @author xpxstar@gmail.com
	 * 2015年11月11日 下午3:08:16
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<TaskViews.Item> getTasks(@RequestParam("operation")int operation) {
		return TaskViews.listViewOf(taskService.getTasksByUser(currentUser()
				.getId(),operation));
	}

	/**
	 * @description create a task
	 * @param taskGraph graph of the task
	 * @return TaskViews.Item
	 * @author xpxstar@gmail.com
	 * 2015年11月13日 下午3:40:41
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public TaskViews.Item createTask(@RequestBody TaskViews.Graph taskGraph) {
		return TaskViews.viewOf(taskService.createTaskFromGraph(taskGraph,
				currentUser()));
	}

	/**
	 * @description execute task
	 * @param taskId
	 * @return Object
	 * @author xpxstar@gmail.com
	 * 2015年11月13日 下午3:41:23
	 */
	@RequestMapping(value = { "/{" + Constants.HTTP_REQUEST_PARAM_TASK_ID + "}" }, method = RequestMethod.POST)
	@ResponseBody
	@CheckPermission(resources = { Task.PERMISSION_KEY })
	public Object executeTask(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_TASK_ID) Long taskId) {
		taskService.executeTask(taskId);
		return SUCCESS;
	}

	/**
	 * @description change a task 
	 * @param taskId
	 * @param taskGraph
	 * @return Object
	 * @author xpxstar@gmail.com
	 * 2015年11月13日 下午3:41:46
	 */
	@RequestMapping(value = { "/{" + Constants.HTTP_REQUEST_PARAM_TASK_ID + "}" }, method = RequestMethod.PUT)
	@ResponseBody
	@CheckPermission(resources = { Task.PERMISSION_KEY })
	public Object updateTask(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_TASK_ID) Long taskId,
			@RequestBody TaskViews.Graph taskGraph) {
		taskService.saveGraphToTask(taskGraph, taskId);
		return SUCCESS;
	}

	@RequestMapping(value = { "/{" + Constants.HTTP_REQUEST_PARAM_TASK_ID + "}" }, method = RequestMethod.DELETE)
	@ResponseBody
	@CheckPermission(resources = { Task.PERMISSION_KEY })
	public Object deleteTask(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_TASK_ID) Long taskId) {
		taskService.deleteTask(taskId);
		return SUCCESS;
	}

	@RequestMapping(value = { "/{" + Constants.HTTP_REQUEST_PARAM_TASK_ID + "}" }, method = RequestMethod.GET)
	@ResponseBody
	@CheckPermission(resources = { Task.PERMISSION_KEY })
	public TaskViews.Graph getTaskById(
			@PathVariable(Constants.HTTP_REQUEST_PARAM_TASK_ID) Long taskId) {
		return TaskViews.graphViewOf(taskService.getTaskById(taskId));
	}
}
