var showXML = {
	init : function() {
		showXML.initCodeMirror();
		showXML.generate();
	},

	initCodeMirror : function() {
		var mime = "text/html";
		window.editor = CodeMirror.fromTextArea(document
				.getElementById('xmlContent'), {
			mode : mime,
			indentWithTabs : true,
			smartIndent : true,
			lineNumbers : true,
			height: "auto",
			matchBrackets : true,
			autofocus : true,
			readOnly : true,
			fullScreen : true
		});
	},

	generate : function() {
		var options = {
			type : 'GET',
			url : dURIs.filesURI + "/" + getUrlParam("id"),
			success : function(data, textStatus, jqXHR) {
				var xmlContent = data;
				editor.setValue(xmlContent);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert("fail to get file content");
			}
		};

		$.ajax(options);
	}
};

$(document).ready(function() {
	showXML.init();
});