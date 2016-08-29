<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Domain binding</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
		<div class="intro">
			<h1>Applications&nbsp;Manager</h1>
				<p class="lead" style="margin-top:10px">
					<em>Applications&nbsp;</em>is a tasks list .It shows schemes user design before. 
				</p>
		</div>
		<div class="content-board">
			<table id="domain-list"
				style="margin-bottom: 0px; border-bottom: 0px;"
				class="table table-bordered table-condensed  deploy-table">
				<thead>
					<tr>
						<th style="width: 20%;">domain</th>
						<th style="width: 20%;">IP</th>
						<th>action</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<div class="col-sm-12">
			<a class="btn btn-default"
				href="javascript:domainList.bindDomainClick()"><i
				class="fa fa-plus"></i> new domain</a>
		</div>
		<div style="margin-top: 200px;" class="modal fade"
			id="bindDomainModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">binding</h4>
					</div>
					<div class="modal-body">
						<div style="width: 80%;">
							<div class="form-group">
								<label for="domainName">domain</label> <input type="text"
									class="form-control" name="domainName" id="domainName"
									placeholder="domain">
							</div>
						</div>
						<div style="width: 80%;">
							<div class="form-group">
								<label for="domainIP">IP</label> <input type="text"
									class="form-control" name="domainIP" id="domainIP"
									placeholder="IP">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">cancel</button>
						<button type="button" id="save-domain-btn" class="btn btn-primary">save</button>
					</div>
				</div>
			</div>
		</div>
		<script src="<c:url value='/js/cloudeploy/domain/domain-list.js' />"></script>
	</div>
</body>
</html>