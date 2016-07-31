var dMain = {
	init : function() {
		dMain.initHostList();
	},

	/**
	 * initiate the host list HTML
	 */
	initHostList : function() {
		ajaxGetJsonAuthc(dURIs.hostURI, null, dMain.initHostListUIAndEvents);
	},

	initHostListUIAndEvents : function(data) {
		var hosts = data;
		var container = $(".d-host-list ul");
		container.html(dHtml.genHostListHtml(hosts));
	}
};

var dHtml = {
	/**
	 * generate host list HTML
	 * 
	 * @param hosts
	 * @returns {String}
	 */
	genHostListHtml : function(hosts) {
		var html = '';
		for ( var i in hosts) {
			var host = hosts[i];
			html += '<li><div class="checkbox"><label><input type="checkbox" name="targetHosts" value="'
					+ host.id
					+ '"> '
					+ host.hostName
					+ '<br /><span style="font-size:12px;">('
					+ host.hostIP
					+ ')</span></label></div></li>';
		}
		return html;
	}
};

/**
 * get the selected host IDs
 * 
 * @returns {Array}
 */
var getTargetHosts = function() {
	var hosts = new Array();
	$(".d-host-list input[name='targetHosts']").each(function() {
		if ($(this).prop('checked')) {
			hosts.push(parseInt($(this).val()));
		}
	});
	return hosts;
};

var getSingleTargetHost = function() {
	var hosts = getTargetHosts();
	return hosts[0];
};

var targetSelected = function() {
	var hosts = getTargetHosts();
	if (hosts.length <= 0) {
		alert("请选择目标主机");
		return false;
	}
	return true;
};

var singleTargetSelected = function() {
	var hosts = getTargetHosts();
	if (hosts.length <= 0) {
		alert("请选择目标主机");
		return false;
	}
	if (hosts.length != 1) {
		alert("只能选择一台目标主机");
		return false;
	}
	return true;
};

var getInstanceParams = function(elementId) {
	var params = new Array();
	$("#" + elementId + " tbody tr").each(function(index, element) {
		var param = {
			paramKey : $(element).find('.paramName').text(),
			paramValue : $(element).find('.paramValue input').val()
		};
		params.push(param);
	});
	return params;
};

var runCommand = function(command) {
	if (singleTargetSelected()) {
		var dataObj = {
			hostId : getSingleTargetHost(),
			params : [ {
				paramKey : "command",
				paramValue : '"' + command + '"'
			} ]
		};
		ajaxPostJsonAuthcWithJsonContent(dURIs.executionsURI, dataObj,
				defaultSuccessFunc, defaultErrorFunc, true);
	}
};

$(document).ready(function() {
	dMain.init();
});