var dRemoteFiles = {
	init : function() {
		dRemoteFiles.getFileList();
	},

	getFileList : function() {
		if (singleTargetSelected()) {
			var data = {
				hostId : getSingleTargetHost()
			};
			ajaxGetJsonAuthc(dURIs.remoteFilesURI, data,
					dRemoteFiles.refreshFileList, null);
		}
	},

	addFileClick : function() {
		$('#addRemoteFileModal').modal('show');
		$('#save-btn').unbind('click');
		$('#save-btn').click(function() {
			if (singleTargetSelected()) {
				ajaxPostJsonAuthc(dURIs.remoteFilesURI, {
					name : $("input[name='fileName']").val(),
					remotePath : $("input[name='filePath']").val(),
					hostId : getSingleTargetHost()
				}, dRemoteFiles.getFileList, null, false);
				$('#addRemoteFileModal').modal('hide');
			}
		});
	},

	removeFile : function(fileId) {
		var r = confirm("确定删除此文件？")
		if (r == true) {
			ajaxDeleteJsonAuthc(dURIs.remoteFilesURI + "/" + fileId, null,
					dRemoteFiles.getFileList, null, false);
		}
	},

	refreshFileList : function(data) {
		var files = data;
		var html = '';
		for ( var i in files) {
			var file = files[i];
			html += '<tr><td class="hide">' + file.id + '</td><td>' + file.name
					+ '</td><td>' + file.path + '</td><td>'
					+ getFormatDateFromLong(file.createdAt) + '</td><td>'
					+ getFormatDateFromLong(file.updatedAt) + '</td><td>';
			html += '<a style="margin-right:15px;" href="javascript:dRemoteFiles.editFile('
					+ file.id
					+ ',\''
					+ file.path
					+ '\')"><i class="fa fa-edit"></i> 编辑</a>'
					+ '<a style="margin-right:15px;" href="javascript:void(0)"><i class="fa fa-upload"></i> 替换</a>'
					+ '<a style="margin-right:15px;" href="javascript:dRemoteFiles.removeFile('
					+ file.id + ')"><i class="fa fa-remove"></i> 删除</a>';
			html += '</td></tr>';
		}
		$("#fileTable tbody").html(html);
	},

	editFile : function(fileId, filePath) {
		loadPage(dURIs.views.remoteFileEdit, function() {
			dFileEdit.init({
				remoteFileId : fileId,
				filePath : filePath
			});
		});
	}
};

$(document).ready(function() {
	dRemoteFiles.init();
});