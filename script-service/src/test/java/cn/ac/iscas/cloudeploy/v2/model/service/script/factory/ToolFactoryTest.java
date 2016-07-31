package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.factory.ToolFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
public class ToolFactoryTest {
	@Autowired
	private ToolFactory toolFactory;
	@Autowired
	private ToolFactory toolfactorys;
	
	@Test
	@Ignore
	public void testGetAllProtype() {
		toolFactory.getAllProtype();
	}
	
	@Test
	@Ignore
	public void testProduceOpertaion() throws FileNotFoundException{
		toolFactory.getAllProtype();
		List<Operation> operations=toolFactory.produceOperations("src/main/resources/operationDefine/consul_define.yaml");
		for (Operation operation : operations) {
			System.out.println(operation.getDefineMd5());
		}
	}
	
	@Test
	@Ignore
	public void produceAllOperation() throws FileNotFoundException{
		toolFactory.getAllProtype();
		File operationDefines=new File("src/main/resources/operationDefine/");
		File[] defines = operationDefines.listFiles();
		for (int i = 0; i < defines.length; i++) {
			toolFactory.produceOperations(defines[i].getAbsolutePath());
		}
	}
	
	@Test
	public void testYamlLoasAllMethod(){
		
	}
}
