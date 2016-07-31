package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleClass;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleDefine;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.model.service.script.factory.AdjacencyListsGraphOperationBuilder;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
@Ignore
public class AdjacencyListsGraphOperationBuilderTest {
//	@Rule
//	public ExpectedException exception = ExpectedException.none();
	@Autowired
	AdjacencyListsGraphOperationBuilder builder;
	@Autowired
	FileService fileService;
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateNewOperationWhenGraphNull() {
		//测试数据
		Map<String, Object> extractParams=new HashMap<String, Object>();
		extractParams.put("catalina_base", "/opt/apache-tomcat/tomcat");
		extractParams.put("source_url", "http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz");
		//测试传入空graph
		builder.createNewOperation(null, "hello", extractParams);
	}
	
	@Test
	public void testCreateNewOperationWhenExtractParamsNull() throws FileNotFoundException{
		String operationName="cloudeploy::default::tomcat::tomcat_install";
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("catalina_base", "'/opt/apache-tomcat/tomcat'");
		params.put("source_url", "\"http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz\"");
		
		
		ModuleClass javaClass=new ModuleClass();
		javaClass.setNodeName("java_install");
		javaClass.setParamsWithType(null);
		javaClass.setName("java");
		List<Node> javaList=new ArrayList<Node>();
		javaList.add(javaClass);	
		
		ModuleDefine tomcat_instance=new ModuleDefine();
		tomcat_instance.setName("tomcat::instance");
		tomcat_instance.setNodeName("tomcat");
		tomcat_instance.setParamsWithType(transform(params));
		List<Node> tomcatList=new ArrayList<Node>();
		tomcatList.add(tomcat_instance);
		tomcatList.add(javaClass);
		
		List<List<Node>> graph=new ArrayList<List<Node>>();
		graph.add(javaList);
		graph.add(tomcatList);
		
		Operation operation = builder.createNewOperation(graph, operationName, null);
		String md5 = operation.getDefineMd5();
		Assert.assertEquals(operationName, operation.getName());
		Assert.assertNull(operation.getImports());
		Assert.assertNotNull(fileService.findFile(md5));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateNewOperationWhenNodeDataIsNull(){
		String operationName="cloudeploy::default::tomcat::tomcat_install";
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("catalina_base", "'/opt/apache-tomcat/tomcat'");
		params.put("source_url", "\"http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz\"");
		
		ModuleClass javaClass=new ModuleClass();
		javaClass.setNodeName(null);
		javaClass.setParamsWithType(null);
		javaClass.setName("java");
		List<Node> javaList=new ArrayList<Node>();
		javaList.add(javaClass);	
		
		ModuleDefine tomcat_instance=new ModuleDefine();
		tomcat_instance.setName("tomcat::instance");
		tomcat_instance.setNodeName(null);
		tomcat_instance.setParamsWithType(transform(params));
		List<Node> tomcatList=new ArrayList<Node>();
		tomcatList.add(tomcat_instance);
		tomcatList.add(javaClass);
		
		List<List<Node>> graph=new ArrayList<List<Node>>();
		graph.add(javaList);
		graph.add(tomcatList);
		builder.createNewOperation(graph, operationName, null);
	}
	
	@Test
	public void testCreateNewOperation() throws FileNotFoundException {
		String operationName="cloudeploy::default::tomcat::tomcat_install";
		Map<String, Object> extractParams=new HashMap<String, Object>();
		extractParams.put("catalina_base", "'/opt/apache-tomcat/tomcat'");
		extractParams.put("source_url", "\"http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz\"");
		ModuleClass javaClass=new ModuleClass();
		javaClass.setNodeName("java_install");
		javaClass.setParamsWithType(null);
		javaClass.setName("java");
		List<Node> javaList=new ArrayList<Node>();
		javaList.add(javaClass);	
		ModuleDefine tomcat_instance=new ModuleDefine();
		tomcat_instance.setName("tomcat::instance");
		tomcat_instance.setNodeName("tomcat");
		tomcat_instance.setParamsWithType(Maps.newHashMap(transform(Maps.newHashMap(extractParams))));
		List<Node> tomcatList=new ArrayList<Node>();
		tomcatList.add(tomcat_instance);
		tomcatList.add(javaClass);
		List<List<Node>> graph=new ArrayList<List<Node>>();
		graph.add(javaList);
		graph.add(tomcatList);
		
		Operation operation = builder.createNewOperation(graph, operationName, extractParams);
		String md5 = operation.getDefineMd5();
		Assert.assertEquals(operationName, operation.getName());
		Assert.assertNull(operation.getImports());
		Assert.assertNotNull(fileService.findFile(md5));
	}
	
	private Map<String, ParamValue> transform(Map<String, Object> params){
		return Maps.transformValues(params, new Function<Object,ParamValue>(){
			@Override
			public ParamValue apply(Object input) {
				ParamValue paramValue=new ParamValue(input,ParamType.UNKNOWN);
				return paramValue;
			}
		});
	}
}
