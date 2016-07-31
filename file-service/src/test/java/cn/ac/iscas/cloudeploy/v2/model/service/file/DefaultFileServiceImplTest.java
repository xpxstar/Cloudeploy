package cn.ac.iscas.cloudeploy.v2.model.service.file;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.io.ByteSource;
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
public class DefaultFileServiceImplTest {
	@Autowired
	private FileService fileService;
	@Test
	public void testSaveFile() throws IOException {
//		String test="testbytesourcestring";
//		ByteSource byteSource=ByteSource.wrap(test.getBytes());
		fileService.findFile("80711ad71553ba30e2f6533ee8c7259a");
//		fileService.saveFile(byteSource);
	}

}
