package cn.ac.iscas.cloudeploy.v2.packet.type.docker;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(locations={"/application_context.xml"})
@Transactional
public class DockerTransferTest {
	
	@Autowired
	DockerTransfer dockerTransfer;
	@Test
	public void testGetTypeImplementions() {
		List<TypeImplemention> typeImplementions = dockerTransfer.getTypeImplementions("cloudeploy::docker::component::package::tomcat");
		assertEquals(1, typeImplementions.size());
	}

	@Test
	public void testGetImplementions() {
		Map<String, TypeImplemention> implementions = dockerTransfer.getImplementions("cloudeploy::docker::component::package::tomcat");
		assertEquals(1, implementions.size());
	}

	@Test
	public void testGetImageInfo() throws IOException {
		Map<String, TypeImplemention> implementions = dockerTransfer.getImplementions("cloudeploy::docker::component::package::tomcat");
		dockerTransfer.getImageInfo(implementions.get("install"));
	}
}
