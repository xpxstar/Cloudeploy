<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Configure</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div id="d-main-content">
	<div class="intro">
			<h1>Configure&nbsp;Manager</h1>
				<p class="lead" style="margin-top:10px">
					<em>Configure&nbsp;</em>of an application is in form of key-value pair. Here you can scan and update configure parameters of a component.
				</p>
		</div>
	<div class ="row content-board">
		<div class="d-app-list col-sm-2">
			<div class="itemlist col-sm-12">
				<h4><i class="fa fa-calendar"></i> applications：</h4>
				<div id="d-app-list-buttons" class="d-app-list-buttons col-sm-12">
				</div>
				</div>
			</div>
		<div class="col-sm-4" >
		</div>
		<div class="col-sm-6" >
			<button type="button" class="btn btn-default param-type active"  onclick="javascript:configManager.propertyOperation(this)">Properties</button>
			<button type="button" class="btn btn-default param-type" onclick="javascript:configManager.attributeOperation(this)">Attributes</button>
		</div>
		
		<div class="d-config-main-content col-sm-9">
			<script src="<c:url value='/js/cloudeploy/app/app-config.js' />"></script>
		</div>
	</div>
</div>
</body>