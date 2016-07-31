var taskPanelHtml = {
	paintComponentMeta : function(componentTypes,show) {
		var types = componentTypes;
		var html = '';
		for ( var i in types) {
			var type = types[i];
			var typeName = type.name.replace(/::/g, "_");
			var headingId = 'heading-' + typeName, bodyId = typeName + '-list';
			var editType = '';
			if (show) {
				editType = '<div class="btn-group"><i class="fa fa-edit dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></i>'
					+'<ul class="dropdown-menu"><li><a onclick="javascript:componentPanel.editType('
					+ type.id + ')">修改</a></li><li><a onclick="javascript:componentPanel.delType('
					+ type.id + ')">删除</a></li></ul></div>';
			};
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
					+ editType
					+'</h4></div><div id="'
					+ bodyId
					+ '" class="panel-collapse collapse" role="tabpanel" aria-labelledby="'
					+ headingId + '">' + '<ul class="list-group"></ul>'
					+ '</div></div>';
		}
		$("#panel-group-operations").html(html);
	},

	paintComponentList : function(components,show) {
		for ( var i in components) {
			var component = components[i];
			var listId = component.type.name.replace(/::/g, "_") + "-list";
			var addBtn = '';
			if('add'==show){
				addBtn = '<i class="fa fa-plus pull-right" onclick="javascript:taskPanel.addNode('
					+ component.id + ')"></i>';
			} else if ('edit' == show) {
				addBtn = '<div class="btn-group"><i class="fa fa-edit pull-right" dropdown-toggle" data-toggle="dropdown"><span class="caret"></span> </i>'
					+ '<ul class="dropdown-menu"><li><a onclick="javascript:componentPanel.editCom('
					+ component.id + ')">修改</a></li><li><a onclick="javascript:componentPanel.delCom('
					+ component.id + ')">删除</a></li></ul></div>';
			}
			var html = '<li class="list-group-item">'
					+ component.displayName +addBtn
					+ '</li>';
			var group = $("#panel-group-operations #" + listId);
			group.find(".list-group").append(html);
			var badge = group.prev().find(".badge");
			badge.html(String(parseInt(badge.html()) + 1));
		}
	},

	/**
	 * 在页面中画出一个活动节点
	 * 
	 * @param component:
	 *            the operation this node will describe
	 * @param nodeId:
	 *            (optional)the specified id for this node
	 */
	paintOperation : function(operation, nodeId) {
		var id = verifyParam(nodeId) ? nodeId : dateToId();
		var html = '<div id='
				+ id
				+ ' data-component-id="'
				+ operation.id
				+ '" class="graph-node">'
				+ '<div class="node-title"><i class="fa fa-minus-circle node-del-btn" onclick="javascript:taskPanel.deleteNode('
				+ id + ')"></i>' + operation.name + '</div>'
				+ '<div class="node-oper"></div>' + '</div>';
		$("#tmp-panel").append(html);
		$("#" + id).unbind("dblclick");
		$("#" + id).dblclick(function() {
			taskPanel.nodeClick(id);
		});
		taskPanelHtml.paintEndPoint(id);
		return id;
	},
	/**
	 * 在页面中画出一个节点
	 * 
	 * @param component:
	 *            the component this node will describe
	 * @param nodeId:
	 *            (optional)the specified id for this node
	 */
	paintNode : function(component, nodeId, actionName) {
		var id = verifyParam(nodeId) ? nodeId : dateToId();
		var actionName = verifyParam(actionName) ? actionName
				: component.actions[0].displayName;
		var html = '<div id='
				+ id
				+ ' data-component-id="'
				+ component.id
				+ '" class="graph-node">'
				+ '<div class="node-title"><i class="fa fa-minus-circle node-del-btn" onclick="javascript:taskPanel.deleteNode('
				+ id + ')"></i>' + component.displayName + '</div>'
				+ '<div class="node-oper">' + actionName + '</div>' + '</div>';
		$("#tmp-panel").append(html);
		$("#" + id).unbind("dblclick");
		$("#" + id).dblclick(function() {
			taskPanel.nodeClick(id);
		});
		taskPanelHtml.paintEndPoint(id);
		return id;
	},

	/**
	 * 给节点添加EndPoint
	 * 
	 * @param id:
	 *            id of the element to paint end point on
	 */
	paintEndPoint : function(id) {
		var instance = taskInstance;
		instance.doWhileSuspended(function() {
			instance.addEndpoint('' + id, taskTargetEndpoint, {
				anchor : [ "Left" ],
				uuid : id + "Left"
			});
			instance.addEndpoint('' + id, taskSourceEndpoint, {
				anchor : [ "Right" ],
				uuid : id + "Right"
			});
			instance.draggable($("#" + id));//使此节点可以拖动
		});
	},

	paintEdge : function(from, to, label) {
		var conn = taskInstance.connect({
			uuids : [ from + "Right", to + "Left" ]
		});
		conn.getOverlay("label").setLabel(label.toLowerCase());//连线上写标签（为小写）
		conn.unbind("dblclick");
		conn.bind("dblclick", function(connection, originalEvent) {
			if (confirm("删除连接?")) {
				jsPlumb.detach(conn);
			}
		});
	},

	/**
	 * 在模态框中生成node的参数
	 * 
	 * @param params:
	 *            the original parameters of the action
	 * @param userParams:
	 *            user parameters for this node
	 */
	paintActionParams : function(params, userParams) {
		var html = '';
		if (userParams == "operation") {
			if (params.length > 0) {
				for ( var i in params) {
					var param = params[i];
					var value = param.value;
					//value = escapeToHtml(value);
					html += '<tr><td class="paramKey">' + param.key
							+ '</td><td class="paramValue">'
							+ '<textarea class="form-control" rows="1">' + value
							+ '</textarea></td><td>无</td></tr>';
				}
			}
			
		} else if (params.length > 0) {
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
				html += '<tr><td class="paramKey">' + param.paramKey
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
	 * 在模态框中生成node的详细信息
	 * 
	 * @param node:the
	 *            node to describe
	 */
	paintNodeDetailAction : function(node) {
		var html = '';
		if (node instanceof OperationNode) {
			taskPanelHtml.paintActionParams(node.params, "operation");
		}else{
			var component = node.component;
			var selectedActionId = node.action.id;
			for ( var i in component.actions) {
			var action = component.actions[i];
			html += '<option value="'
					+ action.id
					+ '" '
					+ (action.id == selectedActionId ? 'selected="selected"'
							: '') + ' data-component-id="' + component.id
					+ '">' + action.displayName + '</option>';
			}
		$("#detailModal #actionName").html(html);
		taskPanelHtml.paintActionParams(node.action.params, node.params);
		}
	},

	/**
	 * 在模态框中初始化host 信息
	 * 
	 * @param hosts:
	 *            the list of hosts
	 * @param currentHosts:
	 *            the selected hosts
	 */
	paintNodeDetailHost : function(hosts, currentHosts) {
		var html = '';
		for ( var i in hosts) {
			var host = hosts[i];
			html += '<li><div class="checkbox"><label><input type="checkbox" name="hostName" value="'
					+ host.id + '"> ' + host.hostName + '</label></div></li>';
		}
		$("#detailModal #hostNames ul").html(html);
		for ( var i in currentHosts) {
			var host = currentHosts[i];
			$("#hostNames input[name='hostName'][value='" + host.id + "']")
					.prop("checked", true);
		}
	},

	paintTaskList : function(tasks) {
		var html = '';
		if (tasks.length > 0) {
			for ( var i in tasks) {
				var task = tasks[i];
				html += '<tr><td style="display:none;">'
						+ task.id
						+ '</td><td>'
						+ decorate(task.name)
						+ '</td><td>'
						+ getFormatDateFromLong(task.createdAt)
						+ '</td><td>'
						+ getFormatDateFromLong(task.updatedAt)
						+ '</td><td>'
						+ getFormatDateFromLong(task.executedAt)
						+ '</td><td>'
						+ '<i class="fa fa-play text-success small"></i> <a class="link-btn" href="javascript:taskPanel.executeTask('
						+ task.id
						+ ');">执行</a>'
						+ '<i class="fa fa-edit text-success small"></i> <a class="link-btn" href="javascript:taskPanel.changeView(2,{taskId:'
						+ task.id
						+ '});">编辑</a>'
						+ '<i class="fa fa-file-code-o text-success small"></i> <a class="link-btn" target="_blank" href="/cloudeploy/views/task/source?id='
						+ task.xmlFile
						+ '">查看源文件</a>'
						+ '<i class="fa fa-remove text-danger small"></i> <a class="link-btn" href="javascript:taskPanel.deleteTask('
						+ task.id + ');">删除</a>' + '</td></tr>';
			}
		} else {
			html = DHtml.emptyRow(6);
		}
		$("#task-list tbody").html(html);
	},

	/**
	 * 绘制detail 模态框中的文件列表
	 * 
	 * @param files
	 * @param selectedFile
	 */
	paintCustomFileList : function(files, selectedFile) {
		var control = '<div class="form-group pull-left" style="width: 50%;">'
				+ '<input type="checkbox" id="file-checkbox">'
				+ '<label for="customFile" class="control-label">选择文件</label>'
				+ '<select disabled="disabled" id="customFile" name="customFile" class="form-control"></select>'
				+ '</div>';
		$("#detailModal #custom-controls").html(control);
		var content = '';
		for ( var i in files) {
			var file = files[i];
			content += '<option value="' + file.id + '">' + file.name
					+ '</option>';
		}
		var fileControl = $('#detailModal #customFile');
		fileControl.html(content);
		if (verifyParam(selectedFile)) {
			$(
					"select[name='customFile'] option[value='"
							+ selectedFile.id + "']").prop("selected", true);
		}
		fileControl.unbind('change');
		fileControl.bind('change', function() {
			taskPanel.fileSelectChanged();
		});

		$("#file-checkbox").unbind("click");
		$("#file-checkbox").bind('click', function() {
			if ($(this).prop('checked')) {
				$("select[name='customFile']").removeAttr("disabled");
				$("#customFile").change();
			} else {
				$("select[name='customFile']").attr("disabled", "disabled");
			}
		});
	},

	/**
	 * 绘制detail 模态框中的脚本列表
	 * 
	 * @param execs
	 * @param execContent
	 */
	paintExecList : function(execs, execContent) {
		var control = '<div class="form-group pull-left" style="width: 50%;">'
				+ '<input type="checkbox" id="exec-checkbox">'
				+ '<label for="exec" class="control-label">选择脚本</label>'
				+ '<select disabled="disabled" id="exec" name="exec" class="form-control"></select>'
				+ '</div>'
				+ '<div class="form-group col-sm-12" style="padding-left: 0px;">'
				+ '<label for="execContent" class="control-label">脚本内容</label>'
				+ '<textarea id="execContent" name="execContent" class="form-control" rows="5"></textarea>'
				+ '</div>';
		$("#detailModal #custom-controls").html(control);
		var content = '';
		for ( var i in execs) {
			var exec = execs[i];
			content += '<option value="' + exec.id + '">' + exec.name
					+ '</option>';
		}
		var execControl = $("#detailModal #exec");
		execControl.html(content);
		if (verifyParam(execContent)) {
			taskPanelHtml.paintExecContent(execContent);
		}

		execControl.unbind("change");
		execControl.bind('change', function() {
			var exec = taskPanel.cachedExecs.get($(this).val());
			taskPanelHtml.paintExecContent(exec.content);
		});

		$("#execContent").unbind("change");
		$("#execContent").bind('change', function() {
			taskPanel.execChanged();
		});

		$("#exec-checkbox").unbind("click");
		$("#exec-checkbox").bind('click', function() {
			if ($(this).prop('checked')) {
				$("select[name='exec']").removeAttr("disabled");
				$("#exec").change();
			} else {
				$("select[name='exec']").attr("disabled", "disabled");
			}
		});

	},

	paintExecContent : function(execContent) {
		$("textarea[name='execContent']").val(execContent);
		$("#execContent").change();
	},

	/**
	 * 清除detail 模态框中的自定义控件
	 */
	clearCustomControls : function() {
		$("#detailModal #custom-controls").html("");
	},

};