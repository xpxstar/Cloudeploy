var dateToId = function() {
	var id = new Date().getTime();
	var x = 1000, y = 0;
	var rand = parseInt(Math.random() * (x - y + 1) + y);
	id = id + rand;
	return id;
};

/**
 * return a header object with the d-token property
 */
var tokenHeader = function() {
	var header = {
		"d-token" : "b07f64868a2e43f198d1ae23208b1fa7"
	};
	return header;
};

/**
 * anon ajax get request with a response type of 'json'
 */
var ajaxGetJson = function(requestUrl, dataObj, callbackOnSuccess,
		callbackOnError) {
	var options = defaultAjaxOptions(requestUrl, "GET", null, dataObj, null,
			null, callbackOnSuccess, callbackOnError);
	$.ajax(options);
};

/**
 * authc ajax get request with a response type of 'json'
 */
var ajaxGetJsonAuthc = function(requestUrl, dataObj, callbackOnSuccess,
		callbackOnError) {
	var options = defaultAjaxOptions(requestUrl, "GET", tokenHeader(), dataObj,
			null, null, callbackOnSuccess, callbackOnError);
	$.ajax(options);
};

/**
 * anon ajax post request with a response type of 'json'
 */
var ajaxPostJsonAnon = function(requestUrl, dataObj, callbackOnSuccess,
		callbackOnError) {
	var options = defaultAjaxOptions(requestUrl, "POST", null, dataObj, null,
			null, callbackOnSuccess, callbackOnError);
	$.ajax(options);
};

/**
 * authc ajax post request with a response type of 'json'
 */
var ajaxPostJsonAuthc = function(requestUrl, dataObj, callbackOnSuccess,
		callbackOnError, withMask) {
	var options = defaultAjaxOptions(requestUrl, "POST", tokenHeader(),
			dataObj, null, null, callbackOnSuccess, callbackOnError);
	if (withMask) {
		ajaxWithMask(options);
	} else {
		$.ajax(options);
	}
};

var ajaxPostJsonAuthcWithJsonContent = function(requestUrl, dataObj,
		callbackOnSuccess, callbackOnError, withMask) {
	var options = defaultAjaxOptions(requestUrl, "POST", tokenHeader(), $
			.toJSON(dataObj), 'application/json; charset=UTF-8', null,
			callbackOnSuccess, callbackOnError);
	options.processData = false;
	if (withMask) {
		ajaxWithMask(options);
	} else {
		$.ajax(options);
	}
};

var ajaxPutJsonAuthc = function(requestUrl, dataObj, callbackOnSuccess,
		callbackOnError, withMask) {
	var options = defaultAjaxOptions(requestUrl, "PUT", tokenHeader(), dataObj,
			"text/plain;charset=UTF-8", null, callbackOnSuccess,
			callbackOnError);
	if (withMask) {
		ajaxWithMask(options);
	} else {
		$.ajax(options);
	}
};

var ajaxPutJsonAuthcWithJsonContent = function(requestUrl, dataObj,
		callbackOnSuccess, callbackOnError, withMask) {
	var options = defaultAjaxOptions(requestUrl, "PUT", tokenHeader(), $
			.toJSON(dataObj), 'application/json; charset=UTF-8', null,
			callbackOnSuccess, callbackOnError);
	options.processData = false;
	if (withMask) {
		ajaxWithMask(options);
	} else {
		$.ajax(options);
	}
};

var ajaxDeleteJsonAuthc = function(requestUrl, dataObj, callbackOnSuccess,
		callbackOnError, withMask) {
	var options = defaultAjaxOptions(requestUrl, "DELETE", tokenHeader(),
			dataObj, null, null, callbackOnSuccess, callbackOnError);
	if (withMask) {
		ajaxWithMask(options);
	} else {
		$.ajax(options);
	}
};

var ajaxWithMask = function(options) {
	options.beforeSend = function(jqXHR, settings) {
		showShade();
	};
	options.complete = function(jqXHR, textStatus) {
		hideShade();
	};
	$.ajax(options);
};

var verifyParam = function(param) {
	return !(typeof (param) == "undefined" || param == null);
};

/**
 * the function to generate a default options
 */
var defaultAjaxOptions = function(requestUrl, type, headers, dataObj,
		contentType, dataType, successFunc, errorFunc) {
	var options = {};
	if (!verifyParam(requestUrl)) {
		throw "invalid parameter";
	}
	options.url = requestUrl;
	options.type = verifyParam(type) ? type : "GET";
	options.headers = verifyParam(headers) ? headers : {};
	options.data = verifyParam(dataObj) ? dataObj : {};
	options.contentType = verifyParam(contentType) ? contentType
			: 'application/x-www-form-urlencoded; charset=UTF-8';
	options.dataType = verifyParam(dataType) ? dataType : 'json';
	if (verifyParam(successFunc)) {
		options.success = function(data, textStatus, jqXHR) {
			successFunc(data);
		};
	}
	if (verifyParam(errorFunc)) {
		options.error = function(jqXHR, textStatus, errorThrown) {
			errorFunc(jqXHR.responseJSON);
		};
	}
	return options;
};

/**
 * keys used for local storage
 */
var dKeys = {
	token : "d-token"
};

var getToken = function() {
	return "b07f64868a2e43f198d1ae23208b1fa7";
	// return getLocalData(dKeys.token);
};

var saveToken = function(value) {
	saveLocalData(dKeys.token, value)
};

var saveLocalData = function(key, value) {
	localStorage.setItem(key, value);
};

var getLocalData = function(key) {
	return localStorage.getItem(key);
};

var DHtml = {
	emptyRow : function(colspan) {
		return "<tr><td colspan='" + colspan
				+ "'><span class='text-success'>没有数据</span></td></tr>";
	}
};

var escapeToHtml = function(content) {
	return content.replace(/"/g, '&quot;').replace(/'/g, '&#039;');
};

/**
 * ajax get request with a response type of 'html'
 */
var ajaxGetPage = function(requestUrl, callback) {
	var options = defaultAjaxOptions(requestUrl, "GET", tokenHeader(), null,
			null, "html", function(content) {
				var p = $("<code></code>").append($(content));
				$(".d-main .d-main-content").html(
						$("#d-main-content", p).html());
				// will these codes executed before the js files were loaded
				if (verifyParam(callback)) {
					callback();
				}
			}, null);
	$.ajax(options);
};

/**
 * load a page to the d-main-content part
 */
var loadPage = function(pageUrl, callback) {
	ajaxGetPage(pageUrl, callback);
};

var getFormatDateFromLong = function(longTime, pattern) {
	if (!pattern) {
		pattern = 'yyyy-MM-dd HH:mm:ss';
	}
	if (longTime == null) {
		return decorate(longTime);
	}
	return $.format.date(new Date(longTime), pattern);
};

var decorate = function(str) {
	return str == null ? '--' : str;
};

var showShade = function(ajaxObj) {
	var maskHtml = '<div id="deploy-state" style="display: none;">'
			+ '<div style="z-index: 1050; position: fixed; top: 45%; left: 30%; width: 500px;">'
			+ '<div style="color: white; margin-bottom: 5px;">操作正在执行，请耐心等待...</div>'
			+ '<div class="progress">'
			+ '<div class="progress-bar progress-bar-striped active" '
			+ 'role="progressbar" aria-valuenow="100" aria-valuemin="0"'
			+ 'aria-valuemax="100" style="width: 100%;"></div></div></div>'
			+ '<div class="modal-backdrop fade in"></div></div>';
	$("body").append(maskHtml);
	$("#deploy-state").show();
};

var hideShade = function() {
	$("#deploy-state").hide();
	$("#deploy-state").remove();
};

var showError = function(msg, callback) {
	$("body").append('<div class="deploy-bar"></div>');
	var bar = $(".deploy-bar");
	if (bar) {
		bar.removeClass("deploy-success-bar");
		bar.addClass("deploy-error-bar");
		showElement(bar, msg, callback);
	}
};

var showSuccess = function(msg, callback) {
	$("body").append('<div class="deploy-bar"></div>');
	var bar = $(".deploy-bar");
	if (bar) {
		bar.removeClass("deploy-error-bar");
		bar.addClass("deploy-success-bar");
		showElement(bar, msg, callback);
	}
};

var showElement = function(element, msg, callback) {
	if (element) {
		element.html(msg ? msg : "数据异常");
		element.css("opacity", "1");
		setTimeout(function() {
			element.animate({
				"opacity" : "0"
			}, 1000);
			setTimeout(function() {
				element.remove();
			}, 1000);
			if (callback) {
				callback();
			}
		}, 2000);
	}
};

var defaultSuccessFunc = function() {
	showSuccess("操作执行成功");
};

var defaultErrorFunc = function() {
	showError("操作执行失败");
};

/*
 * default options for jquery validation custom the respective attributes when
 * using
 */
var default_validation_option = function() {
	var destroyErrors = function() {
		var error_inputs = $('input.error');
		if (error_inputs.length > 0) {
			error_inputs.each(function() {
				$(this).popover("destroy");
			});
		}
	};
	var errorKeyUp = function(e) {
		destroyErrors();
		$(e.target).valid();
	};
	var options = {
		rules : {},
		messages : {},
		validClass : "success",
		onfocusout : function(element) {
			$(element).valid();
		},
		errorPlacement : function(error, element) {
			$(element).unbind("keyup", errorKeyUp);
			element.keyup(errorKeyUp);
			var popoverOptions = {
				trigger : 'manual',
				placement : 'bottom',
				html : true,
				content : '<div class="text-danger"><span class="glyphicon glyphicon-warning-sign"></span> '
						+ error[0].innerHTML + '</div>'
			};
			element.popover(popoverOptions);
			element.popover('show');
			element.parent().addClass('has-error');
		},
		success : function(label, element) {
			$(element).popover('destroy');
			$(element).parent().removeClass('has-error');
		},
		submitHandler : function(form) {
			form.submit();
		}
	};
	return options;
};

var getUrlParam = function(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(decodeURI(r[2]));
	return null;
};
