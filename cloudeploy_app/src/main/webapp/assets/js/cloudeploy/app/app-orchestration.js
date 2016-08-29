var appPanel = {
	cachedHosts : new Map(),
	cachedComponents : new Map(),
	cachedNodes : new Map(),
	currentApp : null,
	cachedContainers : new Map(),
	cachedContainersById : new Map(),
	cachedCustomFiles : new Map(),
	cachedTemplate:new Map(),
	initReady : 0,// 001 hosts ready,010 components ready
	currentTab : -1,

	init : function(param) {
		this.requestHosts();
		this.requestComponentTypes();
		this.initEvents();
		this.initReady = 0;
	},

	initForEdit : function(param) {
		if (appPanel.initReady == 3) {
			appPanel.clearGraph();
			appPanel.requestApp(param.appId);
			appPanel.showSaveAsBtn();
			appPanel.currentTab = param.tab;
			if (appPanel.currentTab == 2 || appPanel.currentTab == 3) {
				$("#group-list").hide();
			}
			$(".operation-list li[data-view-id='" + appPanel.currentTab + "']")
					.css('display', 'inline-block');
		} else {
			setTimeout(function() {
				appPanel.initForEdit(param);
			}, 2000);
		}
	},
	
	initCustomFileList : function(selectedFile) {
		if (appPanel.cachedCustomFiles.isEmpty()) {
			ajaxGetJsonAuthc(dURIs.customFilesURI, null,
					appPanel.requestCustomFileListCallback, null);
		} else {
			orcheHtml.paintCustomFileList(appPanel.cachedCustomFiles
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
			appPanel.cachedCustomFiles.put(files[i].id, files[i]);
		}
		orcheHtml.paintCustomFileList(files);
	},

	showSaveAsBtn : function() {
		$("#btn-save-as").show();
		$("#btn-save-as").unbind("click");
		$("#btn-save-as").click(function() {
			$("#save-app-btn").unbind("click");
			$("#save-app-btn").click(function() {
				appPanel.saveAs();
			});
			$('#saveModal').modal('show');
		});
	},

	initEvents : function() {
		// 保存按钮
		$("#btn-save").unbind("click");
		$("#btn-save").click(function() {
			// 模态框的保存按钮(真正提交任务的按钮)
			var saveBtn = $("#save-app-btn");
			saveBtn.unbind("click");
			saveBtn.click(function() {
				appPanel.appSubmit();
			});
			if (appPanel.currentApp == null) {
				$('#saveModal').modal('show');
			} else {
				saveBtn.click();
			}
		});
	},
	nodeFactory : {
		createNode : function(nodeId, component, hosts) {
			var node = new ContainerNode(nodeId, component, hosts,
					component.displayName, 80, 1, 1, "CREATED");
			for ( var i in component.actions) {
				var action = component.actions[i];
				if (action.name.lastIndexOf("image") >= 0) {
					node.setAction(action.id);
					break;
				}
			}
			return node;
		},

		createNodeFromContainer : function(container) {
			var hosts = new Array();
			var host = appPanel.cachedHosts.get(container.masterId);
			hosts.push(host);
			var component = appPanel.cachedComponents
					.get(container.componentId);
			var containerNode =  new ContainerNode(container.nodeId, component, hosts,
					container.name, container.port, container.initCount,
					container.maxCount, container.status, container.id);
			containerNode.setTemplates(container.templates);
			containerNode.setAttributes(container.attributes);
			return containerNode;
		},

		createNodeFromContainerInstance : function(containerInstance,
				containerId, nodeId) {
			return new ContainerInstanceNode(nodeId, containerInstance.name,
					containerInstance.port, containerInstance.status,
					containerInstance.id, containerId);
		}
	},

	/**
	 * 请求主机列表
	 */
	requestHosts : function() {
		ajaxGetJsonAuthc(dURIs.hostURI, null, appPanel.requestHostsCallback,
				null);
	},

	requestHostsCallback : function(data) {
		var hosts = data;
		appPanel.cachedHosts.clear();
		for ( var i in hosts) {
			appPanel.cachedHosts.put(hosts[i].id, hosts[i]);
		}
		appPanel.initReady |= 1;
	},

	/**
	 * 请求组件类型
	 */
	requestComponentTypes : function() {
		ajaxGetJsonAuthc(dURIs.componentTypeURI, null,
				appPanel.requestComponentTypesCallback, null);
	},

	requestComponentTypesCallback : function(data) {
		orcheHtml.paintComponentMeta(data);
		appPanel.requestComponentList();
	},

	/**
	 * 请求组件列表
	 */
	requestComponentList : function() {
		ajaxGetJsonAuthc(dURIs.componentURI + "?type=PACKAGE::DOCKER", null,
				appPanel.requestComponentsCallback, null);
	},

	requestComponentsCallback : function(data) {
		var components = data;
		appPanel.cachedComponents.clear();
		for ( var i in components) {
			appPanel.cachedComponents.put(components[i].id, components[i]);
		}
		orcheHtml.paintComponentList(components);
		appPanel.initReady |= 2;
	},

	/**
	 * 请求application
	 * 
	 * @param appId
	 */
	requestApp : function(appId) {
		ajaxGetJsonAuthc(dURIs.appURI + "/" + appId, null,
				appPanel.requestAppCallback, null);
	},

	requestAppCallback : function(data) {
		var app = data;
		appPanel.currentApp = app;
		appPanel.cachedContainers.clear();
		appPanel.cachedContainersById.clear();
		// set app name
		$("#appName").val(app.name);
		// paint nodes
		for ( var i in app.containers) {
			var container = app.containers[i];
			if (container.status == "CREATED" || container.status == "DEPLOYED") {
				alert(container.status);
				appPanel.cachedContainers.put(container.nodeId, container);
				appPanel.cachedContainersById.put(container.id, container);
			}
			if (container.status == "CREATED") {
				var node = appPanel.nodeFactory
						.createNodeFromContainer(container);
				node.setParams(container.params);
				orcheHtml.paintNode(node.component, node.id, node.name,
						node.port, node.status);
				appPanel.cachedNodes.put(node.id, node);
				appPanel.rearrangeElementWithPos(node.id, container.xPos,
						container.yPos);
			} else if (container.status == "DEPLOYED") {
				alert(container.instances.length);
				for (var j = 0; j < container.instances.length; j++) {
					var instance = container.instances[j];
					var node = appPanel.nodeFactory
							.createNodeFromContainerInstance(instance,
									container.id, container.nodeId + "-"
											+ instance.seq);
					orcheHtml.paintInstanceNode(node.id, node.name, node.port,
							node.status);
					appPanel.cachedNodes.put(node.id, node);
					appPanel.rearrangeElementWithPos(node.id, instance.xPos,
							instance.yPos);
				}

			}
		}
		// paint edges
		for ( var i in app.relations) {
			var relation = app.relations[i];
			var fromNodes = appPanel.getEndpointIds(appPanel.cachedContainers
					.get(relation.from));
			var toNodes = appPanel.getEndpointIds(appPanel.cachedContainers
					.get(relation.to));
			for ( var m in fromNodes) {
				for ( var n in toNodes) {
					orcheHtml.paintEdge(fromNodes[m], toNodes[n]);
				}
			}
		}
		appInstance.repaintEverything();
	},

	getEndpointIds : function(container) {
		var ids = new Array();
		if (container.status == "CREATED") {
			ids.push(container.nodeId);
		} else if (container.status == "DEPLOYED") {
			for ( var j in container.instances) {
				var instance = container.instances[j];
				ids.push(container.nodeId + "-" + instance.seq);
			}
		}
		return ids;
	},

	rearrangeElementWithPos : function(id, xPos, yPos) {
		var element = $("#" + id);
		var offset = element.offset();
		offset.left = xPos;
		offset.top = yPos;
		element.offset(offset);
	},
	/**
	 * 向绘图区添加一个新的节点
	 * 
	 * @param componentId
	 */
	addNode : function(componentId) {
		var component = appPanel.cachedComponents.get(componentId);
		var nodeId = orcheHtml.paintNode(component);
		var node = appPanel.nodeFactory.createNode(nodeId, component,
				new Array());
		appPanel.cachedNodes.put(nodeId, node);
	},

	/**
	 * 节点的双击事件
	 * 
	 * @param nodeId
	 */
	nodeClick : function(nodeId) {
		var node = appPanel.cachedNodes.get(nodeId);
		if (node instanceof ContainerInstanceNode) {
			$('.operation-list li').unbind('dblclick');
			$('#operationModal').modal('show');
			$('.operation-list li').bind('dblclick', function() {
				appPanel.doOperationOnInstance(node.getInstanceId(), $(this));
			});
		} else if (node instanceof ContainerNode) {
			$('#detailModal').modal('show');
			var component = node.component;
			orcheHtml.paintNodeDetailHost(appPanel.cachedHosts.values(),
					node.hosts);
			orcheHtml.paintNodeDetailAction(node);
			orcheHtml.paintTemplateParams(node);
			orcheHtml.paintAttributes(node);
			
			$("#templates #addTemplate").unbind("click");
			$("#templates #addTemplate").click(function(){
				orcheHtml.addTemplate();
			});
			
			$("#attributes #addAttribute").unbind("click");
			$("#attributes #addAttribute").click(function(){
				orcheHtml.addAttribute();
			});
			
			$("#detailModal #initCount").unbind('change');
			$("#detailModal #initCount").bind(
					'change',
					function() {
						if (parseInt($(this).val()) > parseInt($(
								"#detailModal #maxCount").val())) {
							$(this).val($("#detailModal #maxCount").val());
							alert("primary instances must be less than max instances！")
						}
					});
			$(".editSave").unbind("click");
			$(".editSave").bind("click", function() {
				appPanel.nodeEditSave(nodeId);
				$('#detailModal').modal('hide');
			});
		}
				
	},

	/**
	 * 保存节点的配置信息
	 * 
	 * @param nodeId
	 */
	nodeEditSave : function(nodeId) {
		var node = appPanel.cachedNodes.get(nodeId);
		var component = node.component;
		var newParams = new Array();
		var newTemplates = new Array();
		var newAttributes = new Array();
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
		$("#templates tbody tr").each(function(index, element) {
			var source = $(element).find('.temSource textarea').val();
			var target = $(element).find('.temTarget textarea').val();
			var command = $(element).find('.temCommand textarea').val();
			if (source != "" && target != "") {
				newTemplates.push({
					source : source,
					target : target,
					command: command
				});
			}
		});
		$("#attributes tbody tr").each(function(index, element) {
			var attrKey = $(element).find('.attrKey textarea').val();
			var attrValue = $(element).find('.attrValue textarea').val();
			if (attrKey != "" && attrValue != "") {
				newAttributes.push({
					attrKey : attrKey,
					attrValue : attrValue
				});
			}
		});
		node.setParams(newParams);
		node.setTemplates(newTemplates);
		node.setAttributes(newAttributes);
		node.setHosts(appPanel.getTargetHosts());
		node.setName($("#detailModal #containerName").val());
		node.setPort(parseInt($("#detailModal #containerPort").val()));
		node.setInitCount(parseInt($("#detailModal #initCount").val()));
		node.setMaxCount(parseInt($("#detailModal #maxCount").val()));
		appPanel.cachedNodes.removeByKey(nodeId);
		appPanel.cachedNodes.put(nodeId, node);
		$("#" + nodeId).find(".node-name span").html(node.getName());
		$("#" + nodeId).find(".node-port span").html(node.getPort());
	},

	/**
	 * 获取选中的机器节点
	 * 
	 * @returns {Array}
	 */
	getTargetHosts : function() {
		var hosts = new Array();
		$("#hostNames input[name='hostName']").each(function() {
			if ($(this).prop('checked')) {
				var hostId = parseInt($(this).val());
				var host = appPanel.cachedHosts.get(hostId);
				if (host != false) {
					hosts.push(host);
				}
			}
		});
		return hosts;
	},

	/**
	 * 保存应用
	 * 
	 * @param appId
	 */
	appSubmit : function() {
		if (!appPanel.validateApp()) {
			return;
		}
		// 重置画板的滚动条，保证保存后能正常显示
		appPanel.resetGraphPanelScroll();
		var cachedNodes = appPanel.cachedNodes.values();
		var nodes = new Map();
		for ( var i in cachedNodes) {
			var n = cachedNodes[i];
			if (n instanceof ContainerNode) {
				var node = {
					nodeId : n.getId(),
					id : n.getContainerId(),
					name : n.getName(),
					port : n.getPort(),
					xPos : $("#" + n.id).offset().left,
					yPos : $("#" + n.id).offset().top,
					componentId : n.component.id,
//					masterId : n.hosts[0].id,
					params : n.params,
					maxCount : n.getMaxCount(),
					initCount : n.getInitCount(),
					templates : n.getTemplates(),
					attributes: n.getAttribtues()
				};
				alert(n.getPort());
				if (!nodes.containsKey(node.nodeId)) {
					nodes.put(node.nodeId, node);
				}
			} else {
				var container = appPanel.cachedContainersById.get(n
						.getContainerId());
				if (!nodes.containsKey(container.nodeId)) {
					nodes.put(container.nodeId, container);
				}
			}
		}
		var connections = appInstance.getAllConnections();
		var edges = new Map();
		for ( var i in connections) {
			var conn = connections[i];
			var sourceId = conn.sourceId, targetId = conn.targetId;
			var sourceNode = appPanel.cachedNodes.get(sourceId);
			var targetNode = appPanel.cachedNodes.get(targetId);
			if (sourceNode instanceof ContainerInstanceNode) {
				sourceId = sourceId.substring(0, sourceId.lastIndexOf("-"));
			}

			if (targetNode instanceof ContainerInstanceNode) {
				targetId = targetId.substring(0, targetId.lastIndexOf("-"));
			}
			var edge = {
				from : parseInt(sourceId),
				to : parseInt(targetId),
			};
			if (!edges.containsKey(sourceId + '_' + targetId)) {
				edges.put(sourceId + '_' + targetId, edge);
			}
		}
		var graph = {
			name : $("#appName").val(),
			containers : nodes.values(),
			relations : edges.values()
		};
		if (appPanel.currentApp != null) {
			ajaxPutJsonAuthcWithJsonContent(dURIs.appURI + "/"
					+ appPanel.currentApp.id, graph,
					appPanel.createAppCallBack, defaultErrorFunc, true);
		} else {
			ajaxPostJsonAuthcWithJsonContent(dURIs.appURI, graph,
					appPanel.createAppCallBack, defaultErrorFunc, true);
		}
		$('#saveModal').modal('hide');
	},

	/**
	 * 另存为新的应用
	 */
	saveAs : function() {
		var cachedNodes = appPanel.cachedNodes.values();
		appPanel.cachedNodes.clear();
		var idMap = new Map();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			idMap.put(node.id, dateToId());
			node.id = idMap.get(node.id);
			appPanel.cachedNodes.put(node.id, node);
		}
		var edges = new Array();
		var connections = appInstance.getAllConnections();
		// change ids of source&target of each connection
		for ( var i in connections) {
			var conn = connections[i];
			edges.push({
				from : idMap.get(parseInt(conn.sourceId)),
				to : idMap.get(parseInt(conn.targetId)),
			});
		}
		appInstance.deleteEveryEndpoint();
		for ( var i in idMap.keys()) {
			var oldId = idMap.keys()[i];
			var newId = idMap.get(oldId);
			var element = $("#" + oldId);
			element.attr("id", newId);
			element.unbind("dblclick");
			element.dblclick(function() {
				appPanel.nodeClick($(this).attr("id"));
			});

			orcheHtml.paintEndPoint(newId);
		}
		for ( var i in edges) {
			var edge = edges[i];
			orcheHtml.paintEdge(edge.from, edge.to);
		}
		appPanel.currentApp = null;
		appPanel.appSubmit();
		$('#saveModal').modal('hide');
	},

	/**
	 * 在instance上做操作
	 * 
	 * @param instanceId
	 */
	doOperationOnInstance : function(instanceId, element) {
		if (confirm("execute " + element.find(".oper-text").html() + " action?")) {
			ajaxPutJsonAuthc(dURIs.appInstanceURI + "/" + instanceId
					+ "?operation=" + element.attr("data-oper-type"), null,
					appPanel.doOperationSuccess, appPanel.doOperationError,
					true);
			$('#operationModal').modal('hide');
		}
	},

	doOperationSuccess : function() {
		appPanel.initForEdit({
			appId : appPanel.currentApp.id
		});
		defaultSuccessFunc();
	},

	doOperationError : function(data) {
		alert(data.message);
	},

	/**
	 * 创建新应用的回调函数
	 * 
	 * @param data
	 */
	createAppCallBack : function(data) {
		var app = data;
		appPanel.initForEdit({
			appId : app.id
		});
		defaultSuccessFunc();
	},

	/**
	 * 参数验证
	 * 
	 * @returns
	 */
	validateApp : function() {
		var appName = $("#appName").val().replace(/ /, "");
		if (appName.length <= 0) {
			showError("appname can not be empty");
			return false;
		}
//		return appPanel.checkHostSelect();
		return true;
	},

	/**
	 * 检查是否所有节点都选择了主机
	 * 
	 * @returns {Boolean}
	 */
	checkHostSelect : function() {
		var cachedNodes = appPanel.cachedNodes.values();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			if (node instanceof ContainerNode
					&& (!verifyParam(node.getHosts()) || node.getHosts().length <= 0)) {
				showError("please select hosts for\"" + node.getName()|+"\"");
				return false;
			}
		}
		return true;
	},

	/**
	 * 清除图
	 */
	clearGraph : function() {
		if (verifyParam(appInstance)) {
			appInstance.deleteEveryEndpoint();
		}
		for ( var i in appPanel.cachedNodes.keys()) {
			var node = $("#" + appPanel.cachedNodes.keys()[i]);
			node.remove();
		}
		appPanel.cachedNodes.clear();
		appPanel.currentApp = null;
	},

	/**
	 * 将graph panel的滚动条滑动到开始位置
	 */
	resetGraphPanelScroll : function() {
		appPanel.resetScroll($("#graph-panel"));
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
	},

	deleteNode : function(nodeId) {
		if (confirm("delete this node?")) {
			var node = $("#" + nodeId);
			var endPoints = appInstance.getEndpoints(node);
			for ( var i in endPoints) {
				appInstance.deleteEndpoint(endPoints[i].getUuid());
			}
			appPanel.cachedNodes.removeByKey(nodeId);
			node.remove();
		}
	}
};

var orcheHtml = {
	paintComponentMeta : function(componentTypes) {
		var types = componentTypes;
		var html = '';
		for ( var i in types) {
			var type = types[i];
			if (type.name != "PACKAGE::DOCKER") {
				continue;
			}
			var typeName = type.name.replace(/::/g, "_");
			var headingId = 'heading-' + typeName, bodyId = typeName + '-list';
			html += '<div class="panel panel-default">'
					+ '<div class="panel-heading" role="tab" id="'
					+ headingId
					+ '">'
					+ '<h4 class="panel-title">'
					+ '<a data-toggle="collapse" data-parent="#panel-group-operations"'
					+ ' href="#'
					+ bodyId
					+ '" aria-expanded="true" aria-controls="'
					+ bodyId
					+ '">'
					+ type.displayName
					+ ' <span class="badge">0</span></a>'
					+ '</h4></div>'
					+ '<div id="'
					+ bodyId
					+ '" class="panel-collapse collapse" role="tabpanel" aria-labelledby="'
					+ headingId + '">' + '<ul class="list-group"></ul>'
					+ '</div></div>';
		}
		$("#panel-group-operations").html(html);
	},

	/**
	 * 绘制组件列表
	 * 
	 * @param components
	 */
	paintComponentList : function(components) {
		for ( var i in components) {
			var component = components[i];
			var listId = component.type.name.replace(/::/g, "_") + "-list";
			var html = '<li class="list-group-item">'
					+ component.displayName
					+ '<i class="fa fa-plus pull-right" onclick="javascript:appPanel.addNode('
					+ component.id + ')"></i></li>';
			var group = $("#panel-group-operations #" + listId);
			group.find(".list-group").append(html);
			var badge = group.prev().find(".badge");
			badge.html(String(parseInt(badge.html()) + 1));
		}
	},

	/**
	 * 绘制节点
	 * 
	 * @param component
	 * @param nodeId
	 * @param actionName
	 * @returns
	 */
	paintNode : function(component, nodeId, nodeName, nodePort, nodeStatus) {
		var id = verifyParam(nodeId) ? nodeId : dateToId();
		var nodeName = verifyParam(nodeName) ? nodeName : component.displayName;
		var nodePort = verifyParam(nodePort) ? nodePort : "";
		var nodeStatus = verifyParam(nodeStatus) ? nodeStatus : "CREATED";

		var html = '<div id='
				+ id
				+ ' data-component-id="'
				+ (verifyParam(component) ? component.id : -1)
				+ '" class="graph-node" data-toggle="tooltip" data-placement="left" title="'
				+ nodeName
				+ '">'
				+ '<div class="node-title"><i class="fa fa-minus-circle node-del-btn" onclick="javascript:appPanel.deleteNode('
				+ id + ')"></i></div>' + '<div class="node-name">name:<span>'
				+ nodeName + '</span></div>'
				+ '<div class="node-port">port:<span>' + nodePort
				+ '</span></div>' + '<div class="node-status">status:<span>'
				+ nodeStatus + '</span></div></div>';
		$("#tmp-panel").append(html);
		$("#" + id).unbind("dblclick");
		$("#" + id).dblclick(function() {
			appPanel.nodeClick(id);
		});
		orcheHtml.paintEndPoint(id);
		return id;
	},

	/**
	 * 绘制部署实例节点
	 * 
	 * @param nodeId
	 * @param nodeName
	 * @param nodePort
	 * @param nodeStatus
	 * @returns
	 */
	paintInstanceNode : function(nodeId, nodeName, nodePort, nodeStatus) {
		var id = verifyParam(nodeId) ? nodeId : dateToId();
		var nodeName = verifyParam(nodeName) ? nodeName : component.displayName;
		var nodePort = verifyParam(nodePort) ? nodePort : "";
		var statusStyle = "graph-node-" + nodeStatus.toLowerCase();
		var nodeStatus = appMain.statusMap
				.get(verifyParam(nodeStatus) ? nodeStatus : "RUNNING");

		var html = '<div id=' + id + ' class="graph-node graph-node-deployed '
				+ statusStyle
				+ '" data-toggle="tooltip" data-placement="left" title="'
				+ nodeName + '">' + '<div class="node-title"></div>'
				+ '<div class="node-name">name:<span>' + nodeName
				+ '</span></div>' + '<div class="node-port">port:<span>'
				+ nodePort + '</span></div>'
				+ '<div class="node-status">status:<span>' + nodeStatus
				+ '</span></div></div>';
		$("#tmp-panel").append(html);
		$("#" + id).unbind("dblclick");
		$("#" + id).dblclick(function() {
			appPanel.nodeClick(id);
		});
		orcheHtml.paintEndPoint(id);
		return id;
	},

	/**
	 * 绘制连接点
	 * 
	 * @param id
	 */
	paintEndPoint : function(id) {
		var instance = appInstance;
		instance.doWhileSuspended(function() {
			instance.addEndpoint('' + id, targetEndpoint, {
				anchor : [ "Left" ],
				uuid : id + "Left"
			});
			instance.addEndpoint('' + id, sourceEndpoint, {
				anchor : [ "Right" ],
				uuid : id + "Right"
			});
			instance.draggable($("#" + id));
		});
	},

	/**
	 * 绘制配置对话框中的主机列表
	 * 
	 * @param hosts
	 * @param currentHosts
	 */
	paintNodeDetailHost : function(hosts, currentHosts) {
		var html = '';
		for ( var i in hosts) {
			var host = hosts[i];
			html += '<li><div class="checkbox"><label><input type="checkbox" name="hostName" value="'
					+ host.id
					+ '"> '
					+ host.hostName
					+ ' ('
					+ host.hostIP
					+ ')</label></div></li>';
		}
		$("#detailModal #hostNames ul").html(html);
		for ( var i in currentHosts) {
			var host = currentHosts[i];
			$("#hostNames input[name='hostName'][value='" + host.id + "']")
					.prop("checked", true);
		}
	},

	/**
	 * 绘制配置对话框中的节点信息细节
	 * 
	 * @param node
	 */
	paintNodeDetailAction : function(node) {
		var html = '';
		var component = node.component;
		$("#detailModal #actionParams tbody").html(html);
		$("#detailModal #containerName").val(node.getName());
		$("#detailModal #containerPort").val(node.getPort());
		$("#detailModal #initCount").val(node.getInitCount());
		$("#detailModal #maxCount").val(node.getMaxCount());
		orcheHtml.paintActionParams(node.action.params, node.params);
	},

	/**
	 * 绘制配置对话框中的节点参数信息
	 * 
	 * @param params
	 * @param userParams
	 */
	paintActionParams : function(params, userParams) {
		var html = '';
		if (params.length > 0) {
			for ( var i in params) {
				var param = params[i];
				var value = param.defaultValue;
				if (verifyParam(userParams)) {
					for ( var j in userParams) {
						if (userParams[j].key == param.paramKey) {
							value = userParams[j].value;
						}
					}
				}
				value = escapeToHtml(value);
				html += '<tr style="'
						+ (param.paramKey == "port" ? "display:none" : "")
						+ '"><td class="paramKey">' + param.paramKey
						+ '</td><td class="paramValue">'
						+ '<textarea class="form-control" rows="1">' + value
						+ '</textarea></td><td>' + param.description
						+ '</td></tr>';
			}
		} else {
			html = DHtml.emptyRow(3);
		}

		$("#detailModal #actionParams tbody").html(html);
	},
	/**
	 * 画边
	 * 
	 * @param from
	 * @param to
	 * @param label
	 */
	paintEdge : function(from, to) {
		var conn = appInstance.connect({
			uuids : [ from + "Right", to + "Left" ]
		});
		conn.unbind("dblclick");
		conn.bind("dblclick", function(connection, originalEvent) {
			if (confirm("delete edge?")) {
				jsPlumb.detach(conn);
			}
		});
	},

	/**
	* 增加templates
	*/
	paintTemplateParams : function(node){
		var html = '';
		var thead = '';
		var templates = node.templates;
		if(templates.length > 0){
			thead +=  "<tr>" +
						"<th>template</th>" +
						"<th>target path</th>" +
						"<th>reload cmd(optional)</th>" +
						'<th class="deleterow">' + 
								'<a href="#" role="button" class="text-warning">'+
								   '<i class="fa fa-minus-circle"></i>'
								'</a>'+
						'</th>'+
					  "</tr>";
			$("#detailModal #templates thead").html(thead);
			for(var i in templates){
				var template = templates[i];
				html += "<tr>" +
				   			'<td class="temSource">' + 
				   				'<textarea class="form-control" name="source">'+ template.source + '</textarea>' +
				   			"</td>" +
				   			'<td class="temTarget">' + 
			   					'<textarea class="form-control" name="target">'+ template.target + '</textarea>' +
			   				"</td>" +
			   				'<td class="temCommand">' + 
		   						'<textarea class="form-control" name="command">'+ template.command + '</textarea>' +
		   					"</td>" +
			   				'<td class="deleterow">' + 
			   					'<a href="#" role="button" class="text-warning">'+
			   					   '<i class="fa fa-minus-circle"></i>'
			   					'</a>'+
			   				"</td>"
		   			   "</tr>";
			}
			$(".deleterow").unbind("click");
			$(".deleterow").bind('click', function(){
				var $killrow = $(this).parent('tr');
				$killrow.addClass("danger");
				$killrow.fadeOut(1000, function() {
					$(this).remove();
				});
			});			
		}
		$("#detailModal #templates tbody").html(html);
	},
	
	addTemplate: function(){
		var html = "";
		var thead = "";
		if($("#detailModal #templates thead tr").length <= 0){
			thead +=  "<tr>" +
							"<th>template</th>" +
							"<th>file URL</th>" +
							"<th>reload command(optional)</th>" +
							'<th class="deleterow">' + 
								'<a href="#" role="button" class="text-warning">'+
								   '<i class="fa fa-minus-circle"></i>'
								'</a>'+
						'</th>'+
					  "</tr>";
			$("#detailModal #templates thead").html(thead);
		}
		html += "<tr>" +
			'<td class="temSource">' + 
				'<textarea class="form-control" name="source">'+ '</textarea>' +
			"</td>" +
			'<td class="temTarget">' + 
				'<textarea class="form-control" name="target">'+ '</textarea>' +
			"</td>" +
			'<td class="temCommand">' + 
				'<textarea class="form-control" name="command">'+ '</textarea>' +
			"</td>" +
			'<td class="deleterow">' + 
				'<a href="#" role="button" class="text-warning">'+
				   '<i class="fa fa-minus-circle"></i>'
				'</a>'+
			"</td>"
		"</tr>";
		$("#detailModal #templates tbody").append(html);
		$(".deleterow").unbind("click");
		$(".deleterow").bind('click', function(){
		var $killrow = $(this).parent('tr');
			$killrow.addClass("danger");
			$killrow.fadeOut(1000, function() {
				$(this).remove();
			});
		});
	},
	
	paintAttributes: function(node){
		var attributes = node.attributes;
		var html = '';
		var thead = '';
		if(attributes.length > 0){
			thead +=  "<tr>" +
						"<th>AttrKey</th>" +
						"<th>AttrValue</th>" +
						'<th class="deleterow">' + 
								'<a href="#" role="button" class="text-warning">'+
								   '<i class="fa fa-minus-circle"></i>'
								'</a>'+
						'</th>'+
					  "</tr>";
			$("#detailModal #attributes thead").html(thead);
			for(var i in attributes){
				var attribute = attributes[i];
				html += "<tr>" +
				   			'<td class="attrKey">' + 
				   				'<textarea class="form-control" name="attrKey">'+ attribute.attrKey + '</textarea>' +
				   			"</td>" +
				   			'<td class="attrValue">' + 
			   					'<textarea class="form-control" name="attrValue">'+ attribute.attrValue + '</textarea>' +
			   				"</td>" +
			   				'<td class="deleterow">' + 
			   					'<a href="#" role="button" class="text-warning">'+
			   					   '<i class="fa fa-minus-circle"></i>'
			   					'</a>'+
			   				"</td>"
		   			   "</tr>";
			}
			$(".deleterow").unbind("click");
			$(".deleterow").bind('click', function(){
				var $killrow = $(this).parent('tr');
				$killrow.addClass("danger");
				$killrow.fadeOut(1000, function() {
					$(this).remove();
				});
			});			
		}
		$("#detailModal #attributes tbody").html(html);
	},
	
	addAttribute : function(){
		var html = "";
		var thead = "";
		if($("#detailModal #attributes thead tr").length <= 0){
			thead +=  "<tr>" +
						"<th>AttrKey</th>" +
						"<th>AttrValue</th>" +
						'<th class="deleterow">' + 
								'<a href="#" role="button" class="text-warning">'+
								   '<i class="fa fa-minus-circle"></i>'
								'</a>'+
						'</th>'+
					  "</tr>";
			$("#detailModal #attributes thead").html(thead);
		}
		html += "<tr>" +
			'<td class="attrKey">' + 
				'<textarea class="form-control" name="attrKey">'+ '</textarea>' +
			"</td>" +
			'<td class="attrValue">' + 
				'<textarea class="form-control" name="attrValue">'+ '</textarea>' +
			"</td>" +
			'<td class="deleterow">' + 
				'<a href="#" role="button" class="text-warning">'+
				   '<i class="fa fa-minus-circle"></i>'
				'</a>'+
			"</td>"
		"</tr>";
		$("#detailModal #attributes tbody").append(html);
		$(".deleterow").unbind("click");
		$(".deleterow").bind('click', function(){
		var $killrow = $(this).parent('tr');
			$killrow.addClass("danger");
			$killrow.fadeOut(1000, function() {
				$(this).remove();
			});
		});
	},
	
	paintCustomFileList : function(files, selectedFile){
		
	}
};

$(document).ready(function() {
	appPanel.init();
});
