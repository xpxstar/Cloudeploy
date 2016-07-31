package cn.ac.iscas.cloudeploy.v2.packet.builder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.packet.builder.puppet.VMPuppetPacketBuilder;
import cn.ac.iscas.cloudeploy.v2.util.YamlUtils;
import cn.ac.iscas.cloudeploy.v2.workflowEngine.entity.Deployment;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
public class VMPuppetPacketBuilderTest {
	@Autowired
	VMPuppetPacketBuilder builder;
	@Test
	public void testBuildDeployment() throws IOException {
		Path path = Paths.get("src/test/resources/modelExample.yaml");
		ServiceTemplate application = YamlUtils.loadAs(path, ServiceTemplate.class);
		Deployment deployment = builder.buildDeployment(application);
	}
}
