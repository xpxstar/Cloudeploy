package cn.ac.iscas.cloudeploy.v2.packet.type.docker;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ImageInfo;

import cn.ac.iscas.cloudeploy.v2.model.dao.typeTrans.TypeImpleDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;
import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention.TypeImplementionBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.util.JsonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(locations={"/application_context.xml"})
public class TypeImpleInstaller {
	@Autowired
	private TypeImpleDAO dao;
	@Test
	@Ignore
	public void addTomcatTypeImpl() throws DockerException, InterruptedException, JsonProcessingException{
		TypeImplementionBuilder builder = TypeImplemention.builder();
		builder.interfaceName("install").nodeType("cloudeploy::docker::component::package::tomcat").startegy("docker");
		DockerClient client = DefaultDockerClient.builder()
				.uri("http://" + "133.133.134.103:2375")
				.build();
		ImageInfo imageInfo = client.inspectImage("xpxstar/tomcat");
		builder.metaJson(JsonUtils.convertToJson(imageInfo));
		dao.save(builder.build());
	}
	
	@Test
	@Ignore
	public void addMySQLTypeImpl() throws DockerException, InterruptedException, JsonProcessingException{
		TypeImplementionBuilder builder = TypeImplemention.builder();
		builder.interfaceName("install").nodeType("cloudeploy::docker::component::package::mysql").startegy("docker");
		DockerClient client = DefaultDockerClient.builder()
				.uri("http://" + "133.133.134.103:2375")
				.build();
		ImageInfo imageInfo = client.inspectImage("xpxstar/mysql");
		builder.metaJson(JsonUtils.convertToJson(imageInfo));
		dao.save(builder.build());
	}
	
	@Test
	public void addNginxTypeImpl() throws DockerException, InterruptedException, JsonProcessingException{
		TypeImplementionBuilder builder = TypeImplemention.builder();
		builder.interfaceName("install").nodeType("cloudeploy::docker::component::package::nginx").startegy("docker");
		DockerClient client = DefaultDockerClient.builder()
				.uri("http://" + "133.133.134.171:2375")
				.build();
		ImageInfo imageInfo = client.inspectImage("docker.io/nginx");
//		builder.metaJson(JsonUtils.convertToJson(imageInfo));
//		dao.save(builder.build());
		System.out.println("start");
		System.out.println(JsonUtils.convertToJson(imageInfo));
	}
}
