package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;

import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleClass;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleDefine;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 用于生成Operation Node或者创建新的Operation
 * @author RichardLcc
 *
 */
@Service
public class AdjacencyListsGraphOperationBuilder {
	private Logger logger=LoggerFactory.getLogger(AdjacencyListsGraphOperationBuilder.class);
	/**
	 * 文件服务
	 */
	@Autowired
	private FileService fileService;
    
	/**
	 * 生成Operation定义的模板地址
	 */
	@Value("#{configs['service.operationBuilder.templateFilePath']}")
	private String templateFilePath;
	
	
	/**
	 * 创建新的Operation, graph可以由module class or module define or operation 组成
	 * @param extractParams 操作需要暴露出去的参数，及其默认值
	 * @exception IllegalArgumentException 传入空graph，graph中不存在node，或者operation为空或空串 均会抛出此异常。
	 * @return 有可能返回空值。
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Operation createNewOperation(List<? extends List<? extends Node>> graph,
			String operationName, Map<String,Object> extractParams) {
		logger.info("进入createNewOperation: operationName为："+operationName);
		Preconditions.checkArgument(graph != null,
				"创建Operation出错：传入的参数graph为null");
		Preconditions.checkArgument(graph.size() > 0,
				"创建Operation出错：传入的参数graph的size为空");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(operationName),
				"创建Operation出错：传入的参数operationName为null或者为空字符串");
        //存储整个图中引用的所有操作，即为新生成操作的所有依赖操作。
		TreeSet<Operation> imports = Sets.newTreeSet(new Comparator<Operation>(){
			@Override
			public int compare(Operation o1, Operation o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		Map<String, ParamValue> operationParams = Maps.newHashMap();
		if (extractParams != null) {
			for (Entry<String, Object> itemEntry : extractParams.entrySet()) {
				ParamValue value = new ParamValue(itemEntry.getValue(),
						ParamType.UNKNOWN);
				operationParams.put(itemEntry.getKey(), value);
			}
		}
		
		
		String definedContent=createDefineContent(graph,imports,operationParams);
        
		//传给FreeMaker模板的所有数据
		Map dataModel = new HashMap();
		dataModel.put("operationName", operationName);
		
		//格式化extractParams
		dataModel.put("extractParams", HashParamsUtil.toStringAsVariableFormat(operationParams));
		dataModel.put("definedContent", definedContent);

		String defineFileContent = produceOperationDefine(dataModel);
		logger.info(defineFileContent);

		if (defineFileContent == null)
			return null;
		Operation operation = new Operation();
		if(imports.size()>0){
			operation.setImports(Lists.newArrayList(imports));
		}
		operation.setName(operationName);
		operation.setParamsWithType(operationParams);
		try {
			operation.setDefineMd5(fileService.saveFile(ByteSource.wrap(defineFileContent.getBytes())));
		} catch (IOException e) {
			logger.error("OperationBuilder存储文件失败");
			e.printStackTrace();
		}
		return operation;
	}
	
	/**
	 * 生成Operation定义的内容。
	 * @param graph
	 * @param imports 存储定义生成过程中引用到的所有操作，创建操作时传入值不能为空，只有以任务形式调用该方法时才可为空。
	 * @param extractParams
	 * @return operation定义的内容
	 * @exception IllegalArgumentException 传入空graph，graph中不存在node，
	 */
	public String createDefineContent(List<? extends List<? extends Node>> graph,Set<Operation> imports, Map<String,ParamValue> extractParams){
		Preconditions.checkArgument(graph != null,
				"创建Operation出错：传入的参数graph为null");
		Preconditions.checkArgument(graph.size() > 0,
				"创建Operation出错：传入的参数graph的size为空");
		String newline = System.getProperty("line.separator");
		StringBuilder defineBuilder = new StringBuilder();
		
		// 遍历所有节点
		for (List<? extends Node> list : graph) {
			if (list.size() <= 0)
				continue;
			// 获取起始节点
			Node nodeHead = list.get(0);
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
	 * 利用FreeMaker模板生成Operation完整的定义，并且存储定义文件
	 * 
	 * @return
	 * Operation的定义内容
	 * @throws IllegalArgumentException
	 * service.operationBuilder.templateFilePath配置错误
	 */
	@SuppressWarnings("rawtypes")
	private String produceOperationDefine(Map params) {
		if (templateFilePath != null && !templateFilePath.equals("")) {
			Configuration cfg = new Configuration();
			try {
				Template template = cfg.getTemplate(templateFilePath);
				StringWriter writer = new StringWriter();
				template.process(params, writer);
				writer.append(System.getProperty("line.separator"));
				return writer.toString();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(templateFilePath + "对应的模板无法加载");
			} catch (TemplateException e) {
				e.printStackTrace();
				logger.error("自动生成" + templateFilePath + "脚本出错");
			}
		}
		throw new IllegalArgumentException("service.operationBuilder.templateFilePath配置错误，请查看配置文件application.properties中是否配置该项");
	}
	/**
	 * 为单个节点生成相应的Puppet代码。类似<br>
	 * $resourceName+随机字符= {<br>
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
		
		StringBuilder defineBuilder = new StringBuilder();
		String newline = System.getProperty("line.separator");
		int hashcode=resourceType.hashCode()
				+resourceName.hashCode()
				+(params!=null?params.hashCode():0);//尽可能的减少变量名冲突。
		variableName = "$v" + variableName+"_"+Math.abs(hashcode);
		defineBuilder.append(variableName + "= {");
		defineBuilder.append(newline);
		defineBuilder.append("\""+resourceName + "\" =>{");
		defineBuilder.append(newline);
		defineBuilder.append(params!=null?params:"");
		defineBuilder.append("}");
		defineBuilder.append(newline);
		defineBuilder.append("}");
		defineBuilder.append(newline);
		defineBuilder.append("if !defined("
				+ capitalize(resourceType) + "[\"" + resourceName
				+ "\"]){");
		defineBuilder.append(newline);
		defineBuilder.append("create_resources('"+
					(resourceType.equals("class")?"class":"::"+resourceType)
					+"'," + variableName + ")");
		defineBuilder.append(newline);
		if(resourceType.equals("class")){
			defineBuilder.append("contain "+ resourceName);
		}
		defineBuilder.append(newline + "}");
		logger.info(defineBuilder.toString());
		return defineBuilder.toString();
	}
    
	/**
	 * 将resourceType中的所有域名的首字母大写，如resourcetype为tomcat：：instance<br>
	 * 返回值则为 Tomcat::Instance
	 * @param resourceType
	 * @return
	 */
	private String capitalize(String resourceType) {
		Iterable<String> names = Splitter.on("::").split(resourceType);
		List<String> formatedName=new ArrayList<String>();
		for (String name : names) {
			formatedName.add(StringUtils.capitalize(name));
		}
		return Joiner.on("::").join(formatedName);
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
	private String createParams(Node startNode, List<? extends Node> list, Map<String,ParamValue> extractParams) {
		Preconditions.checkArgument(startNode!=null,"node 不能为空");
		logger.info("进入createParams: \nstartNode:" + startNode.getNodeName());
		String newline = System.getProperty("line.separator");
		String params = HashParamsUtil.toStringAsHashFormat(startNode,extractParams);
		StringBuilder requireBuilder = new StringBuilder();
		if (list.size() > 1) {
			requireBuilder.append("require =>");
			if (list.size() > 2)
				requireBuilder.append("[");
			for (Node node : list.subList(1, list.size())) {
				checkNode(node);
				if (node instanceof ModuleClass) {
					requireBuilder.append("Class["
							+ ((ModuleClass) node).getName() + "],");
				} else if (node instanceof ModuleDefine) {
					String defineName=capitalize(((ModuleDefine) node).getName());
					requireBuilder.append(defineName + "[\"" + node.getNodeName()
							+ "_${name}\"],");
				} else if (node instanceof Operation) {
					String operationName =capitalize(((Operation) node).getName());
					requireBuilder.append(operationName + "[\""
							+ node.getNodeName() + "_${name}\"],");
				}
			}
			if (list.size() > 2)
				requireBuilder.append("],");
			requireBuilder.append(newline);
		}
		if(params==null&& requireBuilder.length()==0) return null;
		params = params + newline + requireBuilder.toString();
		return params;
	}
}
