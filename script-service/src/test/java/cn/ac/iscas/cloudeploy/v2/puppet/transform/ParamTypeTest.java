package cn.ac.iscas.cloudeploy.v2.puppet.transform;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParamTypeTest {

	@Test
	public void test() {
		assertTrue(ParamType.ARRAY.verify("[a,b,c]"));
		assertFalse(ParamType.ARRAY.verify("[a,b,c"));
	}
	
}
