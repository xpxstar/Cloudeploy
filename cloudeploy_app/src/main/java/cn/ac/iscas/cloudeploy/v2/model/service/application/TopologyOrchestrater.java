package cn.ac.iscas.cloudeploy.v2.model.service.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.orbitz.consul.model.catalog.CatalogService;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ApplicationView.DetailedItem;
import cn.ac.iscas.cloudeploy.v2.exception.InternalServerErrorException;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerAttributeView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerParamView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerTemplateView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.RelationView;
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
import cn.ac.iscas.cloudeploy.v2.model.service.config.RegisterCenterService;
import cn.ac.iscas.cloudeploy.v2.model.service.topology.STModelService;
import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;
//Container与Application是与Web界面对应的概念
//STM模型。
//ContainerInstance对应运行时的概念。
@Service
public class TopologyOrchestrater implements ApplicationService{
	private Logger logger = LoggerFactory.getLogger(TopologyOrchestrater.class);
	@Autowired
	private ApplicationDAO appDAO;
	@Autowired
	private ComponentDAO componentDao;
	@Autowired
	private ContainerDAO containerDAO;

	@Autowired
	private ContainerParamDAO cParamDAO;
	
	@Autowired
	private TemplateParamDAO cTemDAO;
	
	@Autowired
	private ContainerAttributeDAO cAttrDAO;
	@Autowired
	private RelationDAO relationDAO;
	@Autowired
	private ContainerInstanceDAO cInstanceDAO;

	@Autowired
	private STModelService stmService;
	@Autowired
	private ApplicationRuntimeService runtimeService;
	@Autowired
	private UserService userService;
	
	@Override
	public Application createApplication(DetailedItem view) {
		logger.info("create application for " + view.name);
		Application app = new Application(view.name);
		try {
			Optional<String> md5 = stmService.transViewToSTM(view);
			if(md5.isPresent()){
				app.setDefinedFileKey(md5.get());
			}
		} catch (IOException e) {
			logger.error("create stm model for view failed",e);
			throw new InternalServerErrorException("create stm model for view failed",e);
		}
		storeGraph(view, app);
		appDAO.save(app);
		createDeploymentPlan(app.getId(), PacketStrategy.DOCKER);
		return app;
	}
	
	/**
	 * store the edge and node as well as their positions of the Graph
	 * @param view
	 * @param app
	 */
	private void storeGraph(DetailedItem view, Application app) {
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
	}
	
	private Container addContainerToApp(
			ContainerView.DetailedItem cView,
			Application app) {
		//TODO 修改component
		Container container = new Container(cView.name, cView.port,
				cView.maxCount,
				cView.initCount, app, componentDao.findOne(cView.componentId),
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
	 * create a Task(plan) with specific strategy(docker,puppet)
	 * @param applicationId
	 * @param startegy
	 */
	public void createDeploymentPlan(Long applicationId, PacketStrategy startegy){
		Application app = appDAO.findOne(applicationId);
		try {
			runtimeService.createDeploymentPlanForApplication(app, startegy);
		} catch (IOException e) {
			logger.error("create deployment plan for application failed:" + app.getName(), e);
		}
	}

	@Override
	public Application deployApplication(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
		runtimeService.deployApplication(app, userService.getCurrentUser(), PacketStrategy.DOCKER);
		app.changeStatusToDeployed();
		appDAO.save(app);
		return appDAO.findOne(applicationId);
	}

	@Override
	public Application getApplication(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
		for(Container container :app.getContainers()){
			getInstanceOfContainer(container);
		}
		return app;
	}

	@Override
	public boolean doOperationOnContainerInstance(Long containerInatanceId, Operation operation) {
		ContainerInstance cInstance = cInstanceDAO.findOne(containerInatanceId);
		ContainerInstance operationInstance = cInstance;
		switch(operation){
		case START:
			runtimeService.startInstance(operationInstance);
			break;
		case STOP:
			runtimeService.stopInstance(operationInstance);
			break;
		case COPY:
			runtimeService.scaleUpContainer(operationInstance.getContainer(), PacketStrategy.DOCKER);
			break;
		case REMOVE:
			runtimeService.removeInstance(operationInstance);
			break;
		case FAIL:
			runtimeService.failedInstance(operationInstance);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean removeApplication(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
		for(Container current: app.getContainers()){
			removeContainer(current);
		}
		relationDAO.delete(app.getRelations());
		appDAO.delete(app);
		return true;
	}

	private void removeContainer(Container cur) {
		if(cur.getStatus().equals(Container.Status.DEPLOYED)){
			for(ContainerInstance instance: cur.getInstances()){
				runtimeService.removeInstance(instance);
			}
		}
		cParamDAO.delete(cur.getParams());
		cTemDAO.delete(cur.getTemplates());
		cAttrDAO.delete(cur.getAttributes());
		containerDAO.delete(cur);
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
		return null;
	}
	
	public void getInstanceOfContainer(Container container){
		runtimeService.instancesOfContainer(container);
		if(container.getInstances().size() > 0) container.changeStatusToDeployed();
	}
}
