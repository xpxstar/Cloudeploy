package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import com.google.common.collect.Maps;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleClass;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleDefine;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.model.service.script.service.OperationService;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetClass;

@Service
public class OperationBuilderFactory {
	
	private Logger logger = LoggerFactory.getLogger(OperationBuilderFactory.class);
	
	@Value("#{configs['service.toolfactory.configpath']}")
	private String configFile;
	@Autowired
	private EdgeTypedGraphOperationBuilderRefacted buildworker;
	@Autowired
	private OperationService operationService;
	
	private Map<String, Node> products = Maps.newHashMap();
	
	public void getAllModules() throws FileNotFoundException{
		ConfigFileFormat files = loadAs(configFile, ConfigFileFormat.class);
		for(String softwareFile: files.getModuleResultYaml()){
			logger.info("transform" + softwareFile +"'s classes or defines to Node");
			List<PuppetClass> klasses = loadAll(softwareFile, PuppetClass.class);
			for(PuppetClass klass: klasses){
				PuppetClassType type = PuppetClassType.fromValue(klass.getType());
				products.put(klass.getKlass(), type.transform(klass));
			}
		}
	}
	
	public List<Operation> produceOperations(String defineFile) throws FileNotFoundException{
		SoftwareDefineMessage defineMessage = loadAs(defineFile, SoftwareDefineMessage.class);
		ComponentMessage componentMes = defineMessage.getComponent();
		Component component = operationService.storeComponent(componentMes.getName(), componentMes.getDisplay_name(), componentMes.getComponentType());
		return produceOperations(defineMessage.getOperations(), component);
	}
	
	private List<Operation> produceOperations(
			List<OperationMessage> operations, Component component) {
		List<Operation> productList = new ArrayList<>();
		for(OperationMessage operationMes: operations){
			Map<String, ParamValue> extractParamsOfOperation = Maps.newHashMap();
			Graph<Node, EdgeType> graph = operationMes.transform(extractParamsOfOperation,this);
			Operation operation = buildworker.createNewOperation(graph, operationMes.getOperationName(), extractParamsOfOperation);
			operationService.storeOperation(operation, component, operationMes.getDisplayName());
			productList.add(operation);
		}
		return productList;
	}
	
	
	private Node findNode(String searchName) {
		logger.info("try to find a protype with name: " + searchName);
		Node result=products.get(searchName);
		if(result==null){
			result=operationService.findOperationByName(searchName);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <T> T loadAs(FileReader reader, Class<T> type){
		Constructor constructor = new Constructor(type);
		Yaml yaml = new Yaml(constructor);
		yaml.setBeanAccess(BeanAccess.FIELD);
		return (T) yaml.load(reader);
	}
	
	private static <T> List<T> loadAll(String file, Class<T> type) throws FileNotFoundException{
		return loadAll(new FileReader(file), type);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List<T> loadAll(FileReader reader, Class<T> type){
		Constructor constructor = new Constructor(type);
		Yaml yaml = new Yaml(constructor);
		yaml.setBeanAccess(BeanAccess.FIELD);
		List<T> klasses = new ArrayList<T>();
		for(Object klass: yaml.loadAll(reader)){
			klasses.add((T) klass);
		}
		return klasses;
	}
	
	private static <T> T loadAs(String file, Class<T> type) throws FileNotFoundException{
		return loadAs(new FileReader(file), type);
	}
	
	static enum PuppetClassType{
		Definition("definition") {
			@Override
			public Node transform(PuppetClass from) {
				return new ModuleDefine(from.getKlass(), from.getParams());
			}
		},Hostclass("hostclass") {
			@Override
			public Node transform(PuppetClass from) {
				return new ModuleClass(from.getKlass(), from.getParams());
			}
		};
		private String name;
		private PuppetClassType(String name) {
			this.name = name;
		}
		public static PuppetClassType fromValue(String typeName){
			for(PuppetClassType type: PuppetClassType.values()){
				if(type.name.equals(typeName)) return type;
			}
			throw new IllegalArgumentException("can't indentify puppetclassType :"+ typeName);
		}
		public abstract Node transform(PuppetClass from);
	}
	
	static class ConfigFileFormat{
		private List<String> moduleResultYaml;
		public List<String> getModuleResultYaml() {
			return moduleResultYaml;
		}
		public void setModuleResultYaml(List<String> moduleResultYaml) {
			this.moduleResultYaml = moduleResultYaml;
		}
	}
	
	static class SoftwareDefineMessage{
		private OperationBuilderFactory.ComponentMessage component;
		private List<OperationBuilderFactory.OperationMessage> operations;
		public OperationBuilderFactory.ComponentMessage getComponent() {
			return component;
		}
		public List<OperationBuilderFactory.OperationMessage> getOperations() {
			return operations;
		}
	}
	
	static class ComponentMessage{
		private String name;
		private String display_name;
		private String componentType;
		public String getName() {
			return name;
		}
		public String getDisplay_name() {
			return display_name;
		}
		public String getComponentType() {
			return componentType;
		}
	}
	
	static class OperationMessage{
		private String operationName;
		private String displayName;
		private List<NodeMessage> nodes;
		
		public Graph<Node, EdgeType> transform(Map<String, ParamValue> extractParamsOfOperation, OperationBuilderFactory factory){
			Graph<Node, EdgeType> graph = new Graph<Node, EdgeType>();
			Map<String, Node> allNodes = Maps.newHashMap();
			for(NodeMessage nodeMes: nodes){
				Node puppetNode = nodeMes.transform(factory);
				graph.addVertex(puppetNode);
				allNodes.put(nodeMes.getNodeName(), puppetNode);
				if(nodeMes.getDependency() != null){
					for(String nodeName: nodeMes.getDependency()){
						graph.addEdge(puppetNode, allNodes.get(nodeName), EdgeType.REQUIRE);
					}
				}
				if(nodeMes.getExtractParams() != null){
					transformForExtractParams(nodeMes, puppetNode, extractParamsOfOperation);
				}
			}
			return graph;
		}
		
		@SuppressWarnings("rawtypes")
		private void transformForExtractParams(NodeMessage nodeMes, Node puppetNode, Map<String, ParamValue> extractParamsOfOperation){
			for(Object param: nodeMes.getExtractParams()){
				if(param instanceof String){
					if(param.equals("$allParams")){
						Set<Entry<String, ParamValue>> sets = puppetNode.getParamsEntrySet();
						for (Entry<String, ParamValue> entry : sets) {
							extractParamsOfOperation.put(entry.getKey(),entry.getValue());
						}
						break;
					}
					extractParamsOfOperation.put((String) param, puppetNode.findValue((String) param));
				}else if(param instanceof Map){
					for (Object paramEntry: ((Map) param).entrySet()) {
						String paramNameString=(String) ((Map.Entry)paramEntry).getKey();
						Object paramDefaultValueString=((Map.Entry)paramEntry).getValue();
						puppetNode.setValueOfParams(paramNameString, paramDefaultValueString);
						extractParamsOfOperation.put(paramNameString, puppetNode.findValue(paramNameString));
					}
				}
			}
		}
		
		public String getOperationName() {
			return operationName;
		}
		public String getDisplayName() {
			return displayName;
		}
		public List<NodeMessage> getNodes() {
			return nodes;
		}
	}
	
	static class NodeMessage{
		private String nodeName;
		private String searchName;
		private List<Object> extractParams;
		private Map<String, Object> params;
		private List<String> dependency;
		
		public Node transform(OperationBuilderFactory factory){
			Node puppetNode = factory.findNode(getSearchName());
			puppetNode.setNodeName(getNodeName());
			if(getParams() != null){
				for(Map.Entry<String, Object> item : getParams().entrySet()){
					puppetNode.setValueOfParams(item.getKey(), item.getValue());
				}
			}
			return puppetNode;
		}
		
		public String getNodeName() {
			return nodeName;
		}
		public String getSearchName() {
			return searchName;
		}
		public List<Object> getExtractParams() {
			return extractParams;
		}
		public Map<String, Object> getParams() {
			return params;
		}
		public List<String> getDependency() {
			return dependency;
		}
	}
}
