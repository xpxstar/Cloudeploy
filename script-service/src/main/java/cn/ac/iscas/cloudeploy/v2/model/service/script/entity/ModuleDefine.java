package cn.ac.iscas.cloudeploy.v2.model.service.script.entity;

import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetParam;
/**
 * 
 * represent a puppet define type defined in module
 * @author RichardLcc
 * @date 2015年1月28日-下午3:30:29
 */
public class ModuleDefine extends Node {
	/**
	 * the name of this puppet define 
	 */
	private String name;
	public ModuleDefine(){}
	/**
	 * construct a new ModuleDefine
	 * @param name the name of this puppet define
	 * @param params the parameters of this puppet define
	 */
	public ModuleDefine(String name,Map<String, PuppetParam> params){
		this.name=name;
		toConvertParams(params);
	}
	/**
	 * get the name of this puppet define 
	 */
	public String getName() {
		return name;
	}
    /**
     * set the name of this puppet define
     * @param name
     */
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String transformToRelationScript() {
		StringBuilder builder = new StringBuilder();
		builder.append(capitalize(name)).append("[\"").append(nodeName).append("_${name}\"],");
		return builder.toString();
	}
}
