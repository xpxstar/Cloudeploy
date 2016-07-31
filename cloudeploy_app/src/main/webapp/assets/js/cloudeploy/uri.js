var rootURI = function() {
	return "/cloudapp";
};

var dURIs = {
	appURI : rootURI() + "/v2/applications",
	appInstanceURI : rootURI() + "/v2/applications/containers/instances",
	componentURI : rootURI() + "/v2/components",
	componentTypeURI : rootURI() + "/v2/components/types",
	domainURI : rootURI() + "/v2/domains",
	hostURI : rootURI() + "/v2/hosts",
	customFilesURI : rootURI() + "/v2/resources/files/custom",
	filesURI : rootURI() + "/v2/files",
	templateURI : rootURI() + "/v2/templates",
	viewsURI : {
		appList : rootURI() + "/v2/views/applications/list",
		appOrchestration : rootURI() + "/v2/views/applications/panel",
		domains : rootURI() + "/v2/views/domains",
		serviceList : rootURI() + "/v2/views/service",
		temptManager : rootURI() + "/v2/views/templates",
		configManager: rootURI() + "/v2/views/config"
	},
	swfs : rootURI() + "/swf",
};