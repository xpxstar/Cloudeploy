package cn.ac.iscas.cloudeploy.v2.puppet.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Charsets;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTASTArray;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTASTHash;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTBlockExpression;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTBoolean;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTConcat;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTFunction;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTHashOrArrayAccess;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTName;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTOperator;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTSelector;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTString;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTUndef;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTVarDef;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTVariable;

public class PuppetParser {
	private Logger logger=LoggerFactory.getLogger(PuppetParser.class);
	private Yaml yaml=new Yaml();
	private String inputFilePath;
	private String paramOutFilePath;
	private String treeOutFilePath;
	private String resultFilePath;
	private List<ResouceType> trees;
	private List<PuppetClass> classes;
	
	public PuppetParser(String inputFilePath,String moduleName){
		File file=new File(System.getProperty("user.dir")+"\\parser",moduleName);
		if(!file.exists()){
			file.mkdirs();
		}
		this.inputFilePath=inputFilePath;
		paramOutFilePath=file.getAbsolutePath()+"\\param.yaml";
		treeOutFilePath=file.getAbsolutePath()+"\\tree.yaml";
		resultFilePath=file.getAbsolutePath()+"\\result.yaml";
	}
	
	/**
	 * 解析module
	 * @return 
	 */
	public List<PuppetClass> parser(String rubyEnvironment,String PuppetParserSource,String PuppetAnalyseRuby){
		try {
			usingRubyAnalyse(inputFilePath,rubyEnvironment,PuppetParserSource,PuppetAnalyseRuby);
			trees=extractTrees(treeOutFilePath);
			classes=extractParams(paramOutFilePath);
			Files.write(yaml.dumpAll(classes.iterator()), new File(resultFilePath), Charsets.UTF_8);
			return classes;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 提取出整个Module的语法树集合
	 * @param filePath 语法树yaml文件路径
	 */
	public List<ResouceType> extractTrees(String filePath) {
		logger.info(filePath);
		String content=preProcess(new File(filePath));
		Iterable<Object> objects = yaml.loadAll(content);
		List<ResouceType> resouceTypes = Lists.newArrayList();
		for (Object resouceType : objects) {
			resouceTypes.add((ResouceType) resouceType);
		}
		return resouceTypes;
	}
	
	/**
	 * 提取所有类以及参数值
	 * @param filePath params Yaml文件路径
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PuppetClass> extractParams(String filePath){
		String content=preProcess(new File(filePath));
		List<PuppetClass> classes = yaml.loadAs(content, List.class);
		cancelVariable(classes);
		return classes;
	}
	
	/**
	 * 将class中参数值为变量的参数重新赋值
	 * @param params
	 * @param tree
	 * @return
	 */
	private List<PuppetClass> cancelVariable(List<PuppetClass> classes){
		LinkedHashMultimap<String, PuppetParam> variables=LinkedHashMultimap.create();
	    for (PuppetClass puppetClass : classes) {
			List<PuppetParam> params = puppetClass.getAllParams();
			for (PuppetParam puppetParam : params) {
				Object puppetParamType=puppetParam.getType();
				if(puppetParam.getParamName().equals("os")){
					System.out.println(puppetParam.getType());
				}
				if(puppetParamType instanceof ASTVariable){
					variables.put(((ASTVariable)puppetParamType).getValue(), puppetParam);
				}
				else if(puppetParamType instanceof ASTConcat){
					List<Object> childrens = ((ASTConcat) puppetParamType).getValue();
					for (Object childrenItem : childrens) {
						if(childrenItem instanceof ASTVariable){
							variables.put(((ASTVariable)childrenItem).getValue(), puppetParam);
						}
					}
				}
				else{
					puppetParam.setValue(formatParamValueByObjectType(puppetParam.getType()));
				}
			}
		}
		Set<String> keyset = variables.keySet();
		for (String key : keyset) {
			Object variableTrueValue = getValueByTraversalTree(key);
			logger.info(key+":"+variableTrueValue);
			if(variableTrueValue==null) continue;
			Set<PuppetParam> puppetParams = variables.get(key);
			for (PuppetParam puppetParam : puppetParams) {
				puppetParam.setValue(formatParamValueByObjectType(variableTrueValue));
				puppetParam.setType(variableTrueValue);
			}
		}
		return classes;
	}
	
	@SuppressWarnings("unchecked")
	private Object formatParamValueByObjectType(Object valueObject){
		if (valueObject instanceof ASTConcat) {
			List<Object> concatValues = ((ASTConcat) valueObject).getValue();
			StringBuilder builder=new StringBuilder();
//			builder.append("\"");
			for (Object concatValueItem : concatValues) {
				builder.append(formatParamValueByObjectType(concatValueItem));
			}
//			builder.append("\"");
			return builder.toString();
		} else if (valueObject instanceof ASTString) {
			return ((ASTString) valueObject).getValue();
		} else if (valueObject instanceof ASTBoolean) {
			return ((ASTBoolean) valueObject).isValue();
		} else if (valueObject instanceof ASTASTArray) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (Object arrayValueItem : ((ASTASTArray) valueObject)
					.getChildren()) {
				if(arrayValueItem instanceof ASTString)
					builder.append("\""+formatParamValueByObjectType(arrayValueItem)+"\""+",");
				else builder.append(formatParamValueByObjectType(arrayValueItem)+",");
			}
			builder.append("]");
			return builder.toString();
		} else if (valueObject instanceof ASTASTHash) {
			Map<Object, Object> hashValues = ((ASTASTHash) valueObject)
					.getValue();
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			for (Object entryObject : hashValues.entrySet()) {
				Entry<Object, Object> item = (Entry<Object, Object>) entryObject;
				if(item.getKey() instanceof String){
					builder.append(item.getKey() + " => ");
					if(item.getValue() instanceof ASTString)
						builder.append("\""+formatParamValueByObjectType(item.getValue())+"\""+",");
					else builder.append(formatParamValueByObjectType(item.getValue())+",");
				}
				else{
					builder.append(formatParamValueByObjectType(item.getKey()) + " => ");
					if(item.getValue() instanceof ASTString)
						builder.append("\""+formatParamValueByObjectType(item.getValue())+"\""+",");
					else builder.append(formatParamValueByObjectType(item.getValue())+",");
				}
			}
			builder.append("}");
			return builder.toString();
		} else if (valueObject instanceof ASTUndef) {
			return "undef";
		} else if (valueObject instanceof ASTFunction) {
			logger.error(valueObject.toString());
		} else if (valueObject instanceof ASTSelector) {
			logger.error(valueObject.toString());
		} else if (valueObject instanceof ASTOperator) {
			logger.error(valueObject.toString());
		}else if(valueObject instanceof ASTName){
			return ((ASTName) valueObject).getValue();
		}else if(valueObject instanceof ASTVariable){
			return "$"+((ASTVariable) valueObject).getValue();
		}else if(valueObject instanceof ASTHashOrArrayAccess){
			StringBuilder builder=new StringBuilder();
			ASTHashOrArrayAccess instance=(ASTHashOrArrayAccess) valueObject;
			builder.append("$");
			builder.append(instance.getVariable());
			builder.append("[");
			Object key=instance.getKey();
			if(key instanceof ASTString)
				builder.append("'"+formatParamValueByObjectType(key)+"'");
			else 
				builder.append(formatParamValueByObjectType(instance.getKey()));
			builder.append("]");
			return builder.toString();
		}
		return valueObject;
	}

	@SuppressWarnings("unchecked")
	private Object getValueByObjectType(Object valueObject,ResouceType resouceType){
		if(valueObject instanceof ASTVariable){
			String variableName = ((ASTVariable) valueObject).getValue();
			if(variableName.contains("::")){
				return getValueByTraversalTree(variableName);
			}
			else{
				return getValueFromResourceType(resouceType,variableName);
			}
		}
		else if(valueObject instanceof ASTConcat){
			List<Object> concatValues = ((ASTConcat) valueObject).getValue();
			for (int i = 0; i < concatValues.size(); i++) {
				Object concatValueItem=concatValues.get(i);
				concatValues.set(i, getValueByObjectType(concatValueItem, resouceType));
			}
		}
		else if(valueObject instanceof ASTASTHash){
			Map<Object, Object> hashValues = ((ASTASTHash) valueObject)
					.getValue();
			for (Object entryObject : hashValues.entrySet()) {
				Entry<Object, Object> item = (Entry<Object, Object>) entryObject;
				Object itemValue=getValueByObjectType(item.getValue(),resouceType);
				hashValues.put(item.getKey(),itemValue!=null?itemValue:item.getValue());
			}
		}
		else if(valueObject instanceof ASTFunction || valueObject instanceof ASTSelector){
			return null;
		}
		return valueObject;
	}

	private Object getValueFromResourceType(ResouceType resouceType,String paramName){
		//首先从argument中查询
		Object paramObject=resouceType.getArguments().get(paramName);
		Object paramValue = getValueByObjectType(paramObject,resouceType);
		if(paramValue!=null){
			return paramValue;
		}
		else{//没有查询到，去codeBlock中查找
			ASTBlockExpression blockCodes = resouceType.getCode();
			List<Object> blockChildrens = blockCodes.getChildren();
			for (Object blockChildrenItem : blockChildrens) {
				if(blockChildrenItem instanceof ASTVarDef){
					String varDefName=((ASTVarDef) blockChildrenItem).getName().getValue();
					if(varDefName.equals(paramName)){
						Object varDefValueObject=((ASTVarDef) blockChildrenItem).getValue();
						return getValueByObjectType(varDefValueObject, resouceType);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 遍历语法树集合，查找variable的值。
	 * @param variable
	 * @return
	 */
	public Object getValueByTraversalTree(String variable){
		String regex=":{0,2}(([a-zA-Z0-9_]+::)*)([a-zA-Z0-9_]+)";
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(variable);
		String namespace = null;
		String paramName = null;
		while(matcher.find())
		{
			namespace=matcher.group(1);
			paramName=matcher.group(3);
			for (ResouceType resouceType : trees) {
				String resouceTypeNamespace=resouceType.getName()+"::";
				if(resouceTypeNamespace.equals(namespace)){
					return getValueFromResourceType(resouceType,paramName);
				}
			}
			System.err.println("没有找到变量"+variable+"对应的值：");
			return null;
		}
		System.err.println("变量格式可能出错："+variable+"");
		return null;
	}

	/**
	 * 用ruby解析Module，生成中间文件
	 * @param inputFilePath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void usingRubyAnalyse(String inputFilePath,String rubyEnvironment,String PuppetParserSource,String PuppetAnalyseRuby) throws IOException, InterruptedException{
		String command=rubyEnvironment+" -e $stdout.sync=true;$stderr.sync=true;load($0=ARGV.shift) "+PuppetAnalyseRuby;
//		String command=" -e $stdout.sync=true;$stderr.sync=true;load($0=ARGV.shift) E:/jruby/smart-proxy-develop/richard/first_ruby.rb";
		command=command+" "+inputFilePath+" "+this.paramOutFilePath+" "+this.treeOutFilePath;
		logger.info(command);
		Process process = Runtime.getRuntime().exec(command,null,new File(PuppetParserSource));
//		Process process = Runtime.getRuntime().exec(command,null,new File("E:/jruby/smart-proxy-develop/"));
		printStream(process.getInputStream());
		printStream(process.getErrorStream());
		int exitValue = process.waitFor();
		if(exitValue!=0){
			throw new RuntimeException("解析"+inputFilePath+"module失败");
		}
	}
	
	
	
	// 对Yaml文件做预处理
	private static String preProcess(File file) {
		String content=null;
		try {
			content = Files.toString(file, Charsets.UTF_8);
			content=content.replaceAll("!|！", "#");
			content=content.replaceAll("#ruby/object:Puppet::Resource::Type","!!cn.ac.iscas.cloudeploy.v2.puppet.transform.ResouceType");
			content=content.replaceAll("#ruby/sym ", "");
			content=content.replaceAll("#ruby/object:PuppetClass","!!cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetClass");
			content=content.replaceAll("#ruby/object:Proxy::Puppet::PuppetClass","!!cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetClass");
			content=content.replaceAll("else: #ruby/object:Puppet::Parser::AST::Else", "ifStatementElse: #ruby/object:Puppet::Parser::AST::Else");
			content=content.replaceAll("#ruby/object:Puppet::Parser::AST::", "!!cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.AST");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
    
	
	private void printStream(InputStream inputStream) {
		logger.info("命令执行的输出");
		if (inputStream == null){
			logger.info("InputStream为空");
			return;
		}
		String line = "";
		try(BufferedReader input = new BufferedReader(new InputStreamReader(inputStream)))
		{
			while ((line = input.readLine()) != null) {
				logger.info(line);
			}
		} catch (IOException e1) {
			logger.error("输出流失败",e1);
			e1.printStackTrace();
		}
	}
	
	public List<ResouceType> getTrees() {
		return trees;
	}

	public void setTrees(List<ResouceType> trees) {
		this.trees = trees;
	}

	public List<PuppetClass> getClasses() {
		return classes;
	}

	public void setClasses(List<PuppetClass> classes) {
		this.classes = classes;
	}
}
