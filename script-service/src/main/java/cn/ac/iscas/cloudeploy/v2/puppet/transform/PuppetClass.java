package cn.ac.iscas.cloudeploy.v2.puppet.transform;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;

public class PuppetClass {
	private String klass;
	private Map<String, PuppetParam> params;
	private String type;
	
	public String getKlass() {
		return klass;
	}
	public void setKlass(String klass) {
		this.klass = klass;
	}
	
	public Map<String, PuppetParam> getParams() {
		return params;
	}
	
	public void setParams(Map<String, PuppetParam> params) {
		this.params = params;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public List<PuppetParam> getAllParams(){
		List<PuppetParam> puppetParams=Lists.newArrayList();
		Set<Entry<String, PuppetParam>> sets = params.entrySet();
		for (Entry<String, PuppetParam> entry : sets) {
			entry.getValue().setParent(this);
			entry.getValue().setParamName(entry.getKey());
			puppetParams.add(entry.getValue());
		}
		return puppetParams;
	}
	
}
