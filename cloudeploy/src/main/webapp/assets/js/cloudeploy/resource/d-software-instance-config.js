var dInstConfig = {
	instanceId : -1,
	actionId : -1,
	init : function(param) {
		if (!verifyParam(param)) {
			return;
		}
		this.instanceId = param.instanceId;
		this.actionId = param.actionId;
		this.getSoftwareInstance(this.instanceId, this.actionId);
		this.initEvent();
	},

	initEvent : function() {
		$("#save-btn").click(function() {
			dInstConfig.saveModification(dInstConfig.instanceId);
		});
	},

	getSoftwareInstance : function(instanceId, actionId) {
		ajaxGetJsonAuthc(dURIs.softwareInstancesURI + "/" + instanceId, {
			actionId : actionId
		}, dInstConfig.fillForm, null);
	},

	fillForm : function(data) {
		var instance = data;
		$("#softwareName").html(instance.softwareName);
		$("#alias").html(instance.alias);

		var html = '';
		for ( var i in instance.params) {
			var param = instance.params[i];
			html += '<tr><td class="paramName">'
					+ param.paramKey
					+ '</td><td class="paramValue"><input type="text" class="form-control" value="'
					+ escapeToHtml(param.paramValue) + '"></td><td>'
					+ param.description + '</td></tr>';
		}
		$("#params-tbl tbody").html(html);
	},

	saveModification : function(id) {
		ajaxPutJsonAuthcWithJsonContent(dURIs.softwareInstancesURI + "/" + id,
				{
					params : getInstanceParams("params-tbl")
				}, dInstConfig.successWithRedirectToList, defaultErrorFunc,
				true);
	},

	successWithRedirectToList : function() {
		showSuccess("操作执行成功", function() {
			loadPage(dURIs.views.softwareInstanceList, function() {
				dInstanceList.init();
			});
		});
	},
};

$(document).ready(function() {
});