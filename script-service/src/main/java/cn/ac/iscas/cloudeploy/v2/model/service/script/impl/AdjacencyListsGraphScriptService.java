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
import cn.ac.iscas.cloudeploy.v2.model.service.script.factory.AdjacencyListsGraphOperationBuilder;
import cn.ac.iscas.cloudeploy.v2.model.service.script.service.OperationService;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetClass;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetParser;
@Service
public class AdjacencyListsGraphScriptService implements ScriptService{
	private Logger logger=LoggerFactory.getLogger(AdjacencyListsGraphScriptService.class);
	@Autowired
	private AdjacencyListsGraphOperationBuilder opBuilder;
	
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
	public Operation createOperation(List<List<? extends Node>> graph,
			String operationName, Map extractParams) {
		return opBuilder.createNewOperation(graph, operationName, extractParams);
	}

	@Override
	public String createTask(List<List<Operation>> graph,String hostname) {
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
		imports.addAll(dependedOperation(imports));
		try {
			for (Operation operation : imports) {
				byteSources.add(fileService.findFile(operation.getDefineMd5()));
			}
			byteSources.add(ByteSource.wrap(builder.toString().getBytes()));
			String md5Key=fileService.saveFile(ByteSource.concat(byteSources));
			return md5Key;
		} catch (IOException e) {
			logger.error("OperationBuilder存储文件失败");
			e.printStackTrace();
		}
	    return null;
	}
	
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
	public Operation createOperation(Graph<Node, EdgeType> graph,
			String operationName, Map extractParams) {
		throw  new UnsupportedOperationException("AdjacencyListsGraphScriptService 不支持非邻接链表的图");
	}

	@Override
	public String createTask(Graph<Operation, EdgeType> graph,
			String hostname) {
		throw  new UnsupportedOperationException("AdjacencyListsGraphScriptService 不支持非邻接链表的图");
	}
}
