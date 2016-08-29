<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Applications</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
		<div class="intro">
			<h1>Applications&nbsp;Manager</h1>
				<p class="lead" style="margin-top:10px">
					An <em>application&nbsp;</em> is a task of deploying or configuring middleware components, including orchestration and properties. Applications Manager shows application list for user to view details and do other actions.
				</p>
		</div>
		
   		<div class="content-board">
			<table style="margin-bottom: 0px; border-bottom: 0px;"
				class="table table-bordered table-condensed deploy-table">
				<thead>
					<tr>
						<th style="width: 20%;">Name</th>
						<th style="width: 20%;">Status</th>
						<th>Action</th>
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