<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>域名绑定</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div id="d-main-content">
	<div class ="row">
	<div class="col-sm-4" >
	</div>
	<div class="col-sm-6" >
		<button type="button" class="btn btn-default"  onclick="javascript:configManager.propertyOperation()">Properties</button>
		<button type="button" class="btn btn-info" onclick="javascript:configManager.attributeOperation()">Attributes</button>
	</div>
	</div>
	<div class ="row">
	<div class="d-app-list col-sm-2">
		<div class="col-sm-12">
			<h4><i class="fa fa-desktop"></i> 应用列表：</h4>
		</div>
		<div class="d-app-list-buttons col-sm-12">
		</div>
	</div>
	<div class="d-config-main-content col-sm-9">
		<script src="<c:url value='/js/cloudeploy/app/app-config.js' />"></script>
	</div>
	</div>
</div>
</body>