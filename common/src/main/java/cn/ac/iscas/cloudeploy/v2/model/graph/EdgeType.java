package cn.ac.iscas.cloudeploy.v2.model.graph;

import java.util.Set;

public enum EdgeType implements RelationScriptAbility{
	REQUIRE {
		@Override
		public String transformToRelationScript() {
			return "require => ";
		}
	},NOTIFY {
		@Override
		public String transformToRelationScript() {
			return "notify => ";
		}
	};

	public String transform(Set<? extends RelationScriptAbility> set) {
		StringBuilder builder = new StringBuilder();
		builder.append(transformToRelationScript());
		if(set.size() > 1) builder.append("[");
		for(RelationScriptAbility node: set){
			builder.append(node.transformToRelationScript());
		}
		if(set.size() > 1) builder.append("],");
		return builder.toString();
	}
}
