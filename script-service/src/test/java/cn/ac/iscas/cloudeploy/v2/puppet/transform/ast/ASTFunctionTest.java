package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class ASTFunctionTest {
	@SuppressWarnings("unused")
	@Test
	public void test() throws FileNotFoundException {
		String content=PreProcessUtil.preProcess(new File("src/test/resources/ast/ASTFunction.yaml"));
		Yaml yaml=new Yaml();
		ASTFunction array = (ASTFunction) yaml.load(content);
	}

}
