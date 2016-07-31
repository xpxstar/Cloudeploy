package cn.ac.iscas.cloudeploy.v2.packet.builder.docker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import cn.ac.iscas.cloudeploy.v2.exception.InternalServerErrorException;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.InterfaceElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.TemplateElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService.ZipBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.builder.NodeTemplatePacketBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Container.ContainerBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Container.HostConfig;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Container.HostConfig.HostConfigBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Service.ServiceBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Template.TemplateBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.type.TypeTransfer;
import cn.ac.iscas.cloudeploy.v2.packet.type.docker.DockerTransfer;
import cn.ac.iscas.cloudeploy.v2.packet.type.docker.ImageInfo;
import cn.ac.iscas.cloudeploy.v2.packet.type.docker.ImageInfo.ContainerConfig;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.util.YamlUtils;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Container.PortBinding;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.SpecificEntityBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Attribute.AttributeBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Check.CheckBuilder;

public class DockerNodeTemplatePacketBuilder extends NodeTemplatePacketBuilder {
	private Logger logger = LoggerFactory.getLogger(DockerNodeTemplatePacketBuilder.class);
	
	public DockerNodeTemplatePacketBuilder(TypeTransfer scriptsService, FileService fileService,
			List<NodeType> nodeTypeList, List<RelationshipType> relationTypeList,
			List<RelationshipTemplate> relationTemplateList) {
		super(scriptsService, fileService, nodeTypeList, relationTypeList, relationTemplateList);
	}

	@Override
	protected List<InterfaceElement> buildScripts(NodeType nodetype, ZipBuilder zipBuilder, NodeTemplate template)
			throws FileNotFoundException {
		return null;
	}
	
	protected Optional<ContainerBuilder> buildContainer(NodeTemplate nodetemplate, int initNum) {
		Optional<ImageInfo> imageInfo = getImageInfo(nodetemplate);
		if(!imageInfo.isPresent()){
			logger.error("can't find a type implemention for nodetemplate:{}",nodetemplate.getName());
			throw new InternalServerErrorException("can't find a type implemention for nodetemplate " + nodetemplate.getName());
		}else{
			ContainerBuilder containerBuilder = SpecificEntity.Container.builder();
			ImageInfo.ContainerConfig imageConfig = imageInfo.get().getConfig();
//			initContainerBuilder(containerBuilder, imageConfig);		
			containerBuilder.image(imageInfo.get().getId());
			containerBuilder.containerName(nodetemplate.getName() + "_" + initNum);
			return Optional.of(containerBuilder);
		}
	}

	private void initContainerBuilder(ContainerBuilder containerBuilder, ContainerConfig imageConfig) {
		try {
			Field[] fields = ContainerConfig.class.getDeclaredFields();
			for(int i = 0; i < fields.length; i++){
				Field sourceField = fields[i];
				sourceField.setAccessible(true);
				Field targetField = SpecificEntity.Container.ContainerBuilder.class.getDeclaredField(sourceField.getName());
				if(targetField != null){
					targetField.setAccessible(true);
					targetField.set(containerBuilder, sourceField.get(imageConfig));
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
			logger.error("init sepecific docker container failed",e);
			throw new InternalServerErrorException("init sepecific docker container failed",e);
		}
	}

	private Optional<ImageInfo> getImageInfo(NodeTemplate nodetemplate) {
		Map<String, TypeImplemention> impls = typeTransferService.getImplementions(nodetemplate.getType());
		TypeImplemention installImpl = impls.get("install");
		try {
			ImageInfo imageInfo = getDockerTransfer().getImageInfo(installImpl);
			return Optional.of(imageInfo);
		} catch (IOException e1) {
			e1.printStackTrace();
			return Optional.absent();
		}
	}
	
	@Override
	protected void buildSpecificFile(NodeTemplate nodetemplate, ZipBuilder zipBuilder,int initNum){
		Preconditions.checkNotNull(nodetemplate, "nodetemplate is null, can't to genenate a specific file for it");
		
		SpecificEntityBuilder builder = SpecificEntity.builder();
		builder.name(nodetemplate.getName() + "_" + initNum);
		/*
		 * build for container
		 */
		Optional<ContainerBuilder> containerBuilder = buildContainer(nodetemplate, initNum);
		/*
		 * build for service
		 */
		List<ServiceBuilder> serviceBuilders = buildServicesForSpecificFile(nodetemplate, 
							initNum, containerBuilder.get());
		for(ServiceBuilder serviceBuilder : ForEachHelper.of(serviceBuilders)){
			builder.service(serviceBuilder.build());
		}
		logger.info("build services for template:{} successfully", nodetemplate.getName());
		
		builder.container(containerBuilder.get().build());
		/*
		 * build for attributes
		 */
		for(AttributeElement attributeElement:ForEachHelper.of(nodetemplate.getAttributes())){
			Preconditions.checkArgument(!Strings.isNullOrEmpty(attributeElement.getName()),
					"attribute name can't be null or empty");
			
			AttributeBuilder attrBuilder = SpecificEntity.Attribute.builder();
			attrBuilder.key(attributeElement.getName())
						.value(attributeElement.getValue());
			builder.attribute(attrBuilder.build());
		}
		logger.info("build attributes for template:{} successfully", nodetemplate.getName());
		
		/*
		 * build for templates
		 */
		TemplateBuilder templateBuilder = SpecificEntity.Template.builder();
		Collection<RelationshipTemplate> relationshipTemplates = relationTemplates.get(nodetemplate.getName());
		for(RelationshipTemplate relationshipTemplate : ForEachHelper.of(relationshipTemplates)){
			List<TemplateElement> templatesOfContainer = relationshipTemplate.getTemplates();
			for(TemplateElement templateElement : ForEachHelper.of(templatesOfContainer)){
				templateBuilder.source(templateElement.getTemplate());
				templateBuilder.target(templateElement.getConfigurationfile());
				templateBuilder.command(templateElement.getCommand());
				builder.template(templateBuilder.build());
			}
		}
		/*
		 * dump as yaml
		 */
		String content = YamlUtils.dumper(builder.build());
		zipBuilder.addFileToPackage("specificFile.yaml",ByteSource.wrap(content.getBytes()));
	}
	
	private List<ServiceBuilder> buildServicesForSpecificFile(NodeTemplate nodetemplate, int initNum, ContainerBuilder containerBuilder) {
		Preconditions.checkNotNull(nodetemplate);
		
		List<ServiceBuilder> serviceBuilders = new ArrayList<>();
		HostConfigBuilder hostConfigBuilder = HostConfig.builder();
		//for services
		for(ServiceElement serviceElement:ForEachHelper.of(nodetemplate.getServices())){
			Preconditions.checkNotNull(serviceElement, "node template {} can't hold a null service element", nodetemplate.getName());
			Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceElement.getName()),
					"node template {} can't hold a service which's name is null or empty", nodetemplate.getName());
			Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceElement.getServiceId()),
					"service id of service({}) can't be null or empty", serviceElement.getName());
			Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceElement.getPort()),
					"port of service({}) can't be null or empty", serviceElement.getName());
			
			ServiceBuilder serviceBuilder = SpecificEntity.Service.builder();
			if(serviceElement.getCheck() != null){
				CheckBuilder checkBuilder = SpecificEntity.Check.builder();
				checkBuilder.script(serviceElement.getCheck());
				checkBuilder.interval(10L);
				serviceBuilder.check(checkBuilder.build());
			}
			
			int port = Integer.valueOf(serviceElement.getPort()) + initNum;
			
			PortBinding binding = PortBinding.builder()
					.port(serviceElement.getPort())
					.targetPort(String.valueOf(port))
					.build();
			hostConfigBuilder.portBinding(binding);
			
			serviceBuilder.name(serviceElement.getName())
						.id(serviceElement.getServiceId() + "_" + initNum)
						.port(String.valueOf(port));
			serviceBuilders.add(serviceBuilder);
		}
		containerBuilder.hostConfig(hostConfigBuilder.build());
		logger.info("build services entity for template {} successfully",
				nodetemplate.getName());
		return serviceBuilders;
	}
	
	private DockerTransfer getDockerTransfer(){
		Preconditions.checkArgument(typeTransferService instanceof DockerTransfer, 
				"typeTransferService must be instance of DockerTransfer");
		return (DockerTransfer)typeTransferService;
	}
}
