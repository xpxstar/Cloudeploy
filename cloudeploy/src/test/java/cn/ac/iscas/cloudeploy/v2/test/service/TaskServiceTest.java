package cn.ac.iscas.cloudeploy.v2.test.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.ExecutableDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.NodeHostRelationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.NodeParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.TaskDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.TaskEdgeDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.TaskNodeDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.Executable;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.NodeHostRelation;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.NodeParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.Task;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskEdge;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskEdge.Relation;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskNode;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.ScriptService;
import cn.ac.iscas.cloudeploy.v2.model.service.task.TaskService;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {
	@Mock
	private ActionDAO actionDAO;

	@Mock
	private HostService hostService;

	@Mock
	private TaskDAO taskDAO;

	@Mock
	private TaskNodeDAO tNodeDAO;

	@Mock
	private TaskEdgeDAO tEdgeDAO;

	@Mock
	private NodeParamDAO paramDAO;

	@Mock
	private NodeHostRelationDAO relationDAO;

	@Mock
	private ExecutableDAO executableDAO;

	@InjectMocks
	private TaskService taskService;

	private TaskViews.Graph taskGraph;

	private DHost host1, host2;

	private Action action6, action8, action9;

	private List<DHost> hosts;
	private List<Action> actions;

	@Mock
	private ScriptService scriptService;

	private AtomicLong TASK_ID = new AtomicLong(1L),
			TASK_NODE_ID = new AtomicLong(1L),
			TASK_EDGE_ID = new AtomicLong(1L), NODE_PARAM_ID = new AtomicLong(
					1L), NODE_HOST_RELATION_ID = new AtomicLong(1L),
			EXECUTABLE_ID = new AtomicLong(1L);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		hosts = new ArrayList<>();
		actions = new ArrayList<>();
		taskGraph = new TaskViews.Graph();
		taskGraph.taskName = "test task";
		TaskViews.NodeBuilder nodeBuilder = new TaskViews.NodeBuilder();
		TaskViews.Node node1 = nodeBuilder
				.setId(1419905429269L)
				.setActionId(6L)
				.setComponentId(1L)
				.addHost(1L)
				.addParam(
						new TaskViews.ParamBuilder().setKey("catalina_base")
								.setValue("/opt/tomcat123/").build())
				.addParam(
						new TaskViews.ParamBuilder().setKey("source_url")
								.setValue("http://...").build()).build();
		nodeBuilder = new TaskViews.NodeBuilder();
		TaskViews.Node node2 = nodeBuilder
				.setId(1419905428518L)
				.setActionId(8L)
				.setComponentId(2L)
				.addHost(2L)
				.addParam(
						new TaskViews.ParamBuilder().setKey("root_password")
								.setValue("root").build()).build();
		nodeBuilder = new TaskViews.NodeBuilder();
		TaskViews.Node node3 = nodeBuilder.setId(1419905439278L)
				.setActionId(9L).setComponentId(3L).addHost(1L).build();
		taskGraph.nodes = Arrays.asList(node1, node2, node3);

		TaskViews.EdgeBuilder edgeBuilder = new TaskViews.EdgeBuilder();
		TaskViews.Edge edge1 = edgeBuilder.setFrom(1419905428518L)
				.setTo(1419905429269L).setRelation(Relation.BEFORE).build();
		edgeBuilder = new TaskViews.EdgeBuilder();
		TaskViews.Edge edge2 = edgeBuilder.setFrom(1419905439278L)
				.setTo(1419905429269L).setRelation(Relation.BEFORE).build();
		taskGraph.edges = Arrays.asList(edge1, edge2);

		host1 = new DHost();
		host1.setId(1L);
		host1.setHostName("host1");
		host2 = new DHost();
		host2.setId(2L);
		host2.setHostName("host2");
		hosts.addAll(Arrays.asList(host1, host2));

		action6 = new Action();
		action6.setId(6L);
		action6.setDefineFileKey("action6_file_key");
		action6.setName("action6");
		ActionParam param1 = new ActionParam(), param2 = new ActionParam();
		param1.setId(1L);
		param1.setParamKey("catalina_base");
		param1.setDefaultValue("/opt/tomcat123/");
		param1.setParamType("STRING");
		param1.setAction(action6);
		param2.setId(2L);
		param1.setParamKey("source_url");
		param1.setDefaultValue("http://...");
		param1.setParamType("STRING");
		param1.setAction(action6);
		action6.setParams(Arrays.asList(param1, param2));

		action8 = new Action();
		action8.setId(8L);
		action8.setDefineFileKey("action8_file_key");
		action8.setName("action8");
		ActionParam param3 = new ActionParam();
		param3.setId(3L);
		param3.setParamKey("root_password");
		param3.setDefaultValue("root");
		param3.setParamType("STRING");
		param3.setAction(action8);
		action8.setParams(Arrays.asList(param3));

		action9 = new Action();
		action9.setId(9L);
		action9.setDefineFileKey("action9_file_key");
		action9.setName("action9");
		actions.addAll(Arrays.asList(action6, action8, action9));

		when(taskDAO.save(Mockito.any(Task.class))).thenAnswer(
				new Answer<Task>() {
					@Override
					public Task answer(InvocationOnMock invocation)
							throws Throwable {
						Task task = invocation.getArgumentAt(0, Task.class);
						if (task.getId() == null) {
							task.setId(TASK_ID.getAndIncrement());
						}
						return task;
					}

				});
		when(hostService.findDHost(Mockito.anyLong())).thenAnswer(
				new Answer<DHost>() {

					@Override
					public DHost answer(InvocationOnMock invocation)
							throws Throwable {
						Long id = invocation.getArgumentAt(0, Long.class);
						for (DHost host : hosts) {
							if (host.getId().equals(id)) {
								return host;
							}
						}
						return null;
					}

				});
		when(actionDAO.findOne(Mockito.anyLong())).thenAnswer(
				new Answer<Action>() {

					@Override
					public Action answer(InvocationOnMock invocation)
							throws Throwable {
						Long id = invocation.getArgumentAt(0, Long.class);
						for (Action action : actions) {
							if (action.getId().equals(id)) {
								return action;
							}
						}
						return null;
					}

				});

		when(tNodeDAO.save(Mockito.any(TaskNode.class))).thenAnswer(
				new Answer<TaskNode>() {
					@Override
					public TaskNode answer(InvocationOnMock invocation)
							throws Throwable {
						TaskNode node = invocation.getArgumentAt(0,
								TaskNode.class);
						if (node.getId() == null) {
							node.setId(TASK_NODE_ID.getAndIncrement());
						}
						return node;
					}

				});

		when(tEdgeDAO.save(Mockito.any(TaskEdge.class))).thenAnswer(
				new Answer<TaskEdge>() {
					@Override
					public TaskEdge answer(InvocationOnMock invocation)
							throws Throwable {
						TaskEdge edge = invocation.getArgumentAt(0,
								TaskEdge.class);
						if (edge.getId() == null) {
							edge.setId(TASK_EDGE_ID.getAndIncrement());
						}
						return edge;
					}

				});
		when(paramDAO.save(Mockito.any(NodeParam.class))).thenAnswer(
				new Answer<NodeParam>() {
					@Override
					public NodeParam answer(InvocationOnMock invocation)
							throws Throwable {
						NodeParam param = invocation.getArgumentAt(0,
								NodeParam.class);
						if (param.getId() == null) {
							param.setId(NODE_PARAM_ID.getAndIncrement());
						}
						return param;
					}

				});
		when(relationDAO.save(Mockito.any(NodeHostRelation.class))).thenAnswer(
				new Answer<NodeHostRelation>() {
					@Override
					public NodeHostRelation answer(InvocationOnMock invocation)
							throws Throwable {
						NodeHostRelation relation = invocation.getArgumentAt(0,
								NodeHostRelation.class);
						if (relation.getId() == null) {
							relation.setId(NODE_HOST_RELATION_ID
									.getAndIncrement());
						}
						return relation;
					}

				});
		when(executableDAO.save(Mockito.any(Executable.class))).thenAnswer(
				new Answer<Executable>() {
					@Override
					public Executable answer(InvocationOnMock invocation)
							throws Throwable {
						Executable executable = invocation.getArgumentAt(0,
								Executable.class);
						if (executable.getId() == null) {
							executable.setId(EXECUTABLE_ID.getAndIncrement());
						}
						return executable;
					}

				});
	}

	@Test
	public void create_TaskFromGraph() {
		// when(
		// scriptService.createTask(
		// Matchers.<List<List<Operation>>> any(),
		// Mockito.anyString())).thenAnswer(new Answer<String>() {
		//
		// @Override
		// public String answer(InvocationOnMock invocation) throws Throwable {
		// return RandomUtils.randomString();
		// }
		//
		// });
		// User user = new User();
		// user.setId(1L);
		// Task task = taskService.createTaskFromGraph(taskGraph, user);
		// assertTrue(task.getNodes().size() == taskGraph.nodes.size());
		// assertTrue(task.getEdges().size() == taskGraph.edges.size());
		// assertTrue(task.getExecutables().size() == 2);
		//Task task = taskService.createTaskFromGraph(graph, user)
	}
}
