<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>应用列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
		<div class="col-sm-9">
			<table style="margin-bottom: 0px; border-bottom: 0px;"
				class="table table-bordered table-condensed">
				<thead>
					<tr>
						<th style="width: 20%;">应用名称</th>
						<th style="width: 20%;">应用状态</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div style="margin-top: 0px;" class="panel-group" id="app-list"
				role="tablist" aria-multiselectable="true">
				
			</div>
		</div>
		<script src="<c:url value='/js/cloudeploy/app/app-list.js' />"></script>
	</div>
</body>
</html>