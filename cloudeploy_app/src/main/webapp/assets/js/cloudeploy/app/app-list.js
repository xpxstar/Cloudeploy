var appList = {
	init : function() {
		appList.requestAppList();
	},

	requestAppList : function() {
		ajaxGetJsonAuthc(dURIs.appURI, null, appList.requestAppListCallback,
				null);
	},

	requestAppListCallback : function(data) {
		var apps = data;
		if (!(apps instanceof Array)) {
			return;
		}
		var html = '';
		if (apps.length > 0) {
			for ( var i in apps) {
				var app = apps[i];
				html += '<div class="panel panel-default">'
						+ '<div class="panel-heading" role="tab" id="app-'
						+ app.id
						+ '">'
						+ '<table class="table table-bordered table-condensed deploy-table">'
						+ '<tr><td style="width: 20%;">'
						+ app.name
						+ '</td><td style="width: 20%;">'
						+ app.status
						+ '</td><td>'
						+ appList.getOperationBtnHtml(app.id, 1)
						+ appList.getOperationBtnHtml(app.id, 2)
						+ appList.getOperationBtnHtml(app.id, 3)
						+ appList.getOperationBtnHtml(app.id, 4)
						+ appList.getOperationBtnHtml(app.id, 5)
						+ appList.getOperationBtnHtml(app.id, 6)
//						+ appList.getOperationBtnHtml(app.id, 7)
						+ '</td></tr></table></div>'
						+ '<div id="detail-app-'
						+ app.id
						+ '" class="panel-collapse collapse" role="tabpanel" aria-labelledby="app-'
						+ app.id
						+ '">'
						+ '<div class="panel-body"><table class="table table-bordered table-condensed deploy-table">'
						+ '<tbody><tr><td style="width:30%;">create-time：'
						+ getFormatDateFromLong(app.createdAt)
						+ '</td><td colspan="3">modify-time：'
						+ getFormatDateFromLong(app.updatedAt)
						+ '</td></tr>'
						+ '<tr><td>middleware-types：'
						+ app.containers.length
						+ '</td><td colspan="3">middleware-instances：';
				var instanceCnt = 0;
				for ( var i in app.containers) {
					instanceCnt += app.containers[i].instances.length;
				}
				html += instanceCnt + '</td></tr>';
				for ( var i in app.containers) {
					var container = app.containers[i];
					html += '<tr><td style="font-weight:bold;">middleware-name：'
							+ container.name + '</td><td>middleware-port:'
							+ container.port + '<br />';
					for ( var k in container.params) {
						var param = container.params[k];
						if (param.key == "jvm_args") {
							html += 'JVM args:' + param.value + '<br />';
						}
						if (param.key == 'session_shared') {
							html += 'session shared:' + param.value + '<br />';
						}
					}
					html += '</td><td>middlewarestatus：'
							+ container.status
							+ '</td><td>primary-instances：' + container.initCount
							+ '<br />max-instances：' + container.maxCount
							+ ' <br />current-instances：' + container.instances.length
							+ '</td> </tr>';
					for ( var j in container.instances) {
						var instance = container.instances[j];
						html += ' <tr><td>instance name：' + instance.name + '</td><td>instance port：'
								+ instance.port + '</td><td colspan="2">instance-status：'
								+ instance.status
								+ '</td></tr>';
					}

				}
				html += ' </tbody></table></div></div></div>';
			}
		} else {
			html = DHtml.emptyRow(6);
		}
		$("#app-list").html(html);
	},

	getOperationBtnHtml : function(appId, operationId) {
		var html = '';
		var style = "fa-circle-o-notch";
		var text = "actions";
		var func = undefined;
		switch (operationId) {
		case 1:
			break;
		case 2:
			text = "scheme";
			func = "appList.orchestration(" + appId + ",1)";
			break;
		case 3:
			text = "deploy";
			func = "appList.deploy(" + appId + ")";
			break;
		case 4:
			text = "scale";
			func = "appList.orchestration(" + appId + ",2)";
			break;
		case 5:
			text = "Fault-Tolerant";
			func = "appList.orchestration(" + appId + ",3)";
			break;
		case 6:
			text = "delete";
			func = "appList.deleteApp(" + appId +")";
			break;
//		case 7:
//			text = "部署编排";
//			func = "appList.deployOrchestration(" + appId + ")";
		default:
			break;
		}
		if (operationId == 1) {

			html += '<i class="fa '
					+ style
					+ ' text-success small"></i> '
					+ '<a class="link-btn" data-toggle="collapse" data-parent="#app-list" href="#detail-app-'
					+ appId
					+ '" aria-expanded="true" aria-controls="detail-app-'
					+ appId + '"> app-details </a>';
		} else {
			html += '<i class="fa ' + style + ' text-success small"></i> '
					+ '<a class="link-btn" href="javascript:'
					+ (func == undefined ? 'void(0)' : func) + '">' + text
					+ '</a>';
		}
		return html;
	},

	orchestration : function(appId, tab) {
		loadPage(dURIs.viewsURI.appOrchestration, function() {
			appPanel.initForEdit({
				appId : appId,
				tab : tab
			});
		});
	},

	deploy : function(appId) {
		ajaxPostJsonAuthc(dURIs.appURI + "/" + appId, null,
				appList.deploySuccess, defaultErrorFunc, true);
	},

	deploySuccess : function() {
		loadPage(dURIs.viewsURI.appList, null);
		defaultSuccessFunc();
	},

	deleteApp: function(appId){
		if(confirm("delete application?")){
			ajaxDeleteJsonAuthc(dURIs.appURI + "/" + appId, null, appList.deploySuccess, defaultErrorFunc, true);
		}
	}
};

$(document).ready(function() {
	appList.init();
});