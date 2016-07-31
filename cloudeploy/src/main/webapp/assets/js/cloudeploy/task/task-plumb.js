var taskInstance = null;
var taskConnectorPaintStyle = {
	lineWidth : 4,
	strokeStyle : "#61B7CF",
	joinstyle : "round",
	outlineColor : "white",
	outlineWidth : 2
};
var taskConnectorHoverStyle = {
	lineWidth : 4,
	strokeStyle : "#216477",
	outlineWidth : 2,
	outlineColor : "white"
};
var taskEndpointHoverStyle = {
	fillStyle : "#216477",
	strokeStyle : "#216477"
};
var taskSourceEndpoint = {
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
	connectorStyle : taskConnectorPaintStyle,
	hoverPaintStyle : taskEndpointHoverStyle,
	connectorHoverStyle : taskConnectorHoverStyle,
	dragOptions : {},
	overlays : [ [ "Label", {
		location : [ 0.5, 1.5 ],
		label : "",
		cssClass : "endpointSourceLabel"
	} ] ],
	maxConnections : -1
};
var taskTargetEndpoint = {
	endpoint : "Dot",
	paintStyle : {
		fillStyle : "#7AB02C",
		radius : 11
	},
	hoverPaintStyle : taskEndpointHoverStyle,
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
	var overlayClick = function(overlay) {
		$("#labelModal").modal('show');
		var currentLabel = overlay.getLabel();
		$("#labelModal #linkTypeoption:selected").removeAttr('selected');
		$("#labelModal #linkType option[value='" + currentLabel + "']").attr(
				'selected', 'selected');
		$("#labelModal #save-label-btn").unbind('click');
		$("#labelModal #save-label-btn").bind('click', function() {
			var type = $("#labelModal #linkType option:selected").val();
			overlay.setLabel(type);
			$("#labelModal").modal('hide');
		});
	};
	var instance = jsPlumb.getInstance({
		DragOptions : {
			cursor : 'pointer',
			zIndex : 2000
		},
		ConnectionOverlays : [ [ "Arrow", {
			location : 1
		} ], [ "Label", {
			location : 0.1,
			id : "label",
			cssClass : "aLabel",
			events : {
				click : function(overlay, originalEvent) {
					overlayClick(overlay);
				}
			}
		} ] ],//连接事件
		Container : "graph-panel"  //对应的div容器的id
	});
	instance.bind("connection", function(connInfo, originalEvent) {
		var conn = connInfo.connection;
		conn.getOverlay("label").setLabel("before");
		conn.bind("dblclick", function(connection, originalEvent) {
			if (confirm("删除连接?")) {
				jsPlumb.detach(conn);
			}
		});
	});

	taskInstance = instance;
});