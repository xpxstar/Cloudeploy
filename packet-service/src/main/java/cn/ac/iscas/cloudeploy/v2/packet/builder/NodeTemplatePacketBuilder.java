package cn.ac.iscas.cloudeploy.v2.packet.builder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.InterfaceElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RootElement;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService.ZipBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.Packet;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;
import cn.ac.iscas.cloudeploy.v2.packet.type.TypeTransfer;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.util.YamlUtils;


public abstract class NodeTemplatePacketBuilder{
	private Logger logger = LoggerFactory.getLogger(NodeTemplatePacketBuilder.class);

	protected TypeTransfer typeTransferService;
	/**
	 * used to store packet to file system;
	 */
	protected FileService fileService;

	protected Map<String, NodeType> nodeTypes;
	protected Map<String, RelationshipType> relationTypes;
	protected Multimap<String, RelationshipTemplate> relationTemplates;
	
	public NodeTemplatePacketBuilder(TypeTransfer scriptsService, FileService fileService,
			List<NodeType> nodeTypeList, List<RelationshipType> relationTypeList,
			List<RelationshipTemplate> relationTemplateList) {
		this(nodeTypeList, relationTypeList, relationTemplateList);
		this.typeTransferService = scriptsService;
		this.fileService = fileService;
	}
	
	private NodeTemplatePacketBuilder(List<NodeType> nodeTypeList, List<RelationshipType> relationTypeList, List<RelationshipTemplate> relationTemplateList){
		nodeTypes = new HashMap<>();
		relationTypes = new HashMap<>();
		relationTemplates = ArrayListMultimap.create();
		
		for(NodeType nodetype : ForEachHelper.of(nodeTypeList)){
			nodeTypes.put(nodetype.getName(), nodetype);
		}
		for(RelationshipType relationType : ForEachHelper.of(relationTypeList)){
			relationTypes.put(relationType.getName(), relationType);
		}
		for(RelationshipTemplate relationTemplate : ForEachHelper.of(relationTemplateList)){
			relationTemplates.put(relationTemplate.getSource(),relationTemplate);
		}
		logger.info("initialize node template packet builder successfully");
	}
	
	public Optional<Packet> buildPacket(NodeTemplate nodetemplate, int initNum) throws IOException {
		Preconditions.checkArgument(nodetemplate != null, "failed to build packet because of nodeTemplate is Null");
		NodeType nodeType = nodeTypes.get(nodetemplate.getType());
		Preconditions.checkArgument(nodeType != null, "failed to build packet because of can't find nodetype %s for nodetemplate %s",
				nodetemplate.getType(), nodetemplate.getName());	
		return Optional.of(buildPacket(nodeType, nodetemplate, initNum));
	}
	
	/**
	 * build packet for template
	 * @param nodetype
	 * @param template
	 * @return
	 * @throws IOException 
	 */
	private Packet buildPacket(NodeType nodetype, NodeTemplate template, int initNum) throws IOException{
		Preconditions.checkNotNull(nodetype);
		Preconditions.checkNotNull(template);
		
		merge(nodetype, template);
		ZipBuilder zipBuilder = fileService.buildZipBuilder();
		
		List<InterfaceElement> interfaces = buildScripts(nodetype, zipBuilder, template);
		if(interfaces != null) template.setInterfaces(interfaces);
		
		buildSpecificFile(template, zipBuilder, initNum);
		
		Packet.PacketBuilder packetBuilder = Packet.builder();
		packetBuilder.specificEntityFile("specificFile.yaml").strategy(PacketStrategy.VMPUPPET).appName(template.getName() + "_" + initNum);
		zipBuilder.addFileToPackage("packet.yaml", ByteSource.wrap(YamlUtils.dumper(packetBuilder.build()).getBytes()));
		
		String md5 = fileService.saveZip(zipBuilder);
		packetBuilder.packet(md5);
		return packetBuilder.build();
	}
	
	protected abstract List<InterfaceElement> buildScripts(NodeType nodetype, ZipBuilder zipBuilder, NodeTemplate template) throws FileNotFoundException;
	protected abstract void buildSpecificFile(NodeTemplate nodetemplate, ZipBuilder zipBuilder, int initNum);
	
	private void merge(NodeType nodetype, NodeTemplate template) {
		Preconditions.checkNotNull(nodetype, "nodetype can't be null for merge method");
		Preconditions.checkNotNull(template, "nodetemplate can't be null for merge method");
		
		mergeProperties(nodetype, template);
		mergeServices(nodetype, template);
		mergeAttributes(nodetype, template);
		logger.info("merge properties, services, attributes of node type: {}  with template {}", nodetype.getName(), template.getName());
	}
	
	private void mergeProperties(NodeType nodetype, NodeTemplate template) {
		template.setProperties(mergeField(nodetype.getProperties(),template.getProperties()));
	}
	
	private void mergeServices(NodeType nodetype, NodeTemplate template){
		template.setServices(mergeField(nodetype.getServices(),template.getServices()));
	}
	
	private void mergeAttributes(NodeType nodetype, NodeTemplate template){
		template.setAttributes(mergeField(nodetype.getAttributes(), template.getAttributes()));
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