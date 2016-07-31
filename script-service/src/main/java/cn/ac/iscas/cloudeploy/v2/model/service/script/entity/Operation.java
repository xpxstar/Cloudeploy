package cn.ac.iscas.cloudeploy.v2.model.service.script.entity;

import java.util.List;

import com.google.common.collect.Lists;
/**
 * represent a operation of a component, like installing or deleting a application 
 * @author RichardLcc
 * @date 2015年1月29日-上午10:51:42
 */
public class Operation extends Node {
	/**
	 * the operation name
	 */
	private String name;
	/**
	 * the definition file's md5Key
	 */
	private String defineMd5;
	/**
	 * all operation it depend on
	 */
	private List<Operation> imports;
	/**
	 * get the operation name
	 * @return operation name
	 */
	public String getName() {
		return name;
	}
	/**
	 * set the operation name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * get the definition file's md5Key
	 * @return the definition file's md5Key
	 */
	public String getDefineMd5() {
		return defineMd5;
	}
	/**
	 * set the definition file's md5Key
	 */
	public void setDefineMd5(String defineMd5) {
		this.defineMd5 = defineMd5;
	}
	/**
	 * get all operation it depend on
	 * @return all operation it depend on
	 */
	public List<Operation> getImports() {
		return imports;
	}
	/**
	 * set all operation it depend on
	 */
	public void setImports(List<Operation> imports) {
		this.imports = imports;
	}
	/**
	 * add a operation it depend on
	 */
	public void addImports(Operation transformAction) {
		if(imports==null)
			imports=Lists.newArrayList();
		imports.add(transformAction);
	}
	@Override
	public String transformToRelationScript() {
		StringBuilder builder = new StringBuilder();
		builder.append(capitalize(name)).append("[\"").append(nodeName).append("_${name}\"],");
		return builder.toString();
	}
}
