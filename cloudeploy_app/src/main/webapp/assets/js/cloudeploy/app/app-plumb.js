var appInstance = null;
var connectorPaintStyle = {
	lineWidth : 4,
	strokeStyle : "#61B7CF",
	joinstyle : "round",
	outlineColor : "white",
	outlineWidth : 2
};
var connectorHoverStyle = {
	lineWidth : 4,
	strokeStyle : "#216477",
	outlineWidth : 2,
	outlineColor : "white"
};
var endpointHoverStyle = {
	fillStyle : "#216477",
	strokeStyle : "#216477"
};
var sourceEndpoint = {
	endpoint : "Dot",
	paintStyle : {
		strokeStyle : "#7AB02C",
		fillStyle : "transparent",
		radius : 7,
		lineWidth : 3
	},
	isSource : true,
	connector : [ "Flowchart", {
		stub : [ 40, 60 ],
		gap : 10,
		cornerRadius : 5,
		alwaysRespectStubs : true
	} ],
	connectorStyle : connectorPaintStyle,
	hoverPaintStyle : endpointHoverStyle,
	connectorHoverStyle : connectorHoverStyle,
	dragOptions : {},
	overlays : [ [ "Label", {
		location : [ 0.5, 1.5 ],
		label : "",
		cssClass : "endpointSourceLabel"
	} ] ],
	maxConnections : -1
};
var targetEndpoint = {
	endpoint : "Dot",
	paintStyle : {
		fillStyle : "#7AB02C",
		radius : 11
	},
	hoverPaintStyle : endpointHoverStyle,
	maxConnections : -1,
	dropOptions : {
		hoverClass : "hover",
		activeClass : "active"
	},
	isTarget : true,
	overlays : [ [ "Label", {
		location : [ 0.5, -0.5 ],
		label : "",
		cssClass : "endpointTargetLabel"
	} ] ]
};

jsPlumb.ready(function() {
	var instance = jsPlumb.getInstance({
		DragOptions : {
			cursor : 'pointer',
			zIndex : 2000
		},
		ConnectionOverlays : [ [ "Arrow", {
			location : 1
		} ] ],
		Container : "graph-panel"
	});
	instance.bind("connection", function(connInfo, originalEvent) {
		var conn = connInfo.connection;
		conn.bind("dblclick", function(connection, originalEvent) {
			if (confirm("delete edge?")) {
				jsPlumb.detach(conn);
			}
		});
	});

	appInstance = instance;
});