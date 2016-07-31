package cn.ac.iscas.cloudeploy.v2.model.service.task;

import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.internal.stubbing.answers.ReturnsElementsOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.Edge;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.Node;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.Param;
import cn.ac.iscas.cloudeploy.v2.exception.BadRequestException;
import cn.ac.iscas.cloudeploy.v2.exception.ConflictException;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.ExecutableDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.NodeHostRelationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.NodeParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.TaskDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.TaskEdgeDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.task.TaskNodeDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ImportAction;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.Executable;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.NodeHostRelation;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.NodeParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.Task;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskEdge;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskEdge.Relation;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskNode;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.proxy.ProxyService;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.ResourceService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.ScriptService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;
import cn.ac.iscas.cloudeploy.v2.util.DateUtils;
import cn.ac.iscas.cloudeploy.v2.util.DeployUtils;
import cn.ac.iscas.cloudeploy.v2.util.RandomUtils;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.SuperNode;

@Service
public class TaskService {
	@Autowired
	private ActionDAO actionDAO;

	@Autowired
	private TaskDAO taskDAO;

	@Autowired
	private TaskNodeDAO tNodeDAO;

	@Autowired
	private TaskEdgeDAO tEdgeDAO;

	@Autowired
	private NodeParamDAO paramDAO;

	@Autowired
	private NodeHostRelationDAO relationDAO;

	@Autowired
	private ExecutableDAO executableDAO;

	@Autowired
	private XMLService xmlService;

	@Autowired
	@Qualifier("edgeTypedGraphScriptService")
	private ScriptService scriptService;

	@Autowired
	private ProxyService proxyService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private HostService hostService;

	public List<Task> getTasksByUser(Long userId,int operation) {
		return (List<Task>) taskDAO.findByUser(userId,operation);
	}

	public Task getTaskById(Long taskId) {
		return taskDAO.findOne(taskId);
	}

	/**
	 * create a task from graph
	 * @param graph
	 * @param user the task belongs
	 * @return
	 */
	public Task createTaskFromGraph(TaskViews.Graph graph, User user) {
		/*save a task to get the taskId first
		 *saveTask as object to be store in database
		 *executeTask as object to be generate script
		 */
		Task task = new Task();
		task.setName(graph.taskName);
		task.setUser(user);
		task.setOperation(graph.operation);
		//task.setXmlFileKey(xmlService.taskToXML(graph));
		
		taskDAO.save(task);
		return mapGraphToTask(task, graph);
	}
	/**
	 * save graph to an existed Task
	 * 
	 * @param graph
	 * @param taskId
	 * @return
	 */
	public Task saveGraphToTask(TaskViews.Graph graph, Long taskId) {
		Task task = taskDAO.findOne(taskId);
		clearTask(task);
		//convert a graph to a task
		mapGraphToTask(task, graph);
		//task.setXmlFileKey(xmlService.taskToXML(graph));
		taskDAO.save(task);
		return task;
	}

	/**
	 * @description match graph to task for analyse or store
	 * @param task
	 * @param graph
	 * @return Task
	 * @author changed by  xpxstar@gmail.com
	 * 2015年11月11日 下午3:25:00
	 */
	private Task mapGraphToTask(Task task, TaskViews.Graph graph) {
		// save the taskNodes and taskEdges
		List<TaskNode> nodes = saveGraphNodesToTask(graph.nodes, task);
		saveGraphEdgesToTask(graph.edges, task, nodes);
		//to generate script for execution if it is not an operation
		if (0 == task.getOperation()) {
			if (1 == graph.complex) {
				//expand task that has operation
				expandTask(task);
			}
			//generate script for execution
			setExecutablesToTask(task);
		}
		return task;
	}

	/**
	 * execute a task
	 * 
	 * @param taskId
	 * @throws FileNotFoundException
	 */
	public void executeTask(Long taskId) {
		Task task = taskDAO.findOne(taskId);
		List<Executable> executables = task.getExecutables();
		//execute on different hosts.
		for (Executable exec : executables) {
			proxyService.applyExecutable(exec.getHost(),
					exec.getExecutableFileKey());
		}
		task.setExecutedTime(DateUtils.current());
		// convert the task nodes to resources(included in resource module)
		//expandTask to save application states on the node
		expandTask(task);
		convertTaskNodesToResources(task);
	}

	/**
	 * delete a task
	 * 
	 * @param taskId
	 */
	public void deleteTask(Long taskId) {
		Task task = taskDAO.findOne(taskId);
		clearTask(task);
		taskDAO.delete(task);
	}

	/**
	 * remove all edges, nodes and executables of a task
	 * 
	 * @param task
	 * @return
	 */
	private Task clearTask(Task task) {
		tEdgeDAO.delete(task.getEdges());
		List<TaskNode> nodes = task.getNodes();
		for (TaskNode node : nodes) {
			paramDAO.delete(node.getParams());
			relationDAO.delete(node.getHostInfo());
		}
		tNodeDAO.delete(nodes);
		executableDAO.delete(task.getExecutables());
		return task;
	}

	/**
	 * @description change nodes of graph to nodes of task
	 * @param nodes
	 * @param task
	 * @return List<TaskNode>
	 * @author changed by xpxstar@gmail.com
	 * 2015年11月11日 下午3:34:23
	 */
	private List<TaskNode> saveGraphNodesToTask(List<TaskViews.Node> nodes,
			Task task) {
		List<TaskNode> taskNodes = tNodeDAO
				.findByIdentifiers(identifiersOfNodes(nodes));
		if (taskNodes.size() > 0) {
			throw new ConflictException("duplicate node id");
		}
		taskNodes = new ArrayList<>();
		Action action = null;
		Task oper =null;
		for (Node node : nodes) {
			TaskNode tNode = new TaskNode();
			//if taske is operation then it should not bind hosts.
			if (1 != task.getOperation()) {
				List<DHost> hosts = hostService.findDHostsByIdsAndUser(
				node.hostIds, task.getUser().getId());
				if (hosts.size() == 0) {
					throw new BadRequestException("invalid host ids");
				}
				List<NodeHostRelation> relations = new ArrayList<>();
				for (DHost host : hosts) {
					NodeHostRelation relation = new NodeHostRelation();
					relation.setHost(host);
					relation.setTaskNode(tNode);
					relationDAO.save(relation);
					relations.add(relation);
				}
				tNode.setHostInfo(relations);
			}
			//test if it is an operationNode
			if (0 != node.actionId) {
				oper = null;
				action = actionDAO.findOne(node.actionId);
			}else if (0 != node.operation) {
				action = null;
				oper = taskDAO.findOne(node.operation);
			}
			
			
			tNode.setOperation(oper);
			//action = actionDAO.findOne(node.actionId);
			tNode.setAction(action);
			// use the node id as the identifier
			tNode.setIdentifier(String.valueOf(node.id));
			tNode.setTask(task);
			tNode.setxPos(node.xPos);
			tNode.setyPos(node.yPos);
			tNodeDAO.save(tNode);
			taskNodes.add(tNode);
			List<NodeParam> nParams = new ArrayList<>();
			for (Param p : node.params) {
				NodeParam param = new NodeParam();
				param.setParamKey(p.key);
				param.setParamValue(p.value);
				param.setTaskNode(tNode);
				paramDAO.save(param);
				nParams.add(param);
			}
				tNode.setParams(nParams);
		}
		task.setNodes(taskNodes);
		return taskNodes;
	}

	/**
	 * @param nodes
	 * @return 节点id的列表
	 */
	private List<String> identifiersOfNodes(List<TaskViews.Node> nodes) {
		List<String> identifiers = new ArrayList<>();
		for (TaskViews.Node node : nodes) {
			identifiers.add(String.valueOf(node.id));
		}
		return identifiers;
	}

	/**
	 * @description change edges of graph to edges of task
	 * @param edges
	 * @param task
	 * @param nodes
	 * @return List<TaskEdge>
	 * @author changed by xpxstar@gmail.com
	 * 2015年11月11日 下午3:37:07
	 */
	private List<TaskEdge> saveGraphEdgesToTask(List<TaskViews.Edge> edges,
			Task task, List<TaskNode> nodes) {
		List<TaskEdge> taskEdges = new ArrayList<>();
		for (Edge edge : edges) {
			Long from = edge.from, to = edge.to;
			if (from == null || to == null) {
				throw new InvalidParameterException("invalid operation graph");
			}
			TaskNode fromNode = null, toNode = null;
			for (TaskNode n : nodes) {
				if (n.getIdentifier().equals(from.toString())) {
					fromNode = n;
				}
				if (n.getIdentifier().equals(to.toString())) {
					toNode = n;
				}
			}
			if (fromNode == null || toNode == null) {
				throw new InvalidParameterException("invalid operation graph");
			}
			TaskEdge tEdge = new TaskEdge();
			tEdge.setFrom(fromNode);
			tEdge.setTo(toNode);
			tEdge.setRelation(edge.relation);
			tEdge.setTask(task);
			tEdgeDAO.save(tEdge);
			taskEdges.add(tEdge);
		}
		task.setEdges(taskEdges);
		return taskEdges;
	}

	/*******************************gy修改：注释*******************************/
	/**
	private List<Executable> setExecutablesToTask(Task task) {
		Map<Long, Graph<Operation, EdgeType>> graphOnHost = decomposeTaskToGraphs(task);

		List<Executable> res = new ArrayList<Executable>();
		for (Entry<Long, Graph<Operation, EdgeType>> entry : graphOnHost
				.entrySet()) {
			DHost host = hostService.findDHost(entry.getKey());
			String taskScript = scriptService.createTask(
					graphOnHost.get(entry.getKey()), host.getHostIP());
			Executable executable = new Executable();
			executable.setExecutableFileKey(taskScript);
			executable.setHost(host);
			executable.setTask(task);
			executableDAO.save(executable);
			res.add(executable);
		}
		task.setExecutables(res);
		return res;
	}
	**/
	/********************************gy修改：注释******************************/
	/******************************gy修改：新setExecutablesToTask********************************/
	//*
	private List<Executable> setExecutablesToTask(Task task) {
		Map<String, Graph<Operation, EdgeType>> graphOnHost = decomposeTaskToGraphs(task);

		List<Executable> res = new ArrayList<Executable>();
		for (Entry<String, Graph<Operation, EdgeType>> entry : graphOnHost
				.entrySet()) {
			String[] str = entry.getKey().split(":");
			Long key = new Long(str[1]);
			DHost host = hostService.findDHost(key);
			String taskScript = scriptService.createTask(
					graphOnHost.get(entry.getKey()), host.getHostIP());
			Executable executable = new Executable();
			executable.setExecutableFileKey(taskScript);
			executable.setHost(host);
			executable.setTask(task);
			executableDAO.save(executable);
			res.add(executable);
		}
		task.setExecutables(res);
		return res;
	}
	//*/
	/*********************************gy修改：新setExecutablesToTask*****************************/
	

	/**
	 * decompose the task to different graphs with each host a graph
	 * 
	 * @param task
	 * @return
	 */
/*****************************gy修改：注释**************************************/
	/**********高强师兄的旧的
	private Map<Long, Graph<Operation, EdgeType>> decomposeTaskToGraphs(
			Task task) {
		// use the map to cache the operation graph on each host
		Map<Long, Graph<Operation, EdgeType>> graphOnHost = new HashMap<>();

		// add vertexes
		for (TaskNode node : task.getNodes()) {
			for (NodeHostRelation hostInfo : node.getHostInfo()) {
				DHost host = hostInfo.getHost();
				Graph<Operation, EdgeType> graph = null;
				if (graphOnHost.containsKey(host.getId())) {
					graph = graphOnHost.get(host.getId());
				} else {
					graph = new Graph<>();
					graphOnHost.put(host.getId(), graph);
				}
				addVertexToGraph(node, graph);
			}
		}

		// add edges
		for (TaskEdge edge : task.getEdges()) {
			for (DHost host : findCommonHostsOfNodes(edge.getFrom(),
					edge.getTo())) {
				addEdgeToGraph(edge, graphOnHost.get(host.getId()));
			}
		}

		return graphOnHost;
	}
	**********高强师兄的旧的/
	/*****************************gy修改：注释**************************************/
	/*****************************gy修改：新decomposeTaskToGraphs**************************************/
	///*
	/**
	 * decompose the task to different graphs with each host a graph
	 * 
	 * @param task
	 * @return
	 */
	private Map<String, Graph<Operation, EdgeType>> decomposeTaskToGraphs(
			Task task) {
		// use the map to cache the operation graph on each super node
		//修改hashmap成treeMap
		Map<String, Graph<Operation, EdgeType>> graphOnHost = new TreeMap<>();
		
		TaskAlgorithmService mergeService = new TaskAlgorithmService();
		Task nTask = mergeService.taskNodeDuplication(task);  //取得有节点有孩子且只有一个目标主机的新任务
		List<SuperNode> superNodes = mergeService.getSuperNodes(nTask);

		// add vertexes
		for (SuperNode superNode:superNodes) {
			Graph<Operation, EdgeType> graph = null;
			String key = superNode.getId();
			if (graphOnHost.containsKey(key)){
				graph = graphOnHost.get(key);
			} else {
				graph = new Graph<>();
				graphOnHost.put(key, graph);
			}
			for(TaskNode node:superNode.getSubNodes()){
				addVertexToGraph(node, graph);
			}
		}

		// add edges
		for (TaskEdge edge : nTask.getEdges()) {
			for(SuperNode supNode:superNodes){
				if(supNode.getSubNodes().contains(edge.getFrom())
						&&supNode.getSubNodes().contains(edge.getTo())){
					addEdgeToGraph(edge, graphOnHost.get(supNode.getId()));
				}
			}
		}

		return graphOnHost;
	}
	//*/
	/*****************************gy修改：新decomposeTaskToGraphs**************************************/

	/**
	 * get the imported operations recursively
	 * 
	 * @param action
	 * @return
	 */
	private List<Operation> getImports(Action action) {
		List<Operation> imports = new ArrayList<>();
		List<ImportAction> localImports = action.getImports();
		if (localImports != null) {
			for (ImportAction impAction : localImports) {
				imports.add(operationFromAction(impAction.getImportedAction()));
			}
		}
		return imports;
	}

	/**
	 * generate an operation object from an action
	 * 
	 * @param action
	 * @return
	 */
	private Operation operationFromAction(Action action) {
		return operationFromAction(action, RandomUtils.randomString());
	}

	/**
	 * generate an operation object from an action with a given node name
	 * 
	 * @param action
	 * @return
	 */
	private Operation operationFromAction(Action action, String nodeName) {
		Operation operation = new Operation();
		operation.setName(action.getName());
		operation.setNodeName(nodeName);
		operation.setParamsWithType(operationParamsOfAction(action));
		operation.setDefineMd5(action.getDefineFileKey());
		operation.setImports(getImports(action));
		return operation;
	}

	/**
	 * generate the operation parameter map from the default parameters of an
	 * action
	 * 
	 * @param action
	 * @return
	 */
	private Map<String, ParamValue> operationParamsOfAction(Action action) {
		Map<String, ParamValue> params = new HashMap<>();
		for (ActionParam p : action.getParams()) {
			ParamValue value = new ParamValue(p.getDefaultValue(),
					ParamType.typeByString(p.getParamType()));
			params.put(p.getParamKey(), value);
		}
		return params;
	}
/******************************************gy修改：注释********************************************/
	/**
	 * find the host intersection of two nodes
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	/*
	private List<DHost> findCommonHostsOfNodes(TaskNode node1, TaskNode node2) {
		List<DHost> commonHosts = new ArrayList<>();
		for (NodeHostRelation r1 : node1.getHostInfo()) {
			for (NodeHostRelation r2 : node2.getHostInfo()) {
				if (r1.getHost().getId().equals(r2.getHost().getId())) {
					commonHosts.add(r1.getHost());
				}
			}
		}
		return commonHosts;
	}
	*/
	/******************************************gy修改：注释********************************************/
	/**
	 * add an corresponding vertext to the graph according to the task node
	 * 
	 * @param node
	 * @param graph
	 */
	private void addVertexToGraph(TaskNode node,
			Graph<Operation, EdgeType> graph) {
		
		Action action = node.getAction();
		// add operation to the corresponding graph
		Operation operation = operationFromAction(action, node.getIdentifier());
		for (NodeParam np : node.getParams()) {
			operation.setValueOfParams(np.getParamKey(), np.getParamValue());
		}
		graph.addVertex(operation);
	}

	/**
	 * map a task edge relation to a graph edge type
	 * 
	 * @param relation
	 * @return
	 */
	private EdgeType getEdgeType(TaskEdge.Relation relation) {
		switch (relation) {
		case BEFORE:
			return EdgeType.REQUIRE;
		case LISTEN:
			return EdgeType.NOTIFY;
		default:
			return EdgeType.REQUIRE;
		}
	}

	/**
	 * add an corresponding edge to the graph according to the task edge
	 * 
	 * @param edge
	 * @param graph
	 */
	private void addEdgeToGraph(TaskEdge edge, Graph<Operation, EdgeType> graph) {
		TaskNode fromNode = edge.getFrom(), toNode = edge.getTo();
		Operation sourceVertex = null, targetVertex = null;
		/**
		 * the from_node of the "before" edge in the task is the target vertex
		 * of the "require" edge in the operation graph, and the to_node of
		 * "before" is the source vertex of "require"; the from_node of the
		 * "listen" edge in the task is the target vertex of the "notify" edge
		 * in the operation graph, and the to_node of "listen" is the source
		 * vertex of "notify"
		 */
		for (Operation operation : graph.getAllVertexEdges().keySet()) {
			if (sourceVertex != null && targetVertex != null) {
				break;
			}
			if (operation.getNodeName().equals(toNode.getIdentifier())) {
				sourceVertex = operation;
				continue;
			}
			if (operation.getNodeName().equals(fromNode.getIdentifier())) {
				targetVertex = operation;
				continue;
			}
		}
		EdgeType edgeType = getEdgeType(edge.getRelation());
		graph.addEdge(sourceVertex, targetVertex, edgeType);
	}

	/**
	 * 
	 * store the operation of node to keeps status consistent between database and real system. 
	 * @param taskNodes
	 */
	private void convertTaskNodesToResources(Task task) {
		// get the in degree map (id, list(id))
		Map<Long, List<Long>> inDegree = new HashMap<>();
		List<TaskEdge> taskEdges = task.getEdges();
		for (TaskEdge edge : taskEdges) {
			if (edge.getRelation().equals(Relation.BEFORE)) {
				Long from = edge.getFrom().getId();
				Long to = edge.getTo().getId();
				if (!inDegree.containsKey(to)) {
					inDegree.put(to, new ArrayList<Long>());
				}
				inDegree.get(to).add(from);
			}
		}

		// get the node map (id, node) and node visited map(id, boolean)
		Map<Long, TaskNode> nodeMap = new HashMap<>();
		Map<Long, Boolean> visited = new HashMap<>();
		List<TaskNode> taskNodes = task.getNodes();
		for (TaskNode node : taskNodes) {
			nodeMap.put(node.getId(), node);
			visited.put(node.getId(), false);
			if (!inDegree.containsKey(node.getId())) {
				inDegree.put(node.getId(), ImmutableList.<Long> of());
			}
		}

		for (TaskNode taskNode : taskNodes) {
			convertTaskNodeToResource(taskNode, nodeMap, visited, inDegree);
		}
	}

	private void convertTaskNodeToResource(TaskNode currentNode,
			Map<Long, TaskNode> nodeMap, Map<Long, Boolean> visited,
			Map<Long, List<Long>> inDegree) {
		if (!visited.get(currentNode.getId())) {
			for (Long precursor : inDegree.get(currentNode.getId())) {
				convertTaskNodeToResource(nodeMap.get(precursor), nodeMap,
						visited, inDegree);
			}
			String componentType = currentNode.getAction().getComponent()
					.getType().getName();
			String actionType = DeployUtils.getLastSegmentOfName(currentNode
					.getAction().getName());
			if (componentType.startsWith(configService
					.getConfigAsString(ConfigKeys.COMPONENT_TYPE_SOFTWARE))) {
				if (actionType.equals(configService
						.getConfigAsString(ConfigKeys.ACTION_NAME_INSTALL))) {
					createSoftwareInstance(currentNode);
				} else if (actionType.equals(configService
						.getConfigAsString(ConfigKeys.ACTION_NAME_DELETE))) {
					removeSoftwareInstance(currentNode);
				} else if (actionType.equals(configService
						.getConfigAsString(ConfigKeys.ACTION_NAME_CONFIG))) {
					configSoftwareInstance(currentNode);
				} else if (actionType.equals(configService
						.getConfigAsString(ConfigKeys.ACTION_NAME_SERVICE))) {
					changeSoftwareInstanceStatus(currentNode);
				}
			}
			visited.put(currentNode.getId(), true);
		}
	}

	private void createSoftwareInstance(TaskNode taskNode) {
		resourceService.createSoftwareInstance(taskNode.getAction(),
				hostListOfTaskNode(taskNode), paramsMapOfTaskNode(taskNode));
	}

	private Map<String, String> paramsMapOfTaskNode(TaskNode taskNode) {
		Map<String, String> params = new HashMap<>();
		for (NodeParam param : taskNode.getParams()) {
			params.put(param.getParamKey(), param.getParamValue());
		}
		return params;
	}

	private List<DHost> hostListOfTaskNode(TaskNode taskNode) {
		List<DHost> hosts = new ArrayList<>();
		for (NodeHostRelation hostInfo : taskNode.getHostInfo()) {
			hosts.add(hostInfo.getHost());
		}
		return hosts;
	}

	private Map<String, String> identifiersOfTaskNode(TaskNode taskNode) {
		Map<String, String> identifiers = new HashMap<>();
		Component component = taskNode.getAction().getComponent();
		String[] identifierKeys = StringUtils.split(component.getIdentifiers(),
				Component.IDENTIFIER_SEPARATOR);
		Map<String, String> params = paramsMapOfTaskNode(taskNode);
		if (component.isRepeatable() && identifierKeys != null) {
			for (String key : identifierKeys) {
				if (!params.containsKey(key)) {
					throw new BadRequestException(
							"identifiers of instance not specified");
				}
				identifiers.put(key, params.get(key));
			}
		}
		return identifiers;
	}

	private void configSoftwareInstance(TaskNode taskNode) {
		resourceService.configSoftwareInstance(taskNode.getAction(),
				hostListOfTaskNode(taskNode), identifiersOfTaskNode(taskNode),
				paramsMapOfTaskNode(taskNode));
	}

	private void removeSoftwareInstance(TaskNode taskNode) {
		resourceService.removeSoftwareInstance(taskNode.getAction(),
				hostListOfTaskNode(taskNode), identifiersOfTaskNode(taskNode));
	}

	private void changeSoftwareInstanceStatus(TaskNode taskNode) {
		resourceService.changeSoftwareInstanceStatus(taskNode.getAction(),
				hostListOfTaskNode(taskNode), identifiersOfTaskNode(taskNode),
				targetInstanceStatusOfTaskNode(taskNode));
	}

	private String targetInstanceStatusOfTaskNode(TaskNode taskNode) {
		Component component = taskNode.getAction().getComponent();
		// get the software name to get the service parameters from the
		// configuration
		String serviceKey = DeployUtils.getLastSegmentOfName(component
				.getName());
		if (StringUtils.isEmpty(serviceKey)) {
			throw new BadRequestException(
					"invalid parameters for service operation");
		}
		JSONObject config = configService
				.getServiceConfigAsJSONObject(serviceKey);
		if (isNodeParamsEqualWithServiceParams(taskNode.getParams(),
				config.getJSONArray(ConfigKeys.SERVICE_START_PARAMS))) {
			return SoftwareInstance.InstanceStatus.RUNNING.name();
		} else if (isNodeParamsEqualWithServiceParams(taskNode.getParams(),
				config.getJSONArray(ConfigKeys.SERVICE_STOP_PARAMS))) {
			return SoftwareInstance.InstanceStatus.STOPPED.name();
		}
		return null;
	}

	/**
	 * decide is the taskNode a service start one or a service stop one
	 * 
	 * @param nodeParams
	 * @param serviceParams
	 * @return
	 */
	private boolean isNodeParamsEqualWithServiceParams(
			List<NodeParam> nodeParams, JSONArray serviceParams) {
		boolean res = true;
		for (int i = 0; i < serviceParams.length(); i++) {
			JSONObject param = serviceParams.getJSONObject(i);
			String key = param.getString(ConfigKeys.PARAM_KEY), value = String
					.valueOf(param.get(ConfigKeys.PARAM_VALUE));
			boolean isParamEqual = false;
			for (NodeParam nodeParam : nodeParams) {
				if (nodeParam.getParamKey().equals(key)
						&& nodeParam.getParamValue().equals(value)) {
					isParamEqual = true;
					break;
				}
			}
			res &= isParamEqual;
		}
		return res;
	}
	
	private static class TaskAlgorithmService{
		/**
		 * 根据一个原始任务返回它的超级节点表
		 * 
		 * @param Task
		 *            有孩子节点的且节点只有一个目标主机的转换后的新任务
		 * @return
		 */
		public List<SuperNode> getSuperNodes(Task task){
			return mergeNodes(getTopoloy(task));     
		}
		
		/**
		 * 把有多个目标主机的节点分裂为多个具有单一目标主机的节点，并为每个节点添加孩子，返回具有新特性的任务
		 * 
		 * @param Task
		 *            从图得到的任务,节点没有自孩子，可能有多个目标主机
		 * @return
		 */
		public Task taskNodeDuplication(Task task){
	        Task nTask = (Task)task.clone();//克隆新任务
			
			nTask.setNodes(new ArrayList());
			for(TaskNode node:task.getNodes()){
				TaskNode nNode = (TaskNode)node.clone();
				nTask.getNodes().add(nNode);
			}
			nTask.setEdges(new ArrayList());
			for(TaskEdge edge:task.getEdges()){
				TaskEdge nEdge = (TaskEdge)edge.clone();
				nTask.getEdges().add(nEdge);
			}
			
			List<TaskNode> nodeList = new ArrayList<>();
			nodeList.addAll(nTask.getNodes());
			nTask.getNodes().clear();//旧节点清空
			List<TaskEdge> edgeList = new ArrayList<>();
			edgeList.addAll(nTask.getEdges());
			nTask.getEdges().clear();//旧边清空
			for(TaskEdge edge:edgeList){
				TaskNode fromNode = edge.getFrom();
				int fromHostCount = fromNode.getHostInfo().size();
				List<TaskEdge> tmpEdges = new ArrayList<>();
				if(fromHostCount > 1){//若头结点的目标主机数大于1则做节点分裂
					for(int i = 0;i < fromHostCount;i++){
						TaskNode tmpNode = (TaskNode)fromNode.clone();
						TaskEdge tmpEdge = (TaskEdge)edge.clone();
						List<NodeHostRelation> hostInfo = new ArrayList<>();
						hostInfo.addAll(fromNode.getHostInfo());
						tmpNode.setHostInfo(new ArrayList());
						tmpNode.getHostInfo().add((hostInfo.get(i)));
						if(!nTask.getNodes().contains(tmpNode)){
							nTask.getNodes().add(tmpNode);//加入新节点
						}else{
							int index = nTask.getNodes().indexOf(tmpNode);
							tmpNode = nTask.getNodes().get(index);
						}
						//错误，此处tmpNode应该为nodes里面的对应node；
						tmpEdge.setFrom(tmpNode);
						tmpEdges.add(tmpEdge);//把新边加入临时表中
					}
				}
				else{
					if(!nTask.getNodes().contains(fromNode)){
						nTask.getNodes().add(fromNode);//加入新节点
					}
					tmpEdges.add(edge);
				}
				
				
				TaskNode toNode = edge.getTo();
				int toHostCount = toNode.getHostInfo().size();
				if(fromHostCount > 1){//若尾结点的目标主机数大于1则做节点分裂
					for(int i = 0;i < toHostCount;i++){
						TaskNode tmpNode = (TaskNode)toNode.clone();
						List<NodeHostRelation> hostInfo = new ArrayList<>();
						hostInfo.addAll(toNode.getHostInfo());
						tmpNode.setHostInfo(new ArrayList());
						tmpNode.getHostInfo().add((hostInfo.get(i)));
						if(!nTask.getNodes().contains(tmpNode)){
							nTask.getNodes().add(tmpNode);//加入新节点
						}else{
							int index = nTask.getNodes().indexOf(tmpNode);
							tmpNode = nTask.getNodes().get(index);
						}
						
						for(TaskEdge tmp:tmpEdges){
							TaskEdge tmpEdge = (TaskEdge)tmp.clone();
							tmpEdge.setTo(tmpNode);
							nTask.getEdges().add(tmpEdge);//加入新边
						}
					}
				}
				else{
					if(!nTask.getNodes().contains(toNode)){
						nTask.getNodes().add(toNode);//加入新节点
					}else{
						int index = nTask.getNodes().indexOf(toNode);
						toNode = nTask.getNodes().get(index);
					}
					for(TaskEdge tmp:tmpEdges){
						TaskEdge tmpEdge = (TaskEdge)tmp.clone();
						tmpEdge.setTo(toNode);
						nTask.getEdges().add(tmpEdge);//加入新边
					}
					
				}
				
				nodeList.remove(fromNode);//清除已经复制的节点
				nodeList.remove(toNode);
			}
			
			for(TaskNode node:nodeList){//如果有没有与任何边连接的节点，则对这些节点进行分裂
				int hostCount = node.getHostInfo().size();
				for(int i = 0; i<hostCount; i++){
					TaskNode tmpNode = (TaskNode)node.clone();
					List<NodeHostRelation> hostInfo = new ArrayList<>();
					hostInfo.addAll(node.getHostInfo());
					tmpNode.setHostInfo(new ArrayList());
					tmpNode.getHostInfo().add((hostInfo.get(i)));
					if(!nTask.getNodes().contains(tmpNode)){
						nTask.getNodes().add(tmpNode);//加入新节点
					}
				}
			}
			
			for(TaskNode node:nTask.getNodes()){//清除旧的孩子节点
				node.getChildren().clear();
			}
			
			for(TaskEdge edge:nTask.getEdges()){//为每个节点增加孩子节点
				TaskNode from = edge.getFrom();
				TaskNode to = edge.getTo();
				if(nTask.getNodes().contains(from)&&nTask.getNodes().contains(to)){
					int idxFrom = nTask.getNodes().indexOf(from);
					int idxTo = nTask.getNodes().indexOf(to);
					nTask.getNodes().get(idxFrom).addChild(nTask.getNodes().get(idxTo));
				}
			}
			
			return nTask;
		}
		/**
		 * 返回没有入度的所有节点列表作为拓扑排序的startNodes
		 * 
		 * @param task
		 *            其中的TaskNode具有孩子节点且只有一个目标主机
		 * @return
		 */
		private List<TaskNode> getStartNodes(Task task){
			//S存放没有入边的节点，初始为所有节点
			List<TaskNode> S = new ArrayList<>();
			S.addAll(task.getNodes());
			List<TaskEdge> nEdges=task.getEdges();
			
			//把有入边的节点从S中删除
			for(TaskEdge edge:nEdges){
				TaskNode node = edge.getTo();
				S.remove(node);
			}
			return S;
		}
		/**
		 * 返回没有入度的所有节点列表作为拓扑排序的startNodes
		 * 
		 * @param task
		 *            其中的TaskNode具有孩子节点且只有一个目标主机
		 * @return
		 */
		private List<TaskNode> getEndNodes(Task task){
			//S存放没有入边的节点，初始为所有节点
			List<TaskNode> S = new ArrayList<>();
			S.addAll(task.getNodes());
			List<TaskEdge> nEdges=task.getEdges();
			
			//把有入边的节点从S中删除
			for(TaskEdge edge:nEdges){
				TaskNode node = edge.getFrom();
				S.remove(node);
			}
			return S;
		}
		/**
		 * 获得一个图的拓补排序
		 * 
		 * @param Task
		 *            其中的TaskNode具有孩子节点且只有一个目标主机
		 * @return
		 */
		private  List<TaskNode> getTopoloy(Task task) {
			List<TaskNode> startNodes = getStartNodes(task);
			Map<TaskNode, Set<TaskNode>> parents = getParentMap(startNodes);
			List<TaskNode> topology = new ArrayList<>();
			Queue<TaskNode> potentials = new LinkedList<>();
			potentials.addAll(startNodes);
			while (!potentials.isEmpty()) {
				TaskNode current = potentials.poll();
				topology.add(current);
				for (TaskNode child : current.children) {
					parents.get(child).remove(current);
					if (parents.get(child).isEmpty()) {
						potentials.add(child);
					}
				}
			}
			return topology;
		}

		/**
		 * 合并superNodes中相邻且在同一host上的的superNode
		 * 
		 * @param superNodes
		 * @return
		 */
		private  List<SuperNode> mergeAdjSame(List<SuperNode> superNodes) {
			List<SuperNode> res = new ArrayList<>();
			SuperNode current = null;
			for (SuperNode sn : superNodes) {
				if (current == null && sn.getSubNodes().size() > 0) {
					current = new SuperNode(sn.getId(),sn.getSubNodes());
				} else if (sn.getSubNodes().size() > 0) {
					if (sn.isSimilarTo(current)) {
						current.getSubNodes().addAll(sn.getSubNodes());
					} else {
						res.add(current);
						current = new SuperNode(sn.getId(),sn.getSubNodes());
					}
				}
			}
			res.add(current);
			return res;
		}

		private  List<SuperNode> mergeNodes(List<TaskNode> topology) {
			List<SuperNode> res = new ArrayList<>();
			/**
			for (TaskNode node : topology) {//把每个节点都初始化为超级节点
				res.add(new SuperNode(node));
			}
			**/
			for (int i = 0; i<topology.size(); i++){//把每个节点都初始化为超级节点
				TaskNode node = topology.get(i);
				String key = ""+i+":"+node.getHostInfo().get(0).getHost().getId();
				res.add(new SuperNode(key,node));
			}
			//res = mergeAdjSame(res);
			Map<TaskNode, Integer> supers = new HashMap<>();
			for (int i = 0; i < res.size(); i++) {
				for (TaskNode node : res.get(i).getSubNodes()) {
					supers.put(node, i);
				}
			}
			
			for (int i=0;i<res.size();i++) {//为每一个超级节点添加父节点index与子节点index
				SuperNode sn = res.get(i);
				for (TaskNode n : sn.getSubNodes()) {
					for (TaskNode child : n.children) {
						Integer superId = supers.get(child);
						sn.getChildren().add(superId);
						res.get(superId).getParents().add(i);
					}
				}
			}
			boolean changed = true;
			while (changed) {
				changed = false;
				for (int i = 0; i < res.size() - 1; i++) {//遍历除最后一个节点外的其余超级节点
					SuperNode sn = res.get(i);
					if (sn.getSubNodes().size() == 0)
						continue;
					int j = 1;
					while (i + j < res.size()
							&& (res.get(i + j).isEmpty() || (!sn.isSimilarTo(res
									.get(i + j)) && !sn.getChildren().contains(i + j)))) {
						//节点i+j在表中并且（节点i+j为空或与节点i不相似）并且节点i没有到节点i+j的边
						j++;
					}
					if (i + j < res.size() && sn.isSimilarTo(res.get(i + j))) {
						//若i+j在表中且与节点i相似
						SuperNode nextsn = res.get(i + j);
						nextsn.getSubNodes().addAll(0, sn.getSubNodes());
						sn.getSubNodes().clear();
						nextsn.getChildren().addAll(sn.getChildren());
						sn.getChildren().clear();
						for(Integer superId: sn.getParents()){
							res.get(superId).getChildren().add(i+j);
						}
						nextsn.getParents().addAll(sn.getParents());
						sn.getParents().clear();
						changed = true;
					}
				}
				//System.out.println(res);
			}

			return mergeAdjSame(res);
		}
		
		private  Map<TaskNode, Set<TaskNode>> getParentMap(List<TaskNode> startNodes) {
			Map<TaskNode, Set<TaskNode>> parents = new HashMap<>();
			Set<TaskNode> visited = new HashSet<>();
			Queue<TaskNode> next = new LinkedList<>();
			next.addAll(startNodes);
			while (!next.isEmpty()) {
				TaskNode current = next.poll();
				if (visited.contains(current)) {
					continue;
				}
				for (TaskNode child : current.children) {
					if (!parents.containsKey(child)) {
						parents.put(child, new HashSet<TaskNode>());
					}
					parents.get(child).add(current);
					if (!visited.contains(child)) {
						next.add(child);
					}
				}
			}
			return parents;
		}
	}
	
	/*
	 * 返回图中 没有入度的所有节点列表
	 * 
	 * @param graph
	 *            graph是目标图
	 * @return
	 
	private Collection<Node> getStartNodes(TaskViews.Graph graph){
		//S存放没有入边的节点，初始为所有节点
		Map<Long,Node> S = new HashMap<>();
		for (Node node : graph.nodes) {
			S.put(node.id, node);
		}
		List<Edge> nEdges=graph.edges;
		
		//把有入边的节点从S中删除
		for(Edge edge:nEdges){
			S.remove(edge.to);
		}
		return  S.values();
	}
	*/
	/*
	 * 返回图中 没有出度的所有节点列表
	 * 
	 * @param graph
	 *            其中graph是目标图
	 * @return
	 
	private Collection<Node> getEndNodes(TaskViews.Graph graph){
		//S存放没有入边的节点，初始为所有节点
		Map<Long,Node> S = new HashMap<>();
		for (Node node : graph.nodes) {
			S.put(node.id, node);
		}
		List<Edge> nEdges=graph.edges;
		
		//把有入边的节点从S中删除
		for(Edge edge:nEdges){
			S.remove(edge.from);
		}
		return S.values();
	}
	*/
	/*
	 * 按照到达节点创建EdgeMap
	 * 
	 * @param graph
	 *            其中graph是目标图
	 * @return
	 
	private static Map<Long,Set<Edge>> getToMap(TaskViews.Graph graph){
		//S存放所有到达某节点的边
		Map<Long,Set<Edge>> S = new HashMap<>();
		for (Edge edge : graph.edges) {
			if (S.get(edge.to) == null) {
				S.put(edge.to, new HashSet<Edge>());
			}
			S.get(edge.to).add(edge);
		}
		return S;
	}
	*/
	/*
	 * 按照到达节点创建EdgeMap
	 * 
	 * @param graph
	 *            其中graph是目标图
	 * @return
	private static Map<Long,Set<Edge>> getFromMap(TaskViews.Graph graph){
		//S存放所有某节点出发的边
		Map<Long,Set<Edge>> S = new HashMap<>();
		for (Edge edge : graph.edges) {
			if (S.get(edge.from) == null) {
				S.put(edge.from, new HashSet<Edge>());
			}
			S.get(edge.from).add(edge);
		}
		return S;
	}
	*/
	/*
	 * 获得operationNode的所有param用于转换
	 * 
	 * @param graph
	 *            其中graph是目标图
	 * @return
	 
	private static Map<String,String> getParamMap(Node node){
		//S存放所有某节点出发的边
		Map<String,String> S = new HashMap<>();
		for (Param p : node.params) {
			S.put(p.key, p.value);
		}
		return S;
	}
	*/
	/**
	 * 按照到达节点创建EdgeMap
	 * 
	 * @param graph
	 *            其中graph是目标图
	 * @return
	 */
	private static Map<Long,Set<TaskEdge>> getToMap(Task task){
		//S存放所有到达某节点的边
		Map<Long,Set<TaskEdge>> S = new HashMap<>();
		for (TaskEdge edge : task.getEdges()) {
			if (S.get(edge.getTo().getId()) == null) {
				S.put(edge.getTo().getId(), new HashSet<TaskEdge>());
			}
			S.get(edge.getTo().getId()).add(edge);
		}
		return S;
	}
	/**
	 * 按照到达节点创建EdgeMap
	 * 
	 * @param graph
	 *            其中graph是目标图
	 * @return
	 */
	private static Map<Long,Set<TaskEdge>> getFromMap(Task task){
		//S存放所有某节点出发的边
		Map<Long,Set<TaskEdge>> S = new HashMap<>();
		for (TaskEdge edge : task.getEdges()) {
			if (S.get(edge.getFrom().getId()) == null) {
				S.put(edge.getFrom().getId(), new HashSet<TaskEdge>());
			}
			S.get(edge.getFrom().getId()).add(edge);
		}
		return S;
	}
	/**
	 * 获得operationNode的所有param用于转换
	 * 
	 * @param graph
	 *            其中graph是目标图
	 * @return
	 */
	private static Map<String,String> getParamMap(TaskNode tasknode){
		//S存放所有某节点出发的边
		Map<String,String> S = new HashMap<>();
		for (NodeParam p : tasknode.getParams()) {
			S.put(p.getParamKey(), p.getParamValue());
		}
		return S;
	}
	/*更新代码使用task 扩展，graph 扩展已废弃不用
	 * 对于是operationNode的节点：
	//生成内部节点的identifier
			//将原来的边设为新图中的边
	private void expandGraph(TaskViews.Graph graph){
		//遍历，找到是operationNode的节点，如果没有，直接返回。
		Map<Long,Set<Edge>> toMap = getToMap(graph);
		Map<Long,Set<Edge>> fromMap = getFromMap(graph);
		TaskViews.EdgeBuilder eb = new TaskViews.EdgeBuilder();
		List<Node> removeNode = new ArrayList<>();
		List<Node> addNode = new ArrayList<>();
		for (Node node : graph.nodes) {
			if (0 != node.operation) {//如果是operation
				Task oper = taskDAO.findOne(node.operation);
				TaskViews.Graph subGraph = TaskViews.graphViewOf(oper);
				refreshGraph(node,subGraph,node.hostIds);
				//找到operation内部 所有起始节点
				Collection<Node> startNodes = getStartNodes(subGraph);
				//找到operation内部所有终结节点
				Collection<Node> endNodes = getEndNodes(subGraph);
				
				Set<Edge> inEdge = toMap.get(node.id);
				//将指向operationNode的原边指向所有内部起始节点
				if (null != inEdge) {
					for (Edge ie : inEdge) {
					//移除原来的边
					graph.edges.remove(ie);
					for (Node start : startNodes) {
						eb.setFrom(ie.from);
						eb.setTo(start.id);
						eb.setRelation(ie.relation);
						graph.edges.add(eb.build());
						}
					}
				}
				Set<Edge> outEdge = fromMap.get(node.id);
				//将所有operationNode出发的边起点设为内部终结节点
				if (null != outEdge) {
					for (Edge oe : outEdge) {
						graph.edges.remove(oe);
						for (Node end : endNodes) {
							eb.setFrom(end.id);
							eb.setTo(oe.to);
							eb.setRelation(oe.relation);
							graph.edges.add(eb.build());
						}
					}
				}
				//子图中的node添加为新图的node
				addNode.addAll(subGraph.nodes);
				graph.edges.addAll(subGraph.edges);
				//去掉原来的node
				removeNode.add(node);
			}
		}
		graph.nodes.removeAll(removeNode);
		graph.nodes.addAll(addNode);
	}
	/**刷新Graph 主要是更新nodeid以及edge的id避免node冲突
	 * @param origin
	 * @param graph
	 * @param hostIds
	
	private static void refreshGraph(Node origin, TaskViews.Graph graph,List<Long> hostIds){
		Map<Long,Set<Edge>> toMap = getToMap(graph);
		Map<Long,Set<Edge>> fromMap = getFromMap(graph);
		Map<String,String> paramap = getParamMap(origin);
		for (Node node : graph.nodes) {
			node.hostIds = hostIds;
			Long oldId = node.id;
			node.id = dateToId();
			Set<Edge> tmpto = toMap.get(oldId);
			if (null != tmpto) {
				for (Edge edge : tmpto) {
					edge.to = node.id;
				}
			}
			Set<Edge> tmpfrom = fromMap.get(oldId);
			if (null != tmpfrom) {
				for (Edge edge : tmpfrom) {
					edge.from = node.id;
				}
			}
			for (Param pr : node.params) {
				pr.value = paramap.get(pr.key);
			}
		}
	}*/
	
	/**
	 * @description
	 * 对于是operationNode的节点：
	 * 生成内部节点的identifier将原来的边设为新图中的边，扩展Task中的operationNode
	 * @param task void
	 * @author xpxstar@gmail.com
	 * 2015年11月11日 下午4:32:48
	 */
	private void expandTask(Task task){
		//遍历，找到是operationNode的节点，如果没有，直接返回。
		Map<Long,Set<TaskEdge>> toMap = getToMap(task);
		Map<Long,Set<TaskEdge>> fromMap = getFromMap(task);
		List<TaskNode> removeNode = new ArrayList<>();
		List<TaskNode> addNode = new ArrayList<>();
		TaskAlgorithmService expandService = new TaskAlgorithmService();
		for (TaskNode node : task.getNodes()) {
			if (null != node.getOperation()) {//如果是operation
				//找到operation内部 所有起始节点
				List<TaskNode> startNodes = expandService.getStartNodes(node.getOperation());
				//找到operation内部所有终结节点
				List<TaskNode> endNodes = expandService.getEndNodes(node.getOperation());
				
				Set<TaskEdge> inEdge = toMap.get(node.getId());
				//将指向operationNode的原边指向所有内部起始节点
				if (null != inEdge) {
					for (TaskEdge ie : inEdge) {
						//移除原来边，并添加新的边
						task.getEdges().remove(ie);
						for (TaskNode start : startNodes) {
							TaskEdge te = new TaskEdge();
							te.setFrom(ie.getFrom());
							te.setTo(start);
							te.setRelation(ie.getRelation());
							task.getEdges().add(te);
							//如果起点也是operation，就把新边加入fromMap中
							if (null != ie.getFrom().getOperation()) {
								fromMap.get(ie.getFrom().getId()).add(te);
							}
						}
						//如果起点也是operation，就在fromMap中去掉旧边
						if (null != ie.getFrom().getOperation()) {
							fromMap.get(ie.getFrom().getId()).remove(ie);
						}
					}
				}
				Set<TaskEdge> outEdge = fromMap.get(node.getId());
				//将所有operationNode出发的边起点设为内部终结节点
				if (null != outEdge) {
					for (TaskEdge oe : outEdge) {
						task.getEdges().remove(oe);
						for (TaskNode end : endNodes) {
							TaskEdge te = new TaskEdge();
							te.setFrom(end);
							te.setTo(oe.getTo());
							te.setRelation(oe.getRelation());
							task.getEdges().add(te);
							if (null != oe.getTo().getOperation()) {
								toMap.get(oe.getTo().getId()).add(te);
							}
						}
						if (null != oe.getTo().getOperation()) {
							toMap.get(oe.getTo().getId()).remove(oe);
						}
					}
				}
				Map<String,String> paramap = getParamMap(node);
				
				//子图中的node添加为新图的node
				for (TaskNode nn : node.getOperation().getNodes()) {
					List<NodeHostRelation> relations = new ArrayList<>();
						relations.addAll(node.getHostInfo());
					nn.setHostInfo(relations);
					//获取对应的参数key以及value，注意action displayName的对应
					for (NodeParam np : nn.getParams()) {
						np.setParamValue(paramap.get(np.getParamKey()+'-'+nn.getAction().getDisplayName()));
					}
				}
				addNode.addAll(node.getOperation().getNodes());
				task.getEdges().addAll(node.getOperation().getEdges());
				//去掉原来的node
				removeNode.add(node);
			}
		}
		task.getNodes().removeAll(removeNode);
		task.getNodes().addAll(addNode);
	}
	/**
	 * @description generate id from date
	 * @return Long
	 * @author xpxstar@gmail.com
	 * 2015年11月11日 下午4:32:20
	 */
	private static Long dateToId(){
		Long id = new Date().getTime();
		int x = 1000, y = 0;
		long rand = (long)(Math.random() * (x - y + 1) + y);
		id = id + rand;
		return id;
	}
}
