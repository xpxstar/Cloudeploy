package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class ASTASTArrayTest {
	
	@Test
	public void testGetChildren() throws FileNotFoundException {
		Yaml yaml=new Yaml();
		ASTASTArray array = (ASTASTArray) yaml.load(new FileReader("src/test/resources/ast/ASTASTArray.yaml"));
		assertEquals(array.getLine(), 16);
		assertNull(array.getFile());
		assertNotNull(array.getChildren());
	}

}
