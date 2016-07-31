package cn.ac.iscas.cloudeploy.v2.model.service.script.entity;

import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetParam;
/**
 * represent a puppet class type defined in module
 * @author RichardLcc
 * @date 2015年1月28日-下午3:26:49
 */
public class ModuleClass extends Node {
	/**
	 * the name of this puppet class 
	 */
	private String name;
	/**
	 *
	 * construct a new ModuleClass
	 * @param name the name of this puppet class
	 * @param params the parameters of this puppet class
	 */
	public ModuleClass(String name,Map<String, PuppetParam> params){
		this.name=name;
		toConvertParams(params);
	}
	public ModuleClass(){}
	
	/**
	 * get the name of this puppet class 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * set the name of this puppet class 
	 * @param name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String transformToRelationScript() {
		StringBuilder builder = new StringBuilder();
		builder.append("Class[").append(name).append("],");
		return builder.toString();
	}
}
