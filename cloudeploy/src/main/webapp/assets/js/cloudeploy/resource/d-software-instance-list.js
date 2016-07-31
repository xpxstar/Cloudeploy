var dInstanceList = {
	init : function() {
		dInstanceList.getSoftwareInstances();
	},

	/**
	 * 渲染软件列表
	 * 
	 * @param instances
	 */
	refreshList : function(data) {
		var instances = data;
		var html = '';
		if (instances.length <= 0) {
			html = DHtml.emptyRow(5);
		}

		for ( var i in instances) {
			var inst = instances[i];
			html += '<tr><td class="hide">' + inst.id + '</td><td>'
					+ inst.softwareName + '</td><td>' + inst.alias
					+ '</td><td>' + inst.status + '</td><td>'
					+ getFormatDateFromLong(inst.createdAt) + '</td><td>'
					+ getFormatDateFromLong(inst.updatedAt) + '</td><td>';
			var actions = inst.actions;
			actions.sort(function(actionA, actionB) {
				return actionA.name > actionB.name ? 1 : -1;
			});
			var operations = [];
			for ( var j in actions) {
				var action = actions[j];
				if (action.showInResourceView) {
					var actionName = action.name.substring(action.name
							.lastIndexOf("::") + 2);
					switch (actionName) {
					case "delete":
						operations.push({
							css : "fa fa-remove",
							func : "javascript:dInstanceList.removeInstance("
									+ inst.id + ")",
							text : action.displayName
						});
						break;
					case "service":
						operations
								.push({
									css : "fa fa-play",
									func : "javascript:dInstanceList.changeInstanceStatus("
											+ inst.id + ",'RUNNING')",
									text : "启动"
								});
						operations
								.push({
									css : "fa fa-stop",
									func : "javascript:dInstanceList.changeInstanceStatus("
											+ inst.id + ",'STOPPED')",
									text : "停止"
								});
						break;
					default:
						operations
								.push({
									css : "fa fa-circle-o-notch",
									func : "javascript:dInstanceList.redirectToActionParamPage("
											+ inst.id + "," + action.id + ")",
									text : action.displayName
								});
					}
				}
			}
			for ( var k in operations) {
				var operation = operations[k];
				html += '<i class="' + operation.css
						+ ' small"></i> <a style="margin-right:15px;" href="'
						+ operation.func + '">' + operation.text + '</a>';
			}
			html += '</td></tr>';
		}
		$('#instance-table tbody').html(html);
	},

	redirectToActionParamPage : function(instanceId, actionId) {
		loadPage(dURIs.views.softwareActionParams, function() {
			dActionParam.init({
				instanceId : instanceId,
				actionId : actionId
			});
		});
	},

	redirectToSqlRunPage : function(instanceId) {
		loadPage(dURIs.views.dbSqlRun, function() {
			dSqlRun.init({
				instanceId : instanceId
			});
		});
	},

	/**
	 * 获取host上的软件安装列表
	 */
	getSoftwareInstances : function() {
		if (singleTargetSelected()) {
			var data = {
				hostId : getSingleTargetHost()
			};
			ajaxGetJsonAuthc(dURIs.softwareInstancesURI, data,
					dInstanceList.refreshList, null);
		}
	},

	/**
	 * start or stop a service
	 * 
	 * @param instanceId
	 * @param operationType
	 */
	changeInstanceStatus : function(instanceId, status) {
		ajaxPutJsonAuthc(dURIs.softwareInstancesURI + "/" + instanceId
				+ "/service?status=" + status, null,
				dInstanceList.successFuncWithRefresh, defaultErrorFunc, true);
	},

	successFuncWithRefresh : function() {
		showSuccess("操作执行成功", dInstanceList.getSoftwareInstances);
	},

	/**
	 * uninstall a package
	 * 
	 * @param instanceId
	 */
	removeInstance : function(instanceId) {
		var r = confirm("确定删除此软件？")
		if (r == true) {
			ajaxDeleteJsonAuthc(dURIs.softwareInstancesURI + "/" + instanceId,
					null, dInstanceList.successFuncWithRefresh,
					defaultErrorFunc, true);
		}
	}
};

$(document).ready(function() {
	dInstanceList.init();
});