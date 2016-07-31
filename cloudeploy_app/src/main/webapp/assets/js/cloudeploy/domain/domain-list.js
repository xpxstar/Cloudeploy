var domainList = {
	init : function() {
		domainList.requestDomainList();
	},

	requestDomainList : function() {
		ajaxGetJsonAuthc(dURIs.domainURI, null,
				domainList.requestDomainListCallback, null);
	},

	requestDomainListCallback : function(data) {
		var domains = data;
		if (!(domains instanceof Array)) {
			return;
		}
		var html = '';
		if (domains.length > 0) {
			for ( var i in domains) {
				var domain = domains[i];
				html += '<tr><td>'
						+ domain.name
						+ '</td><td>'
						+ domain.ip
						+ '</td><td><i class="fa fa-circle-o-notch text-success small"></i> '
						+ '<a class="link-btn" href="javascript:domainList.deleteDomain('
						+ domain.id + ')">解除绑定</a></td></tr>';
			}
		} else {
			html = DHtml.emptyRow(6);
		}
		$("#domain-list tbody").html(html);
	},

	deleteDomain : function(domainId) {
		if (confirm("确定解除域名绑定?")) {
			ajaxDeleteJsonAuthc(dURIs.domainURI + "/" + domainId, null,
					domainList.requestDomainList, defaultErrorFunc, true);
		}
	},

	bindDomainClick : function() {
		$("#bindDomainModal").modal('show');
		$("#save-domain-btn").unbind('click');
		$("#save-domain-btn").bind(
				'click',
				function() {
					$("#bindDomainModal").modal('hide');
					domainList.bindDomain($("input[name='domainName']").val(),
							$("input[name='domainIP']").val());
				});
	},
	bindDomain : function(name, ip) {
		ajaxPostJsonAuthc(dURIs.domainURI + "?name=" + name + "&ip=" + ip,
				null, domainList.bindCallback, defaultErrorFunc, true);
	},

	bindCallback : function(data) {
		domainList.requestDomainList();
	}
};

$(document).ready(function() {
	domainList.init();
});