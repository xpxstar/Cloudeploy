package cn.ac.iscas.cloudeploy.v2.model.service.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RootElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.key.KeyGenerator;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;

@Component
public class AppConfigManager {
	@Autowired
	private Consul consul;
	
	public boolean registerAppProperties(ServiceTemplate app, User user){
		KeyValueClient keyAgent = consul.keyValueClient();
		Map<String, NodeType> nodeTypes = new HashMap<>();
		Map<String, RelationshipType> relationTypes = new HashMap<>();
		Multimap<String, RelationshipTemplate> relationTemplates = ArrayListMultimap.create();
		
		for(NodeType nodetype : ForEachHelper.of(app.getNodetypes())){
			nodeTypes.put(nodetype.getName(), nodetype);
		}
		for(RelationshipType relationType : ForEachHelper.of(app.getRelationshiptypes())){
			relationTypes.put(relationType.getName(), relationType);
		}
		for(RelationshipTemplate relationTemplate : ForEachHelper.of(app.getTopologytemplate().getRelationshiptemplates())){
			relationTemplates.put(relationTemplate.getSource(),relationTemplate);
		}
		for(NodeTemplate nodetemplate: ForEachHelper.of(app.getTopologytemplate().getNodetemplates())){
			mergeProperties(nodeTypes.get(nodetemplate.getType()), nodetemplate);
			for(PropertyElement ele : nodetemplate.getProperties()){
				String key = KeyGenerator.appPropertyKey(ele.getName(), nodetemplate.getName(), app.getName(), user);
				keyAgent.putValue(key, ele.getDefaultValue());
			}
		}
		return true;
	}
	
	private void mergeProperties(NodeType nodetype, NodeTemplate template) {
		Preconditions.checkNotNull(nodetype, "node type can't be null, template's type name is " + template.getType());
		template.setProperties(mergeField(nodetype.getProperties(),template.getProperties()));
	}
	
	private <T extends RootElement> List<T> mergeField(List<T> nodetypeLists, List<T> templateLists){
		Map<String, T> changed = new HashMap<>();
		List<T> elements = templateLists;
		if(elements == null) elements = new ArrayList<>();
		for(T element: elements){
			changed.put(element.getName(), element);
		}
		for(T element : ForEachHelper.of(nodetypeLists)){
			if(!changed.containsKey(element.getName())){
				elements.add(element);
			}
		}
		return elements;
	}
}
