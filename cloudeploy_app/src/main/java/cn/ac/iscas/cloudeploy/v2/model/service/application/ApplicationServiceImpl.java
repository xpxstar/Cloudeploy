package cn.ac.iscas.cloudeploy.v2.model.service.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ApplicationView.DetailedItem;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerParamView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.ContainerView;
import cn.ac.iscas.cloudeploy.v2.dataview.application.RelationView;
import cn.ac.iscas.cloudeploy.v2.exception.ForbiddenException;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ApplicationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerInstanceDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.RelationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance.Operation;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Relation;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.log.LogService;
import cn.ac.iscas.cloudeploy.v2.model.service.proxy.ProxyService;
//目前不可用，需要重写部分逻辑
//@Service
public class ApplicationServiceImpl implements ApplicationService {
	private static final String INSTANCE_NAME_FORMAT = "%s-%d";
	private static final String NGINX_LINKS_FORMAT = "\"%s:%s\",";
	private static final int POS_OFFSET_Y = 150;
	private static final String DOCKER_CONF = "docker_component.properties";
	private static final String DOCKER_KEY_ACTION_CREATE = "docker.action.suffix.create";
	private static final String DOCKER_KEY_ACTION_START = "docker.action.suffix.start";
	private static final String DOCKER_KEY_ACTION_STOP = "docker.action.suffix.stop";
	private static final String DOCKER_KEY_ACTION_REMOVE = "docker.action.suffix.remove";
	private static final String DOCKER_KEY_PARAM_PORT = "docker.action.param.instance.port";
	private static final String DOCKER_KEY_PARAM_NAME = "docker.action.param.instance.name";
	private static final String DOCKER_KEY_PARAM_SERVICE = "docker.action.param.instance.service";
	private static final String DOCKER_KEY_PARAM_NGINX = "docker.action.param.instance.nginx";
	private static final String DOCKER_KEY_PARAM_LINKS = "docker.action.param.instance.links";
	private static final String DOCKER_KEY_COMPONENT_NGINX = "docker.component.nginx";

	private String actionCreate;
	private String actionStart;
	private String actionStop;
	private String actionRemove;
	private String paramPort;
	private String paramName;
	private String paramService;
	private String paramNginx;
	private String paramLinks;
	private String componentNginx;

	private enum ActionType {
		CREATE, START, STOP, REMOVE
	}

	@Autowired
	private ApplicationDAO appDAO;

	@Autowired
	private ContainerDAO containerDAO;

	@Autowired
	private ContainerParamDAO cParamDAO;

	@Autowired
	private ContainerInstanceDAO cInstanceDAO;

	@Autowired
	private HostService hostService;

	@Autowired
	private ActionDAO actionDAO;

	@Autowired
	private ComponentDAO componentDAO;

	@Autowired
	private ProxyService proxyService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private RelationDAO relationDAO;

	@Autowired
	private LogService logService;

	@PostConstruct
	private void init() {
		actionCreate = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_ACTION_CREATE);
		actionStart = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_ACTION_START);
		actionStop = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_ACTION_STOP);
		actionRemove = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_ACTION_REMOVE);
		paramPort = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_PARAM_PORT);
		paramName = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_PARAM_NAME);
		paramService = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_PARAM_SERVICE);
		paramNginx = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_PARAM_NGINX);
		paramLinks = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_PARAM_LINKS);
		componentNginx = configService.getConfigAsString(DOCKER_CONF,
				DOCKER_KEY_COMPONENT_NGINX);
	}

	@Override
	public Application createApplication(DetailedItem view) {
		Application app = new Application(view.name);
		appDAO.save(app);
		Map<String, Container> nodeToContainer = new HashMap<>();
		for (ContainerView.DetailedItem cView : view.containers) {
			Container container = addContainerToApp(cView, app);
			nodeToContainer.put(cView.nodeId, container);
		}
		for (RelationView.Item rView : view.relations) {
			Relation relation = new Relation(nodeToContainer.get(rView.from),
					nodeToContainer.get(rView.to), app);
			relationDAO.save(relation);
			// app.addRelation(relation);
			// nodeToContainer.get(rView.from).addOutRelation(relation);
			// nodeToContainer.get(rView.to).addInRelation(relation);
		}
		return app;
	}

	private Container addContainerToApp(ContainerView.DetailedItem cView,
			Application app) {
		Container container = new Container(cView.name, cView.port,
				cView.maxCount,
				cView.initCount, app, componentDAO.findOne(cView.componentId),
				cView.xPos, cView.yPos, cView.nodeId);
		containerDAO.save(container);
		// app.addContainer(container);
		for (ContainerParamView.Item cParamView : cView.params) {
			ContainerParam param = new ContainerParam(cParamView.key,
					cParamView.value, container);
			cParamDAO.save(param);
			// container.addParam(param);
		}
		return container;
	}

	@Override
	public List<Application> getAllApplications() {
		return (List<Application>) appDAO.findAll();
	}

	@Override
	public Application deployApplication(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
		Stack<Container> toVisit = new Stack<>();
		Queue<Container> cache = new LinkedList<>();
		Set<Long> inStack = new HashSet<>();
		Set<Long> inVisit = new HashSet<>();
		for (Container container : app.getContainers()) {
			if (container.getInRelations().isEmpty()) {
				cache.add(container);
				inStack.add(container.getId());
			}
		}
		while (!cache.isEmpty()) {
			Container current = cache.poll();
			if (current.getStatus().equals(Container.Status.CREATED)
					&& !inVisit.contains(current.getId())) {
				toVisit.push(current);
			}
			for (Relation out : current.getOutRelations()) {
				if (!inStack.contains(out.getTo().getId())) {
					cache.add(out.getTo());
					inStack.add(out.getTo().getId());
				}
			}
		}
		while (!toVisit.isEmpty()) {
			Container current = toVisit.pop();
			if (current.getStatus().equals(Container.Status.CREATED)) {// 部署新容器
				if (current.getComponent().getName().equals(componentNginx)) {
					setNginxLinks(current);
				}
				deployContainer(current);
				if (hasNginxParam(current)) {
					triggerNginxByContainer(current);
				}
			} else if (current.getStatus().equals(Container.Status.REMOVED)) {// 移除旧容器
				removeContainer(current);
				if (hasNginxParam(current)) {
					triggerNginxByContainer(current);
				}
			}
		}
		app.changeStatusToDeployed();
		appDAO.save(app);
		return appDAO.findOne(applicationId);
	}

	private void setNginxLinks(Container container) {
		ContainerParam links = null;
		for (ContainerParam param : container.getParams()) {
			if (param.getParamKey().equals(paramLinks)) {
				links = param;
				break;
			}
		}
		if (links != null) {
			Set<String> linkSet = new HashSet<>();
			for (Relation relation : container.getOutRelations()) {
				Container target = relation.getTo();
				for (ContainerInstance inst : target.getInstances()) {
					if (inst.getStatus().equals(
							ContainerInstance.Status.RUNNING)) {
						linkSet.add(String.format(NGINX_LINKS_FORMAT,
								inst.getName(), inst.getName()));
					}
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (String l : linkSet) {
				sb.append(l);
			}
			sb.append("]");
			links.setParamValue(sb.toString());
			cParamDAO.save(links);
		}
	}

	private void triggerNginxByContainer(Container container) {
		for (Relation relation : container.getInRelations()) {
			if (relation.getFrom().getComponent().getName()
					.equals(componentNginx)) {
				setNginxLinks(relation.getFrom());
				if (relation.getFrom().getInstances().size() == 0) {
					deployContainer(relation.getFrom());
				} else {
					for (ContainerInstance nginxInstance : relation.getFrom()
							.getInstances()) {
						doActionOnContainerInstance(nginxInstance,
								actionCreate, ActionType.CREATE);
					}
				}
			}
		}
	}

	private boolean hasNginxParam(Container container) {
		for (ContainerParam param : container.getParams()) {
			if (param.getParamKey().equals(paramNginx)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 初始部署
	 * 
	 * @param container
	 */
	private void deployContainer(Container container) {
		for (int i = 0; i < container.getMaxCount()
				&& i < container.getInitCount(); i++) {
			addInstanceToContainer(container, i + 1);
		}
		container.changeStatusToDeployed();
		containerDAO.save(container);
	}

	private void removeContainer(Container container) {
		for (ContainerInstance instance : container.getInstances()) {
			doActionOnContainerInstance(instance, actionRemove,
					ActionType.REMOVE);
		}
		cInstanceDAO.delete(container.getInstances());
		cParamDAO.delete(container.getParams());
		relationDAO.delete(container.getInRelations());
		relationDAO.delete(container.getOutRelations());
		containerDAO.delete(container);
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
		doActionOnContainerInstance(cInstance, actionCreate, ActionType.CREATE);
		// doActionOnContainerInstance(cInstance, actionStart,
		// ActionType.START);
		cInstance.changeStatusToRunning();
		cInstanceDAO.save(cInstance);
		container.addInstance(cInstance);
		return cInstance;
	}

	/**
	 * 在某个容器实例上实施操作
	 * 
	 * @param instance
	 * @param actionKey
	 * @param type
	 */
	private void doActionOnContainerInstance(ContainerInstance instance,
			String actionKey, ActionType type) {
		Action action = actionOfComponent(instance.getContainer()
				.getComponent(), actionKey);
		Map<String, String> containerParams = new HashMap<>();
		for (ContainerParam p : instance.getContainer().getParams()) {
			containerParams.put(p.getParamKey(), p.getParamValue());
		}
		Map<String, String> params = new HashMap<>();
		for (ActionParam p : action.getParams()) {
			if (p.getParamKey().equals(paramPort)) {// 设置实例端口号
				params.put(p.getParamKey(), String.valueOf(instance.getPort()));
				continue;
			}
			if (p.getParamKey().equals(paramName)) {// 设置实例名
				params.put(p.getParamKey(), String.valueOf(instance.getName()));
				continue;
			}
			if (p.getParamKey().equals(paramService)) {// 启动服务
				if (type.equals(ActionType.START)) {
					params.put(p.getParamKey(), String.valueOf(true));
				} else if (type.equals(ActionType.STOP)) {
					params.put(p.getParamKey(), String.valueOf(false));
				}
				continue;
			}
			params.put(
					p.getParamKey(),
					containerParams.containsKey(p.getParamKey()) ? containerParams
							.get(p.getParamKey()) : p.getDefaultValue());
		}
		//TODO 需要重写的部分
//		logService.printActionLog(action, instance.getContainer().getMaster(),
//				params);
//		proxyService.applyActionOnHost(action, params, instance.getContainer()
//				.getMaster());
	}

	private Action actionOfComponent(Component component, String suffix) {
		for (Action action : component.getActions()) {
			if (action.getName().endsWith(suffix)) {
				return action;
			}
		}
		return null;
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
							ContainerInstance.Status.ERROR)) {
				doActionOnContainerInstance(cInstance, actionStart,
						ActionType.START);
				cInstance.changeStatusToRunning();
				cInstanceDAO.save(cInstance);
			}
			break;
		case STOP:
			if (cInstance.getStatus().equals(ContainerInstance.Status.RUNNING)) {
				doActionOnContainerInstance(cInstance, actionStop,
						ActionType.STOP);
				cInstance.changeStatusToStop();
				cInstanceDAO.save(cInstance);
			}
			break;
		case COPY:
			ContainerInstance newInst = addInstanceToContainer(
					cInstance.getContainer(),
					getSeqOfNextInstance(cInstance.getContainer()));
			operationedInstance = newInst;
			break;
		case REMOVE:
			doActionOnContainerInstance(cInstance, actionRemove,
					ActionType.REMOVE);
			cInstanceDAO.delete(cInstance);
			cInstance.getContainer().removeInstance(cInstance);
			break;
		case FAIL:
			doActionOnContainerInstance(cInstance, actionStop, ActionType.STOP);
			cInstance.changeStatusToError();
			cInstanceDAO.save(cInstance);
			break;
		default:
			break;
		}
		if (hasNginxParam(operationedInstance.getContainer())) {
			triggerNginxByContainer(operationedInstance.getContainer());
		}
		Container container = operationedInstance.getContainer();
		if (container.getInstances().size() == 0) {
			container.changeStatusToCreated();
			containerDAO.save(container);
		}
		return true;
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
		Queue<Container> toVisit = new LinkedList<>();
		Set<Long> inVisit = new HashSet<>();
		for (Container container : app.getContainers()) {
			if (container.getInRelations().isEmpty()
					&& !inVisit.contains(container.getId())) {
				toVisit.add(container);
			}
		}
		while (!toVisit.isEmpty()) {
			Container current = toVisit.poll();
			if (current.getStatus().equals(Container.Status.DEPLOYED)) {
				for (ContainerInstance instance : current.getInstances()) {
					doActionOnContainerInstance(instance, actionRemove,
							ActionType.REMOVE);
				}
			}
			cInstanceDAO.delete(current.getInstances());
			cParamDAO.delete(current.getParams());
			for (Relation out : current.getOutRelations()) {
				if (!inVisit.contains(out.getTo().getId())) {
					toVisit.add(out.getTo());
				}
			}
		}
		relationDAO.delete(app.getRelations());
		containerDAO.delete(app.getContainers());
		appDAO.delete(app);
		return true;
	}

	@Override
	public Application modifyApplication(Long applicationId, DetailedItem view) {
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
				// app.addRelation(relation);
				// nodeToContainer.get(rView.from).addOutRelation(relation);
				// nodeToContainer.get(rView.to).addInRelation(relation);
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

	private Container saveModificationToContainer(
			ContainerView.DetailedItem cView, Container container) {
		container.reset(cView.name, cView.port,
				cView.maxCount,
				cView.initCount, container.getApplication(),
				componentDAO.findOne(cView.componentId), cView.xPos,
				cView.yPos, cView.nodeId);
		containerDAO.save(container);
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
		return container;
	}
}
