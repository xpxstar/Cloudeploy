var actionPanel = {
	componentTypeUrl : dURIs.componentTypeURI,
	componentUrl : dURIs.componentURI,
	taskUrl : dURIs.taskURI,
	cachedComponentTypes : new Map(),//组件类型
	cachedComponents : new Map(),//组件
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
		DEFAULT : 0
	},

	/**
	 * 判断node的类型，主要用于处理一些特殊类型的节点，比如：文件&上传，命令&执行
	 * 
	 * @param component
	 * @param action
	 * @returns {Number}
	 */
	typeOfNode : function(component, action) {
		var res = actionPanel.nodeType.DEFAULT;
		var componentName = actionPanel.getComponentName(component.name);
		if (componentName == "file") {
			res = actionPanel.nodeType.FILE;
			if (verifyParam(action)
					&& (action.name.indexOf("::upload") >= 0 || action.name
							.indexOf("::deploy") >= 0)) {
				res = actionPanel.nodeType.FILE_UPLOAD;
			}
		} else if (componentName == "exec") {
			res = actionPanel.nodeType.EXEC;
			if (verifyParam(action) && action.name.indexOf("::run") >= 0) {
				res = actionPanel.nodeType.EXEC_RUN;
			}
		} else if (componentName == "mysql") {
			res = actionPanel.nodeType.MYSQL;
			if (verifyParam(action) && action.name.indexOf("::sql") >= 0) {
				res = actionPanel.nodeType.MYSQL_SQL;
			}
		} else if (componentName == "tomcat") {
			res = actionPanel.nodeType.TOMCAT; 
			if (verifyParam(action) && action.name.indexOf("::deployWar") >= 0) {
				res = actionPanel.nodeType.TOMCAT_DEPLOY;
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
			var type = actionPanel.typeOfNode(component);
			switch (type) {
			case actionPanel.nodeType.FILE:
				return new FileComponentNode(nodeId, component, hosts);
			case actionPanel.nodeType.EXEC:
				return new ExecComponentNode(nodeId, component, hosts);
			case actionPanel.nodeType.MYSQL:
				return new MySQLComponentNode(nodeId, component, hosts);
			case actionPanel.nodeType.TOMCAT:
				return new TomcatComponentNode(nodeId, component, hosts);
			case actionPanel.nodeType.DEFAULT:
				return new ComponentNode(nodeId, component, hosts);
			}
		}
	},

	// initiate the page
	init : function() {
		actionPanel.changeView(actionPanel.currentTab);
	},

	// actionPanel 中对图的操作

	/**
	 * 添加一个图节点
	 * 
	 * @param componentId:
	 *            the corresponding component of this node
	 */
	addNode : function(componentId) {
		var component = actionPanel.cachedComponents.get(componentId);
		var nodeId = actionPanelHtml.paintNode(component);
		var node = actionPanel.nodeFactory.createNode(nodeId, component,
				new Array());
		actionPanel.cachedNodes.put(nodeId, node);
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
			actionPanel.cachedNodes.removeByKey(nodeId);
			node.remove();
		}
	},

	/**
	 * 双击图节点，显示详细信息
	 */
	nodeClick : function(nodeId) {
		$('#detailModal').modal('show');
		var node = actionPanel.cachedNodes.get(nodeId);
		var component = node.component;
		$('#detailModal .modal-title').html(component.displayName);

		actionPanelHtml.paintNodeDetailAction(node);
		$(".editSave").unbind("click");
		$(".editSave").bind("click", function() {
			actionPanel.nodeEditSave(nodeId);
			$('#detailModal').modal('hide');
		});
		actionPanel.postProcessDetail(node.component, node.action, node);
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
		var type = actionPanel.typeOfNode(component, action);
		switch (type) {
		case actionPanel.nodeType.FILE_UPLOAD:
			actionPanel.initCustomFileList(verifyParam(node) ? node.getFile()
					: null);
			break;
		case actionPanel.nodeType.EXEC_RUN:
			actionPanel.initExecList(verifyParam(node) ? node.getExec() : null);
			break;
		case actionPanel.nodeType.MYSQL_SQL:
			actionPanel.initCustomFileList(verifyParam(node) ? node.getSqlFile()
					: null);
			break;
		case actionPanel.nodeType.TOMCAT_DEPLOY:
			actionPanel.initCustomFileList(verifyParam(node) ? node.getWarFile()
					: null);
			break;
		default:
			actionPanelHtml.clearCustomControls();
			break;
		}
	},

	/**
	 * 填充选择文件列表，如果还没有缓存文件列表就先请求文件列表
	 * 
	 * @param selectedFile
	 */
	initCustomFileList : function(selectedFile) {
		if (actionPanel.cachedCustomFiles.isEmpty()) {
			ajaxGetJsonAuthc(dURIs.customFilesURI, null,
					actionPanel.requestCustomFileListCallback, null);
		} else {
			actionPanelHtml.paintCustomFileList(actionPanel.cachedCustomFiles
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
			actionPanel.cachedCustomFiles.put(files[i].id, files[i]);
		}
		actionPanelHtml.paintCustomFileList(files);
	},

	/**
	 * 选择文件的事件
	 */
	fileSelectChanged : function() {
		var fileControl = $('#detailModal #customFile').find("option:selected");
		var fileId = fileControl.val();
		var file = actionPanel.cachedCustomFiles.get(parseInt(fileId));
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
		if (actionPanel.cachedExecs.isEmpty()) {
			ajaxGetJsonAuthc(dURIs.execsURI, null,
					actionPanel.requestExecListCallback, null);
		} else {
			actionPanelHtml.paintExecList(actionPanel.cachedExecs.values(),
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
			actionPanel.cachedExecs.put(execs[i].id, execs[i]);
		}
		actionPanelHtml.paintExecList(execs);
	},

	initComponents : function() {
		this.requestComponentTypes();
		this.requestCustomTypes();
	},

	initEvents : function() {
		// 保存按钮
		$("#btn-save").unbind("click");
		$("#btn-save").click(function() {
			// 模态框的保存按钮(真正提交任务的按钮)
			var saveBtn = $("#save-task-btn");
			saveBtn.unbind("click");
			if (actionPanel.currentTab == 1) {// 保存新任务
				saveBtn.click(function() {
					actionPanel.taskSubmit();
				});
				$('#saveModal').modal('show');
			} else if (actionPanel.currentTab == 2) {
				saveBtn.click(function() {// 保存正在编辑的任务
					actionPanel.taskSubmit(actionPanel.currentTaskId);
				});
				$("#save-task-btn").click();
			}
		});

		// 另存为按钮
		$("#btn-save-as").unbind("click");
		$("#btn-save-as").click(function() {
			$("#save-task-btn").unbind("click");
			$("#save-task-btn").click(function() {
				actionPanel.saveAs();
			});
			$('#saveModal').modal('show');
		});

		$('#detailModal #actionName').unbind('change');
		$('#detailModal #actionName').change(
				function() {
					var selectedOption = $(this).find("option:selected");
					actionPanel.actionChangedFunc(selectedOption
							.attr('data-component-id'), selectedOption.val());
				});
	},

	changeView : function(tab, param) {
		actionPanel.currentTab = tab;
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
		actionPanel.clearGraph();
		switch (tab) {
		case 1:
		case 2:
			actionPanel.initEvents();
			actionPanel.cachedCustomFiles.clear();
			actionPanel.cachedExecs.clear();
			taskList.hide();
			if (tab == 1) {
				btnEditTask.hide();
				btnActive = btnCreateTask;
				btnSaveAs.hide();
			} else {
				btnCreateTask.hide();
				btnActive = btnEditTask;
				if (verifyParam(param)) {
					actionPanel.currentTaskId = param.taskId;
				}
			}
			actionPanel.initComponents();
			
			break;
		case 3:
			actionPanel.requestTaskList(1);
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
		var cachedNodes = actionPanel.cachedNodes.values();
		actionPanel.cachedNodes.clear();
		var idMap = new Map();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			idMap.put(node.id, dateToId());
			node.id = idMap.get(node.id);
			actionPanel.cachedNodes.put(node.id, node);
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
				actionPanel.nodeClick($(this).attr("id"));
			});

			actionPanelHtml.paintEndPoint(newId);
		}
		for ( var i in edges) {
			var edge = edges[i];
			actionPanelHtml.paintEdge(edge.from, edge.to, edge.label);
		}
		actionPanel.taskSubmit();
		$('#saveModal').modal('hide');
	},

	/**
	 * 提交任务
	 * 
	 * @param taskId:(optional)
	 *            要保存的taskId,如果空缺就创建新任务
	 */
	taskSubmit : function(taskId) {
		if (!actionPanel.validateTask()) {
			return;
		}

		// 重置画板的滚动条，保证保存后能正常显示
		actionPanel.resetGraphPanelScroll();
		//取得所有节点
		var cachedNodes = actionPanel.cachedNodes.values();
		var nodes = new Array();
		for ( var i in cachedNodes) {
			var n = cachedNodes[i];
			var node = {
				id : n.id,
				xPos : $("#" + n.id).offset().left,
				yPos : $("#" + n.id).offset().top,
				componentId : n.component.id,
				actionId : n.action.id,
				operation : 0,
				hostIds : [],
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
			operation: 1,
			complex: 0,
			nodes : nodes,
			edges : edges
		};
		if (verifyParam(taskId)) {
			ajaxPutJsonAuthcWithJsonContent(actionPanel.taskUrl + "/" + taskId,
					graph, defaultSuccessFunc, defaultErrorFunc, true);
		} else {
			ajaxPostJsonAuthcWithJsonContent(actionPanel.taskUrl, graph,
					actionPanel.createTaskCallBack, defaultErrorFunc, true);
		}
		$('#saveModal').modal('hide');
	},

	validateTask : function() {
		var taskName = $("#task-name").val().replace(/ /, "");
		if (taskName.length <= 0) {
			showError("任务名不能为空");
			return false;
		}
		return true
	},
	/**
	 * 创建新任务的回调函数
	 * 
	 * @param data
	 */
	createTaskCallBack : function(data) {
		var task = data;
		actionPanel.currentTaskId = task.id;
		$("#btn-save").unbind("click");
		$("#btn-save").click(function() {
			var saveBtn = $("#save-task-btn");
			saveBtn.unbind("click");
			saveBtn.click(function() {
				actionPanel.taskSubmit(actionPanel.currentTaskId);
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
		ajaxPostJsonAuthc(actionPanel.taskUrl + "/" + taskId, null,
				defaultSuccessFunc, defaultErrorFunc, true);
	},

	/**
	 * 向服务器发送删除任务的请求
	 * 
	 * @param taskId
	 */
	deleteTask : function(taskId) {
		if (confirm("删除任务?")) {
			ajaxDeleteJsonAuthc(actionPanel.taskUrl + "/" + taskId, null,
					actionPanel.requestTaskList, defaultErrorFunc, true);
		}
	},

	clearGraph : function() {
		if (verifyParam(taskInstance)) {
			taskInstance.deleteEveryEndpoint();
		}
		for ( var i in actionPanel.cachedNodes.keys()) {
			var node = $("#" + actionPanel.cachedNodes.keys()[i]);
			node.remove();
		}
		actionPanel.cachedNodes.clear();
		actionPanel.currentTaskId = -1;
	},

	/**
	 * 单机detail 模态框中的保存按钮触发的事件
	 * 
	 * @param nodeId
	 */
	nodeEditSave : function(nodeId) {
		var node = actionPanel.cachedNodes.get(nodeId);
		var component = node.component;
		var actionId = $("#actionName option:selected").val();
		node.setAction(actionId);
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
		var type = actionPanel.typeOfNode(node.getComponent(), node.getAction());
		switch (type) {
		case actionPanel.nodeType.FILE_UPLOAD:
			node.setFile(actionPanel.cachedCustomFiles.get(parseInt($(
					"select[name='customFile']").val())));
			break;
		case actionPanel.nodeType.EXEC_RUN:
			node.setExec($("textarea[name='execContent']").val());
			break;
		case actionPanel.nodeType.MYSQL_SQL:
			node.setSqlFile(actionPanel.cachedCustomFiles.get(parseInt($(
					"select[name='customFile']").val())));
			break;
		case actionPanel.nodeType.TOMCAT_DEPLOY:
			node.setWarFile(actionPanel.cachedCustomFiles.get(parseInt($(
					"select[name='customFile']").val())));
			break;
		}

		actionPanel.cachedNodes.removeByKey(nodeId);
		actionPanel.cachedNodes.put(nodeId, node);

		$("#" + nodeId).find(".node-oper").html(node.getAction().displayName);
	},


	/**
	 * 修改action的操作
	 * 
	 * @param componentId
	 * @param actionId
	 */
	actionChangedFunc : function(componentId, actionId) {
		var component = actionPanel.cachedComponents.get(componentId);
		var params = new Array();
		for ( var i in component.actions) {
			var action = component.actions[i];
			if (action.id == actionId) {
				params = action.params;
				actionPanel.postProcessDetail(component, action);
				break;
			}
		}
		actionPanelHtml.paintActionParams(params, null);
	},
	requestComponentList : function() {
		ajaxGetJsonAuthc(dURIs.componentURI, null,
				actionPanel.requestComponentsCallback, null);
	},
	requestCustomList : function() {
		ajaxGetJsonAuthc(dURIs.componentURI+"/custom", null,
				actionPanel.requestCustomCallback, null);
	},
	requestComponentsCallback : function(data) {
		var components = data;
		actionPanel.cachedComponents.clear();
		for ( var i in components) {
			actionPanel.cachedComponents.put(components[i].id, components[i]);
		}
		
		actionPanelHtml.paintComponentList(components,'add');//显示"+"表示可操作
	},
	requestCustomCallback : function(data) {
		var components = data;
		for ( var i in components) {
			actionPanel.cachedComponents.put(components[i].id, components[i]);
		}
		
		actionPanelHtmlAct.paintComponentList(components,'add');//显示"+"表示可操作
		if (actionPanel.currentTab == 2) {
			ajaxGetJsonAuthc(actionPanel.taskUrl + "/" + actionPanel.currentTaskId,
					null, actionPanel.requestTaskCallback, null);
		}
	},

	requestComponentTypes : function() {
		ajaxGetJsonAuthc(actionPanel.componentTypeUrl, null,
				actionPanel.requestComponentTypesCallback, null);
	},

	requestCustomTypes : function() {
		ajaxGetJsonAuthc(actionPanel.componentTypeUrl+'/custom', null,
				actionPanel.requestCustomTypeCallback, null);
	},
	requestComponentTypesCallback : function(data) {
		actionPanelHtml.paintComponentMeta(data,false);
		actionPanel.requestComponentList();
	},
	requestCustomTypeCallback : function(data) {
		actionPanelHtmlAct.paintComponentMeta(data,false);
		actionPanel.requestCustomList();
	},
	requestTaskList : function(operation) {
		ajaxGetJsonAuthc(actionPanel.taskUrl,{'operation':operation},
				actionPanel.requestTaskListCallback, null);
	},

	requestTaskListCallback : function(data) {
		actionPanelHtml.paintTaskList(data);
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
			var componentNode = actionPanel.graphNodeOfTaskNode(task.nodes[i]);
			actionPanelHtml.paintNode(componentNode.component, componentNode.id,
					componentNode.action.displayName);
			actionPanel.cachedNodes.put(componentNode.id, componentNode);
		}
		// paint task edges
		for ( var i in task.edges) {
			var edge = task.edges[i];
			actionPanelHtml.paintEdge(edge.from, edge.to, edge.relation);
		}
		// rearrange the nodes
		actionPanel.rearrangeNodesWithOriginalPos(task.nodes);
		taskInstance.repaintEverything();
	},

	/**
	 * 从请求回来的task的节点，转化成也面需要的图节点
	 * 
	 * @param taskNode
	 * @returns
	 */
	graphNodeOfTaskNode : function(taskNode) {
		var hosts = new Array();
		var component = actionPanel.cachedComponents.get(taskNode.componentId);
		var componentNode = actionPanel.nodeFactory.createNode(taskNode.id,
				component, hosts);
		componentNode.setAction(taskNode.actionId);
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
		actionPanel.resetScroll($("#graph-panel"));
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
	actionPanel.init();
});