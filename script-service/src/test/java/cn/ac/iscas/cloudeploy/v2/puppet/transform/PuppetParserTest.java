package cn.ac.iscas.cloudeploy.v2.puppet.transform;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
@Ignore
public class PuppetParserTest {
	PuppetParser parser;
	String rubyEnvironment;
	String PuppetParserSource;
	String PuppetAnalyseRuby;
	@Before
	public void initPuppetParser(){
		parser=new PuppetParser("D:/tmp/consul_template","consul_template");
//		parser=new PuppetParser("D:/tmp/redis","redis");
//		parser=new PuppetParser("D:/tmp/nginx","nginx");
//		parser=new PuppetParser("D:/tmp/mysql","mysql");
//		parser=new PuppetParser("D:/tmp/java","java");
//		parser=new PuppetParser("D:/tmp/bootstrap", "bootstrap");
		rubyEnvironment = "E:\\ruby\\Ruby21-x64\\bin\\ruby.exe";
		PuppetParserSource = "D:\\puppet_parser\\resource";
		PuppetAnalyseRuby = "D:\\puppet_parser\\main\\first_ruby.rb";
	}
	@Test
	@Ignore
	public void testExtractTrees() {
		System.out.println("+++testExtractTrees+++++");
		parser.setTrees(parser.extractTrees("parser/redis/tree.yaml"));
		List<ResouceType> trees = parser.getTrees();
		for (ResouceType resouceType : trees) {
			System.out.println(resouceType.getName()+" "+resouceType.getType());
		}
	}

	@Test
	@Ignore
	public void testExtractParams() {
		System.out.println("+++testExtractParams+++++");
//		parser.setTrees(parser.extractTrees("parser/tomcat/tree.yaml"));
		List<PuppetClass> params=parser.extractParams("parser/tomcat/param.yaml");
		for (PuppetClass puppetClass : params) {
			System.out.println("----------------------");
			System.out.println(puppetClass.getKlass());
			System.out.println(puppetClass.getParams());
		}
	}

	@Test
	@Ignore
	public void testCancelVariable() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetValueByTraversalTreeListOfString() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetValueByTraversalTreeString() {
		System.out.println("+++testGetValueByTraversalTreeString+++++");
		parser.setTrees(parser.extractTrees("D:/tmp/tree1.yaml"));
		Object value = parser.getValueByTraversalTree("::tomcat::config::server::connector::attributes_to_remove");
		System.out.println(value);
	}
	@Test
	public void testParser(){
		parser.parser(rubyEnvironment,PuppetParserSource,PuppetAnalyseRuby);
	}

	@Test
	@Ignore
	public void testUsingRubyAnalyse() {
		try {
			parser.usingRubyAnalyse("D:/tmp/tomcat",rubyEnvironment,PuppetParserSource,PuppetAnalyseRuby);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	@Ignore
	public void testPreProcess() {
		fail("Not yet implemented");
	}
}
