package cn.ac.iscas.cloudeploy.v2.puppet.transform;
import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTBlockExpression;

public class ResouceType {
	private String type;
	private String name;
	private String namespace;
	private Map<String,Object> arguments;
	private Object argument_types;
	private Object match;
	private String module_name;
	private ASTBlockExpression code;
	private String doc;
	private Integer line;
	private String parent;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public Map<String, Object> getArguments() {
		return arguments;
	}
	public void setArguments(Map<String, Object> arguments) {
		this.arguments = arguments;
	}
	public Object getArgument_types() {
		return argument_types;
	}
	public void setArgument_types(Object argument_types) {
		this.argument_types = argument_types;
	}
	public Object getMatch() {
		return match;
	}
	public void setMatch(Object match) {
		this.match = match;
	}
	public String getModule_name() {
		return module_name;
	}
	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}
	public ASTBlockExpression getCode() {
		return code;
	}
	public void setCode(ASTBlockExpression code) {
		this.code = code;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public Integer getLine() {
		return line;
	}
	public void setLine(Integer line) {
		this.line = line;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
}
