package cn.ac.iscas.cloudeploy.v2.model.service.topology;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;

import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerAttributeView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerParamView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerTemplateView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.RelationView;
import cn.ac.iscas.cloudeploy.v2.exception.NotFoundException;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ApplicationView.DetailedItem;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.TemplateElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.TopologyTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeElement.AttributeElementBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate.NoteTemplateBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement.PropertyElementBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.RelationshipTemplate.RelationshipTemplateBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceElement.ServiceElementBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate.ServiceTemplateBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.TemplateElement.TemplateElementBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.TopologyTemplate.TopologyTemplateBuilder;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.key.KeyGenerator;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.util.YamlUtils;

@Service
public class DefaultSTModelService implements STModelService{
	private static Logger logger = LoggerFactory.getLogger(DefaultSTModelService.class);
	@Autowired
	private ComponentDAO componentDao;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private KeyGenerator keyGenerator;
	
	@Override
	public Optional<String> transViewToSTM(DetailedItem view) throws IOException {
		//create builder for topology model
		ServiceTemplateBuilder appBuilder = ServiceTemplate.builder();
		appBuilder.name(keyGenerator.serviceTemplateNameKey(view.name));
		TopologyTemplateBuilder topoBuilder = TopologyTemplate.builder();
		
		Map<String, ContainerView.DetailedItem> nodeToContainerView = Maps.newHashMap();
		
		for(ContainerView.DetailedItem cView : view.containers){
			nodeToContainerView.put(cView.nodeId, cView);
			NoteTemplateBuilder templateBuilder = NodeTemplate.templateBuilder();
			templateBuilder.name(keyGenerator.nodeTemplateKey(cView.name, cView.nodeId))
				.maxInstance(cView.maxCount)
				.minInstance(cView.initCount);
			Component component = componentDao.findOne(cView.componentId);
			try {
				ByteSource byteSource = fileService.findFile(component.getDefineFileKey());
				NodeType nodetype = YamlUtils.loadAs(byteSource, NodeType.class);
				appBuilder.nodetype(nodetype);
				//TODO Service添加不合理
				for(ServiceElement serviceEle : ForEachHelper.of(nodetype.getServices())){
					//add Service to node template
					serviceEle.setName(keyGenerator.stmServiceNameKey(cView.name, cView.nodeId));
					ServiceElementBuilder serviceBuilder = ServiceElement.builder();
					serviceBuilder.port(String.valueOf(cView.port))
						.serviceId(keyGenerator.stmServiceIdKey(cView.name, cView.nodeId))
						.name(keyGenerator.stmServiceNameKey(cView.name, cView.nodeId));
					templateBuilder.service(serviceBuilder.build());
				}
				templateBuilder.type(nodetype.getName());
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("add type failed");
			}
			//add property to node template
			PropertyElementBuilder propertyBuilder = PropertyElement.builder();
			propertyBuilder.defaultValue(String.valueOf(cView.port)).name("port");
			templateBuilder.property(propertyBuilder.build());
			for(ContainerParamView.Item cParamView : cView.params){
				propertyBuilder.defaultValue(cParamView.value).name(cParamView.key);
				templateBuilder.property(propertyBuilder.build());
			}
			//add attributes to node template
			AttributeElementBuilder attrBuilder = AttributeElement.builder();
			for(ContainerAttributeView.Item cAttrView : cView.attributes){
				attrBuilder.name(cAttrView.attrKey).value(cAttrView.attrValue);
				templateBuilder.attribute(attrBuilder.build());
			}
			topoBuilder.nodetemplate(templateBuilder.build());
		}
		
		for(RelationView.Item rView : view.relations){
			RelationshipTemplateBuilder relationBuilder = RelationshipTemplate.templateBuilder();
			ContainerView.DetailedItem from = nodeToContainerView.get(rView.from);
			ContainerView.DetailedItem to = nodeToContainerView.get(rView.to);
			relationBuilder.name(String.valueOf(from.name + "To" + to.name))
				.source(keyGenerator.nodeTemplateKey(from.name, from.nodeId))
				.target(keyGenerator.nodeTemplateKey(to.name, to.nodeId));
			for(ContainerTemplateView.Item cTemView : from.templates){
				TemplateElementBuilder tmptBuilder = TemplateElement.builder();
				tmptBuilder.command(cTemView.command)
					.configurationfile(cTemView.target)
					.template(fileService.generateDownloadURL(cTemView.source));
				relationBuilder.template(tmptBuilder.build());
			}
			topoBuilder.relationshiptemplate(relationBuilder.build());
		}
		appBuilder.topologytemplate(topoBuilder.build());
		logger.info(YamlUtils.dumper(appBuilder.build()));
		String md5 = fileService.writeFileContent(YamlUtils.dumper(appBuilder.build()));
		return Optional.of(md5);
	}

	@Override
	public ServiceTemplate findServiceTemplateOfApplication(Application app){
		Preconditions.checkNotNull(app,"application can't be null");
		Preconditions.checkNotNull(app.getDefinedFileKey(), "can't find stm model for app");
		try {
			return YamlUtils.loadAs(fileService.findFile(app.getDefinedFileKey()), ServiceTemplate.class);
		} catch (FileNotFoundException e) {
			logger.error("can't find stm model define file for application " + app.getName(),e);
			throw new NotFoundException("can't find stm model define file for application",e);
		}
	}
	
	@Override
	public Optional<NodeTemplate> findNodeTemplateOfContainer(ServiceTemplate stm, Container container) {
		Preconditions.checkNotNull(stm.getTopologytemplate(),
				"application stm model was wrong, can't find topologyTemplate define");
		List<NodeTemplate> ntemplates = stm.getTopologytemplate().getNodetemplates();
		String key = keyGenerator.nodeTemplateKey(container.getName(), container.getIdentifier());
		for(NodeTemplate ntmpl : ForEachHelper.of(ntemplates)){
			if(ntmpl.getName().equals(key)){
				return Optional.of(ntmpl);
			}
		}
		return Optional.absent();
	}
}
