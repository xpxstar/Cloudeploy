var configManager = {
	init : function() {
		configManager.initAppList(1);
	},
	
	operationId : 1,
	
	propertyOperation : function(item){
		if(configManager.operationId != 1){
			configManager.operationId = 1;
			$(".param-type").removeClass("active");
			$(item).addClass("active");
			$("#d-app-list-buttons .active").click();
		}
	},
	
	attributeOperation : function(item){
		if(configManager.operationId != 2){
			configManager.operationId = 2;
			$(".param-type").removeClass("active");
			$(item).addClass("active");
			$("#d-app-list-buttons .active").click();
		}
	},

	/**
	 * initiate the host list HTML
	 */
	initAppList : function(operationId) {
		ajaxGetJsonAuthc(dURIs.appURI, null, configManager.initHostListUIAndEvents,
				null);
	},

	initHostListUIAndEvents : function(data) {
		var apps = data;
		var container = $(".d-app-list-buttons");
		container.html(dHtml.genAppsListHtml(apps));
	},
	
	getContainers : function(appitem,appId, appName){
		$(".cfg-container").removeClass("active");
		$(appitem).addClass("active");
		ajaxGetJsonAuthc(dURIs.appURI + "/" + appId, null,
			function(data){
				var apps = data;
				var container = $(".d-config-main-content");
				container.html(dHtml.genContainerTabHtml(apps));
				$('#tabs-758944 a').each(function(index, element){
					var containerId = $(this).attr('href');
					var containerName = $(this).text();
					var callFunc = function(){
						switch(configManager.operationId){
						case 1: 
							configManager.getProperties(appId, appName, containerId, containerName);
							break;
						case 2:
							configManager.getAttributes(appId, appName, containerId, containerName);
							break;
						}
					}
					if(index == 0){
						callFunc();
					}
					$(this).on("shown.bs.tab", function(){
						callFunc();
				});	
			});
		}, null);
	},
	

	getProperties : function(appId, appName, containerId, containerName) {
		ajaxGetJsonAuthc(dURIs.viewsURI.configManager + "/properties/" + appId + "/"
				+ containerId, null, function(data) {
			var properties = data;
			$('.tab-pane').each(function(){
				$(this).hide();
			});
			var container = $("#"+containerId);
			container.show();
			container.html(dHtml.genPropertyTableHtml(properties));
//			$('#tabs-758944 a[href="'+ containerId +'"]').tab('show');
		}, null);
	},
	
	getAttributes : function(appId, appName, containerId, containerName) {
		ajaxGetJsonAuthc(dURIs.viewsURI.configManager + "/attributes/" + appId + "/"
				+ containerId, null, function(data) {
			$('.tab-pane').each(function(){
				$(this).hide();
			});
			var properties = data;
			var container = $("#"+containerId);
			container.show();
			container.html(dHtml.genPropertyTableHtml(properties));
		}, null);
	},
	
	updateProperty : function(appId, containerId, propertyName){
		var action = dURIs.viewsURI.configManager + '/properties/' + appId + '/' + containerId + '/' + propertyName;
		var id = containerId + "_" + propertyName;
		var value = $("#"+id).val();
		ajaxPostJsonAuthcWithJsonContent(action, value, null, null, true);
	},
	
	updateAttribute : function(appId, containerId, attributeName, i){
		var action = dURIs.viewsURI.configManager + '/attributes/' + appId + '/' + containerId 
			+ "?attributeName=" + attributeName;
		var id = containerId + "_" + i;
		var value = $("#"+id).val();
		ajaxPostJsonAuthcWithJsonContent(action, value, null, null, true);
	}
};

var dHtml = {
	/**
	 * generate app list HTML
	 * 
	 * @param apps
	 * @returns {String}
	 */
	genAppsListHtml : function(apps) {
		var html = '';
		for ( var i in apps) {
			var app = apps[i];
			html += '<div class="btn-group-vertical btn-group-justified" role="group" aria-label="...">'
				 + '<a role="button" class="cfg-container list-group-item" href="javascript:void(0);" onclick="configManager.getContainers(this,'
				 + app.id 
				 +',\'' 
				 + app.name
				 +'\')"> ' 
				 + app.name +' </a></div>'
		}
		return html;
	},
	
	/**
	 * generate app containers HTML
	 * 
	 * @param properties
	 * @returns {String}
	 */
	genContainerTabHtml : function(app){
		var html="";
		var navHtml = '';
		var contentHtml = '';
		var panelId = 'panel';
		html += "<div class=\"tabbable\" id=\"tabs-758944\">";
		navHtml += "   <ul class=\"nav nav-tabs\">";
		contentHtml += "   <div class=\"d-config-tab-content\">";
		for(var i in app.containers){
			var container = app.containers[i];
			if(i == 0){
				navHtml += "      <li class=\"active\">";
				navHtml += "         <a href=\"" + container.id +"\" data-toggle=\"tab\">" +  container.name + "<\/a>";
				navHtml += "      <\/li>";
				
				contentHtml += "      <div class=\"tab-pane\" id=\"" + container.id +"\">";
				contentHtml += "      <\/div>";
			}else{
				navHtml += "      <li>";
				navHtml += "         <a href=\"" + container.id +"\" data-toggle=\"tab\">" +  container.name + "<\/a>";
				navHtml += "      <\/li>";
				contentHtml += "      <div class=\"tab-pane\" id=\"" + container.id +"\">";
				contentHtml += "      <\/div>";
			}
		}
		navHtml += "   <\/ul>";
		contentHtml += "   <\/div>";
		html += navHtml;
		html += contentHtml;
		html += "</div>"
		return html;
	},
	
	/**
	 * generate app properties HTML
	 * 
	 * @param properties
	 * @returns {String}
	 */
	genPropertyTableHtml : function(properties){
		var func = undefined;
		switch(configManager.operationId){
		case 1: 
			func = "updateProperty"
			break;
		case 2:
			func = "updateAttribute"
			break;
		}
		var html = '';
		html += "<table class=\"table\">";
		html += "   <thead>";  
		html += "      <tr>";
		html += "         <th>";
		html += "            #";
		html += "         <\/th>";
		html += "         <th>";
		html += "            key";
		html += "         <\/th>";
		html += "         <th>";
		html += "            value";
		html += "         <\/th>";
		html += "         <th>";
		html += "            action";
		html += "         <\/th>";
		html += "      <\/tr>";
		html += "   <\/thead>";
		html += "   <tbody>";
		for(var i in properties) {
			var property = properties[i];
			var args = "'" + property.appId + "','" + property.containerId + "','" + property.key + "'," + i;
			var id = property.containerId + "_" + i;
			html += "      <tr>";
			html += "         <td>";
			html += i;
			html += "         <\/td>";
			html += "         <td>";
			html += property.key;
			html += "         <\/td>";
			html += "         <td>";
			html +='<input type="text" class="form-control" value='+ property.value +' id='+ id +'>';
			html += "         <\/td>";
			html += "         <td>";
			html += "<button type=\"button\" class=\"btn btn-success btn-xs\" onclick=\"javascript:configManager."+ func +"("+args+")\">";
			html += "update";
			html += "<\/button>";
			html += "         <\/td>";
			html += "      <\/tr>";
		}
		html += "   <\/tbody>";
		html += "<\/table>";
		return html;
	}
};

$(document).ready(function() {
	configManager.init();
});