package cn.ac.iscas.cloudeploy.v2.test.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.ac.iscas.cloudeploy.v2.model.dao.task.TaskNodeDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskNode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/contextDBTest.xml" })
public class TaskNodeDAOTest {
	@Autowired
	private TaskNodeDAO taskNodeDAO;

	@Test
	public void findByIdentifiers_test() {
		 taskNodeDAO.findAll();
		List<TaskNode> nodes = (List<TaskNode>) taskNodeDAO.findAll();
		if (nodes.size() > 0) {
			assertEquals(
					taskNodeDAO.findByIdentifiers(
							Arrays.asList(nodes.get(0).getIdentifier())).size(),
					1);
		} else {
			assertEquals(taskNodeDAO.findByIdentifiers(Arrays.asList("aaa"))
					.size(), 0);
		}
	}
}
