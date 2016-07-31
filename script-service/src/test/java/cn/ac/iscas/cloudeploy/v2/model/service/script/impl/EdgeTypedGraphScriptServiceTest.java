package cn.ac.iscas.cloudeploy.v2.model.service.script.impl;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ImportAction;
import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.ScriptService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.factory.ToolFactory;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;
import com.google.common.io.ByteSource;
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
public class EdgeTypedGraphScriptServiceTest {
	@Autowired
	@Qualifier("edgeTypedGraphScriptService")
	private ScriptService scriptService;
	
	@Autowired
	private ActionDAO actionDao;
	
	@Autowired
	private ToolFactory toolFactory;
	
	@Autowired
	private FileService fileService;
	
	private Operation transformAction(Action action){
		Operation operation=new Operation();
		operation.setDefineMd5(action.getDefineFileKey());
		operation.setName(action.getName());
		List<ActionParam> actionParams = action.getParams();
		for (ActionParam actionParam : actionParams) {
			operation.setValueOfParams(actionParam.getParamKey(), actionParam.getDefaultValue());
			operation.setParamTypeOfParam(actionParam.getParamKey(), ParamType.typeByString(actionParam.getParamType()));
		}
		List<ImportAction> importActions = action.getImports();
		for (ImportAction importAction : importActions) {
			operation.addImports(transformAction(importAction.getImportedAction()));
		}
		return operation;
	}
	@Test
	@Ignore
	public void testCreateOperation() {
		Action action=actionDao.findByName("cloudeploy::default::tomcat::install");
		Operation tomcatInstall=transformAction(action);
		tomcatInstall.setNodeName("operation_tomcatInstall");
		toolFactory.getAllProtype();
		Node configServer=toolFactory.getNode("tomcat::config::server");
		configServer.setNodeName("configServer");
		Node configConnector=toolFactory.getNode("tomcat::config::server::connector");
		configConnector.setNodeName("configConnector");
		
		Graph<Node, EdgeType> graph=new Graph<Node, EdgeType>();
		graph.addVertex(tomcatInstall);
		graph.addEdge(configServer, tomcatInstall, EdgeType.REQUIRE);
		graph.addEdge(configConnector, configServer, EdgeType.REQUIRE);
		
		String operationName="tomcatInstallTest";
		scriptService.createOperation(graph, operationName, null);
	}

	@Test
	public void testCreateTaskOfTomcatInstall() {
		Action action=actionDao.findByName("cloudeploy::default::tomcat::install");
		Operation tomcatInstallOperation=transformAction(action);
		tomcatInstallOperation.setNodeName("tomcat_install_operation");
		
		Action tomcatServiceAction=actionDao.findByName("cloudeploy::default::tomcat::service");
		Operation tomcatServiceOperation=transformAction(tomcatServiceAction);
		tomcatServiceOperation.setNodeName("tomcat_service_operation");
		
		Graph<Operation, EdgeType> graph=new Graph<Operation, EdgeType>();
		
		graph.addVertex(tomcatInstallOperation);
		graph.addEdge(tomcatServiceOperation, tomcatInstallOperation, EdgeType.REQUIRE);
		
		String scripts=scriptService.createTask(graph, "richard.agent1");
		try {
			 ByteSource bytesource = fileService.findFile(scripts);
			 System.out.println(bytesource.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateTaskOfTomcatDelete(){
		Action action=actionDao.findByName("cloudeploy::default::tomcat::delete");
		Operation tomcatDeleteOperation=transformAction(action);
		tomcatDeleteOperation.setNodeName("tomcat_delete_operation");
		
		Graph<Operation, EdgeType> graph=new Graph<Operation, EdgeType>();
		graph.addVertex(tomcatDeleteOperation);
		
		String scripts=scriptService.createTask(graph, "richard.agent1");
		try {
			 ByteSource bytesource = fileService.findFile(scripts);
			 System.out.println(bytesource.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateTaskOfTomcatConfig(){
		Action action=actionDao.findByName("cloudeploy::default::tomcat::config");
		Operation tomcatConfigOperation=transformAction(action);
		tomcatConfigOperation.setNodeName("tomcat_config_operation");
		
		Graph<Operation, EdgeType> graph=new Graph<Operation, EdgeType>();
		graph.addVertex(tomcatConfigOperation);
		
		String scripts=scriptService.createTask(graph, "richard.agent1");
		try {
			 ByteSource bytesource = fileService.findFile(scripts);
			 System.out.println(bytesource.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
