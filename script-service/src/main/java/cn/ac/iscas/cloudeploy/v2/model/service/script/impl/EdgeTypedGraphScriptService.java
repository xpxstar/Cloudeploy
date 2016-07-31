package cn.ac.iscas.cloudeploy.v2.model.service.script.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;

import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.ScriptService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleClass;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleDefine;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.model.service.script.factory.EdgeTypedGraphOperationBuilderRefacted;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetClass;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetParser;
/**
 * Using for transform a graph to a executable puppet script.<br>
 * Don't support adjacency lists graph, if the graph parameter is a adjacency lists graph,
 * it will throw a UnsupportedOperationException.
 * @author RichardLcc
 *
 */
/**
 * @description
 * @author xpxstar@gmail.com
 * 2015年12月14日 下午4:12:13
 */
@Service
public class EdgeTypedGraphScriptService implements ScriptService{
	private Logger logger=LoggerFactory.getLogger(EdgeTypedGraphScriptService.class);
	
	@Autowired
	private EdgeTypedGraphOperationBuilderRefacted opBuilder;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ConfigService configService;

	private static String PuppetParserSource;
	private static String PuppetAnalyseRuby;
	private static String rubyEnvironment;
	@PostConstruct
	private void init() {
		rubyEnvironment = configService
				.getConfigAsString(ConfigKeys.RUBY_ENVIRONMENT);
		PuppetParserSource = configService
				.getConfigAsString(ConfigKeys.PUPPET_PARSER_SOURCE);
		PuppetAnalyseRuby = configService
				.getConfigAsString(ConfigKeys.PUPPET_ANALYSE_RUBY);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Operation createOperation(Graph<Node, EdgeType> graph,
			String operationName, Map extractParams) {
		return opBuilder.createNewOperation(graph, operationName, extractParams);
	}

	@Override
	public String createTask(Graph<Operation, EdgeType> graph,String hostname) {
		logger.info("create task script for host:"+hostname);
		List<ByteSource> byteSources=Lists.newArrayList();
		TreeSet<Operation> imports = Sets.newTreeSet(new Comparator<Operation>() {
			@Override
			public int compare(Operation o1, Operation o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		StringBuilder builder=new StringBuilder();
		String changeLine=System.getProperty("line.separator");
		
		builder.append(changeLine+"node '"+hostname+"'{"+changeLine);
		String definedContent=opBuilder.createDefineContent(graph,imports,null);
		builder.append(definedContent);
		builder.append(changeLine+"}");
		
		logger.debug(builder.toString());
		
		imports.addAll(dependedOperation(imports));
		try {
			for (Operation operation : imports) {
				if(operation.getDefineMd5() != null){
					byteSources.add(fileService.findFile(operation.getDefineMd5()));
				}
			}
			byteSources.add(ByteSource.wrap(builder.toString().getBytes()));
			String md5Key=fileService.saveFile(ByteSource.concat(byteSources));
			logger.info("the md5key of this task script is"+md5Key);
			logger.info("create task script successful");
			return md5Key;
		} catch (IOException e) {
			logger.error("store task script file failed");
			e.printStackTrace();
		}
	    return null;
	}
	
	/**
	 * To get all operation a task depended on.<br>
	 * this method rely on  the method to store a operation. 
	 * you need store all operation including the operation recursive depended on when you store a operation.
	 * if you change the store method, you also need to change this method.
	 * @param imports
	 * @return
	 */
	//TODO be optimized is possible
	private TreeSet<Operation> dependedOperation(TreeSet<Operation> imports){
		TreeSet<Operation> dependedOperations = Sets.newTreeSet(new Comparator<Operation>() {
			@Override
			public int compare(Operation o1, Operation o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for(Operation operation:imports){
			if(operation.getImports()!=null)
				dependedOperations.addAll(operation.getImports());
		}
		return dependedOperations;
	}
	

	@Override
	public List<Node> analysisModule(String moduleFilePath,String moduleName) {
		PuppetParser parser=new PuppetParser(moduleFilePath, moduleName);
		List<PuppetClass> klasses = parser.parser(rubyEnvironment,PuppetParserSource,PuppetAnalyseRuby);
		List<Node> nodes=new ArrayList<Node>();
		for (PuppetClass puppetClass : klasses) {
			switch (puppetClass.getType()) {
			case "definition":
				ModuleDefine defineNode=new ModuleDefine(puppetClass.getKlass(),puppetClass.getParams());
				nodes.add(defineNode);
				break;
			case "hostclass":
				ModuleClass classNode=new ModuleClass(puppetClass.getKlass(),puppetClass.getParams());
				classNode.setNodeName(classNode.getName());
				nodes.add(classNode);
				break;
			default:
				logger.error("分析Module中发现不匹配的新类型"+puppetClass.getType());
				break;
			}
		}
		return nodes;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Operation createOperation(List<List<? extends Node>> graph,
			String operationName, Map extractParams) {
		throw new UnsupportedOperationException("EdgeTypedGraphScriptService 不支持邻接链表格式的图");
	}

	@Override
	public String createTask(List<List<Operation>> graph,
			String hostname) {
		throw new UnsupportedOperationException("EdgeTypedGraphScriptService 不支持邻接链表格式的图");
	}
	public String createClassDefinefile(Node node,
			String defineName, Map<String, ParamValue> extractParams){
		return opBuilder.createClassDefineFileByNode(node, defineName, extractParams).get();
	}
}
