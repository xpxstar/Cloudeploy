<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>自动化运维平台</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="<c:url value='/css/bootstrap/bootstrap.min.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/font-awesome-4.2.0/css/font-awesome.min.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/bootstrap/bootstrap.custom.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/cloudeploy.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/task-panel.css' />" type="text/css" rel="stylesheet">
</head>
<body>
	<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<button class="navbar-toggle collapsed" type="button"
					data-toggle="collapse" data-target="#depoly-navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="/cloudeploy"> <span
					style="font-size: 20px;">Cloudeploy</span>
				</a>
			</div>
			
			<!-- Collect the nav links, forms, and other content for toggling -->
		    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
	      		<ul class="nav navbar-nav">
	        		<li class="active"><a href="/cloudeploy/views/task/home">任务管理</a></li>
	        		<li><a href="/cloudeploy/views/resource/home">资源管理</a></li>
	        		<li><a href="#">主机管理</a></li>
	        		<li><a href="/cloudeploy/views/component/home">组件管理</a></li>
	        		<li ><a href="/cloudeploy/views/action/home">活动管理</a></li>
	      		</ul>
    		</div><!-- /.navbar-collapse -->
		</div>
	</nav>
	<div class="d-main row" >
		<div class="d-main-content"></div>
	</div>
	<script src="<c:url value='/js/jquery/jquery-1.11.0.js' />"></script>
	<script src="<c:url value='/js/jquery/jquery-ui-1.11.2/jquery-ui.js' />"></script>
	<script src="<c:url value='/js/jquery/json/jquery.json-2.4.min.js' />"></script>
	<script src="<c:url value='/js/jquery/dateFormat/jquery.dateFormat.js' />"></script>
	<script src="<c:url value='/js/bootstrap/bootstrap.min.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/map.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/uri.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/common.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/task/d-task-main.js' />"></script>
</body>
</html>