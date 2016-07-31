var taskPanel = {
	componentTypeUrl : dURIs.componentTypeURI,
	hostUrl : dURIs.hostURI,
	taskUrl : dURIs.taskURI,
	cachedComponentTypes : new Map(),//组件类型
	cachedComponents : new Map(),//系统组件
	cachedOperations : new Map(),//活动
	cachedHosts : new Map(),//主机
	cachedCustomFiles : new Map(),
	cachedExecs : new Map(),
	cachedNodes : new Map(),
	currentTab : 1,
	currentTaskId : -1,

	nodeType : {
		FILE : 10,
		FILE_UPLOAD : 11,
		EXEC : 20,
		EXEC_RUN : 21,
		MYSQL : 30,
		MYSQL_SQL : 31,
		TOMCAT : 40,
		TOMCAT_DEPLOY : 41,
		DEFAULT : 0,
		OPERATION : 50,
	},

	/**
	 * 判断node的类型，主要用于处理一些特殊类型的节点，比如：文件&上传，命令&执行
	 * 
	 * @param component
	 * @param action
	 * @returns {Number}
	 */
	typeOfNode : function(component, action) {
		var res = taskPanel.nodeType.DEFAULT;
		if ('operation' in component) {//如果是operation
			return taskPanel.nodeType.OPERATION;
		}
		var componentName = taskPanel.getComponentName(component.name);
		if (componentName == "file") {
			res = taskPanel.nodeType.FILE;
			if (verifyParam(action)
					&& (action.name.indexOf("::upload") >= 0 || action.name
							.indexOf("::deploy") >= 0)) {
				res = taskPanel.nodeType.FILE_UPLOAD;    
			}
		} else if (componentName == "exec") {
			res = taskPanel.nodeType.EXEC;
			if (verifyParam(action) && action.name.indexOf("::run") >= 0) {
				res = taskPanel.nodeType.EXEC_RUN;
			}
		} else if (componentName == "mysql") {
			res = taskPanel.nodeType.MYSQL;
			if (verifyParam(action) && action.name.indexOf("::sql") >= 0) {
				res = taskPanel.nodeType.MYSQL_SQL;
			}
		} else if (componentName == "tomcat") {
			res = taskPanel.nodeType.TOMCAT; 
			if (verifyParam(action) && action.name.indexOf("::deployWar") >= 0) {
				res = taskPanel.nodeType.TOMCAT_DEPLOY;
			}
		}
		return res;
	},

	getComponentName : function(originalName) {
		var seperator = "::";
		return originalName.substring(originalName.lastIndexOf(seperator)
				+ seperator.length);
	},

	nodeFactory : {
		createNode : function(nodeId, component, hosts) {
			var type = taskPanel.typeOfNode(component);
			switch (type) {
			case taskPanel.nodeType.FILE:
				return new FileComponentNode(nodeId, component, hosts);
			case taskPanel.nodeType.EXEC:
				return new ExecComponentNode(nodeId, component, hosts);
			case taskPanel.nodeType.MYSQL:
				return new MySQLComponentNode(nodeId, component, hosts);
			case taskPanel.nodeType.TOMCAT:
				return new TomcatComponentNode(nodeId, component, hosts);
			case taskPanel.nodeType.OPERATION:
				return new OperationNode(nodeId, component,hosts);
			case taskPanel.nodeType.DEFAULT:
				return new ComponentNode(nodeId, component, hosts);
			}
		}
	},

	// initiate the page
	 init : function() {
	 	 taskPanel.changeView(taskPanel.currentTab);
	 },

	// taskPanel 中对图的操作

	/**
	 * 添加一个图节点
	 * 
	 * @param componentId:
	 *            the corresponding component of this node
	 */
	addNode : function(componentId,operation) {
		if (verifyParam(operation) && 1== operation) {
			var operation = taskPanel.cachedOperations.get(componentId);
			var nodeId = taskPanelHtml.paintOperation(operation);
			var node = taskPanel.nodeFactory.createNode(nodeId, operation,
				new Array());
			taskPanel.cachedNodes.put(nodeId, node);
		} else{
			var component = taskPanel.cachedComponents.get(componentId);
			var nodeId = taskPanelHtml.paintNode(component);
			var node = taskPanel.nodeFactory.createNode(nodeId, component,
				new Array());
			taskPanel.cachedNodes.put(nodeId, node);
		}
	},

	/**
	 * 删除一个图节点
	 */
	deleteNode : function(nodeId) {
		if (confirm("删除节点?")) {
			var node = $("#" + nodeId);
			var endPoints = taskInstance.getEndpoints(node);
			for ( var i in endPoints) {
				taskInstance.deleteEndpoint(endPoints[i].getUuid());
			}
			taskPanel.cachedNodes.removeByKey(nodeId);
			node.remove();
		}
	},

	/**
	 * 双击图节点，显示详细信息
	 */
	nodeClick : function(nodeId) {
		$('#detailModal').modal('show');
		var node = taskPanel.cachedNodes.get(nodeId);
		var component='';
		if(node instanceof OperationNode ){
			component = node.operation;
			$('#detailModal #actionOp').hide();
			$('#detailModal .modal-title').html(component.name);
		}else{
			component = node.component;
			$('#detailModal #actionOp').show();
			$('#detailModal .modal-title').html(component.displayName);		
		}
		taskPanelHtml.paintNodeDetailHost(taskPanel.cachedHosts.values(),
				node.hosts);
		taskPanelHtml.paintNodeDetailAction(node);
		$(".editSave").unbind("click");
		$(".editSave").bind("click", function() {
			taskPanel.nodeEditSave(nodeId);
			$('#detailModal').modal('hide');
		});
		if( !(node instanceof OperationNode) ){
			taskPanel.postProcessDetail(node.component, node.action, node);
		}
	},

	/**
	 * 特定操作的特殊处理，比如：上传文件、执行命令等
	 * 
	 * @param component
	 * @param action
	 * @param node:
	 *            (optional)用来获取特定信息，如：node.getFile()
	 */
	postProcessDetail : function(component, action, node) {
		var type = taskPanel.typeOfNode(component, action);
		switch (type) {
		case taskPanel.nodeType.FILE_UPLOAD:
			taskPanel.initCustomFileList(verifyParam(node) ? node.getFile()
					: null);
			break;
		case taskPanel.nodeType.EXEC_RUN:
			taskPanel.initExecList(verifyParam(node) ? node.getExec() : null);
			break;
		case taskPanel.nodeType.MYSQL_SQL:
			taskPanel.initCustomFileList(verifyParam(node) ? node.getSqlFile()
					: null);
			break;
		case taskPanel.nodeType.TOMCAT_DEPLOY:
			taskPanel.initCustomFileList(verifyParam(node) ? node.getWarFile()
					: null);
			break;
		default:
			taskPanelHtml.clearCustomControls();
			break;
		}
	},

	/**
	 * 填充选择文件列表，如果还没有缓存文件列表就先请求文件列表
	 * 
	 * @param selectedFile
	 */
	initCustomFileList : function(selectedFile) {
		if (taskPanel.cachedCustomFiles.isEmpty()) {
			ajaxGetJsonAuthc(dURIs.customFilesURI, null,
					taskPanel.requestCustomFileListCallback, null);
		} else {
			taskPanelHtml.paintCustomFileList(taskPanel.cachedCustomFiles
					.values(), selectedFile);
		}
	},

	/**
	 * 请求文件列表的回掉函数
	 * 
	 * @param data
	 */
	requestCustomFileListCallback : function(data) {
		var files = data;
		for ( var i in files) {
			taskPanel.cachedCustomFiles.put(files[i].id, files[i]);
		}
		taskPanelHtml.paintCustomFileList(files);
	},

	/**
	 * 选择文件的事件
	 */
	fileSelectChanged : function() {
		var fileControl = $('#detailModal #customFile').find("option:selected");
		var fileId = fileControl.val();
		var file = taskPanel.cachedCustomFiles.get(parseInt(fileId));
		ajaxPostJsonAuthc(dURIs.filesURI + "/" + file.fileKey, null, function(
				data) {
			var res = data;
			$("#actionParams tbody tr").each(
					function(index, element) {
						var key = $.trim($(this).find("td.paramKey").html());
						if (key == "source" || key == "sqlScript_url"
								|| key == "war_source") {
							var valueInput = $(this).find(
									"td.paramValue  textarea");
							valueInput.attr("disabled", "disabled");
							valueInput.val('"' + res.url + "?name="
									+ fileControl.text() + '"');
						}
					});
		}, defaultErrorFunc, false);
	},

	/**
	 * 填充选择脚本列表，如果还没有缓存脚本列表就先请求脚本列表
	 * 
	 * @param execContent
	 */
	initExecList : function(execContent) {
		if (taskPanel.cachedExecs.isEmpty()) {
			ajaxGetJsonAuthc(dURIs.execsURI, null,
					taskPanel.requestExecListCallback, null);
		} else {
			taskPanelHtml.paintExecList(taskPanel.cachedExecs.values(),
					execContent);
		}
	},

	/**
	 * 选择执行脚本的事件
	 */
	execChanged : function() {
		$("#actionParams tbody tr").each(function(index, element) {
			var key = $.trim($(this).find("td.paramKey").html());
			if (key == "command") {
				var valueInput = $(this).find("td.paramValue textarea");
				valueInput.attr("disabled", "disabled");
				valueInput.val('"' + $("#execContent").val() + '"');
			}
		});
	},

	/**
	 * 请求脚本列表的回掉函数
	 * 
	 * @param data
	 */
	requestExecListCallback : function(data) {
		var execs = data;
		for ( var i in execs) {
			taskPanel.cachedExecs.put(execs[i].id, execs[i]);
		}
		taskPanelHtml.paintExecList(execs);
	},

	/**
	 * 初始化绘制组件列表以及主机列表
	 * 
	 */
	initComponents : function() {
		this.requestHosts();
		this.requestComponentTypes();
		this.requestCustomTypes();
		this.requestOperations(1);//1表示是operation列表
	},

	/**
	 * 初始化按钮事件
	 * 
	 */
	initEvents : function() {
		// 保存按钮
		$("#btn-save").unbind("click");
		$("#btn-save").click(function() {
			// 模态框的保存按钮(真正提交任务的按钮)
			var saveBtn = $("#save-task-btn");
			saveBtn.unbind("click");
			if (taskPanel.currentTab == 1) {// 保存新任务
				saveBtn.click(function() {
					taskPanel.taskSubmit();
				});
				$('#saveModal').modal('show');
			} else if (taskPanel.currentTab == 2) {
				saveBtn.click(function() {// 保存正在编辑的任务
					taskPanel.taskSubmit(taskPanel.currentTaskId);
				});
				$("#save-task-btn").click();
			}
		});

		// 另存为按钮
		$("#btn-save-as").unbind("click");
		$("#btn-save-as").click(function() {
			$("#save-task-btn").unbind("click");
			$("#save-task-btn").click(function() {
				taskPanel.saveAs();
			});
			$('#saveModal').modal('show');
		});

		$('#detailModal #actionName').unbind('change');
		$('#detailModal #actionName').change(
				function() {
					var selectedOption = $(this).find("option:selected");
					taskPanel.actionChangedFunc(selectedOption
							.attr('data-component-id'), selectedOption.val());
				});
	},
	/**
	 * 选项卡切换
	 * 
	 */
	
	changeView : function(tab, param) {
		taskPanel.currentTab = tab;
		var btnCreateTask = $("#btn-create-task"), btnEditTask = $("#btn-edit-task"), btnTaskList = $("#btn-task-list");
		btnCreateTask.show();
		btnEditTask.show();
		btnTaskList.show();
		$(".d-menu .menu-btn").removeClass("active");
		var btnActive = btnCreateTask;
		var taskList = $("#task-list");
		taskList.show();
		var graphPanel = $("#graph-panel");
		graphPanel.show();
		var operationList = $("#group-list");
		operationList.show();
		var btnSaveAs = $("#btn-save-as");
		btnSaveAs.show();
		taskPanel.clearGraph();
		switch (tab) {
		case 1:
		case 2:
			taskPanel.initEvents();
			taskPanel.cachedCustomFiles.clear();
			taskPanel.cachedExecs.clear();
			taskPanel.cachedHosts.clear();
			taskPanel.cachedOperations.clear();
			taskList.hide();
			if (tab == 1) {
				btnEditTask.hide();
				btnActive = btnCreateTask;
				btnSaveAs.hide();
			} else {
				btnCreateTask.hide();
				btnActive = btnEditTask;
				if (verifyParam(param)) {
					taskPanel.currentTaskId = param.taskId;
				}
			}
			taskPanel.initComponents();
			
			break;
		case 3:
			taskPanel.requestTaskList(0);
			graphPanel.hide();
			operationList.hide();
			btnEditTask.hide();
			btnActive = btnTaskList;
			break;
		default:
		}
		btnActive.find(".menu-btn").addClass("active");
	},

	/**
	 * 另存为新的任务
	 */
	saveAs : function() {
		var cachedNodes = taskPanel.cachedNodes.values();
		taskPanel.cachedNodes.clear();
		var idMap = new Map();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			idMap.put(node.id, dateToId());
			node.id = idMap.get(node.id);
			taskPanel.cachedNodes.put(node.id, node);
		}
		var edges = new Array();
		var connections = taskInstance.getAllConnections();
		// change ids of source&target of each connection
		for ( var i in connections) {
			var conn = connections[i];
			edges.push({
				from : idMap.get(parseInt(conn.sourceId)),
				to : idMap.get(parseInt(conn.targetId)),
				label : conn.getOverlay("label").getLabel()
			});
		}
		taskInstance.deleteEveryEndpoint();
		for ( var i in idMap.keys()) {
			var oldId = idMap.keys()[i];
			var newId = idMap.get(oldId);
			var element = $("#" + oldId);
			element.attr("id", newId);
			element.unbind("dblclick");
			element.dblclick(function() {
				taskPanel.nodeClick($(this).attr("id"));
			});

			taskPanelHtml.paintEndPoint(newId);
		}
		for ( var i in edges) {
			var edge = edges[i];
			taskPanelHtml.paintEdge(edge.from, edge.to, edge.label);
		}
		taskPanel.taskSubmit();
		$('#saveModal').modal('hide');
	},

	/**
	 * 提交任务
	 * 
	 * @param taskId:(optional)
	 *            要保存的taskId,如果空缺就创建新任务
	 */
	taskSubmit : function(taskId) {
		// 检查是否所有的节点都选择了host
		if (!taskPanel.validateTask()) {
			return;
		}

		// 重置画板的滚动条，保证保存后能正常显示
		taskPanel.resetGraphPanelScroll();
		//取得所有节点
		var cachedNodes = taskPanel.cachedNodes.values();
		var complex = 0;//0表示简单任务，1表示复杂任务
		var nodes = new Array();
		for ( var i in cachedNodes) {
			var n = cachedNodes[i];
			var hostIds = new Array();
			for ( var k in n.hosts) {
				hostIds.push(n.hosts[k].id);
			}
			var oper = 0;
			var ai = 0;
			var ci = 0;
			if (n instanceof OperationNode) {
				oper = n.operation.id;
				ai = 0; 
				ci = -1;
				complex = 1;
			} else{
				oper = 0;
				ai = n.action.id
				ci = n.compone
			};
			var node = {
				id : n.id,
				xPos : $("#" + n.id).offset().left,
				yPos : $("#" + n.id).offset().top,
				componentId : ci,
				actionId : ai,
				operation : oper,
				hostIds : hostIds,
				params : n.params
			};
			nodes.push(node);
		}
		//取得所有边
		var connections = taskInstance.getAllConnections();
		var edges = new Array();
		for ( var i in connections) {
			var conn = connections[i];
			var edge = {
				from : parseInt(conn.sourceId),
				to : parseInt(conn.targetId),
				relation : conn.getOverlay("label").getLabel().toUpperCase()
			};
			edges.push(edge);
		}
		//将任务流程专为图保存
		var graph = {
			taskName : $("#task-name").val(),
			operation : 0 ,
			complex : complex ,
			nodes : nodes,
			edges : edges
		};
		if (verifyParam(taskId)) {
			ajaxPutJsonAuthcWithJsonContent(taskPanel.taskUrl + "/" + taskId,
					graph, defaultSuccessFunc, defaultErrorFunc, true);
		} else {
			ajaxPostJsonAuthcWithJsonContent(taskPanel.taskUrl, graph,
					taskPanel.createTaskCallBack, defaultErrorFunc, true);
		}
		$('#saveModal').modal('hide');
	},

	validateTask : function() {
		var taskName = $("#task-name").val().replace(/ /, "");
		if (taskName.length <= 0) {
			showError("任务名不能为空");
			return false;
		}
		return taskPanel.checkHostSelect();
	},

	checkHostSelect : function() {
		var cachedNodes = taskPanel.cachedNodes.values();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			var displayName = (node instanceof OperationNode)?node.operation.name:node.component.displayName+'::'+node.action.displayName;
			if (!verifyParam(node.getHosts()) || node.getHosts().length <= 0) {
				showError("请为节点\"" + displayName + "\"选择主机");
				return false;
			}
		}
		return true;
	},

	/**
	 * 创建新任务的回调函数
	 * 
	 * @param data
	 */
	createTaskCallBack : function(data) {
		var task = data;
		taskPanel.currentTaskId = task.id;
		$("#btn-save").unbind("click");
		$("#btn-save").click(function() {
			var saveBtn = $("#save-task-btn");
			saveBtn.unbind("click");
			saveBtn.click(function() {
				taskPanel.taskSubmit(taskPanel.currentTaskId);
			});
			$("#save-task-btn").click();
		});
		$("#btn-save-as").show();
		defaultSuccessFunc();
	},

	/**
	 * 向服务器发送执行任务的请求
	 * 
	 * @param taskId
	 */
	executeTask : function(taskId) {
		ajaxPostJsonAuthc(taskPanel.taskUrl + "/" + taskId, null,
				defaultSuccessFunc, defaultErrorFunc, true);
	},

	/**
	 * 向服务器发送删除任务的请求
	 * 
	 * @param taskId
	 */
	deleteTask : function(taskId) {
		if (confirm("删除任务?")) {
			ajaxDeleteJsonAuthc(taskPanel.taskUrl + "/" + taskId, null,
					taskPanel.requestTaskList, defaultErrorFunc, true);
		}
	},

	clearGraph : function() {
		if (verifyParam(taskInstance)) {
			taskInstance.deleteEveryEndpoint();
		}
		for ( var i in taskPanel.cachedNodes.keys()) {
			var node = $("#" + taskPanel.cachedNodes.keys()[i]);
			node.remove();
		}
		taskPanel.cachedNodes.clear();
		taskPanel.currentTaskId = -1;
	},

	/**
	 * 单机detail 模态框中的保存按钮触发的事件
	 * 
	 * @param nodeId
	 */
	nodeEditSave : function(nodeId) {
		var node = taskPanel.cachedNodes.get(nodeId);
		var component='';
		if (node instanceof OperationNode) {
			component = node.operation;
		} else{
			component = node.component;
			var actionId = $("#actionName option:selected").val();
			node.setAction(actionId);
		};
			var newParams = new Array();
			$("#actionParams tbody tr").each(function(index, element) {
				var value = $(element).find('.paramValue textarea').val();
				var key = $(element).find('.paramKey').text();
				if (key != "") {
					newParams.push({
						key : key,
						value : value
					});
				}
			});
			node.setParams(newParams);
			node.setHosts(taskPanel.getTargetHosts());
			var type = taskPanel.typeOfNode(component, node.getAction());
			switch (type) {
			case taskPanel.nodeType.FILE_UPLOAD:
				node.setFile(taskPanel.cachedCustomFiles.get(parseInt($(
						"select[name='customFile']").val())));
				break;
			case taskPanel.nodeType.EXEC_RUN:
				node.setExec($("textarea[name='execContent']").val());
				break;
			case taskPanel.nodeType.MYSQL_SQL:
				node.setSqlFile(taskPanel.cachedCustomFiles.get(parseInt($(
						"select[name='customFile']").val())));
				break;
			case taskPanel.nodeType.TOMCAT_DEPLOY:
				node.setWarFile(taskPanel.cachedCustomFiles.get(parseInt($(
						"select[name='customFile']").val())));
				break;
			}
		taskPanel.cachedNodes.removeByKey(nodeId);
		taskPanel.cachedNodes.put(nodeId, node);
		if (!(node instanceof OperationNode)) {
			$("#" + nodeId).find(".node-oper").html(node.getAction().displayName);
		}
		
	},

	getTargetHosts : function() {
		var hosts = new Array();
		$("#hostNames input[name='hostName']").each(function() {
			if ($(this).prop('checked')) {
				var hostId = parseInt($(this).val());
				var host = taskPanel.cachedHosts.get(hostId);
				if (host != false) {
					hosts.push(host);
				}
			}
		});
		return hosts;
	},

	/**
	 * 修改action的操作
	 * 
	 * @param componentId
	 * @param actionId
	 */
	actionChangedFunc : function(componentId, actionId) {
		var component = taskPanel.cachedComponents.get(componentId);
		var params = new Array();
		for ( var i in component.actions) {
			var action = component.actions[i];
			if (action.id == actionId) {
				params = action.params;
				taskPanel.postProcessDetail(component, action);
				break;
			}
		}
		taskPanelHtml.paintActionParams(params, null);
	},

	requestHosts : function() {
		ajaxGetJsonAuthc(taskPanel.hostUrl, null,
				taskPanel.requestHostsCallback, null);
	},
	
	requestHostsCallback : function(data) {
		var hosts = data;
		taskPanel.cachedHosts.clear();
		for ( var i in hosts) {
			taskPanel.cachedHosts.put(hosts[i].id, hosts[i]);
		}
	},
	/**
	 * 请求operation 列表，operation为1
	 */
	requestOperations : function(operation) {
		ajaxGetJsonAuthc(taskPanel.taskUrl,{'operation':operation},
				taskPanel.requestActionCallback, null, true     					);
	},
	requestActionCallback : function(data) {
		taskPanel.cachedOperations.clear();
		for ( var i in data) {
			taskPanel.cachedOperations.put(data[i].id, data[i]);
		}
		taskPanelHtmlAct.paintActionList(data);
		//获取当前的task
		if (taskPanel.currentTab == 2) {
			ajaxGetJsonAuthc(taskPanel.taskUrl + "/" + taskPanel.currentTaskId,
					null, taskPanel.requestTaskCallback, null);
		}
	},
	requestComponentList : function() {
		ajaxGetJsonAuthc(dURIs.componentURI, null,
				taskPanel.requestComponentsCallback, null);
	},
	requestCustomList : function() {
		ajaxGetJsonAuthc(dURIs.componentURI+"/custom", null,
				taskPanel.requestCustomCallback, null);
	},
	requestComponentsCallback : function(data) {
		var components = data;
		taskPanel.cachedComponents.clear();
		for ( var i in components) {
			taskPanel.cachedComponents.put(components[i].id, components[i]);
		}
		
		taskPanelHtml.paintComponentList(components,'add');//显示"+"表示可操作
	},
	requestCustomCallback : function(data) {
		var components = data;
		for ( var i in components) {
			taskPanel.cachedComponents.put(components[i].id, components[i]);
		}
		
		taskPanelHtmlAct.paintComponentList(components,'add');//显示"+"表示可操作
	},
	requestComponentTypes : function() {
		ajaxGetJsonAuthc(taskPanel.componentTypeUrl, null,
				taskPanel.requestComponentTypesCallback, null);
	},

	requestCustomTypes : function() {
		ajaxGetJsonAuthc(taskPanel.componentTypeUrl+'/custom', null,
				taskPanel.requestCustomTypeCallback, null);
	},
	requestComponentTypesCallback : function(data) {
		taskPanelHtml.paintComponentMeta(data,false);
		taskPanel.requestComponentList();
	},
	requestCustomTypeCallback : function(data) {
		taskPanelHtmlAct.paintComponentMeta(data,false);
		taskPanel.requestCustomList();
	},

	requestTaskList : function(operation) {
		if (1 != operation) {
			operation = 0;
		}
		ajaxGetJsonAuthc(taskPanel.taskUrl,{'operation':operation},
				taskPanel.requestTaskListCallback, null);
	},

	requestTaskListCallback : function(data) {
		taskPanelHtml.paintTaskList(data);
	},

	/**
	 * 编辑视图中，请求某个task 的回掉函数
	 * 
	 * @param data
	 */
	requestTaskCallback : function(data) {
		var task = data;
		// set task name
		$("#task-name").val(task.taskName);
		// paint task nodes
		for ( var i in task.nodes) {
			var componentNode = taskPanel.graphNodeOfTaskNode(task.nodes[i]);
			if (componentNode instanceof OperationNode) {
				taskPanelHtml.paintOperation(componentNode.operation, componentNode.id);
			} else{
			taskPanelHtml.paintNode(componentNode.component, componentNode.id,
					componentNode.action.displayName);
			}
			taskPanel.cachedNodes.put(componentNode.id, componentNode);
		}
		// paint task edges
		for ( var i in task.edges) {
			var edge = task.edges[i];
			taskPanelHtml.paintEdge(edge.from, edge.to, edge.relation);
		}
		// rearrange the nodes
		taskPanel.rearrangeNodesWithOriginalPos(task.nodes);
		taskInstance.repaintEverything();
	},

	/**
	 * 从请求回来的task的节点，转化成页面需要的图节点
	 * 
	 * @param taskNode
	 * @returns
	 */
	graphNodeOfTaskNode : function(taskNode) {
		var hosts = new Array();
		for ( var k in taskNode.hostIds) {
			var host = taskPanel.cachedHosts.get(taskNode.hostIds[k]);
			if (host != false) {
				hosts.push(host);
			}
		}
		var component = '';
		var componentNode = ''
		if (0 != taskNode.operation) {
			component = taskPanel.cachedOperations.get(taskNode.operation);
			componentNode = taskPanel.nodeFactory.createNode(taskNode.id,
				component, hosts);
		}else{
			component = taskPanel.cachedComponents.get(taskNode.componentId);
			componentNode = taskPanel.nodeFactory.createNode(taskNode.id,
				component, hosts);
			componentNode.setAction(taskNode.actionId);
		}
		componentNode.setParams(taskNode.params);
		return componentNode;
	},

	/**
	 * 随机在页面中排列图节点
	 * 
	 * @param elementIds
	 * @param containerId
	 */
	rearrangeNodes : function(elementIds, containerId) {
		var container = $("#" + containerId)[0];
		var x1 = container.offsetLeft, y1 = container.offsetTop;
		var width = container.offsetWidth - 100, height = container.offsetHeight - 100;
		for ( var i in elementIds) {
			var element = $("#" + elementIds[i]);
			var offset = element.offset();
			offset.left = Math.floor(x1 + Math.random() * width);
			offset.top = Math.floor(y1 + Math.random() * height);
			element.offset(offset);
		}
	},

	/**
	 * 按照保存的位置在页面中排列图节点
	 * 
	 * @param taskNodes:
	 *            the nodes of the returned task
	 */
	rearrangeNodesWithOriginalPos : function(taskNodes) {
		for ( var i in taskNodes) {
			var element = $("#" + taskNodes[i].id);
			var offset = element.offset();
			offset.left = taskNodes[i].xPos;
			offset.top = taskNodes[i].yPos;
			element.offset(offset);
		}
	},

	/**
	 * 将graph panel的滚动条滑动到开始位置
	 */
	resetGraphPanelScroll : function() {
		taskPanel.resetScroll($("#graph-panel"));
	},

	/**
	 * 滑动scrollTo 到container的左上角
	 * 
	 * @param container
	 * @param scrollTo
	 */
	resetScroll : function(scrollTo) {
		scrollTo.scrollTop(0);
		scrollTo.scrollLeft(0);
	}
};

$(document).ready(function() {
	   taskPanel.init();
});
