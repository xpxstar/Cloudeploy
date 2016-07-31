//package cn.ac.iscas.cloudeploy.v2.workflowEngine.impl;
//
//import static org.junit.Assert.*;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//import org.yaml.snakeyaml.TypeDescription;
//import org.yaml.snakeyaml.Yaml;
//import org.yaml.snakeyaml.constructor.Constructor;
//import org.yaml.snakeyaml.introspector.BeanAccess;
//
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
//import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
//import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
//import cn.ac.iscas.cloudeploy.v2.user.security.SecurityUtils;
//import cn.ac.iscas.cloudeploy.v2.user.security.Subject;
//@RunWith(SpringJUnit4ClassRunner.class)
//@ActiveProfiles("dev")
//@ContextConfiguration("/application_context.xml")
//@Transactional
//public class VMTaskPluginTest {
//	@Autowired
//	private VMTaskPlugin vmTaskPlugin;
//	
//	@Before
//	public void initEnv(){
//		User user = new User();
//		user.setId(1L);
//		user.setName("gaoqiang");
//		user.setEmail("gaoqiang11@otcaix.iscas.ac.cn");
//		SecurityUtils.setSubject(new Subject(user));
//	}
//	@Test
//	public void test() throws IOException {
//		ServiceTemplate template = getTemplate();
//		NodeTemplate nodeTemplate = template.getTopology_template()
//				.getNode_templates().get(0);
////		vmTaskPlugin.createTask(template, nodeTemplate);
//	}
//	
//	private ServiceTemplate getTemplate() throws IOException {
//		URL resource = getClass().getResource("/modelExample.yaml");
//		assertNotNull(resource);
//		InputStream ins = resource.openStream();
//		Constructor constructor = new Constructor(ServiceTemplate.class);
//		TypeDescription typeDescription = new TypeDescription(ServiceTemplate.class);
//		constructor.addTypeDescription(typeDescription);
//		Yaml yaml = new Yaml(constructor);
//		yaml.setBeanAccess(BeanAccess.FIELD);
//		ServiceTemplate template = (ServiceTemplate) yaml.load(ins);
//		return template;
//	}
//}
