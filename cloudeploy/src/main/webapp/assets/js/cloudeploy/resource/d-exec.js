var dExec = {
	init : function() {
		this.initCodeMirror();
		$("#submit-btn").click(function() {
			runCommand(editor.getValue());
		});
	},
	initCodeMirror : function() {
		var mime = "text";
		window.editor = CodeMirror.fromTextArea(document
				.getElementById('command'), {
			mode : mime,
			indentWithTabs : true,
			smartIndent : true,
			lineNumbers : true,
			matchBrackets : true,
			autofocus : true
		});
	}
};

$(document).ready(function() {
	dExec.init();
});