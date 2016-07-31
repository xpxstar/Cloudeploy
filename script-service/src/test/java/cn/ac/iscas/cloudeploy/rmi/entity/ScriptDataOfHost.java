package cn.ac.iscas.cloudeploy.rmi.entity;

import java.util.Map;

import com.google.common.collect.Maps;

public class ScriptDataOfHost {
	private String md5OfScript;
	private Map<String, String> dependedOperations;
	
	public Map<String, String> getDependedOperations() {
		return dependedOperations;
	}

	public ScriptDataOfHost(){
		dependedOperations=Maps.newHashMap();
	}
	
	public String getMd5OfScript() {
		return md5OfScript;
	}
	
	public void setMd5OfScript(String md5OfScript) {
		this.md5OfScript = md5OfScript;
	}
	
	public void putDependedOperation(String operationName,String value){
		this.dependedOperations.put(operationName, value);
	}
	
	
}
