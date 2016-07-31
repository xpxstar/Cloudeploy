<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>应用管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="<c:url value='/css/bootstrap/bootstrap.min.css' />"
	type="text/css" rel="stylesheet">
<link
	href="<c:url value='/css/font-awesome-4.2.0/css/font-awesome.min.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/cloudeploy.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/app-list.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/app-panel.css' />"
	type="text/css" rel="stylesheet">
	<link href="<c:url value='/css/cloudeploy/template.css' />" type="text/css" rel="stylesheet">
	<link href="<c:url value='/css/uploadify/uploadify.css' />" type="text/css" rel="stylesheet">
	
</head>
<body>
	<script src="<c:url value='/js/jquery/jquery-1.11.0.js' />"></script>
	<script src="<c:url value='/js/jquery/json/jquery.json-2.4.min.js' />"></script>
	<script
		src="<c:url value='/js/jquery/dateFormat/jquery.dateFormat.js' />"></script>
	<script src="<c:url value='/js/bootstrap/bootstrap.min.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/uri.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/map.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/common.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/app/app-main.js' />"></script>
	<div class="d-main">
		<div class="btn-group" role="group" aria-label="nav-btn-group"
			style="margin: 15px;">
			<button type="button" class="btn btn-default" id="app-list-btn">应用列表</button>
			<button type="button" class="btn btn-default"
				id="app-orchestration-btn">模型编排</button>
			<button type="button" class="btn btn-default" id="domain-btn">域名管理</button>
			<button type="button" class="btn btn-default" id="service-list-btn">服务状态</button>
			<button type="button" class="btn btn-default" id="config-btn">配置管理</button>
			<button type="button" class="btn btn-default" id="config-btn">应用类型</button>
			<button type="button" class="btn btn-default" id="tempt-btn">模板管理</button>
		</div>
		<div class="d-main-content"></div>
	</div>
</body>
</html>