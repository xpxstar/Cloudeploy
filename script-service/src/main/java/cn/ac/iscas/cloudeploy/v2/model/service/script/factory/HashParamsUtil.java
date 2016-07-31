package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
/**
 * 格式化输出map对象。
 * 包级别私有，factory外不可调用
 * @author RichardLcc
 */
class HashParamsUtil {
	public static String toStringAsHashFormat(final Node node,Map<String,ParamValue> extractParams) {
		if(node==null||node.getParamsEntrySet()==null) return null;
		node.intersection(extractParams);
		Collection<String> params = Collections2.transform(node.getParamsEntrySet(), new Function<Map.Entry<String,ParamValue>,String>(){
			@Override
			public String apply(Entry<String, ParamValue> input) {
				Object value = input.getValue().getValue();
				String key=input.getKey();
				ParamType valueType=node.findParamTypeForParam(key);
				return "'"+key+"' => "+HashParamsUtil.toString(value,valueType,(node instanceof Operation));
			}
		});
		return Joiner.on(System.getProperty("line.separator")).join(params);
	}
	/**
	 * 将maps中与extractParams中对应的变量的值更改为$变量名，同时输出所有的key => value对。
	 * @param maps
	 * @param extractParams
	 * @return
	 */
	public static String toStringAsVariableFormat(Map<String, ParamValue> maps){
		if(maps==null) return null;
		Collection<String> params = Collections2.transform(maps.entrySet(), new Function<Map.Entry<String,ParamValue>,String>(){
			@Override
			public String apply(Entry<String, ParamValue> input) {
				ParamValue value = input.getValue();
				return "$"+input.getKey()+"="+HashParamsUtil.toString(value.getValue(),value.getType(),true);
			}
		});
		return Joiner.on(System.getProperty("line.separator")).join(params);
	}
	/**
	 * 内部方法，不暴露，实现Array，Collection，Map类型的对象的toString(),其他类型的Object，依赖Object自身的toString方法。
	 * @param object
	 * @param valueType 
	 * @param value 
	 * @param klass 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String toString(Object value, ParamType valueType, boolean isOperation){
		if(value == null) return "";
		StringBuilder builder=null;
		if(valueType==null) valueType=ParamType.UNKNOWN;
		if(value instanceof String){
			switch(valueType){
			case STRING:
				if(isOperation){
					String format = (String) value;
					char start = format.charAt(0);
					char end = format.charAt(format.length() - 1);
					if(start == '\"' && end == '\"'){
						return value.toString()+",";
					}else{
						return "\""+value+"\",";
					}
				}
				else
					return "\""+value+"\",";
			case VARIABLE:
				return value+",";
			default:
				return value.toString()+",";
			}
		}else if(value.getClass().isArray()){
			int length=Array.getLength(value);
			builder=new StringBuilder();
			builder.append("[");
			for(int i=0;i<length;i++){
				builder.append(toString(Array.get(value, i),valueType,isOperation));
			}
			builder.append("],");
			return builder.toString();
		}else if(value instanceof Map){
			Map map=(Map) value;
			builder=new StringBuilder();
			builder.append("{");
			for (Object entryObject:map.entrySet()) {
				Entry item = (Entry) entryObject;
				builder.append(item.getKey()+" => "+toString(item.getValue(),valueType,isOperation));
			}
			builder.append("},");
			return builder.toString();
		}else if(value instanceof Collection){
			Collection collection=(Collection) value;
			builder=new StringBuilder();
			builder.append("[");
			for (Object item : collection) {
				builder.append(toString(item,valueType,isOperation));
			}
			builder.append("],");
			return builder.toString();
		}
		return value.toString()+",";
	}
}
