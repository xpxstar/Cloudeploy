//package cn.ac.iscas.cloudeploy.v2.workflowEngine.impl;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews;
//import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.Node;
//import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.Param;
//import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.ParamBuilder;
//import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
//import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
//import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
//import cn.ac.iscas.cloudeploy.v2.model.entity.task.Task;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeDefinition;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeElement;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ConsulService;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyDefinition;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipTemplate;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
//import cn.ac.iscas.cloudeploy.v2.model.service.task.TaskService;
//import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;
//import cn.ac.iscas.cloudeploy.v2.util.RandomUtils;
//import cn.ac.iscas.cloudeploy.v2.workflowEngine.ComponentPlugin;
//import cn.ac.iscas.cloudeploy.v2.workflowEngine.TaskPlugin;
//import cn.ac.iscas.cloudeploy.v2.workflowEngine.entity.DeploymentTask;
//
//@Service
//public class VMTaskPlugin implements TaskPlugin{
//	private static Logger logger = LoggerFactory.getLogger(VMTaskPlugin.class);
//	@Autowired
//	private ComponentPlugin componentPlugin;
//	@Autowired
//	private HostSelector hostSelector;
//	
//	@Autowired
//	private TaskService taskService;
//	
//	@Autowired
//	private UserService userService;
//	
//	@Override
//	public DeploymentTask createTask(ServiceTemplate template,
//			NodeTemplate nodeTemplate, List<RelationshipTemplate> relations) {
//		logger.info("create task for %s", nodeTemplate.getName());
//		DeploymentTask task = new DeploymentTask();
//		TaskViews.Graph graph = createGraphOfTask(template, nodeTemplate);
//		Task taskScript = taskService.createTaskFromGraph(graph, userService.getCurrentUser());
//		task.setTask(taskScript);
//		return task;
//	}
//	
//	private TaskViews.Graph createGraphOfTask(ServiceTemplate template, NodeTemplate nodeTemplate){
//		DeploymentTask task = new DeploymentTask();
//		int minInstance = nodeTemplate.getMinInstance();
//		for (int i = 0; i < minInstance; i++) {
//			DHost host = hostSelector.getHost();
//			task.addHost(host);
//		}
//		String taskName = RandomUtils.randomString();
//		
//		TaskViews.GraphBuilder graphBuilder = new TaskViews.GraphBuilder();
//		graphBuilder.setTaskName(taskName);
//		
//		NodeType nodeType = template.getNodeType(nodeTemplate.getType());
//		
//		TaskViews.Node installNode = createInstallTask(template, nodeTemplate, task);
//		TaskViews.Node registerServiceNode = buildServiceRegisterTask(nodeTemplate, task);
//		List<Node> registerAttributesNode = buildAttributesRegisterTask(nodeType, nodeTemplate, task);
//		
//		graphBuilder.addNode(installNode);
//		graphBuilder.addNode(registerServiceNode);
//		graphBuilder.addNode(registerAttributesNode);
//		
//		return graphBuilder.build();
//	}
//	/**
//	 * HostOn关系有点特殊，HostOn虚机或者HostOn应用容器，是完全不一样的
//	 * @param template
//	 * @param nodeTemplate
//	 * @param task 
//	 * @return
//	 */
//	private Node createInstallTask(ServiceTemplate template, NodeTemplate nodeTemplate, DeploymentTask task) {
//		NodeType nodeType = template.getNodeType(nodeTemplate.getType());
//		Component component = componentPlugin.getComponent(nodeType);
//		Action action = component.getAction("install");
//		TaskViews.NodeBuilder nodeBuilder = new TaskViews.NodeBuilder();
//		nodeBuilder.setId(System.currentTimeMillis())
//				.setComponentId(component.getId())
//				.setActionId(action.getId())
//				.setXPos(500)
//				.setYPos(500);
//		for(DHost host: task.getHosts()){
//			nodeBuilder.addHost(host.getId());
//		}
//		Map<String, String> properties = mergeProperties(nodeType, nodeTemplate);
//		for(Entry<String, String> entry : properties.entrySet()){
//			TaskViews.ParamBuilder paramBuilder = new TaskViews.ParamBuilder();
//			paramBuilder.setKey(entry.getKey())
//						.setValue(entry.getValue());
//			nodeBuilder.addParam(paramBuilder.build());
//		}
//		return nodeBuilder.build();
//	}
//	
//	private void createConfigurationTask(NodeTemplate template) {
//		//TODO nodetemplate配置文件注册
//	}
//	
//	/**
//	 * 依赖于Puppet，注册组件服务。
//	 * @param nodeTemplate
//	 * @param task
//	 * @return
//	 */
//	private TaskViews.Node buildServiceRegisterTask(NodeTemplate nodeTemplate,
//			DeploymentTask task) {
//		TaskViews.NodeBuilder nodeBuilder = new TaskViews.NodeBuilder();
//		//TODO 不能写死，需要改成配置项
//		Component component = componentPlugin.getComponentByName("consul");
//		Action action = component.getAction("service");
//		nodeBuilder.setId(System.currentTimeMillis())
//					.setComponentId(component.getId())
//					.setActionId(action.getId())
//					.setXPos(600)
//					.setYPos(600);
//		for(DHost host: task.getHosts()){
//			nodeBuilder.addHost(host.getId());
//		}
//		List<TaskViews.Param> params = buildServiceParam(nodeTemplate.getService());
//		nodeBuilder.addParam(params);
//		return nodeBuilder.build();
//	}
//	
//	/**
//	 * 依赖Puppet将Attributes注册至Consul集群中
//	 * @param nodeType
//	 * @param nodeTemplate
//	 * @return
//	 */
//	private List<Node> buildAttributesRegisterTask(NodeType nodeType,
//			NodeTemplate nodeTemplate, DeploymentTask task) {
//		Component component = componentPlugin.getComponentByName("consul");
//		Action action = component.getAction("keystore");
//		List<TaskViews.Node> nodes = new ArrayList<TaskViews.Node>();
//		Map<String, String> attributes = mergeAttributes(nodeType, nodeTemplate);
//		for(Entry<String, String> entry : attributes.entrySet()){
//			TaskViews.NodeBuilder nodeBuilder = new TaskViews.NodeBuilder();
//			nodeBuilder.setId(System.currentTimeMillis())
//				.setComponentId(component.getId())
//				.setActionId(action.getId())
//				.setXPos((int) (600 + Math.random() * 100))
//				.setYPos((int) (600 + Math.random() * 100));
//			ParamBuilder paramBuilder = new ParamBuilder();
//			paramBuilder.setKey(entry.getKey()).setValue(entry.getValue());
//			nodeBuilder.addParam(paramBuilder.build());
//			for(DHost host: task.getHosts()){
//				nodeBuilder.addHost(host.getId());
//			}
//			nodes.add(nodeBuilder.build());
//		}
//		return nodes;
//	}
//
//	private List<Param> buildServiceParam(ConsulService service) {
//		TaskViews.ParamBuilder paramBuilder = new TaskViews.ParamBuilder();
//		List<TaskViews.Param> params = new ArrayList<TaskViews.Param>();
//		paramBuilder.setKey("address")
//					.setValue("$::ipaddress");
//		params.add(paramBuilder.build());
//		
//		paramBuilder.setKey("port").setValue(service.getPort());
//		params.add(paramBuilder.build());
//		
//		paramBuilder.setKey("id").setValue(service.getId() + System.currentTimeMillis());
//		params.add(paramBuilder.build());
//		
//		paramBuilder.setKey("service_name").setValue(service.getName());
//		params.add(paramBuilder.build());
//		
//		//TODO addChecks
//		return params;
//	}
//
//	private Map<String, String> mergeAttributes(NodeType nodeType, NodeTemplate nodeTemplate) {
//		Map<String, String> attributes = new HashMap<String, String>();
//		for(AttributeDefinition definition : nodeType.getAttributes()){
//			attributes.put(definition.getName(), definition.getDefaultValue());
//		}
//		for(AttributeElement element : nodeTemplate.getAttributes()){
//			attributes.put(element.getName(), element.getValue());
//		}
//		return attributes;
//	}
//
//	private Map<String, String> mergeProperties(NodeType nodeType, NodeTemplate nodeTemplate) {
//		Map<String, String> properties = new HashMap<>();
//		if(nodeType.getProperties() != null){
//			for(PropertyDefinition property : nodeType.getProperties()){
//				properties.put(property.getName(), property.getDefaultValue());
//			}
//		}
//		if(nodeTemplate.getProperties() != null){
//			for(PropertyElement element: nodeTemplate.getProperties()){
//				properties.put(element.getName(), element.getValue());
//			}
//		}
//		return properties;
//	}
//	
//}
