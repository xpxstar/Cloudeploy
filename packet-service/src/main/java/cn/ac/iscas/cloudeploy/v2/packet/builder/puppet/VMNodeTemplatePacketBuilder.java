package cn.ac.iscas.cloudeploy.v2.packet.builder.puppet;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.IParameterElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.InterfaceElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipType;
import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService.ZipBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.Packet;
import cn.ac.iscas.cloudeploy.v2.packet.builder.NodeTemplatePacketBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.SpecificEntityBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Property.PropertyBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.VMContainer.VMContainerBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.VMInterface.VMInterfaceBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.type.TypeTransfer;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.util.YamlUtils;


public class VMNodeTemplatePacketBuilder extends NodeTemplatePacketBuilder{
	private Logger logger = LoggerFactory.getLogger(VMNodeTemplatePacketBuilder.class);
	
	public VMNodeTemplatePacketBuilder(TypeTransfer scriptsService, FileService fileService,
			List<NodeType> nodeTypeList, List<RelationshipType> relationTypeList,
			List<RelationshipTemplate> relationTemplateList) {
		super(scriptsService, fileService, nodeTypeList, relationTypeList, relationTemplateList);
	}


	@Override
	@SuppressWarnings("unchecked")
	protected List<InterfaceElement> buildScripts(NodeType nodetype, ZipBuilder zipBuilder, NodeTemplate template) throws FileNotFoundException{
		Preconditions.checkNotNull(nodetype);
		Preconditions.checkNotNull(zipBuilder);
		Preconditions.checkNotNull(template);
		//TODO 
//		List<TypeImplemention> implementions = typeTransferService.getTypeImplementions(Packet.PacketStrategy.VMPUPPET, nodetype.getName());
//		List<InterfaceElement> interfaceElements = new ArrayList<>();
//		for(TypeImplemention implemention: ForEachHelper.of(implementions)){
//			ByteSource byteSource = fileService.findFile(implemention.getDefinedFile());
//			zipBuilder.addFileToPackage("scripts/" + implemention.getInterfaceFilename(), byteSource);
//			InterfaceElement element = new InterfaceElement();
//			element.setImplementation("scripts/" + implemention.getInterfaceFilename());
//			//TODO change
////			element.setParams(YamlUtils.loadAs(implemention.getParamSpecific(), List.class));
//			element.setName(implemention.getInterfaceName());
//			interfaceElements.add(element);
//		}
//		return interfaceElements;
		return null;
	}

	protected void buildContainer(NodeTemplate nodetemplate, SpecificEntityBuilder builder) {
			VMContainerBuilder containerBuilder = SpecificEntity.VMContainer.builder();
			PropertyBuilder propertyBuilder = SpecificEntity.Property.builder();
			for(PropertyElement propertyElement:ForEachHelper.of(nodetemplate.getProperties())){
				Preconditions.checkArgument(!Strings.isNullOrEmpty(propertyElement.getName()),
						"VMContainer can't hold a property which's name is null");
				propertyBuilder.name(propertyElement.getName())
						.value(propertyElement.getDefaultValue());
				containerBuilder.property(propertyBuilder.build());
			}
			
			VMInterfaceBuilder interfaceBuilder = SpecificEntity.VMInterface.builder();
			for(InterfaceElement interfaceElement:ForEachHelper.of(nodetemplate.getInterfaces())){
				for(IParameterElement paramElement:ForEachHelper.of(interfaceElement.getParams())){
					propertyBuilder.name(paramElement.getName()).value(paramElement.getValue());
					interfaceBuilder.property(propertyBuilder.build());
				}
				interfaceBuilder.path(interfaceElement.getImplementation());
				interfaceBuilder.name(interfaceElement.getName());
				containerBuilder.operation(interfaceBuilder.build());
				logger.info("add operation {} to container", interfaceElement.getImplementation());
			}
			builder.vmContainer(containerBuilder.build());
			logger.info("add vmcontainer to specificEntity");
	}


	protected void buildSpecificFile(NodeTemplate nodetemplate, ZipBuilder zipBuilder) {
		// TODO Auto-generated method stub
	}


	@Override
	protected void buildSpecificFile(NodeTemplate nodetemplate, ZipBuilder zipBuilder, int initNum) {
		// TODO Auto-generated method stub
		
	}
}
