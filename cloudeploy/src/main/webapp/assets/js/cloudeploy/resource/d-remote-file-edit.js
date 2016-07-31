var dFileEdit = {
	remoteFileId : -1,
	downCounter : 30,
	filePath : null,
	accessKey : null,
	editorInitiated : false,
	init : function(param) {
		if (verifyParam(param)) {
			if (verifyParam(param.remoteFileId)) {
				dFileEdit.remoteFileId = param.remoteFileId;
			}
			if (verifyParam(param.filePath)) {
				dFileEdit.filePath = param.filePath;
				$("#filePath").html(dFileEdit.filePath);
			}
			dFileEdit.getUploadKey();
		}
		if (!dFileEdit.editorInitiated) {
			this.initCodeMirror();
		}

		$("#submit-btn").click(
				function() {
					dFileEdit.saveContentToFile(editor.getValue(),
							dFileEdit.saveFileToRemote);
				});

	},

	initCodeMirror : function() {
		var mime = "text";
		window.editor = CodeMirror.fromTextArea(document
				.getElementById('fileContent'), {
			mode : mime,
			indentWithTabs : true,
			smartIndent : true,
			lineNumbers : true,
			matchBrackets : true,
			autofocus : true
		});
		dFileEdit.editorInitiated = true;
	},

	/**
	 * get the accessKey of a temp file
	 */
	getUploadKey : function() {
		ajaxPostJsonAuthc(dURIs.tmpFileAccessKeyURI, null,
				dFileEdit.requestFileUpload, defaultErrorFunc, false);
	},

	/**
	 * request the remote host to upload target file to webapp
	 * 
	 * @param data
	 */
	requestFileUpload : function(data) {
		dFileEdit.accessKey = data.accessKey;
		var dataObj = {
			accessKey : dFileEdit.accessKey
		};
		ajaxPostJsonAuthc(dURIs.remoteFilesURI + "/" + dFileEdit.remoteFileId
				+ "/pull", dataObj, dFileEdit.fileUploadSuccess,
				defaultErrorFunc, true);
	},

	fileUploadSuccess : function() {
		dFileEdit.downCounter = 30;
		dFileEdit.requestFileObj(dFileEdit.accessKey);
	},

	/**
	 * request the uploaded file content
	 * 
	 * @param accessKey
	 */
	requestFileObj : function(accessKey) {
		ajaxGetJsonAuthc(dURIs.tmpFilesURI + "/" + accessKey, null,
				dFileEdit.requestFileContent, dFileEdit.requestFileObjFail);
		dFileEdit.downCounter = dFileEdit.downCounter - 1;
	},

	requestFileObjFail : function() {
		if (dFileEdit.downCounter > 0) {
			setTimeout(function() {
				dFileEdit.requestFileObj(dFileEdit.accessKey);
			}, 2000);
		} else {
			showError("获取文件失败");
		}
	},

	/**
	 * 读取文件内容
	 * 
	 * @param data
	 */
	requestFileContent : function(data) {
		var fileKey = data.fileKey;
		ajaxGetJsonAuthc(dURIs.fileContentURI + "/" + fileKey, null,
				dFileEdit.requestFileContentSuccess, defaultErrorFunc);
	},

	requestFileContentSuccess : function(data) {
		var content = data.content;
		editor.setValue(content);
	},

	saveContentToFile : function(content, callback) {
		var dataObj = {
			fileContent : content
		};
		ajaxPostJsonAuthc(dURIs.fileContentURI, dataObj,
				(verifyParam(callback) ? callback : defaultSuccessFunc),
				defaultErrorFunc, false);
	},

	saveFileToRemote : function(data) {
		var dataObj = {
			fileKey : data.fileKey
		};
		ajaxPostJsonAuthc(dURIs.remoteFilesURI + "/" + dFileEdit.remoteFileId
				+ "/push", dataObj, defaultSuccessFunc,
				defaultErrorFunc, true);
	}

};

$(document).ready(function() {
});