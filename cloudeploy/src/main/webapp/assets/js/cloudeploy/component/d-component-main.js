var componentPanel = {
	cachedComponentTypes : new Map(),//组件类型
	cachedComponents : new Map(),//系统组件
	cachedCustomTypes : new Map(),//组件类型
	cachedCustomComponents : new Map(),//系统组件
	init : function() {
		componentPanel.initComponents();
		componentPanel.initEvents();
	},
	initComponents : function() {
		if (componentPanel.cachedComponentTypes.isEmpty()) {
			ajaxGetJsonAuthc(dURIs.componentTypeURI, null,
				componentPanel.requestComponentTypesCallback, null);
		} else {
			taskPanelHtml.paintComponentMeta(componentPanel.cachedComponentTypes.values(),false);
			taskPanelHtml.paintComponentList(componentPanel.cachedComponents.values(),'none');
		}

	},
	requestComponentTypesCallback : function(data) {
		taskPanelHtml.paintComponentMeta(data,false);
		var types = data;
		componentPanel.requestComponents();
		for ( var i in types) {
			componentPanel.cachedComponentTypes.put(types[i].id, types[i]);
		}
	},
	requestComponents : function() {
		ajaxGetJsonAuthc(dURIs.componentURI, null,
				componentPanel.requestComponentsCallback, null);
	},
	requestComponentsCallback : function(data) {
		var components = data;
		taskPanelHtml.paintComponentList(components,'none');//不显示"edit"表示可操作
		for ( var i in components) {
			componentPanel.cachedComponents.put(components[i].id, components[i]);
		}
		
	},
	//fresh 表示是否强制刷新列表
	initCustomComponents : function(fresh){
		if (componentPanel.cachedCustomTypes.isEmpty() || fresh) {
			componentPanel.cachedCustomTypes.clear();
			componentPanel.cachedCustomComponents.clear();
			ajaxGetJsonAuthc(dURIs.componentTypeURI+'/custom', null,
				componentPanel.requestCustomTypesCallback, null)
		} else {
			taskPanelHtml.paintComponentMeta(componentPanel.cachedCustomTypes.values(),true);
			taskPanelHtml.paintComponentList(componentPanel.cachedCustomComponents.values(),'edit');
		}
	},
	requestCustomTypesCallback : function(data) {
		taskPanelHtml.paintComponentMeta(data,true);
		componentPanel.requestCustoms();
		var types = data;
		for ( var i in types) {
			componentPanel.cachedCustomTypes.put(types[i].id, types[i]);
		}
	},
	requestCustoms : function() {
		ajaxGetJsonAuthc(dURIs.componentURI+'/custom', null,
				componentPanel.requestCustomCallback, null);
	},
	requestCustomCallback : function(data) {
		var components = data;
		taskPanelHtml.paintComponentList(components,'edit');//显示"edit"表示可操作
		for ( var i in components) {
			componentPanel.cachedCustomComponents.put(components[i].id, components[i]);
		}
	},

	initEvents : function(){
			
		$('#sys_component').click(function() {
			componentPanel.initComponents();
			$('#cus_component').removeClass('active');
			$('#sys_component').addClass('active');
			$('#add_component').addClass('hide');
			$('#add_componenttype').addClass('hide');
			$('#detailModal').addClass('hide');
		});
		$('#cus_component').click(function() {
			componentPanel.initCustomComponents(false);
			$('#sys_component').removeClass('active');
			$('#cus_component').addClass('active');
			$('#add_component').removeClass('hide');
			$('#add_componenttype').removeClass('hide');
			
		
			
		});
		$('#add_componenttype').click(function(){
			$('#typename').val('');$('#displayname').val('');
			$('#addCustomTypeModal').modal('show');
					
		});
		$('#add_component').click(function(){
			$('#addCustomComponentModal').modal('show');
			componentPanel.loadTypeList();
		});
		$('#save_type').click(function(){
			var typename = $('#typename').val();
			var displayName = $('#displayname').val();
			var typeId = $('#addCustomTypeModal').attr('typeId');
			if ('' == typename || '' == displayName){
				$('#type-error').removeClass('hide');
			} else {
				componentPanel.createType(typeId,typename,displayName);
			}
			
		});
		$('#close-type-alert').click(function(){
			$('#type-error').addClass('hide');
		});
		$('#detailModal #comName').change(
				function() {
					var selectedOption = $(this).find("option:selected");
					var comid = selectedOption.attr('data-component-id');
					var order = selectedOption.attr('action-order');
					var actionid = selectedOption.val();
					var com = componentPanel.cachedCustomComponents.get(comid);
					var action = com.actions[order];
					if (action.id == actionid) {
						componentPanel.paintComParams(action.params);
						$("#detailModal #actionEdit").val(action.displayName);
					}
				});
	},
	loadTypeList : function(){
		var html = '';
		if(componentPanel.cachedCustomTypes.isEmpty()){
			html='<option value="0">请添加自定义组件类型</option>';
		}else{
			var types = componentPanel.cachedCustomTypes.values()
			html ='';
			for (var i in types){
			html+='<option value="'+types[i].id+'">'+types[i].displayName+'</option>';
			}
		}
		$('#belong').html(html);
	},
	editCom :function(id){
		var com = componentPanel.cachedCustomComponents.get(id);
		$("#detailModal").attr('comid',id);
		$("#myModalLabel").html(com.displayName);
		$("#detailModal").removeClass('hide');
		var html = '';
		if (com.actions.length > 0 ) {
			for ( var i in com.actions) {
				var action = com.actions[i];
				html += '<option action-order="'+i
						+'" value="'
						+ action.id
						+ '"  data-component-id="' + com.id
						+ '">' + action.displayName + '</option>';
			}
			$("#detailModal #comName").html(html);
			var action0 = com.actions[0];
			componentPanel.paintComParams(com.actions[0].params);
			$("#detailModal #actionEdit").val(action0.displayName);
			$('#detailModal #changeActionName').removeClass('disabled');
			
		} else{
			$("#detailModal #comName").html('');
			componentPanel.paintComParams(null);
			$("#detailModal #actionEdit").val('');
			$('#detailModal #changeActionName').addClass('disabled');
			// $('#detailModal #saveParamBtn').addClass('disabled');
		}
	},
	delCom :function(id){
		if(window.confirm('将删除组件所有内容，确认继续删除？')){
                 ajaxPostJsonAuthc(dURIs.componentURI+'/del', {id:id},
				null, null,false,false);
				componentPanel.initCustomComponents(true);
              }
		
	},
	paintComParams : function(params) {
		var html = '';
		if (null != params && params.length > 0) {
			for ( var i in params) {
				var param = params[i];
				var value = param.defaultValue;
				html += '<tr param="'
						+param.id+'" order="'+i
						+'"><td class="paramKey">' + param.paramKey
						+ '</td><td class="paramValue">'
						+ '<textarea class="form-control" rows="1" fun="value">' + value
						+ '</textarea></td><td class="paramDesc">'
						+'<textarea class="form-control" rows="1" fun="desc">' + param.description
						+ '</textarea></td></tr>';
			}
		}else{
			html='<tr><td colspan="3"><span class="text-success">没有数据</span></td></tr>'
		}

		$("#detailModal #comParams tbody").html(html);
		$("#comParams .form-control").change(
			function(){
				var selectedOption = $('#detailModal #comName').find("option:selected");
				var comid = selectedOption.attr('data-component-id');
				var order = selectedOption.attr('action-order');
				var com = componentPanel.cachedCustomComponents.get(comid);
				var action = com.actions[order];
				var order2 = $(this).parent().parent().attr('order');
				var paramid = $(this).parent().parent().attr('param');
				var param = action.params[order2];
				if (paramid == param.id) {
					if ("value" ==  $(this).attr('fun')) {
						param.defaultValue = $(this).val();
					}else if ("desc" ==  $(this).attr('fun')) {
						param.description = $(this).val();
					}
					componentPanel.changeParam(param.id,param.defaultValue,param.description);
				};

			});
	},
	changeActionName: function(){
		var newName = $('#actionEdit').val();
		var selectedOption = $('#detailModal #comName').find("option:selected");
		selectedOption.html(newName);
		var actionid = selectedOption.val();
		ajaxPostJsonAuthc(dURIs.actionURI+'/changeName', {
					id : actionid,
					displayName : newName,
				},componentPanel.changeActionCallBack, null, false);
		
	},
	changeActionCallBack:function(data){
		var comid = $("#detailModal").attr('comid');
		var newName = $('#actionEdit').val();
		var com = componentPanel.cachedCustomComponents.get(comid);
		var action;
		for ( var i in com.actions) {
			if(data==com.actions[i].id){
				action = com.actions[i].displayName = newName;
				break;
			}
		}
	},
	changeParam :function(id,value,desc){
		ajaxPostJsonAuthc(dURIs.actionURI+'/param', {
					id : id,
					value:value,
					desc:desc
				},null, null, false);
	},
	editType :function(typeId){
		var comType = componentPanel.cachedCustomTypes.get(typeId);
		$('#typename').val(comType.name);
		$('#displayname').val(comType.displayName);
		$('#addCustomTypeModal').attr('typeId',typeId);
		$('#addCustomTypeModal').modal('show');
	},
	createType:function(typeId,typename,displayName){
		ajaxPostJsonAuthc(dURIs.componentURI+'/types/custom', {
					id : typeId,
					name : typename,
					displayName : displayName
				},componentPanel.createTypeCallBack, null, false);
				
	},
	createTypeCallBack:function(data){
		$('#addCustomTypeModal').modal('hide');
		componentPanel.initCustomComponents(true);
	},
	delType :function(typeId){
		var comType = componentPanel.cachedCustomTypes.get(typeId);
		var child = $('#heading-'+comType.name).find(".badge").html();
		if ('0' == child ) {
			ajaxPostJsonAuthc(dURIs.componentTypeURI+'/del', {id:typeId},
				null, null,false,false);
			componentPanel.initCustomComponents(true);
		} else {
			alert('组内该类型中尚有组件');
		}
	}
};

$(document).ready(function() {
	componentPanel.init();
	create.init();
});