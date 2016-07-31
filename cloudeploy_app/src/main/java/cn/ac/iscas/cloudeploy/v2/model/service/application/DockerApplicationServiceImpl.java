package cn.ac.iscas.cloudeploy.v2.model.service.application;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerAttributeView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerParamView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerTemplateView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.RelationView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ApplicationView.DetailedItem;
import cn.ac.iscas.cloudeploy.v2.exception.ForbiddenException;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ApplicationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerAttributeDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerInstanceDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.RelationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.TemplateParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerAttribute;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Relation;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.TemplateParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance.Operation;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.SpecificEntityBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Attribute.AttributeBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Container.ContainerBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Container.HostConfig;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Container.PortBinding;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Service.ServiceBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Template.TemplateBuilder;
import cn.ac.iscas.cloudeploy.v2.util.application.HostSelector;

import com.orbitz.consul.Consul;
import com.orbitz.consul.option.EventOptions;
import com.orbitz.consul.option.ImmutableEventOptions;

//@Service
public class DockerApplicationServiceImpl implements ApplicationService{
	private static Logger logger = LoggerFactory.getLogger(DockerApplicationServiceImpl.class);
	private static final String INSTANCE_NAME_FORMAT = "%s-%d";
	private static final int POS_OFFSET_Y = 150;
	private static final String DOCKER_KEY_PARAM_NAME = "docker.action.param.instance.name";
	private static final String DOCKER_CONF = "docker_component.properties";
	@Autowired
	private ApplicationDAO appDAO;

	@Autowired
	private ContainerDAO containerDAO;

	@Autowired
	private ContainerParamDAO cParamDAO;
	
	@Autowired
	private TemplateParamDAO cTemDAO;
	
	@Autowired
	private ContainerAttributeDAO cAttrDAO;

	@Autowired
	private ContainerInstanceDAO cInstanceDAO;

	@Autowired
	private HostService hostService;
	
	@Autowired
	private HostSelector hostSelector;
	
	@Autowired
	private Consul consul;
	
	@Autowired
	private RelationDAO relationDAO;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ComponentDAO componentDAO;
	
	private String paramName;
	@PostConstruct
	private void init() {
		paramName = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_PARAM_NAME);
	}
	@Override
	public Application createApplication(DetailedItem view) {
		logger.info("create application for " + view.name);
		Application app = new Application(view.name);
		appDAO.save(app);
		Map<String, Container> nodeToContainer = new HashMap<String, Container>();
		for (ContainerView.DetailedItem cView : view.containers) {
			Container container = addContainerToApp(cView, app);
			nodeToContainer.put(cView.nodeId, container);
		}
		for (RelationView.Item rView : view.relations) {
			Relation relation = new Relation(nodeToContainer.get(rView.from),
					nodeToContainer.get(rView.to), app);
			relationDAO.save(relation);
		}
		return app;
	}

	private Container addContainerToApp(
			ContainerView.DetailedItem cView,
			Application app) {
		//TODO 修改component
		Container container = new Container(cView.name, cView.port,
				cView.maxCount,
				cView.initCount, app, componentDAO.findOne(cView.componentId),
				cView.xPos, cView.yPos, cView.nodeId);
		containerDAO.save(container);
		for (ContainerParamView.Item cParamView : cView.params) {
			ContainerParam param = new ContainerParam(cParamView.key,
					cParamView.value, container);
			cParamDAO.save(param);
		}
		for(ContainerTemplateView.Item cTemView : cView.templates){
			TemplateParam template = new TemplateParam(cTemView.source, cTemView.target, cTemView.command, container);
			cTemDAO.save(template);
		}
		for(ContainerAttributeView.Item cAttrView : cView.attributes){
			ContainerAttribute attr = new ContainerAttribute(cAttrView.attrKey, cAttrView.attrValue, container);
			cAttrDAO.save(attr);
		}
		return container;
	}
	
	
	@Override
	public List<Application> getAllApplications() {
		return (List<Application>) appDAO.findAll();
	}
	
	/**
	 * 与强哥最大不同点在于部署的行为，以及实时的状态查询上。从图转化为Application Container其实可以维持不变
	 * 
	 */
	@Override
	public Application deployApplication(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
		transformAppToYaml(app);
		app.changeStatusToDeployed();
		appDAO.save(app);
		return appDAO.findOne(applicationId);
	}
	
	private void transformAppToYaml(Application app){
		List<Container> containers = app.getContainers();
		for(Container cur : containers){
			if(cur.getStatus().equals(Container.Status.CREATED)){
				deployContainer(cur);
			}else if(cur.getStatus().equals(Container.Status.REMOVED)){
				removeContainer(cur);
			}
		}
	}
	
	private void removeContainer(Container cur) {
		if(cur.getStatus().equals(Container.Status.DEPLOYED)){
			for(ContainerInstance instance: cur.getInstances()){
				fireRemoveEvent(instance.getName(), instance.getMaster());
			}
		}
		cInstanceDAO.delete(cur.getInstances());
		cParamDAO.delete(cur.getParams());
		cTemDAO.delete(cur.getTemplates());
		cAttrDAO.delete(cur.getAttributes());
		relationDAO.delete(cur.getInRelations());
		relationDAO.delete(cur.getOutRelations());
		containerDAO.delete(cur);
	}
	
	private void deployContainer(Container container){
		for (int i = 0; i < container.getMaxCount()
				&& i < container.getInitCount(); i++) {
			addInstanceToContainer(container, i + 1);
		}
		container.changeStatusToDeployed();
		containerDAO.save(container);
	}
    
	private ContainerInstance addInstanceToContainer(Container container,
			int instanceSeq) {
		if (instanceSeq > container.getMaxCount()) {
			throw new ForbiddenException("容器最大实例数为" + container.getMaxCount()
					+ ",不能继续扩充");
		}
		String instanceName = "";
		for (ContainerParam param : container.getParams()) {
			if (param.getParamKey().equals(paramName)) {
				instanceName = param.getParamValue().replace("\"", "");
			}
		}
		String name = String.format(INSTANCE_NAME_FORMAT, instanceName,
				instanceSeq);
		int yPos = container.getyPos() + (instanceSeq - 1) * POS_OFFSET_Y;
		int port = container.getPort() + instanceSeq - 1;
		ContainerInstance cInstance = new ContainerInstance(instanceSeq, name,
				port, container.getxPos(), yPos, container);
		cInstance.setMaster(hostSelector.getHost());
		deployContainerInstance(cInstance);
		cInstance.changeStatusToRunning();
		cInstanceDAO.save(cInstance);
		container.addInstance(cInstance);
		return cInstance;
	}
	
	private void deployContainerInstance(ContainerInstance cInstance) {
		//builder service
		ServiceBuilder serviceBuilder = SpecificEntity.Service.builder().name("");
		serviceBuilder.name(cInstance.getContainer().getName())
			.id(cInstance.getName())
			.port(String.valueOf(cInstance.getPort()));
		//build container
		ContainerBuilder containerBuilder = SpecificEntity.Container.builder();
		PortBinding binding = PortBinding.builder()
				.port(String.valueOf(cInstance.getContainer().getPort()))
				.targetPort(String.valueOf(cInstance.getPort()))
				.build();
		containerBuilder.hostConfig(HostConfig.builder().portBinding(binding).build());
		
		for(ContainerParam p : cInstance.getContainer().getParams()){
			try {
				if(p.getParamKey().equals(paramName)) continue;
				Field field = SpecificEntity.Container.class.getDeclaredField(p.getParamKey());
				Method method = ContainerBuilder.class.getMethod(field.getName(), field.getType());
				method.invoke(containerBuilder, p.getParamValue());
			} catch (NoSuchFieldException | SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error(e.getMessage());
				logger.error("failed to handle ContainerParam for ContainerInstance");
				e.printStackTrace();
			}
		}

		containerBuilder.containerName(cInstance.getName());
		SpecificEntityBuilder builder = SpecificEntity.builder();
		builder.container(containerBuilder.build())
				.service(serviceBuilder.build())
				.name(cInstance.getName());
		
		for(TemplateParam temParam : cInstance.getContainer().getTemplates()){
			TemplateBuilder temBuilder = SpecificEntity.Template.builder()
					.source(fileService.generateDownloadURL(temParam.getSource()))
					.target(temParam.getTarget())
					.command(temParam.getCommand());
			builder.template(temBuilder.build());
		}
		
		for(ContainerAttribute containerAttr : cInstance.getContainer().getAttributes()){
			AttributeBuilder attrBuilder = SpecificEntity.Attribute.builder();
			attrBuilder.key(containerAttr.getAttrKey()).value(containerAttr.getAttrValue());
			builder.attribute(attrBuilder.build());
		}
		fireDeployEvent(builder, cInstance.getName(), cInstance.getMaster());
	}
	
	private void fireDeployEvent(SpecificEntityBuilder builder, String instanceName, DHost host){
		Yaml yaml = new Yaml();
		String content = yaml.dumpAs(builder.build(), Tag.MAP, FlowStyle.AUTO);
		logger.info("transform container to yaml specific : " +content);
		try {
			String md5 = fileService.writeFileContent(content);
			String url = fileService.generateDownloadURL(md5);
			String payload = url;
			fireEvent("deploy", host, payload);
		} catch (IOException e) {
			logger.error("store containerInstance's specificEntity as file failed : " + instanceName);
			e.printStackTrace();
		}
	}

	private void fireRemoveEvent(String name, DHost host) {
		fireEvent("delete", host, name);
		consul.agentClient().deregister(name);
	}
	
	private void fireEvent(String eventName, DHost host, String payload){
		logger.info("fire a event: " + eventName +" host: " + host.getHostName() + "payload: " + payload );
		EventOptions eventOptions = ImmutableEventOptions.builder()
				.nodeFilter(host.getHostName()).build();
		consul.eventClient().fireEvent(eventName, eventOptions, payload);
	}
	
	@Override
	public Application getApplication(Long applicationId) {
		return appDAO.findOne(applicationId);
	}

	@Override
	public boolean doOperationOnContainerInstance(Long containerInatanceId,
			Operation operation) {
		ContainerInstance cInstance = cInstanceDAO.findOne(containerInatanceId);
		ContainerInstance operationedInstance = cInstance;
		switch (operation) {
		case START:
			if (cInstance.getStatus().equals(ContainerInstance.Status.STOPPED)
					|| cInstance.getStatus().equals(
							ContainerInstance.Status.ERROR)){
				fireStartEvent(cInstance);
				cInstance.changeStatusToRunning();
				cInstanceDAO.save(cInstance);
			}
			break;
		case STOP:
			if (cInstance.getStatus().equals(ContainerInstance.Status.RUNNING)) {
				fireStopEvent(cInstance);
				cInstance.changeStatusToStop();
				cInstanceDAO.save(cInstance);
			}
			break;
		case COPY:
			ContainerInstance newInst = addInstanceToContainer(cInstance.getContainer(), 
					getSeqOfNextInstance(cInstance.getContainer()));
			operationedInstance = newInst;
			break;
		case REMOVE:
			fireRemoveEvent(cInstance.getName(), cInstance.getMaster());
			cInstanceDAO.delete(cInstance);
			cInstance.getContainer().removeInstance(cInstance);
			break;
		case FAIL:
			fireStopEvent(cInstance);
			cInstance.changeStatusToError();
			cInstanceDAO.save(cInstance);
			break;
		default:
			break;
		}
		Container container = operationedInstance.getContainer();
		if (container.getInstances().size() == 0) {
			container.changeStatusToCreated();
			containerDAO.save(container);
		}
		return true;
	}

	private void fireStopEvent(ContainerInstance cInstance) {
		fireEvent("stop", cInstance.getMaster(), cInstance.getName());
	}
	private void fireStartEvent(ContainerInstance cInstance) {
		fireEvent("start", cInstance.getMaster(), cInstance.getName());
	}
	private int getSeqOfNextInstance(Container container) {
		if (container.getInstances().size() >= container.getMaxCount()) {
			throw new ForbiddenException("容器最大实例数为" + container.getMaxCount()
					+ ",不能继续扩充");
		}
		List<ContainerInstance> instances = new ArrayList<>();
		instances.addAll(container.getInstances());
		Collections.sort(instances, new Comparator<ContainerInstance>() {
			@Override
			public int compare(ContainerInstance o1, ContainerInstance o2) {
				return o1.getSeq() - o2.getSeq();
			}
		});
		for (int i = 0; i < container.getMaxCount() && i < instances.size(); i++) {
			if (i + 1 != instances.get(i).getSeq()) {
				return i + 1;
			}
		}
		return instances.size() + 1;
	}
	@Override
	public boolean removeApplication(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
		for(Container current: app.getContainers()){
			removeContainer(current);
		}
		appDAO.delete(app);
		return true;
	}

	@Override
	public Application modifyApplication(Long applicationId, DetailedItem view) {
		logger.info("modify application");
		Application app = appDAO.findOne(applicationId);
		Map<Long, Container> idToContainers = new HashMap<>();
		Map<Long, Relation> idToRelations = new HashMap<>();
		Map<String, Relation> fromAndToRelations = new HashMap<>();
		for (Container container : app.getContainers()) {
			idToContainers.put(container.getId(), container);
		}
		for (Relation relation : app.getRelations()) {
			idToRelations.put(relation.getId(), relation);
			fromAndToRelations.put(relation.getFrom().getIdentifier()
					+ relation.getTo().getIdentifier(), relation);
		}
		Map<String, Container> nodeToContainer = new HashMap<>();
		Set<Long> currentIds = new HashSet<>();
		boolean changed = false;
		for (ContainerView.DetailedItem cView : view.containers) {
			Container container = null;
			if (idToContainers.containsKey(cView.id)) {// 保存旧节点
				container = idToContainers.get(cView.id);
				if (container.getStatus().equals(Container.Status.CREATED)) {
					saveModificationToContainer(cView, container);
					changed = true;
				}
			} else {// 保存新节点
				container = addContainerToApp(cView, app);
				changed = true;
			}
			if (container != null) {
				currentIds.add(container.getId());
				nodeToContainer.put(container.getIdentifier(), container);
			}
		}
		// 保存被移除的节点到remove状态，等待部署时实施
		for (Container container : app.getContainers()) {
			if (!currentIds.contains(container.getId())) {
				container.changeStatusToRemoved();
				containerDAO.save(container);
				changed = true;
			}
		}

		// 保存边
		Set<String> currentRelations = new HashSet<>();
		for (RelationView.Item rView : view.relations) {
			if (rView.from == null || rView.to == null) {
				continue;
			}
			String relationKey = rView.from + rView.to;
			if (!fromAndToRelations.containsKey(relationKey)) {
				Relation relation = new Relation(
						nodeToContainer.get(rView.from),
						nodeToContainer.get(rView.to), app);
				relationDAO.save(relation);
				changed = true;
			}
			currentRelations.add(relationKey);
		}
		for (Relation relation : app.getRelations()) {
			if (!currentRelations.contains(relation.getFrom().getIdentifier()
					+ relation.getTo().getIdentifier())) {
				relationDAO.delete(relation);
				changed = true;
			}
		}
		if (changed) {
			app.changeStatusToModified();
			appDAO.save(app);
		}
		return app;
	}
	private void saveModificationToContainer(
			ContainerView.DetailedItem cView,
			Container container) {
		container.reset(cView.name, cView.port,
				cView.maxCount,
				cView.initCount, container.getApplication(),
				componentDAO.findOne(cView.componentId), cView.xPos,
				cView.yPos, cView.nodeId);
		containerDAO.save(container);
		saveParamModificationToContainerParam(cView, container);
		saveTemplateModificationToContainer(cView, container);
		saveAttributeModificationToContainer(cView, container);
	}
	private void saveParamModificationToContainerParam(
			ContainerView.DetailedItem cView, Container container) {
		Map<String, ContainerParam> oldParams = new HashMap<>();
		for (ContainerParam param : container.getParams()) {
			oldParams.put(param.getParamKey(), param);
		}
		for (ContainerParamView.Item cParamView : cView.params) {
			if (oldParams.containsKey(cParamView.key)) {
				oldParams.get(cParamView.key).setParamValue(cParamView.value);
			} else {
				ContainerParam cp = new ContainerParam(cParamView.key,
						cParamView.value, container);
				oldParams.put(cParamView.key, cp);
			}
		}
	    cParamDAO.save(oldParams.values());
	}
	
	private void saveTemplateModificationToContainer(
			ContainerView.DetailedItem cView, Container container) {
		Map<String, TemplateParam> oldParams = new HashMap<>();
		for (TemplateParam param : container.getTemplates()) {
			oldParams.put(param.getSource(), param);
		}
		Set<Long> currentIds = new HashSet<Long>();
		for (ContainerTemplateView.Item cParamView : cView.templates) {
			if (oldParams.containsKey(cParamView.source)) {
				TemplateParam oldParam = oldParams.get(cParamView.source);
				oldParam.setTarget(cParamView.target);
				oldParam.setCommand(cParamView.command);
				currentIds.add(oldParam.getId());
			} else {
				TemplateParam newParam = new TemplateParam(cParamView.source, cParamView.target, cParamView.command,container);
				cTemDAO.save(newParam);
				currentIds.add(newParam.getId());
			}
		}
		
		//删除未用的
		List<TemplateParam> removes = new ArrayList<TemplateParam>();
		for(TemplateParam param : container.getTemplates()){
			if(!currentIds.contains(param.getId())){
				oldParams.remove(param.getSource());
				removes.add(param);
			}
		}
		cTemDAO.delete(removes);
	    cTemDAO.save(oldParams.values());
	}
	
	private void saveAttributeModificationToContainer(
			ContainerView.DetailedItem cView, Container container) {
		Map<String, ContainerAttribute> oldParams = new HashMap<>();
		for (ContainerAttribute param : container.getAttributes()) {
			oldParams.put(param.getAttrKey(), param);
		}
		Set<Long> currentIds = new HashSet<Long>();
		for (ContainerAttributeView.Item cParamView : cView.attributes) {
			if (oldParams.containsKey(cParamView.attrKey)) {
				ContainerAttribute oldParam = oldParams.get(cParamView.attrKey);
				oldParam.setAttrValue(cParamView.attrValue);
				currentIds.add(oldParam.getId());
			} else {
				ContainerAttribute newParam = new ContainerAttribute(cParamView.attrKey, cParamView.attrValue, container);
				cAttrDAO.save(newParam);
				currentIds.add(newParam.getId());
			}
		}
		
		//删除未用的
		for(ContainerAttribute param : container.getAttributes()){
			if(!currentIds.contains(param.getId())){
				oldParams.remove(param.getAttrKey());
				cAttrDAO.delete(param);
			}
		}
	    cAttrDAO.save(oldParams.values());
	}
}
