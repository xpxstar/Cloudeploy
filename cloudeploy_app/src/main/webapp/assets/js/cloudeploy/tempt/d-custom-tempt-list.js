var dCustomFiles = {
	currentFileId : -1,
	init : function() {
		dCustomFiles.getFileList();
		$('#upload-btn').click(function() {
			$('#file-upload').uploadify('upload');
		});
	},

	initUploadify : function(uploadCallBack) {
		var uploadifyOptions = {
			'auto' : false,
			'multi' : false,
			'uploadLimit' : 1,
			'buttonText' : 'choose file',
			'swf' : dURIs.swfs + '/uploadify.swf',
			'uploader' : dURIs.filesURI,
			'fileObjName' : 'file',
			'formData' : {
				"d-token" : getToken()
			},
			'onUploadSuccess' : function(file, data, response) {
				data = $.parseJSON(data);
				if (verifyParam(uploadCallBack)) {
					uploadCallBack(data.fileKey, $("input[name='fileName']")
							.val());
				}
			},
			'onSelect' : function(file) {
				if ($("input[name='fileName']").attr("disabled") == "disabled") {
					return;
				}
				$("input[name='fileName']").val(file.name);
			}
		};
		$('#file-upload').uploadify(uploadifyOptions);
	},

	getFileList : function() {
		ajaxGetJsonAuthc(dURIs.customFilesURI, null,
				dCustomFiles.refreshFileList, null);
	},

	addFileClick : function() {
		$('#addCustomFileModal').modal('show');
		$("#fileName").val("");
		$("#fileName").removeAttr("disabled");
		dCustomFiles.initUploadify(dCustomFiles.addFile);
	},

	addFile : function(fileKey, fileName) {
		ajaxPostJsonAuthc(dURIs.customFilesURI, {
			name : fileName,
			fileKey : fileKey
		}, dCustomFiles.getFileList, null, false);
	},

	replaceFileClick : function(fileId) {
		$('#addCustomFileModal').modal('show');
		var fileName = $('#fileTable tbody tr[data-id="' + fileId + '"]').find(
				"td")[1].innerHTML;
		$("#fileName").val(fileName);
		$("#fileName").attr("disabled", "disabled");
		dCustomFiles.currentFileId = fileId;
		dCustomFiles.initUploadify(dCustomFiles.replaceFile);
	},

	replaceFile : function(fileKey, file) {
		ajaxPutJsonAuthc(dURIs.customFilesURI + "/"
				+ dCustomFiles.currentFileId + "?fileKey=" + fileKey, null,
				null, null, false);
		dCustomFiles.currentFileId = -1;
	},

	removeFile : function(fileId) {
		var r = confirm("delete this file?")
		if (r == true) {
			ajaxDeleteJsonAuthc(dURIs.customFilesURI + "/" + fileId, null,
					dCustomFiles.getFileList, null, false);
		}
	},

	refreshFileList : function(data) {
		var files = data;
		var html = '';
		for ( var i in files) {
			var file = files[i];
			html += '<tr data-id="' + file.id + '"><td class="hide"></td><td>'
					+ file.name + '</td><td>'
					+ getFormatDateFromLong(file.createdAt) + '</td><td>'
					+ getFormatDateFromLong(file.updatedAt) + '</td><td>';
			html += '<a style="margin-right:15px;" href="javascript:dCustomFiles.replaceFileClick('
					+ file.id
					+ ')"><i class="fa fa-upload"></i> exchange</a>'
					+ '<a style="margin-right:15px;" href="javascript:dCustomFiles.removeFile('
					+ file.id + ')"><i class="fa fa-remove"></i> delete</a>';
			html += '</td></tr>';
		}
		$("#fileTable tbody").html(html);
	}
};

$(document).ready(function() {
	dCustomFiles.init();
});