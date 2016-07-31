package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RequirementElement extends RootElement{

	private String target;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
