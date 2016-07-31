package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import java.util.List;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;
@NoArgsConstructor
public class ServiceTemplate extends RootElement{
	/**
	 * list of import statements for importing other definitions files
	 */
	private List<String> imports;
	/**
	 * topology template definition of the cloud application or service
	 */
	private TopologyTemplate topologytemplate;
	/**
	 * This section contains a set of node type definitions for use in service
	 * templates. 
	 */
	private List<NodeType> nodetypes;
	/**
	 * This section contains a set of relationship type definitions for use in
	 * service templates. 
	 */
	private List<RelationshipType> relationshiptypes;
	
	@Builder
	public ServiceTemplate(String description, String name, @Singular List<String> imports, TopologyTemplate topologytemplate,
			@Singular List<NodeType> nodetypes, @Singular List<RelationshipType> relationshiptypes) {
		super(description, name);
		this.imports = imports;
		this.topologytemplate = topologytemplate;
		this.nodetypes = nodetypes;
		this.relationshiptypes = relationshiptypes;
	}
	public List<String> getImports() {
		return imports;
	}
	public void setImports(List<String> imports) {
		this.imports = imports;
	}
	public TopologyTemplate getTopologytemplate() {
		return topologytemplate;
	}
	public void setTopologytemplate(TopologyTemplate topologytemplate) {
		this.topologytemplate = topologytemplate;
	}
	public List<NodeType> getNodetypes() {
		return nodetypes;
	}
	public void setNodetypes(List<NodeType> nodetypes) {
		this.nodetypes = nodetypes;
	}
	public List<RelationshipType> getRelationshiptypes() {
		return relationshiptypes;
	}
	public void setRelationshiptypes(List<RelationshipType> relationshiptypes) {
		this.relationshiptypes = relationshiptypes;
	}
}
