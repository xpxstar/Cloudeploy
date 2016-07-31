var taskPanelHtmlAct = {
	paintComponentMeta : function(componentTypes,show) {
		var types = componentTypes;
		var html = '';
		if (types.length < 1) {
			$("#self-com").hide();
		}
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
		$("#panel-group-operations2").html(html);
	},

	paintComponentList : function(components,show) {
		for ( var i in components) {
			var component = components[i];
			var listId = component.type.name.replace(/::/g, "_") + "-list";
			var addBtn = '';
			if('add'==show){
				addBtn = '<i class="fa fa-plus pull-right" onclick="javascript:taskPanel.addNode('
					+ component.id + ',0)"></i>';
			}
			var html = '<li class="list-group-item">'
					+ component.displayName +addBtn
					+ '</li>';
			var group = $("#panel-group-operations2 #" + listId);
			group.find(".list-group").append(html);
			var badge = group.prev().find(".badge");
			badge.html(String(parseInt(badge.html()) + 1));
		}
	},
	paintActionList : function(actions) {
		if (actions.length < 1) {
			$("#self-action").hide();
		}
		var html='';
		for ( var i in actions) {
			var action = actions[i];
			var addBtn = '';
			addBtn = '<i class="fa fa-plus pull-right" onclick="javascript:taskPanel.addNode('
					+ action.id + ',1)"></i>';
			 html+= '<li class="list-group-item">'
					+ action.name +addBtn
					+ '</li>';
		}
		var group = $("#self-action #action-options");
			group.find(".list-group").html(html);
			var badge = group.prev().find(".badge");
			badge.html(actions.length);
	},
};