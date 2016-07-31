package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopologyTemplate {

	/**
	 * definition of the node tempaltes of the topology
	 */
	@Singular private List<NodeTemplate> nodetemplates;
	
	/**
	 * definition of the node relationship templates of the topology
	 */
	@Singular private List<RelationshipTemplate> relationshiptemplates;
	
	public List<NodeTemplate> getNodetemplates() {
		return nodetemplates;
	}

	public void setNodetemplates(List<NodeTemplate> nodetemplates) {
		this.nodetemplates = nodetemplates;
	}

	public List<RelationshipTemplate> getRelationshiptemplates() {
		return relationshiptemplates;
	}

	public void setRelationshiptemplates(List<RelationshipTemplate> relationshiptemplates) {
		this.relationshiptemplates = relationshiptemplates;
	}
	
	public NodeTemplate findTemplate(String containerName){
		if(nodetemplates == null) return null;
		for(NodeTemplate template : nodetemplates){
			if(template.getName().equals(containerName)) return template;
		}
		return null;
	}
	
}
