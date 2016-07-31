package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
public class OperationBuilderFactoryTest {
	
	@Autowired
	private OperationBuilderFactory factory;
	
	@Test
	public void testProduceAllOperation() throws FileNotFoundException{
		factory.getAllModules();
		List<Operation> operations = factory.produceOperations("src/main/resources/operationDefine/consul_template_define.yaml");
		assertNotNull(operations);
	}
}
