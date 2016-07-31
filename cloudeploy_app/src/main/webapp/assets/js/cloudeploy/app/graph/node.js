var ComponentNode = jClass({
	init : function(id, component, hosts) {
		this.id = id;
		this.component = component;
		this.action = null;
		this.params = new Array();
		this.hosts = new Array();
		if (verifyParam(component) && component.actions.length > 0) {
			this.action = component.actions[0];
			for ( var i in component.actions[0].params) {
				var p = component.actions[0].params[i];
				this.params.push({
					key : p.paramKey,
					value : p.defaultValue
				});
			}
		}
		if (verifyParam(hosts) && hosts.length > 0) {
			for ( var i in hosts) {
				this.hosts.push(hosts[i]);
			}
		}
	},

	getId : function() {
		return this.id;
	},

	getComponent : function() {
		return this.component;
	},

	getAction : function() {
		return this.action;
	},

	setAction : function(actionId) {
		for ( var i in this.component.actions) {
			if (this.component.actions[i].id == actionId) {
				this.action = this.component.actions[i];
				this.params = [];
				for ( var i in this.action.params) {
					var p = this.action.params[i];
					this.params.push({
						key : p.paramKey,
						value : p.defaultValue
					});
				}
			}
		}
	},

	setParams : function(params) {
		this.params = params;
	},

	getHosts : function() {
		return this.hosts;
	},

	setHosts : function(hosts) {
		this.hosts = hosts;
	}
});

var ContainerNode = jClass(ComponentNode, {
	init : function(id, component, hosts, name, port, initCount, maxCount,
			status, containerId) {
		this.base.init.apply(this, [ id, component, hosts ]);
		this.name = name;
		this.port = port;
		this.initCount = initCount;
		this.maxCount = maxCount;
		this.status = status;
		this.containerId = verifyParam(containerId) ? containerId : -1;
		this.templates = new Array();
		this.attributes = new Array();
	},
	
	getTemplates: function(){
		return this.templates;
	},
	
	setTemplates: function(templates){
		this.templates = templates;
	},
	
	getAttribtues: function(){
		return this.attributes;
	},
	
	setAttributes: function(attributes){
		this.attributes = attributes;
	},

	getName : function() {
		return this.name;
	},

	setName : function(name) {
		this.name = name;
	},

	getPort : function() {
		return this.port;
	},

	setPort : function(port) {
		this.port = port;
	},

	getInitCount : function() {
		return this.initCount;
	},

	setInitCount : function(initCount) {
		this.initCount = initCount;
	},

	getMaxCount : function() {
		return this.maxCount;
	},

	setMaxCount : function(maxCount) {
		this.maxCount = maxCount;
	},

	getStatus : function() {
		return this.status;
	},

	setStatus : function(status) {
		this.status = status;
	},

	getContainerId : function() {
		return this.containerId;
	},

	setContainerId : function(containerId) {
		this.containerId = containerId;
	}
});

var ContainerInstanceNode = jClass(ComponentNode, {
	init : function(id, name, port, status, instanceId, containerId) {
		this.base.init.apply(this, [ id, null, null ]);
		this.name = name;
		this.port = port;
		this.status = status;
		this.instanceId = instanceId;
		this.containerId = verifyParam(containerId) ? containerId : -1;
	},

	getName : function() {
		return this.name;
	},

	setName : function(name) {
		this.name = name;
	},

	getPort : function() {
		return this.port;
	},

	setPort : function(port) {
		this.port = port;
	},

	getStatus : function() {
		return this.status;
	},

	setStatus : function(status) {
		this.status = status;
	},
	
	getInstanceId : function() {
		return this.instanceId;
	},

	setInstanceId : function(instanceId) {
		this.instanceId = instanceId;
	},

	getContainerId : function() {
		return this.containerId;
	},

	setContainerId : function(containerId) {
		this.containerId = containerId;
	}
});