package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CapabilityElement extends RootElement{
	private String source;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
}
