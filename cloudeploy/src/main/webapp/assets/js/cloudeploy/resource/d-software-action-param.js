var dActionParam = {
	instanceId : -1,
	actionId : -1,
	cachedCustomFiles : new Map(),
	currentSoftwareInstance : null,
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
		$("#save-btn").click(
				function() {
					dActionParam.saveModification(dActionParam.instanceId,
							dActionParam.actionId);
				});
	},

	getSoftwareInstance : function(instanceId, actionId) {
		ajaxGetJsonAuthc(dURIs.softwareInstancesURI + "/" + instanceId, {
			actionId : actionId
		}, dActionParam.preFillForm, null);
	},

	preFillForm : function(data) {
		var instance = data;
		dActionParam.currentSoftwareInstance = instance;
		var fileIncluded = false;
		for ( var i in instance.params) {
			var param = instance.params[i];
			if (param.viewType == "view:file") {
				dActionParam.initCustomFileList();
				fileIncluded = true;
				break;
			}
		}
		if (!fileIncluded) {
			dActionParam.fillForm(instance);
		}
	},

	fillForm : function(instance) {
		$("#softwareName").html(instance.softwareName);
		$("#alias").html(instance.alias);
		var html = '';
		for ( var i in instance.params) {
			var param = instance.params[i];
			html += '<tr><td class="paramName">' + param.paramKey
					+ '</td><td class="paramValue">';
			if (param.viewType == "view:file") {
				html += '<select class="customFiles form-control">';
				var files = dActionParam.cachedCustomFiles.values();
				for ( var i in files) {
					var file = files[i];
					html += '<option value="' + file.id + '">' + file.name
							+ '</option>';
				}
				html += '</select><input type="hidden">';
			} else {
				html += '<input type="text" class="form-control" value="'
						+ escapeToHtml(param.paramValue) + '">';
			}
			html += '</td><td>' + param.description + '</td></tr>';
		}
		$("#params-tbl tbody").html(html);
		$("#params-tbl tbody .customFiles").change(function() {
			dActionParam.fileSelectChanged($(this));
		});
		$("#params-tbl tbody .customFiles").change();
	},

	saveModification : function(instanceId, actionId) {
		ajaxPostJsonAuthcWithJsonContent(dURIs.softwareInstancesURI + "/"
				+ instanceId + "/actions/" + actionId, {
			params : getInstanceParams("params-tbl")
		}, dActionParam.successWithRedirectToList, defaultErrorFunc, true);
	},

	successWithRedirectToList : function() {
		showSuccess("操作执行成功", function() {
			loadPage(dURIs.views.softwareInstanceList, function() {
				dInstanceList.init();
			});
		});
	},

	initCustomFileList : function() {
		if (dActionParam.cachedCustomFiles.isEmpty()) {
			ajaxGetJsonAuthc(dURIs.customFilesURI, null,
					dActionParam.requestCustomFileListCallback, null);
		} else {
			dActionParam.fillForm(dActionParam.currentSoftwareInstance);
		}
	},

	requestCustomFileListCallback : function(data) {
		var files = data;
		for ( var i in files) {
			dActionParam.cachedCustomFiles.put(files[i].id, files[i]);
		}
		dActionParam.fillForm(dActionParam.currentSoftwareInstance);
	},

	fileSelectChanged : function(selector) {
		var fileControl = selector.find("option:selected");
		var fileId = fileControl.val();
		var file = dActionParam.cachedCustomFiles.get(parseInt(fileId));
		ajaxPostJsonAuthc(dURIs.filesURI + "/" + file.fileKey, null,
				function(data) {
					var res = data;
					var valueInput = selector.next();
					valueInput.val('"' + res.url + "?name="
							+ fileControl.text() + '"');
				}, defaultErrorFunc, false);
	}
};

$(document).ready(function() {
});