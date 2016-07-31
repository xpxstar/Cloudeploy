package cn.ac.iscas.cloudeploy.v2.model.service.script.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentTypeDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ImportActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.file.FileSourceDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ComponentType;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ImportAction;
import cn.ac.iscas.cloudeploy.v2.model.entity.file.FileSource;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ModuleClass;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.model.service.script.impl.EdgeTypedGraphScriptService;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;
@Service
@Transactional
public class OperationService {
	private Logger logger=LoggerFactory.getLogger(OperationService.class);
	@Autowired
	private ActionDAO actionDao;
	
	@Autowired
	private ActionParamDAO actionParamDao;
	
	@Autowired
	private ComponentDAO componentDao;
	
	@Autowired
	private FileSourceDAO fileSourceDao;
	
	@Autowired
	private ComponentTypeDAO componentTypeDao;
	
	@Autowired
	private ImportActionDAO importActionDAO;
	
	@Autowired
	private EdgeTypedGraphScriptService edgeTypedGraphScriptService;
	
	@Autowired
	private ConfigService configService;
	
	private String fileRoot;
	
	@PostConstruct
	private void init() {
		fileRoot = configService
				.getConfigAsString(ConfigKeys.SERVICE_FILE_DEFAULT_ROOT);
	}/**
	 * store a operation
	 * @param operation 
	 * @param component this operation belong to which component
	 * @param operationDisplayName the name display in webUI 
	 * @return
	 */
	public Action storeOperation(Operation operation,Component component, String operationDisplayName){
		Action action=actionDao.findByName(operation.getName());
		if(action==null)action=new Action();
		action.setDefineFileKey(operation.getDefineMd5());
		action.setName(operation.getName());
		action.setComponent(component);
		action.setDisplayName(operationDisplayName);
		action.setShowInResourceView(true);
		actionDao.save(action);
		List<ImportAction> importActions = transformOperationImport(operation, action);
		if(importActions!=null)importActionDAO.save(importActions);
		List<ActionParam> actionParams = getAllActionParamOfOperation(operation,action);
		actionParamDao.save(actionParams);
		return action;
	}
	
	public Component storeComponent(String name,String displayName,String componentTypeString){
		 ComponentType componentType=componentTypeDao.findByName(componentTypeString);
		 if(componentType==null){
			 componentType=new ComponentType();
			 componentType.setName(componentTypeString);
			 componentType.setDisplayName(componentTypeString);
			 componentType=componentTypeDao.save(componentType);
		 }
		 Component component = componentDao.findByName(name);
		 if(component==null)component=new Component();
		 component.setName(name);
		 component.setDisplayName(displayName);
		 component.setType(componentType);
		 component.setRepeatable(false);
		 return componentDao.save(component); 
	}
	
	private List<ImportAction> transformOperationImport(Operation operation,Action action){
		TreeSet<ImportAction> actionImports=Sets.newTreeSet(new Comparator<ImportAction>() {
			@Override
			public int compare(ImportAction o1, ImportAction o2) {
				long o1Int= (o1.getImportedAction()!=null?o1.getImportedAction().getId():0);
				long o2Int=(o2.getImportedAction()!=null?o2.getImportedAction().getId():0);
				return (int) (o1Int-o2Int);
			}
		});
		if(action.getImports()!=null)
			actionImports.addAll(action.getImports());
		List<Operation> operationImports=operation.getImports();
		if(operationImports==null||operationImports.size()==0) return null;
		for (Operation opeartionItem : operationImports) {
			ImportAction importAction=new ImportAction();
			importAction.setAction(action);
			Action dependedAction=actionDao.findByName(opeartionItem.getName());
			if(dependedAction==null){
				logger.error("创建操作"+operation.getName()+"依赖的操作无法找到定义："+opeartionItem.getName());
				throw new IllegalArgumentException("创建操作"+operation.getName()+"依赖的操作无法找到定义："+opeartionItem.getName());
			}else{
				logger.info("添加操作依赖"+dependedAction.getId()+" "+dependedAction.getName());
			}
			importAction.setImportedAction(dependedAction);
			actionImports.add(importAction);
			if(dependedAction.getImports()!=null){
				actionImports.addAll(dependedAction.getImports());
			}
		}
		return new ArrayList<>(actionImports);
	}
	
	private List<ActionParam> getAllActionParamOfOperation(Operation operation, Action action){
		List<ActionParam> paramsList=Lists.newArrayList();
		for (Entry<String, ParamValue> item: operation.getParamsEntrySet()) {
			ActionParam param=actionParamDao.findByActionAndParamKey(action,item.getKey());
			if(param==null) param=new ActionParam();
			param.setAction(action);
			if(item.getValue().getValue() != null){
				param.setDefaultValue(item.getValue().store());
			}
			param.setParamType(item.getValue().getType().toString());
			param.setParamKey(item.getKey());
			param.setViewType("view:text");
		    paramsList.add(param);
		}
		return paramsList;
	}
	
	public Operation findOperationByName(String operationName){
		Action action=actionDao.findByName(operationName);
		return transformAction(action);
	}
	private Operation transformAction(Action action){
		Operation operation=new Operation();
		operation.setDefineMd5(action.getDefineFileKey());
		operation.setName(action.getName());
		List<ActionParam> actionParams = action.getParams();
		for (ActionParam actionParam : actionParams) {
			operation.setValueOfParams(actionParam.getParamKey(), actionParam.getDefaultValue());
			operation.setParamTypeOfParam(actionParam.getParamKey(), ParamType.typeByString(actionParam.getParamType()));
		}
		List<ImportAction> importActions = action.getImports();
		if(importActions!=null){
			for (ImportAction importAction : importActions) {
				operation.addImports(transformAction(importAction.getImportedAction()));
			}
		}
		return operation;
	}

	/**
	 * @description extract modules file to component and actions
	 * @param filekey
	 * @param componentId
	 * @return Long :componentId or 0 if given component exists.
	 * @author xpxstar@gmail.com
	 * 2015年11月12日 上午9:21:33
	 */
	public Long extractComponent(String filekey, Long componentId) {
		FileSource fs = fileSourceDao.findByMd5(filekey);
		Component com = componentDao.findOne(componentId);
		if (actionDao.getByComponent(com).size() != 0 ){
			return 0l;
			}
		List<Node> nodes = edgeTypedGraphScriptService.analysisModule(fileRoot+fs.getCredential(), com.getName());
		for (Node node : nodes) {
			Action ac = new Action  ();
			ac.setName("cloudeploy::"+node.getName());
			if (node instanceof ModuleClass) {
				ac.setDefineFileKey(edgeTypedGraphScriptService.createClassDefinefile(node, ac.getName(), node.getParamsWithTypes()));
			}
			ac.setDisplayName(node.getName());
			ac.setComponent(com);
			ac.setShowInResourceView(true);
			actionDao.save(ac);
			for (Entry<String, ParamValue> pe : node.getParamsEntrySet()) {
				ActionParam ap = new ActionParam();
				ap.setAction(ac);
				ap.setParamKey(pe.getKey());
				if(pe.getValue().getValue() != null){
					ap.setDefaultValue(pe.getValue().store());
				}else {
					ap.setDefaultValue("");
				}
				ap.setParamType(pe.getValue().getType().toString());
				ap.setViewType("view:text");
				ap.setDescription("");
			    actionParamDao.save(ap);
			}
		}
		
		return componentId;
	}
}
