package cn.ac.iscas.cloudeploy.v2.test.dao;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.ac.iscas.cloudeploy.v2.model.dao.host.DHostDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/contextDBTest.xml" })
public class DHostDAOTest {
	@Autowired
	private DHostDAO dHostDAO;

	@Test
	public void findHostsByIds() {
		List<DHost> hostsInDB = (List<DHost>) dHostDAO.findAll();
		if (hostsInDB.size() > 0) {
			assertEquals(
					dHostDAO.findByIds(Arrays.asList(hostsInDB.get(0).getId()))
							.size(), 1);
		}
	}
}
