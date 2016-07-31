package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleClass;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleDefine;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
public class EdgeTypedGraphOperationBuilderRefactedTest {
	@Autowired
	EdgeTypedGraphOperationBuilderRefacted builder;
	@Autowired
	FileService fileService;
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateNewOperationWhenGraphNull() {
		//测试数据
		Map<String, ParamValue> extractParams=new HashMap<String, ParamValue>();
		extractParams.put("catalina_base", new ParamValue("/opt/apache-tomcat/tomcat", ParamType.STRING));
		extractParams.put("source_url", 
				new ParamValue("http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz", ParamType.STRING));
		//测试传入空graph
		builder.createNewOperation(null, "hello", extractParams);
	}
	
	@Test
	public void testCreateNewOperationWhenExtractParamsNull() throws FileNotFoundException{
		Graph<Node, EdgeType> graph=new Graph<Node, EdgeType>();
		
		String operationName="cloudeploy::default::tomcat::tomcat_install";
		Map<String, ParamValue> params=new HashMap<String, ParamValue>();
		params.put("catalina_base", new ParamValue("/opt/apache-tomcat/tomcat", ParamType.STRING));
		params.put("source_url", 
				new ParamValue("http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz", ParamType.STRING));
		
		
		ModuleClass javaClass=new ModuleClass();
		javaClass.setNodeName("java_install");
		javaClass.setParamsWithType(null);
		javaClass.setName("java");
		graph.addEdge(javaClass, null, null);

		ModuleDefine tomcat_instance=new ModuleDefine();
		tomcat_instance.setName("tomcat::instance");
		tomcat_instance.setNodeName("tomcat");
		tomcat_instance.setParamsWithType(params);
		graph.addEdge(tomcat_instance, javaClass, EdgeType.REQUIRE);
		
		Operation operation = builder.createNewOperation(graph, operationName, null);
		String md5 = operation.getDefineMd5();
		assertEquals(operationName, operation.getName());
		assertNull(operation.getImports());
		assertNotNull(fileService.findFile(md5));
	}
	
	@Test
	public void testCreateNewOperation() throws FileNotFoundException {
		Graph<Node, EdgeType> graph=new Graph<Node, EdgeType>();  
		
		String operationName="cloudeploy::default::tomcat::tomcat_install";
		Map<String, ParamValue> extractParams=new HashMap<String, ParamValue>();
		extractParams.put("catalina_base", new ParamValue("/opt/apache-tomcat/tomcat", ParamType.STRING));
		extractParams.put("source_url", 
				new ParamValue("http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz", ParamType.STRING));
		
		ModuleClass javaClass=new ModuleClass();
		javaClass.setNodeName("java_install");
		javaClass.setParamsWithType(null);
		javaClass.setName("java");
		graph.addEdge(javaClass, null, null);
		
		ModuleDefine tomcat_instance=new ModuleDefine();
		tomcat_instance.setName("tomcat::instance");
		tomcat_instance.setNodeName("tomcat");
		tomcat_instance.setParamsWithType(Maps.newHashMap(extractParams));
		graph.addEdge(tomcat_instance, javaClass, EdgeType.REQUIRE);
		
		Operation operation = builder.createNewOperation(graph, operationName, extractParams);
		String md5 = operation.getDefineMd5();
		Assert.assertEquals(operationName, operation.getName());
		Assert.assertNull(operation.getImports());
		Assert.assertNotNull(fileService.findFile(md5));
	}

	@Test
	public void testUsingTemplateProduceScript() throws IOException {
		Map dataModel = new HashMap<>();
		String variableName = "$vtestTemplateFile_12222334234";
		dataModel.put("variableName", variableName);
		dataModel.put("resourceName", "tomcat::server");
		dataModel.put("capitalizedResourceType", "Class");
		dataModel.put("resourceType", "class");
		dataModel.put("isClassType", true);
		String template = builder.usingTemplateProduceScript(dataModel, "src/main/resources/template.ftl");
		String testFile = Files.toString(new File("src/test/resources/test/variableTemplateTest.txt"), Charsets.UTF_8);
		assertEquals(testFile, template);
	}

}
