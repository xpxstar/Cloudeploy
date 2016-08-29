<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Cloudeploy</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="<c:url value='/css/bootstrap/bootstrap.min.css' />"
	type="text/css" rel="stylesheet">
<link
	href="<c:url value='/css/font-awesome-4.2.0/css/font-awesome.min.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/cloudeploy.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/new/dashboard.css' />" type="text/css" rel="stylesheet" >
<link href="<c:url value='/css/cloudeploy/app-list.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/app-panel.css' />"
	type="text/css" rel="stylesheet">
	<link href="<c:url value='/css/cloudeploy/template.css' />" type="text/css" rel="stylesheet">
	<link href="<c:url value='/css/uploadify/uploadify.css' />" type="text/css" rel="stylesheet">
	<link href="<c:url value='/css/new/style.css'/>" type="text/css"  rel="stylesheet"/>
	<%@ include file="../share/head.jsp" %>
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
		<div class="navigation">
		<div class="profile">
			<div class="avartar">
				<div id="avartar-top"></div>
				<div id="avartar-bottom"></div>
			</div>
		
			<p class="user-name">
				<a href="javascript:void(0)">Tom</a>
			</p>
			<div class="user-link">
				<a title="index"><span class="glyphicon glyphicon-home"></span></a>
				<a href="#" title="dashboard"><span class="glyphicon glyphicon-dashboard"></span></a>
				<a title="help"><span class="glyphicon glyphicon-book"></span></a>
				<a href="#" title="logout"><span class="glyphicon glyphicon-off"></span></a>
			</div>
		</div>
		<div class="sidebar">
			<ul id="sidebar-list" class="nav nav-list">
				<li id="app-list-btn"><a><span class="glyphicon glyphicon-list-alt cool-orange"></span><span class="name">Applications</span><span class="title">应用列表</span></a><div class="cool-border"></div></li>
				<li id="app-orchestration-btn"><a><span class="glyphicon glyphicon-retweet cool-cyan"></span><span class="name">Orchestration</span><span class="title">模型编排</span></a><div class="cool-border"></div></li>
				<!-- <li id="domain-btn"><a><span class="glyphicon glyphicon-cloud-upload cool-green"></span><span class="name">Domain</span><span class="title">域名管理</span></a><div class="cool-border"></div></li>
				 --><li id="service-list-btn"><a><span class="glyphicon glyphicon-cloud cool-blue"></span><span class="name">Services</span><span class="title">服务状态</span></a><div class="cool-border"></div></li>
				<li id="config-btn"><a><span class="glyphicon glyphicon-cog cool-purple"></span><span class="name">Configure</span><span class="title">配置管理</span></a><div class="cool-border"></div></li>
				<!-- <li id="config-btn"><a><span class="glyphicon glyphicon-th cool-orange"></span><span class="name">Apptype</span><span class="title">应用类型</span></a><div class="cool-border"></div></li>
				 --><li id="tempt-btn"><a><span class="glyphicon glyphicon-road cool-blue"></span><span class="name">Templates</span><span class="title">模板管理</span></a><div class="cool-border"></div></li>
			
			</ul>
			<ul class="nav nav-list" style="margin-top:50px">
			</ul>
		</div>
		</div>
		<div class="d-main-content"></div>
	</div>
</body>
</html>