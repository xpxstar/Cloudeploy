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

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ImportAction;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.ScriptService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.factory.ToolFactory;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
@Ignore
public class AdjacencyListsGraphScriptServiceTest {
	@Autowired
	@Qualifier("adjacencyListsGraphScriptService")
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
		List<List<? extends Node>> graph=Lists.newArrayList();
		
		graph.add(Lists.newArrayList(tomcatInstall));
		graph.add(Lists.newArrayList(configServer,tomcatInstall));
		graph.add(Lists.newArrayList(configConnector,configServer));
		String operationName="tomcatInstallTest";
		
		scriptService.createOperation(graph, operationName, null);
	}

	@Test
	@Ignore
	public void testCreateTaskOfTomcatInstall() {
		Action action=actionDao.findByName("cloudeploy::default::tomcat::install");
		Operation tomcatInstallOperation=transformAction(action);
		tomcatInstallOperation.setNodeName("tomcat_install_operation");
		
		Action tomcatServiceAction=actionDao.findByName("cloudeploy::default::tomcat::service");
		Operation tomcatServiceOperation=transformAction(tomcatServiceAction);
		tomcatServiceOperation.setNodeName("tomcat_service_operation");
		
		List<List<Operation>> graph=Lists.newArrayList();
		graph.add(Lists.newArrayList(tomcatInstallOperation));
		graph.add(Lists.newArrayList(tomcatServiceOperation,tomcatInstallOperation));
		
		String scripts=scriptService.createTask(graph, "richard.agent1");
		try {
			 ByteSource bytesource = fileService.findFile(scripts);
			 System.out.println(bytesource.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testCreateTaskOfTomcatDelete(){
		Action action=actionDao.findByName("cloudeploy::default::tomcat::delete");
		Operation tomcatDeleteOperation=transformAction(action);
		tomcatDeleteOperation.setNodeName("tomcat_delete_operation");
		
		List<List<Operation>> graph=Lists.newArrayList();
		graph.add(Lists.newArrayList(tomcatDeleteOperation));
		
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
		
		List<List<Operation>> graph=Lists.newArrayList();
		graph.add(Lists.newArrayList(tomcatConfigOperation));
		
		String scripts=scriptService.createTask(graph, "richard.agent1");
		try {
			 ByteSource bytesource = fileService.findFile(scripts);
			 System.out.println(bytesource.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

