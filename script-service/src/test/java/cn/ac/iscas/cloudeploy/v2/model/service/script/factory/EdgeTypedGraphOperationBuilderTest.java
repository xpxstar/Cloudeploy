package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
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

import com.google.common.base.Function;
import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
@Ignore
public class EdgeTypedGraphOperationBuilderTest {
	
	@Autowired
	EdgeTypedGraphOperationBuilder builder;
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
		Graph<Node, EdgeType> graph=new Graph<Node, EdgeType>();
		
		String operationName="cloudeploy::default::tomcat::tomcat_install";
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("catalina_base", "'/opt/apache-tomcat/tomcat'");
		params.put("source_url", "\"http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz\"");
		
		
		ModuleClass javaClass=new ModuleClass();
		javaClass.setNodeName("java_install");
		javaClass.setParamsWithType(null);
		javaClass.setName("java");
		graph.addEdge(javaClass, null, null);

		ModuleDefine tomcat_instance=new ModuleDefine();
		tomcat_instance.setName("tomcat::instance");
		tomcat_instance.setNodeName("tomcat");
		tomcat_instance.setParamsWithType(transform(params));
		graph.addEdge(tomcat_instance, javaClass, EdgeType.REQUIRE);
		
		Operation operation = builder.createNewOperation(graph, operationName, null);
		String md5 = operation.getDefineMd5();
		assertEquals(operationName, operation.getName());
		assertNull(operation.getImports());
		assertNotNull(fileService.findFile(md5));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateNewOperationWhenNodeDataIsNull(){
		Graph<Node, EdgeType> graph=new Graph<Node, EdgeType>();
		
		String operationName="cloudeploy::default::tomcat::tomcat_install";
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("catalina_base", "'/opt/apache-tomcat/tomcat'");
		params.put("source_url", "\"http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz\"");
		
		ModuleClass javaClass=new ModuleClass();
		javaClass.setNodeName(null);
		javaClass.setParamsWithType(null);
		javaClass.setName("java");
		graph.addEdge(javaClass, null, null);	
		
		ModuleDefine tomcat_instance=new ModuleDefine();
		tomcat_instance.setName("tomcat::instance");
		tomcat_instance.setNodeName(null);
		tomcat_instance.setParamsWithType(transform(params));
		graph.addEdge(tomcat_instance, javaClass, EdgeType.REQUIRE);
		
		builder.createNewOperation(graph, operationName, null);
	}
	
	@Test
	public void testCreateNewOperation() throws FileNotFoundException {
		Graph<Node, EdgeType> graph=new Graph<Node, EdgeType>();  
		
		String operationName="cloudeploy::default::tomcat::tomcat_install";
		Map<String, Object> extractParams=new HashMap<String, Object>();
		extractParams.put("catalina_base", "'/opt/apache-tomcat/tomcat'");
		extractParams.put("source_url", "http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz");
		
		ModuleClass javaClass=new ModuleClass();
		javaClass.setNodeName("java_install");
		javaClass.setParamsWithType(null);
		javaClass.setName("java");
		graph.addEdge(javaClass, null, null);
		
		ModuleDefine tomcat_instance=new ModuleDefine();
		tomcat_instance.setName("tomcat::instance");
		tomcat_instance.setNodeName("tomcat");
		tomcat_instance.setParamsWithType(Maps.newHashMap(transform(Maps.newHashMap(extractParams))));
		graph.addEdge(tomcat_instance, javaClass, EdgeType.REQUIRE);
		System.out.println("come from out:add edge:"+javaClass+"---"+null);
		System.out.println("come from outside:"+graph.getClass().getName());
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
