var rootURI = function() {
	return "/cloudeploy";
};

var dURIs = {
	componentURI : rootURI() + "/v2/components",
	componentTypeURI : rootURI() + "/v2/components/types",
	operationURI : rootURI() + "/v2/operations",
	actionURI : rootURI() + "/v2/actions",
	customFilesURI : rootURI() + "/v2/resources/files/custom",
	execsURI : rootURI() + "/v2/resources/execs",
	executionsURI : rootURI() + "/v2/resources/execs/executions",
	filesURI : rootURI() + "/v2/files",
	fileContentURI : rootURI() + "/v2/files/content",
	tmpFilesURI : rootURI() + "/v2/files/tmp",
	tmpFileAccessKeyURI : rootURI() + "/v2/files/tmp/accesskey",
	tmpFileUploadURI : rootURI() + "/v2/files/tmp/upload",
	hostURI : rootURI() + "/v2/hosts",
	remoteFilesURI : rootURI() + "/v2/resources/files/remote",
	softwareInstancesURI : rootURI() + "/v2/resources/softwares/instances",
	swfs : rootURI() + "/swf",
	taskPanelURI : rootURI() + "/views/task/panel",
	taskURI : rootURI() + "/v2/tasks",
	views : {
		customFileList : rootURI() + "/views/resource/custom/file/list",
		exec : rootURI() + "/views/resource/exec",
		execList : rootURI() + "/views/resource/exec/list",
		softwareInstanceList : rootURI()
				+ "/views/resource/software/instance/list",
		softwareInstanceInstall : rootURI()
				+ "/views/resource/software/instance/install",
		softwareInstanceConfig : rootURI()
				+ "/views/resource/software/instance/config",
		softwareActionParams : rootURI()
				+ "/views/resource/software/instance/params",
		remoteFileList : rootURI() + "/views/resource/remote/file/list",
		remoteFileEdit : rootURI() + "/views/resource/remote/file/edit",
		taskSource : rootURI() + "/views/task/source"
	}
};