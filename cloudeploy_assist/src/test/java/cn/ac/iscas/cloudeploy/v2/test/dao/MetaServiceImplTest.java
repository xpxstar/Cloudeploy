package cn.ac.iscas.cloudeploy.v2.test.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.ac.iscas.cloudeploy.v2.model.entity.Meta;
import cn.ac.iscas.cloudeploy.v2.model.service.MetaServiceImpl;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application_context.xml")
public class MetaServiceImplTest {
	@Autowired
	private MetaServiceImpl metaService;
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    @Test
	public void test()
    {    	List<Meta> result = metaService.getByComponent("redis");
    	for (Meta meta : result) {
			System.out.println(meta);
		}
    	
    }
}
