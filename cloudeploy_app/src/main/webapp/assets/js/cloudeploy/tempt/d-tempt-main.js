var dMain = {
	currApp:null,
	init : function() {
		dMain.initAppList();
	},

	/**
	 * initiate the host list HTML
	 */
	initAppList : function() {
		ajaxGetJsonAuthc(dURIs.templateURI, null, dMain.initAppListUIAndEvents);
	},
	initAppListUIAndEvents : function(data) {
		var apps = data;
		var container = $("#d-app-list ul");
		container.html(dHtml.genAppListHtml(apps));
	}
};
/**
 * app-container-template relation acitons
 */
var dPath = {
	showContianers : function(app,appId){
		$(".app-item").removeClass("active");
		$(app).addClass("active");
		ajaxGetJsonAuthc(dURIs.templateURI+"/"+appId, null, dPath.refreshContainers);
	},
	refreshContainers : function(data){
		currApp = data;
		$("#d-container-list").removeClass('hide');
		$("#d-tempt-list").removeClass('col-sm-10').addClass('col-sm-8');
		var container = $("#d-container-list ul");
		container.html(dHtml.genContainerHtml(data.containers));
		var first = $(".container-item");
		if(first.length >0){
			first[0].click();
		}
	},
	showTemplates : function(container,index){
		$(".container-item").removeClass("active");
		$(container).addClass("active");
		dHtml.genTemplateHtml(currApp.containers[index].templates);
	}
};
var dHtml = {
	/**
	 * generate host list HTML
	 * 
	 * @param hosts
	 * @returns {String}
	 */
	genAppListHtml : function(apps) {
		var html = '';
		for ( var i in apps) {
			var app = apps[i];
			html += '<li><a class="app-item list-group-item" href="javascript:void(0)" onclick="dPath.showContianers(this,'
				+ app.id + ')">'
					+ app.name
					+'</a></li>';
		}
		return html;
	},
	genContainerHtml:function(containers){
		var html = '';
		for ( var i in containers) {
			var container = containers[i];
			html += '<li><a class="container-item list-group-item" href="javascript:void(0);" onclick="dPath.showTemplates(this,'
				+ i + ')">'
					+ container.name
					+'</a></li>';
		}
		return html;
	},
	genTemplateHtml:function(templates){
		var html = '';
		for ( var i in templates) {
			var file = templates[i];
			var key = file.source;
			var name = file.target;
			html += '<tr data-id="' + file.id + '"><td class="hide"></td><td >'
					+ name + '</td><td>'
					+ getFormatDateFromLong(file.createdAt) + '</td><td>'
					+ getFormatDateFromLong(file.updatedAt) + '</td><td>';
			html += '<a key = "'+key
			+'" name="'+name
			+'" style="margin-right:15px;" href="javascript:void(0);" onclick="dHtml.scanFileClick(this)"><i class="fa fa-eye"></i> view</a>'
			html += '</td></tr>';
		}
		$("#fileTable tbody").html(html);
	},
	scanFileClick:function(temp){
		var filekey = $(temp).attr('key');
		var filename = $(temp).attr('name');
		var sources = dURIs.filesURI+"/content/"+filekey;
		$('#scanFileModal').modal('show');
		$("#scanFileName").html(filename);
		ajaxGetJsonAuthc(sources, null, dHtml.showContext);
	},
	showContext :function(data){
		$("#scanFileBody").html(data.content);
	}
};

$(document).ready(function() {
	dMain.init();
});