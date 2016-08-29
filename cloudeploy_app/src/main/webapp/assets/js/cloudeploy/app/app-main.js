var appMain = {
	statusMap : new Map(),
	init : function() {
		appMain.initData();
		appMain.initControlEvent();
		$("#app-list-btn").click();
		calendar();
	},
	initData : function() {
		this.statusMap.put("CREATED", "新建");
		this.statusMap.put("DEPLOYED", "已部署");
		this.statusMap.put("MODIFIED", "已修改");
		this.statusMap.put("新建", "CREATED");
		this.statusMap.put("已部署", "DEPLOYED");
		this.statusMap.put("已修改", "MODIFIED");

		this.statusMap.put("RUNNING", "运行");
		this.statusMap.put("STOPPED", "停止");
		this.statusMap.put("ERROR", "故障");
		this.statusMap.put("运行", "RUNNING");
		this.statusMap.put("故障", "ERROR");
	},

	initControlEvent : function() {
		$("#app-list-btn").click(function() {
			appMain.cleanState();
			$(this).addClass("active");
			loadPage(dURIs.viewsURI.appList, null);
		});
		$("#app-orchestration-btn").click(function() {
			appMain.cleanState();
			$(this).addClass("active");
			loadPage(dURIs.viewsURI.appOrchestration, null);
		});
		$("#domain-btn").click(function() {
			appMain.cleanState();
			$(this).addClass("active");
			loadPage(dURIs.viewsURI.domains, null);
		});
		$("#service-list-btn").click(function(){
			appMain.cleanState();
			$(this).addClass("active");
			loadPage(dURIs.viewsURI.serviceList, null);
		});
		$("#config-btn").click(function(){
			appMain.cleanState();
			$(this).addClass("active");
			loadPage(dURIs.viewsURI.configManager, null);
		});
		$("#tempt-btn").click(function() {
			appMain.cleanState();
			$(this).addClass("active");
			loadPage(dURIs.viewsURI.temptManager, null);
		});
	},
	cleanState: function(){
		$("#sidebar-list li").each(function(){
			$(this).removeClass("active");
		});
	}
};
function calendar() {
    var date = new Date();
    var monthStr = new Array("Jan", "Feb", "March", "April", "May", "June", "July", "August", "Sep", "Oct", "Nov", "Dec");
    $("#avartar-top").html(monthStr[date.getMonth()]);
    $("#avartar-bottom").html(date.getDate());
}
$(document).ready(function() {
	appMain.init();
});