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
	
	getAction : function(){
		return this.action;
	},

	setAction : function(actionId) {
		for ( var i in this.component.actions) {
			if (this.component.actions[i].id == actionId) {
				this.action = this.component.actions[i];
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

var FileComponentNode = jClass(ComponentNode, {
	init : function(id, component, hosts, file) {
		this.base.init.apply(this, [ id, component, hosts ]);
		this.file = file;
	},

	getFile : function() {
		return this.file;
	},
	
	setFile : function(file){
		this.file = file;
	}
});

var ExecComponentNode = jClass(ComponentNode, {
	init : function(id, component, hosts, exec) {
		this.base.init.apply(this, [ id, component, hosts ]);
		this.exec = exec;
	},

	getExec : function() {
		return this.exec;
	},
	
	setExec : function(exec){
		this.exec = exec;
	}
});

var MySQLComponentNode = jClass(ComponentNode, {
	init : function(id, component, hosts, sqlFile) {
		this.base.init.apply(this, [ id, component, hosts ]);
		this.sqlFile = sqlFile;
	},

	getSqlFile : function() {
		return this.sqlFile;
	},
	
	setSqlFile : function(sqlFile){
		this.sqlFile = sqlFile;
	}
});

var TomcatComponentNode = jClass(ComponentNode, {
	init : function(id, component, hosts, warFile) {
		this.base.init.apply(this, [ id, component, hosts ]);
		this.warFile = warFile;
	},

	getWarFile : function() {
		return this.warFile;
	},
	
	setWarFile : function(warFile){
		this.warFile = warFile;
	}
});

var OperationNode = jClass({
	init : function(id, operation, hosts) {
		this.id = id;
		this.operation = operation;
		this.params = new Array();
		this.hosts = new Array();
		if (verifyParam(operation) && operation.params.length > 0) {
			for ( var i in operation.params) {
				var para = operation.params[i];
				this.params.push({
					key : para.key,
					value : para.value
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

	getOperation : function() {
		return this.operation;
	},

	setParams : function(params) {
		this.params = params;
	},

	getHosts : function() {
		return this.hosts;
	},

	setHosts : function(hosts) {
		this.hosts = hosts;
	},
	getAction : function(){
		return null;
	}
});