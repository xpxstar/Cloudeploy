package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleClass;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleDefine;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.model.service.script.service.OperationService;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetClass;
@Service
public class ToolFactory {
	private Logger logger=LoggerFactory.getLogger(ToolFactory.class);
	private Map<String,Node> products=Maps.newHashMap();
	@Value("#{configs['service.toolfactory.configpath']}")
	private String configFile;
	@Autowired
	private AdjacencyListsGraphOperationBuilder buildworker;
	
	@Autowired
	private OperationService operationService;
	/**
	 * 生成所有需要的node原型，并存入maps
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getAllProtype() {
		Yaml yaml = new Yaml();
		try {
			Map configs = yaml.loadAs(new FileReader(configFile), Map.class);
			List<String> moduleResultFiles = (List<String>) configs
					.get("moduleResultYaml");
			for (String string : moduleResultFiles) {
				logger.info("解析" + string + ",转化为node");
				Iterable<Object> klasses = yaml.loadAll(new FileReader(string));
				for (Object klass : klasses) {
					if (klass instanceof PuppetClass) {
						PuppetClass puppetClass = (PuppetClass) klass;
						switch (puppetClass.getType().trim()) {
						case "definition":
							ModuleDefine defineNode = new ModuleDefine(puppetClass.getKlass(),puppetClass.getParams());
							products.put(puppetClass.getKlass(),defineNode);
							break;
						case "hostclass":
							ModuleClass classNode = new ModuleClass(puppetClass.getKlass(),puppetClass.getParams());
							products.put(puppetClass.getKlass(),classNode);
							break;
						default:
							logger.error("分析Module中发现不匹配的新类型"
									+ puppetClass.getType());
							break;
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List<Operation> produceOperations(String defineFile) throws FileNotFoundException{
		Yaml yaml=new Yaml();
		Map definesMap=yaml.loadAs(new FileReader(defineFile),Map.class);
		Map componentMap=(Map) definesMap.get("component");
		String componentName=(String) componentMap.get("name");
		String componentDisplayName=(String) componentMap.get("display_name");
		String componentType=(String)componentMap.get("componentType");
		Component component = operationService.storeComponent(componentName, componentDisplayName, componentType);
		List operations=(List) definesMap.get("operations");
		return produceOperations(operations,component);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Operation> produceOperations(List operationsDefine,Component component){
		List<Operation> productList=Lists.newArrayList();
		for (Object operationObject : operationsDefine) {
			if(operationObject instanceof Map){
				Map operatonMap=(Map) operationObject;
				String operationName=(String) operatonMap.get("operationName");
				String operationDisplayName=(String)operatonMap.get("displayName");
				List<List<Node>> graph=Lists.newArrayList();
				Map<String, Object> extractParamsOfOperation=Maps.newHashMap();			
				Map<String, Node> allNodes=Maps.newHashMap();
				List<Map<String,Object>> nodes=(List<Map<String, Object>>) operatonMap.get("nodes");
				for (Map<String, Object> node : nodes) {
					String nodeName=(String) node.get("nodeName");
					String searchName=(String) node.get("searchName");
					Node puppetNode=getNode(searchName);
					puppetNode.setNodeName(nodeName);
					if(node.containsKey("params")){
						Map<String,Object> nodeParamsOfYam=(Map) node.get("params");
						for (Object item : nodeParamsOfYam.entrySet()) {
							Entry<String, Object> entryItem = (Map.Entry<String,Object>)item;
							puppetNode.setValueOfParams(entryItem.getKey(), entryItem.getValue());
						}
					}
					List<Node> graphPart=Lists.newArrayList(puppetNode);
					if(node.containsKey("dependency")){
						List dependencyes=(List) node.get("dependency");
						for (Object dependencyObject : dependencyes) {
							graphPart.add(allNodes.get(dependencyObject));
						}
					}
					if(node.containsKey("extractParams")){
						List extractParamsList=(List) node.get("extractParams");
						for (Object param : extractParamsList) {
							if(param instanceof String){
								if(param.equals("$allParams")){
									Set<Entry<String, ParamValue>> sets = puppetNode.getParamsEntrySet();
									for (Entry<String, ParamValue> entry : sets) {
										extractParamsOfOperation.put(entry.getKey(),entry.getValue().getValue());
									}
									break;
								}
								extractParamsOfOperation.put((String) param, puppetNode.findValueOfParam((String) param));
							}else if(param instanceof Map){
								for (Object paramEntry: ((Map) param).entrySet()) {
									String paramNameString=(String) ((Map.Entry)paramEntry).getKey();
									Object paramDefaultValueString=((Map.Entry)paramEntry).getValue();
									extractParamsOfOperation.put(paramNameString, paramDefaultValueString);
									puppetNode.setValueOfParams(paramNameString, paramDefaultValueString);
								}
							}
						}
					}
					graph.add(graphPart);
					allNodes.put(nodeName, puppetNode);
				}
				Operation newOperation=buildworker.createNewOperation(graph, operationName, extractParamsOfOperation);
				operationService.storeOperation(newOperation, component,operationDisplayName);
				productList.add(newOperation);
			}
			else{
				//TODO 错误处理
			}
		}
		return productList;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */

	public Node getNode(String key) {
		Node result=products.get(key);
		if(result==null){
			result=operationService.findOperationByName(key);
		}
		return result;
	}
	
	static class SoftwareDefineMessage{
		private ToolFactory.ComponentMessage component;
		private List<ToolFactory.OperationMessage> operations;
		public ToolFactory.ComponentMessage getComponent() {
			return component;
		}
		public void setComponent(ToolFactory.ComponentMessage component) {
			this.component = component;
		}
		public List<ToolFactory.OperationMessage> getOperations() {
			return operations;
		}
		public void setOperations(List<ToolFactory.OperationMessage> operations) {
			this.operations = operations;
		}
	}
	
	static class ComponentMessage{
		private String name;
		private String display_name;
		private String componentType;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDisplay_name() {
			return display_name;
		}
		public void setDisplay_name(String display_name) {
			this.display_name = display_name;
		}
		public String getComponentType() {
			return componentType;
		}
		public void setComponentType(String componentType) {
			this.componentType = componentType;
		}
	}
	
	static class OperationMessage{
		private String operationName;
		private String displayName;
		private List<NodeMessage> nodes;
		public String getOperationName() {
			return operationName;
		}
		public void setOperationName(String operationName) {
			this.operationName = operationName;
		}
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public List<NodeMessage> getNodes() {
			return nodes;
		}
		public void setNodes(List<NodeMessage> nodes) {
			this.nodes = nodes;
		}
	}
	
	static class NodeMessage{
		private String nodeName;
		private String searchName;
		private List<Object> extractParams;
		public String getNodeName() {
			return nodeName;
		}
		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}
		public String getSearchName() {
			return searchName;
		}
		public void setSearchName(String searchName) {
			this.searchName = searchName;
		}
		public List<Object> getExtractParams() {
			return extractParams;
		}
		public void setExtractParams(List<Object> extractParams) {
			this.extractParams = extractParams;
		}
	}
	
}
