package cn.ac.iscas.cloudeploy.v2.model.service.script.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import cn.ac.iscas.cloudeploy.v2.model.graph.RelationScriptAbility;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetParam;

/**
 * represent a node in a graph. it has a nodeName and some parameters.
 * @author RichardLcc
 * @date 2015�?1�?28�?-下午3:34:07
 * @see ModuleClass
 * @see ModuleDefine
 * @see Operation
 */
public abstract class Node implements RelationScriptAbility{
	protected String nodeName;
	protected Map<String, ParamValue> paramsWithTypes;
	protected String name;
	public Node(){
		paramsWithTypes=Maps.newHashMap();
	}
	
	public Map<String, ParamValue> getParamsWithTypes() {
		return paramsWithTypes;
	}

	public void setParamsWithTypes(Map<String, ParamValue> paramsWithTypes) {
		this.paramsWithTypes = paramsWithTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get the name of node
	 * @return
	 */
	public String getNodeName() {
		return nodeName;
	}
	/**
	 * set node name
	 * @param nodeName
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * get parameter value's type by parameter's name.
	 * return a parameters value's type.represent a boolean,string, variable, array or hash.
	 * @param paramName
	 * @return
	 */
	public ParamType findParamTypeForParam(String paramName) {
		ParamValue value=paramsWithTypes.get(paramName);
		if(value==null) return ParamType.UNKNOWN;
		return value.getType();
	}
	/**
	 * set parameter value's type 
	 * @param paramName
	 * @param type
	 */
	public void setParamTypeOfParam(String paramName,ParamType type){
		ParamValue value=paramsWithTypes.get(paramName);
		if(value==null) value=new ParamValue();
		value.setType(type);
		paramsWithTypes.put(paramName, value);
	}
	/**
	 * get the value of this parameter
	 * @param paramName
	 * @return
	 */
	public Object findValueOfParam(String paramName){
		ParamValue value=paramsWithTypes.get(paramName);
		return value!=null?value.getValue():null;
	}
	
	public ParamValue findValue(String param) {
		return paramsWithTypes.get(param);
	}
	/**
	 * set the value of this parameter
	 * @param paramName
	 * @param value
	 */
	public void setValueOfParams(String paramName,Object value){
		ParamValue paramValue=paramsWithTypes.get(paramName);
		if(paramValue==null) paramValue=new ParamValue();
		paramValue.setValue(value);
		paramsWithTypes.put(paramName, paramValue);
	}
	
	/**
	 * get all parameters
	 * @return
	 */
	public Set<Entry<String, ParamValue>> getParamsEntrySet(){
		if(paramsWithTypes==null) 
			return null;
		return paramsWithTypes.entrySet();
	}
	
	/**
	 * set parameters of node
	 * @param operationParams
	 */
	public void setParamsWithType(Map<String, ParamValue> operationParams) {
		this.paramsWithTypes=operationParams;
	}
	
	/**
	 * 该方法不建议使用，传入的参数没有附带类型信息，全部认为类型为UNKNOWN�?
	 * @param params
	 */
	@Deprecated
	public void setParams(Map<String, Object> params){
		for (Map.Entry<String, Object> item : params.entrySet()) {
			ParamValue paramValue=new ParamValue(item.getValue(),ParamType.UNKNOWN);
			paramsWithTypes.put(item.getKey(), paramValue);
		}
	}
	
	/**
	 * this method is used to convert a puppet class's or define's parameters format to node parameters' format,
	 * @param puppetParams
	 */
	protected void toConvertParams(Map<String, PuppetParam> puppetParams) {
		if(puppetParams==null) return;
		for (Map.Entry<String, PuppetParam> item : puppetParams.entrySet()) {
			if(item.getKey().equals("os")){
				System.out.println(item.getValue().getValue());
			}
			ParamValue paramValue=new ParamValue(item.getValue().getValue(),item.getValue().getTypeString());
			paramsWithTypes.put(item.getKey(), paramValue);
		}
	}
	
	/**
	 * first get the intersection of node's parameters and extractParams,
	 * then using node's value replace their value in extractParams, 
	 * at the last change the value to ${key} in node
	 * @param maps
	 * @param extractParams
	 * @return 
	 */
	public void intersection(Map<String,ParamValue> extractParams) {
		if(paramsWithTypes ==null || extractParams==null) return;
		SetView<String> intersections = Sets.intersection(paramsWithTypes.keySet(), extractParams.keySet());
		for (String key : intersections) {
			ParamValue paramValue=new ParamValue(paramsWithTypes.get(key).getValue(), this.findParamTypeForParam(key));
			extractParams.put(key, paramValue);
			this.setValueOfParams(key, "$"+key);
			this.setParamTypeOfParam(key,ParamType.VARIABLE);
		}
	}
	
	/**
	 * 将resourceType中的�?有域名的首字母大写，如resourcetype为tomcat：：instance<br>
	 * 返回值则�? Tomcat::Instance
	 * @param resourceType
	 * @return
	 */
	protected String capitalize(String resourceType) {
		Iterable<String> names = Splitter.on("::").split(resourceType);
		List<String> formatedName=new ArrayList<String>();
		for (String name : names) {
			formatedName.add(StringUtils.capitalize(name));
		}
		return Joiner.on("::").join(formatedName);
	}
	
//	@SuppressWarnings("rawtypes")
//	private String toString(String key, Object value) {
//		if(value == null ) return "";
//		logger.info("transform value to extract params format,param:"+key);
//		logger.info("value object type is:"+value.getClass().getSimpleName());
//		logger.info("value is:"+value.toString());
//		StringBuilder builder=null;
//		ParamType valueType=this.findParamTypeForParam(key);
//		logger.debug("param's type is:"+valueType);
//		if(value instanceof String){
//			switch(valueType){
//			case ARRAY:case HASH:case BOOLEAN:case UNKNOWN:
//				return value.toString();
//			case UNDEF:
//				if(value.equals("undef"))
//					return value.toString();
//				else 
//					return "\""+value+"\"";
//			default:	
//				return "\""+value+"\"";
//			}
//		}else if(value.getClass().isArray()){
//			int length=Array.getLength(value);
//			builder=new StringBuilder();
//			builder.append("[");
//			for(int i=0;i<length;i++){
//				builder.append(toString(key,Array.get(value, i)));
//			}
//			builder.append("]");
//			return builder.toString();
//		}else if(value instanceof Map){
//			Map map=(Map) value;
//			builder=new StringBuilder();
//			builder.append("{");
//			for (Object entryObject:map.entrySet()) {
//				Entry item = (Entry) entryObject;
//				builder.append(item.getKey()+" => "+toString(key,item.getValue()));
//			}
//			builder.append("}");
//			return builder.toString();
//		}else if(value instanceof Collection){
//			Collection collection=(Collection) value;
//			builder=new StringBuilder();
//			builder.append("[");
//			for (Object item : collection) {
//				builder.append(toString(key,item));
//			}
//			builder.append("]");
//			return builder.toString();
//		}
//		return value.toString();
//	}
	
	public abstract String transformToRelationScript();
	
	public String transformParamsToScript(Map<String, ParamValue> extractParams){
		if(this.paramsWithTypes == null) return "";
		intersection(extractParams);
		Collection<String> params = Collections2.transform(this.getParamsEntrySet(), new Function<Map.Entry<String,ParamValue>,String>(){
			@Override
			public String apply(Entry<String, ParamValue> input) {
				String key=input.getKey();
				return "'"+key+"' => "+ input.getValue().transformToScript();
			}
		});
		return Joiner.on(System.getProperty("line.separator")).join(params);
	}
	
}
