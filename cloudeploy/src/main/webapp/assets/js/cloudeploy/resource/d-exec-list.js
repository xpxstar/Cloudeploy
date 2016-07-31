var dExecs = {
	init : function() {
		dExecs.getExecList();
		dExecs.initCodeMirror();
		$('#addExecModal').on('shown.bs.modal', function(e) {
			editor.refresh();
		});
	},

	initCodeMirror : function() {
		var mime = "text";
		window.editor = CodeMirror.fromTextArea(document
				.getElementById('execContent'), {
			mode : mime,
			indentWithTabs : true,
			smartIndent : true,
			lineNumbers : true,
			matchBrackets : true,
			autofocus : true
		});
	},

	getExecList : function() {
		ajaxGetJsonAuthc(dURIs.execsURI, null, dExecs.refreshExecList, null);
	},

	addExecClick : function() {
		$('#addExecModal').modal('show');
		$("input[name='execName']").removeAttr("disabled");
		$("input[name='execName']").val('');
		editor.setValue('');
		$('#save-btn').unbind('click');
		$('#save-btn').click(function() {
			ajaxPostJsonAuthc(dURIs.execsURI, {
				name : $("input[name='execName']").val(),
				content : editor.getValue()
			}, dExecs.getExecList, null, false);
			$('#addExecModal').modal('hide');
		});
	},

	refreshExecList : function(data) {
		var execs = data;
		var html = '';
		for ( var i in execs) {
			var exec = execs[i];
			html += '<tr><td class="hide exec-id" data-exec-id="' + exec.id
					+ '">' + exec.id + '</td><td>' + exec.name + '</td><td>'
					+ exec.content + '</td><td>'
					+ getFormatDateFromLong(exec.createdAt) + '</td><td>'
					+ getFormatDateFromLong(exec.updatedAt) + '</td><td>';
			html += '<a style="margin-right:15px;" href="javascript:dExecs.editExec('
					+ exec.id
					+ ')"><i class="fa fa-edit"></i> 编辑</a>'
					+ '<a style="margin-right:15px;" href="javascript:dExecs.runExec('
					+ exec.id
					+ ')"><i class="fa fa-upload"></i> 执行</a>'
					+ '<a style="margin-right:15px;" href="javascript:dExecs.removeExec('
					+ exec.id + ')"><i class="fa fa-remove"></i> 删除</a>';
			html += '</td></tr>';
		}
		$("#execTable tbody").html(html);
	},

	removeExec : function(execId) {
		var r = confirm("确定删除此文件？")
		if (r == true) {
			ajaxDeleteJsonAuthc(dURIs.execsURI + "/" + execId, null,
					dExecs.getExecList, null, false);
		}
	},

	editExec : function(execId) {
		var row = dExecs.findRow(execId);
		if (row) {
			$('#addExecModal').modal('show');
			$("input[name='execName']").val(dExecs.findExecName(row));
			$("input[name='execName']").attr("disabled", "disabled");
			editor.setValue(dExecs.findExecContent(row));
			$('#save-btn').unbind('click');
			$('#save-btn').click(
					function() {
						ajaxPutJsonAuthc(dURIs.execsURI + "/" + execId
								+ "?content=" + editor.getValue(), null,
								dExecs.getExecList, null, false);
						$('#addExecModal').modal('hide');
					});
		}
	},

	runExec : function(execId) {
		var row = dExecs.findRow(execId);
		if (row) {
			runCommand(dExecs.findExecContent(row));
		}
	},

	findRow : function(execId) {
		var res = false;
		$("#execTable tbody tr").each(function(index, element) {
			var td = $(element).find("td.exec-id");
			if (parseInt(td.attr("data-exec-id")) == execId) {
				res = element;
			}
		});
		return res;
	},

	findExecName : function(row) {
		return $(row).find('td')[1].innerHTML;
	},

	findExecContent : function(row) {
		return $(row).find('td')[2].innerHTML;
	}
};

$(document).ready(function() {
	dExecs.init();
});