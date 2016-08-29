package cn.ac.iscas.cloudeploy.v2.model.service.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.Node;
import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;

import cn.ac.iscas.cloudeploy.v2.exception.ForbiddenException;
import cn.ac.iscas.cloudeploy.v2.exception.InternalServerErrorException;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerInstanceDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.NodeTemplatePacketDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance.Status;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.NodeTemplatePacket;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.NodeTemplatePacket.NtmplPacketEntityBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.DeployEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.RemoveEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StartEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.StopEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.config.RegisterCenterService;
import cn.ac.iscas.cloudeploy.v2.model.service.event.EventFactory;
import cn.ac.iscas.cloudeploy.v2.model.service.event.EventService;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.key.KeyGenerator;
import cn.ac.iscas.cloudeploy.v2.model.service.packet.PacketService;
import cn.ac.iscas.cloudeploy.v2.model.service.topology.STModelService;
import cn.ac.iscas.cloudeploy.v2.packet.Packet;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.util.YamlUtils;
import cn.ac.iscas.cloudeploy.v2.workflowEngine.entity.Deployment;

@org.springframework.stereotype.Service
public class ApplicationRuntimeService {
	private static Logger logger = LoggerFactory.getLogger(ApplicationRuntimeService.class);
	private static final String INSTANCE_NAME_FORMAT = "%s_%d";
	private static final int POS_OFFSET_Y = 150;
	@Autowired
	private ContainerDAO containerDAO;
	@Autowired
	private ContainerInstanceDAO cInstanceDAO;
	@Autowired
	private STModelService stmService;
	@Autowired
	private PacketService packetService;
	@Autowired
	private NodeTemplatePacketDAO ntmplDAO;
	@Autowired
	private FileService fileService;
	@Autowired
	private AppConfigManager appConfigManager;
	@Autowired
	private RegisterCenterService regCenterService;
	@Autowired
	@Qualifier("dockerEventFactory")
	private EventFactory dockerEventFactory;
	@Autowired
	@Qualifier("puppetEventFactory")
	private EventFactory puppetEventFactory;
	@Autowired
	private EventService eventService;
	@Autowired
	private KeyGenerator keyGenerator;
	@Autowired
	private HostService hostService;
	/**
	 * 调用packetService为应用创建部署任务。
	 * @param app
	 * @param strategy
	 * @throws IOException
	 */
	public void createDeploymentPlanForApplication(Application app, PacketStrategy strategy) throws IOException{
		ServiceTemplate stm = stmService.findServiceTemplateOfApplication(app);
		Preconditions.checkNotNull(stm.getTopologytemplate(),
				"application stm model was wrong, can't find topologyTemplate define");
		Deployment deployment = new Deployment();
		List<NodeTemplate> ntemplates = stm.getTopologytemplate().getNodetemplates();
		for(NodeTemplate ntmpl : ForEachHelper.of(ntemplates)){
			int init = ntmpl.getMinInstance(), max = ntmpl.getMaxInstance();
			for(int i = 0; i < init && i < max; i++){
				Optional<Packet> packet = packetService.createPacketForNodeTemplate(stm, ntmpl, strategy, i);
				NtmplPacketEntityBuilder builder = NodeTemplatePacket.builder();
				builder.templateName(ntmpl.getName())
					.packetMd5(packet.get().getPacket())
					.packetStrategy(strategy);
				ntmplDAO.save(builder.build());
				deployment.addPacket(packet.get());
			}
		}
		String md5 = fileService.writeFileContent(YamlUtils.dumper(deployment));
		app.setDeployFileKey(md5);
	}
	
	/**
	 * deploy application task of user with a specific strategy(Docker,Puppet)
	 * @param app
	 * @param user
	 * @param strategy
	 */
	public void deployApplication(Application app, User user, PacketStrategy strategy){
		EventFactory factory  = null;
		switch(strategy){
		case DOCKER:
			factory = dockerEventFactory;
			break;
		case VMPUPPET:
			factory = puppetEventFactory;
			break;
		default:
			throw new UnsupportedOperationException("don't support deploy application using " + strategy);
		}
		deployApplication(app, user, factory);
	}
	
	private void deployApplication(Application app, User user, EventFactory factory){
		ServiceTemplate application;
		try {
			application = YamlUtils.loadAs(fileService.findFile(app.getDefinedFileKey()), ServiceTemplate.class);
			Deployment deployment = YamlUtils.loadAs(fileService.findFile(app.getDeployFileKey()), Deployment.class);
			appConfigManager.registerAppProperties(application, user);
			for(Packet packet : ForEachHelper.of(deployment.getPackets())){
				deployPacket(packet, factory);
			}
			initContainerInstances(app);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}
	
	private void initContainerInstances(Application app){
		List<Container> containers = app.getContainers();
		for(Container container : ForEachHelper.of(containers)){
			for (int i = getSeqOfNextInstance(container); i < container.getMaxCount()
					&& i < container.getInitCount(); i++) {
				addContainerInstance(container, i);
			}
			containerDAO.save(container);
		}
	}
	
	private void addContainerInstance(Container container, int instanceSeq){
		if (instanceSeq > container.getMaxCount()) {
			throw new ForbiddenException("容器最大实例数为" + container.getMaxCount()
					+ ",不能继续扩充");
		}
		String instanceName = keyGenerator.stmServiceIdKey(container.getName(), container.getIdentifier());
		String name = String.format(INSTANCE_NAME_FORMAT, instanceName,
				instanceSeq);
		int yPos = container.getyPos() + instanceSeq * POS_OFFSET_Y;
		int port = container.getPort() + instanceSeq;
		ContainerInstance cInstance = new ContainerInstance(instanceSeq, name,
				port, container.getxPos(), yPos, container);
		cInstanceDAO.save(cInstance);
		container.addInstance(cInstance);
	}
	
	public void runningInstancesOfContainer(Container container){
		String serviceName = container.getName();
		List<ServiceHealth> serviceHealths = regCenterService.findServiceInstanceWithStatus(serviceName);
	}
	
	private ContainerInstance instanceOfContainer(Service service, Container container) {
		ContainerInstance cInstance = cInstanceDAO.findByName(service.getId());
		if(cInstance == null){
			String id = service.getId();
			int seq = getSeq(id);
			int yPos = container.getyPos() + seq * POS_OFFSET_Y;
			int xPos = container.getxPos();
			cInstance = new ContainerInstance(seq, id, service.getPort(), xPos, yPos, container);
			container.addInstance(cInstance);
			cInstanceDAO.save(cInstance);
		}
		return cInstance;
	}
	
	private int getSeq(String id){
		String[] strs = id.split("_");
		return Integer.valueOf(strs[strs.length - 1]);
	}

	public void instancesOfContainer(Container container){
		String serviceName = keyGenerator.stmServiceNameKey(container.getName(), container.getIdentifier());
		List<ServiceHealth> serviceHealths = regCenterService.findServiceInstanceWithStatus(serviceName);
		for(ServiceHealth serviceHealth: ForEachHelper.of(serviceHealths)){
			Service service = serviceHealth.getService();
			ContainerInstance cInstance = instanceOfContainer(service,container);
			//set host
			Node node = serviceHealth.getNode();
			DHost host = hostService.findByHostIp(node.getAddress());
			cInstance.setMaster(host);
			//set status
			List<HealthCheck> checks = serviceHealth.getChecks();
			for(HealthCheck healthCheck: ForEachHelper.of(checks)){
				String status = healthCheck.getStatus();
				switch(status){
				case "passing":
					cInstance.changeStatusToRunning();
					break;
				default:
					cInstance.changeStatusToStop();
					break;
				}
			}
			cInstanceDAO.save(cInstance);
		}
	}
	
	public void scaleUpContainer(Container container, PacketStrategy strategy){
		EventFactory factory  = null;
		switch(strategy){
		case DOCKER:
			factory = dockerEventFactory;
			break;
		case VMPUPPET:
			factory = puppetEventFactory;
			break;
		default:
			throw new UnsupportedOperationException("don't support deploy application using " + strategy);
		}
		Optional<Packet> packet;
		try {
			packet = addContainerInstance(container, factory, strategy);
			deployPacket(packet.get(), factory);
		} catch (IOException e) {
			logger.error("scale up container failed", e);
			e.printStackTrace();
		}
	}
	
	private void deployPacket(Packet packet, EventFactory factory){
		String url = fileService.generateDownloadURL(packet.getPacket());
		DeployEvent deployEvent = factory.createDeployEvent(url);
		eventService.pubDeployEvent(deployEvent);
	}
	
	private Optional<Packet> addContainerInstance(Container container, EventFactory factory, PacketStrategy strategy) throws IOException {
		Application app = container.getApplication();
		ServiceTemplate stm = stmService.findServiceTemplateOfApplication(app);
		Optional<NodeTemplate> ntmpl = stmService.findNodeTemplateOfContainer(stm, container);
		Preconditions.checkArgument(ntmpl.isPresent(),"can't find nodetemplate define for container:" + container.getName());
		int seq = getSeqOfNextInstance(container);
		Optional<Packet> packet = packetService.createPacketForNodeTemplate(stm, ntmpl.get(), strategy, seq);
		addContainerInstance(container, seq);
		return packet;
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
			if (i != instances.get(i).getSeq()) {
				return i;
			}
		}
		return instances.size();
	}

	public void startInstance(ContainerInstance cInstance) {
		Status status = cInstance.getStatus();
		if(status == Status.CREATED){
			logger.info("containerInstance status is created and it's not running or stoped");
			return;
		}
		StartEvent event = dockerEventFactory.createStartEvent(cInstance);
		eventService.pubStartEvent(event);
		cInstance.changeStatusToRunning();
		cInstanceDAO.save(cInstance);
	}

	public void stopInstance(ContainerInstance cInstance) {
		Status status = cInstance.getStatus();
		if(status == Status.CREATED) {
			logger.info("containerInstance status is created and it's not running or stoped");
			return;
		}
		StopEvent event = dockerEventFactory.createStopEvent(cInstance);
		eventService.pubStopEvent(event);
		cInstance.changeStatusToStop();
		cInstanceDAO.save(cInstance);
	}

	public void removeInstance(ContainerInstance cInstance) {
		Status status = cInstance.getStatus();
		if(status == Status.CREATED) {
			logger.info("containerInstance status is created");
		}else{
			RemoveEvent event = dockerEventFactory.createRemoveEvent(cInstance);
			eventService.pubRemoveEvent(event);
		}
		cInstanceDAO.delete(cInstance);
		cInstance.getContainer().removeInstance(cInstance);
		Container container = cInstance.getContainer();
		if (container.getInstances().size() == 0) {
			container.changeStatusToCreated();
		}
		containerDAO.save(container);
	}

	public void failedInstance(ContainerInstance cInstance) {
		stopInstance(cInstance);
		cInstance.changeStatusToError();
		cInstanceDAO.save(cInstance);
	}
}
