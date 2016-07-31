package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import java.util.List;

import lombok.NoArgsConstructor;
@NoArgsConstructor
public class InterfaceElement extends RootElement{
	/**
	 * The optional list of input parameter definitions.
	 */
	private List<IParameterElement> params;
	/**
	 * The optional implementation artifact name (e.g., a script file name)
	 */
	private String implementation;
	
	public String getImplementation() {
		return implementation;
	}
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	public List<IParameterElement> getParams() {
		return params;
	}
	public void setParams(List<IParameterElement> params) {
		this.params = params;
	}
}
