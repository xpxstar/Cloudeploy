package cn.ac.iscas.cloudeploy.v2.packet.type.docker;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(locations={"/application_context.xml"})
public class TypeInstaller {
	@Autowired
	private FileService fileService;
	@Autowired
	private ComponentDAO componentDAO;
	@Test
	@Ignore
	public void addMySQLType() throws IOException{
		File file = new File("src/test/resources/types/mysqltype.yaml");
		ByteSource byteSource = Files.asByteSource(file);
		Component mysqlComponent = componentDAO.findByName("cloudeploy::docker::component::package::mysql");
		String defineFileKey = fileService.saveFile(byteSource);
		mysqlComponent.setDefineFileKey(defineFileKey);
		componentDAO.save(mysqlComponent);
	}
	@Test
	public void addNginxType() throws IOException{
		File file = new File("src/test/resources/types/nginxtype.yaml");
		ByteSource byteSource = Files.asByteSource(file);
		Component nginxComponent = componentDAO.findByName("cloudeploy::docker::component::package::nginx");
		String defineFileKey = fileService.saveFile(byteSource);
		nginxComponent.setDefineFileKey(defineFileKey);
		componentDAO.save(nginxComponent);
	}
}
