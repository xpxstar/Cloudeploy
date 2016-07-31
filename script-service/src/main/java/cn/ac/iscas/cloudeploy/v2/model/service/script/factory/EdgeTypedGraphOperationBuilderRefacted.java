package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;

import cn.ac.iscas.cloudeploy.v2.model.graph.Edge;
import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleClass;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleDefine;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class EdgeTypedGraphOperationBuilderRefacted {
	private Logger logger = LoggerFactory.getLogger(EdgeTypedGraphOperationBuilderRefacted.class);
	
	@Autowired
	private FileService fileService;
	
	/**
	 * 生成Operation定义的模板地址
	 */
	@Value("#{configs['service.operationBuilder.templateFilePath']}")
	private String templateFilePath;
	
	@Value("#{configs['service.operationBuilder.variableTemplateFilePath']}")
	private String variableFilePath;
	
	public Operation createNewOperation(Graph<Node, EdgeType> graph,
			String operationName, Map<String, ParamValue> extractParams){
		logger.info("Begining createNewOperation for " + operationName);
		Preconditions.checkArgument(graph != null,"创建Operation出错：传入的参数graph为null");
		Preconditions.checkArgument(graph.size() > 0,"创建Operation出错：传入的参数graph的size为空");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(operationName),"创建Operation出错：传入的参数operationName为null或者为空字符串");
		Operation operation = new Operation();
		operation.setName(operationName);
		operation.setParamsWithType(extractParams);
        //存储整个图中引用的所有操作，即为新生成操作的所有依赖操作。
		TreeSet<Operation> imports = Sets.newTreeSet(new Comparator<Operation>(){
			@Override
			public int compare(Operation o1, Operation o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		operation.setDefineMd5(produceOperationDefineContent(graph,
				operationName, extractParams, imports).get());
		if(imports.size()>0){
			operation.setImports(Lists.newArrayList(imports));
		}
		return operation;
	}

	private Optional<String> produceOperationDefineContent(Graph<Node, EdgeType> graph,
			String operationName, Map<String, ParamValue> extractParams,
			TreeSet<Operation> imports){
		//传给FreeMaker模板的所有数据
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("operationName", operationName);
		dataModel.put("extractParams", formatExtractParams(extractParams));
		dataModel.put("definedContent", createDefineContent(graph,imports,extractParams));
		String defineFileContent = usingTemplateProduceScript(dataModel,templateFilePath);
		logger.info(defineFileContent);
		Optional<String> optional = Optional.of(defineFileContent);
		try {
			Optional<String> md5 = Optional.of(fileService.saveFile(ByteSource.wrap(optional.get().getBytes())));
			return md5;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Operation script store failed");
		}
	}
	public Optional<String> createClassDefineFileByNode(Node node,
			String defineName, Map<String, ParamValue> extractParams){
		//传给FreeMaker模板的所有数据
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("operationName", defineName);
		dataModel.put("extractParams", formatExtractParams(extractParams));
		dataModel.put("definedContent", createClassDefineByNode(node, extractParams));
		String defineFileContent = usingTemplateProduceScript(dataModel,templateFilePath);
		logger.info(defineFileContent);
		Optional<String> optional = Optional.of(defineFileContent);
		try {
			Optional<String> md5 = Optional.of(fileService.saveFile(ByteSource.wrap(optional.get().getBytes())));
			return md5;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Operation script store failed");
		}
		}
	private String formatExtractParams(Map<String, ParamValue> extractParams) {
		if(extractParams == null) return null;
		Collection<String> params = Collections2.transform(extractParams.entrySet(), new Function<Map.Entry<String,ParamValue>,String>(){
			@Override
			public String apply(Entry<String, ParamValue> input) {
				ParamValue value = input.getValue();
				if(input.getKey().equals("image")){
					System.out.println(value.transformToScript());
				}
				if(value.getValue() == null) return "$"+input.getKey() + ",";
				return "$"+input.getKey() + "=" + value.transformToScript();
			}
		});
		return Joiner.on(System.getProperty("line.separator")).join(params);
	}

	public String usingTemplateProduceScript(Map<String, Object> dataModel, String filename){
		Preconditions.checkArgument(!Strings.isNullOrEmpty(filename), "模板地址不能为空或者null");
		Configuration cfg = new Configuration();
		try{
			TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "/");
			cfg.setTemplateLoader(templateLoader);
			Template template = cfg.getTemplate(filename);
			StringWriter writer = new StringWriter();
			template.process(dataModel, writer);
			writer.append(System.getProperty("line.separator"));
			return writer.toString();
		}catch(TemplateException e){
			e.printStackTrace();
			logger.error("there is a template exception when using" + templateFilePath + "to produce a script");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("there is a IOException when using" + templateFilePath + "tp produce a script");
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String createDefineContent(Graph<? extends Node, EdgeType> graph,
			TreeSet<Operation> imports, Map<String, ParamValue> extractParams) {
		Preconditions.checkArgument(graph != null,"创建Operation出错：传入的参数graph为null");
		Preconditions.checkArgument(graph.size() > 0,"创建Operation出错：传入的参数graph的size为空");
		String newline = System.getProperty("line.separator");
		StringBuilder defineBuilder = new StringBuilder();
		Map<? extends Node, ? extends Set> vertexs = graph.getAllVertexEdges();
		// 遍历所有节点
		for (Entry<? extends Node, ? extends Set> entry : vertexs.entrySet()) {
			Set<Edge<? extends Node, EdgeType>> list = entry.getValue();
			Node nodeHead = entry.getKey();
			checkNode(nodeHead);
			// 如果node为class类型
			if (nodeHead instanceof ModuleClass) {
				defineBuilder.append(template("class",
						((ModuleClass) nodeHead).getName(),nodeHead.getNodeName(),
						createParams(nodeHead, list,extractParams)));
				defineBuilder.append(newline);
			} else if (nodeHead instanceof ModuleDefine) {
				defineBuilder.append(template(
						((ModuleDefine) nodeHead).getName(),nodeHead.getNodeName()+"_${name}",
						nodeHead.getNodeName(), createParams(nodeHead, list,extractParams)));
				defineBuilder.append(newline);
			} else if (nodeHead instanceof Operation) {
				imports.add((Operation) nodeHead);
				defineBuilder.append(template(((Operation) nodeHead).getName(),nodeHead.getNodeName()+"_${name}",
						nodeHead.getNodeName(), createParams(nodeHead, list,extractParams)));
				defineBuilder.append(newline);
			}
		}
		return defineBuilder.toString();
	}
	private String createClassDefineByNode(Node node,
			 Map<String, ParamValue> extractParams) {
		Preconditions.checkArgument(node != null,"创建ClassDefine出错：传入的参数node为null");
		String newline = System.getProperty("line.separator");
		StringBuilder defineBuilder = new StringBuilder();
		// 遍历所有节点
			checkNode(node);
			// 如果node为class类型
			if (node instanceof ModuleClass) {
				defineBuilder.append(template("class",
						((ModuleClass) node).getName(),node.getNodeName(),
						createParams(node, null,extractParams)));
				defineBuilder.append(newline);
		}
		return defineBuilder.toString();
	}
	/**
	 * 为单个节点生成相应的Puppet代码。类似<br>
	 * $variableName+随机字符= {<br>
			resourceName =>{<br>
                    catalina_base    => $catalina_base,<br>
                  source_url    => $source_url,<br>
                    require             => Java_Install[‘java_install_name’]<br>
            }<br>
      }<br>
      if !defined(resourceType[‘resourceName’]){
         create_resources(‘resourceType’,$resourceName+随机字符)
      }
	 * @param resourceType
	 * @param resourceName
	 * @param params
	 */
	private String template(String resourceType, String resourceName,String variableName,
			String params) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(resourceType), "resourceType不能为空");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(resourceName),"resourceName不能为空");		
		int hashcode=resourceType.hashCode()
				+resourceName.hashCode()
				+(params!=null?params.hashCode():0);//尽可能的减少变量名冲突。
		variableName = "$v" + variableName+"_"+Math.abs(hashcode);
		resourceType = resourceType.equals("class") ? resourceType : "::" + resourceType;
		Map<String, Object> dataModel = new HashMap<>();
		dataModel .put("variableName", variableName);
		dataModel.put("resourceName", resourceName);
		dataModel.put("capitalizedResourceType", capitalize(resourceType));
		dataModel.put("params", params);
		dataModel.put("resourceType", resourceType);
		dataModel.put("isClassType", resourceType.equals("class"));
		String template = usingTemplateProduceScript(dataModel, variableFilePath);
		logger.info(template);
		return template;
	}
    
	
	/**
	 * 将resourceType中的所有域名的首字母大写，如resourcetype为tomcat：：instance<br>
	 * 返回值则为 Tomcat::Instance
	 * @param resourceType
	 * @return
	 */
	protected String capitalize(String resourceType) {
		Iterable<String> names = Splitter.on("::").split(resourceType);
		List<String> formatedName=new ArrayList<String>();
		for (String name : names) {
			formatedName.add(StringUtils.capitalize(name));
		}
		return Joiner.on("::").join(formatedName);
	}
	
	private void checkNode(Node nodeHead) {
		Preconditions.checkArgument(nodeHead!=null,"node不能为空");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(nodeHead.getNodeName()),"node必须包含nodeName");
		if(nodeHead instanceof ModuleDefine){
			Preconditions.checkArgument(!Strings.isNullOrEmpty(((ModuleDefine) nodeHead).getName()),"define的name不能为空");
		}
		else if(nodeHead instanceof ModuleClass){
			Preconditions.checkArgument(!Strings.isNullOrEmpty(((ModuleClass) nodeHead).getName()),"class的name不能为空");
		}
		else if(nodeHead instanceof Operation){
			Preconditions.checkArgument(!Strings.isNullOrEmpty(((Operation) nodeHead).getName()),"operation的name不能为空");
		}
	}
	
	/**
	 * 为Node节点创建Puppet Hash参数，并且添加依赖关系。类似：<br>
	 *catalina_base    => $catalina_base,<br>
      source_url    => $source_url,<br>
      require             => Java_Install[‘java_install_name’]<br>
	 * @param list
	 * @param extractParams 
	 * @return 如果startNode的参数为空时，则会返回null
	 */
	private String createParams(Node startNode, Set<Edge<? extends Node,EdgeType>> list, Map<String,ParamValue> extractParams) {
		Preconditions.checkArgument(startNode!=null,"node 不能为空");
		logger.info("createParams for node:" + startNode.getNodeName());
		String newline = System.getProperty("line.separator");
		String params = startNode.transformParamsToScript(extractParams);
		LinkedHashMultimap<EdgeType, Node> edgeTypeMaps = LinkedHashMultimap.create();
		if(list != null && list.size() > 0){
			for(Edge<? extends Node, EdgeType> edge : list){
				if(edge.getTarget() != null) edgeTypeMaps.put(edge.getType(), edge.getTarget());
			}
		}
		StringBuilder dependencyBuilder = new StringBuilder();
		for(EdgeType type : edgeTypeMaps.keySet()){
			dependencyBuilder.append(type.transform(edgeTypeMaps.get(type)));
			dependencyBuilder.append(newline);
		}
		if(params==null&& dependencyBuilder.length()==0) return null;
		params = params + newline + dependencyBuilder.toString();
		return params;
	}
}
