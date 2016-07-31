var dSoftwareInstall = {
	cachedSoftwares : new Map(),
	init : function() {
		this.initEvent();
		dSoftwareInstall.requestSoftwares();
	},

	initEvent : function() {
		$('select[name="softwareType"]').change(function() {
			dSoftwareInstall.typeChanged($(this).val());
		});

		$('select[name="softwareName"]').change(function() {
			dSoftwareInstall.softwareChanged($(this).val());
		});

		$('#install-btn').click(function() {
			dSoftwareInstall.installSoftware();
		});
	},

	initValidation : function() {
		var options = default_validation_option();
		options.rules = {
			installName : "required"
		};
		options.messages = {
			installName : "请输入软件别名.",
		};
		$("#initParams tbody tr").each(function(index, element) {
			var name = $(element).find('.paramName').text();
			options.rules[name] = "required";
			options.messages[name] = "请输入参数" + name;
		});
		$("#install-form").validate(options);
		$("input[name='installName']").focus();
	},

	validateParam : function() {
		var valid = true;
		$("#initParams tbody tr").each(function(index, element) {
			var name = $(element).find('.paramName').text();
			var control = $("input[name='" + name + "']");
			control.valid();
			if (!control.hasClass("success")) {
				showError("无效的参数");
				valid = false;
			}
		});
		return valid;
	},

	requestSoftwares : function() {
		ajaxGetJsonAuthc(dURIs.componentURI, {
			type : "PACKAGE"
		}, dSoftwareInstall.requestSoftwaresCallBack, null);
	},

	requestSoftwaresCallBack : function(data) {
		var softwares = data;

		var cache = new Map();
		var types = new Array();
		for ( var i in softwares) {
			var software = softwares[i];
			var type = software.type.name;
			if (!cache.containsKey(type)) {
				types.push(software.type);
			}
			cache.put(type, software);
		}
		dSoftwareInstall.cachedSoftwares.clear();
		dSoftwareInstall.cachedSoftwares = cache;
		dSoftwareInstallHtml.paintSoftwareTypeList(types);
		dSoftwareInstall.typeChanged($('select[name="softwareType"]').val());
	},

	/**
	 * 选择软件类型事件
	 * 
	 * @param inputType
	 */
	typeChanged : function(type) {
		dSoftwareInstallHtml.paintSoftwareList(dSoftwareInstall.cachedSoftwares
				.valuesByKey(type));
		dSoftwareInstall
				.softwareChanged($('select[name="softwareName"]').val());
	},

	/**
	 * 选择软件包的事件
	 * 
	 * @param packageId
	 */
	softwareChanged : function(softwareId) {
		var softwares = dSoftwareInstall.cachedSoftwares.values();
		var software = {};
		for ( var i in softwares) {
			if (softwares[i].id == softwareId) {
				software = softwares[i];
				break;
			}
		}

		var actions = software.actions;
		for ( var i in actions) {
			var action = actions[i];
			var actionName = action.name;
			if (actionName.substring(actionName.lastIndexOf("::") + 2)
					.toLowerCase() == "install") {
				$('input[name="actionId"]').val(action.id);
				dSoftwareInstallHtml.paintSoftwareParams(action.params);
				break;
			}
		}
		dSoftwareInstall.initValidation();
	},

	installSoftware : function() {
		if (!dSoftwareInstall.validateParam()) {
			return;
		}
		if (singleTargetSelected()) {
			var dataObj = {
				actionId : parseInt($('input[name="actionId"]').val()),
				hostId : getSingleTargetHost(),
				alias : $('input[name="installName"]').val(),
				params : getInstanceParams("initParams")
			};
			ajaxPostJsonAuthcWithJsonContent(dURIs.softwareInstancesURI,
					dataObj, dSoftwareInstall.successWithRedirectToList,
					defaultErrorFunc, true);
		}
	},

	successWithRedirectToList : function() {
		showSuccess("操作执行成功", function() {
			loadPage(dURIs.views.softwareInstanceList, function() {
				dInstanceList.init();
			});
		});
	},

	wizardBtnClick : function(btnId) {
		var activeSeq = parseInt($(".wizard-sec.active").attr('data-seq'));
		if (activeSeq == 1) {
			if (!$("input[name='installName']").hasClass("success")) {
				showError("无效的别名");
				return;
			}
		}
		var targetSeq = (btnId == 0) ? (activeSeq - 1) : (activeSeq + 1);
		var targetSec = $(".wizard-sec[data-seq='" + targetSeq + "']");
		if (targetSec.length > 0) {
			$(".wizard-sec").removeClass('active');
			targetSec.addClass('active');
		}
		$(".wizard-btn").removeClass('active');
		if ($(".wizard-sec[data-seq='" + (targetSeq - 1) + "']").length > 0) {
			$(".wizard-btn.previous").addClass('active');
		}
		if ($(".wizard-sec[data-seq='" + (targetSeq + 1) + "']").length > 0) {
			$(".wizard-btn.next").addClass('active');
		}
	}
};

var dSoftwareInstallHtml = {
	paintSoftwareTypeList : function(types) {
		var types = types;
		var html = '';
		for ( var i in types) {
			var type = types[i];
			html += '<option value="' + type.name + '">' + type.displayName
					+ '</option>';
		}
		$('select[name="softwareType"]').html(html);
	},

	/**
	 * data is the software map with software type names as the keys
	 * 
	 * @param data
	 */
	paintSoftwareList : function(softwares) {
		var softwareList = softwares;
		var html = '';
		for ( var i in softwareList) {
			var s = softwareList[i];
			html += '<option value="' + s.id + '">' + s.displayName
					+ '</option>';
		}
		$('select[name="softwareName"]').html(html);
	},

	paintSoftwareParams : function(params) {
		var html = '';
		for ( var i in params) {
			var param = params[i];
			html += '<tr><td class="paramName">' + param.paramKey
					+ '</td><td class="paramValue"><input type="text" name="'
					+ param.paramKey + '" class="form-control" value="'
					+ escapeToHtml(param.defaultValue) + '"></td><td>'
					+ param.description + '</td></tr>';
		}
		$("#initParams tbody").html(html);
	}

};

$(document).ready(function() {
	dSoftwareInstall.init();
});