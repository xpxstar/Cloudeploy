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
		<div class="col-sm-9">
			<table id="domain-list"
				style="margin-bottom: 0px; border-bottom: 0px;"
				class="table table-bordered table-condensed">
				<thead>
					<tr>
						<th style="width: 20%;">域名</th>
						<th style="width: 20%;">IP地址</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<div class="col-sm-12">
			<a class="btn btn-default"
				href="javascript:domainList.bindDomainClick()"><i
				class="fa fa-plus"></i> 绑定新域名</a>
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
						<h4 class="modal-title" id="myModalLabel">域名绑定</h4>
					</div>
					<div class="modal-body">
						<div style="width: 80%;">
							<div class="form-group">
								<label for="domainName">域名</label> <input type="text"
									class="form-control" name="domainName" id="domainName"
									placeholder="域名">
							</div>
						</div>
						<div style="width: 80%;">
							<div class="form-group">
								<label for="domainIP">IP地址</label> <input type="text"
									class="form-control" name="domainIP" id="domainIP"
									placeholder="IP地址">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						<button type="button" id="save-domain-btn" class="btn btn-primary">保存</button>
					</div>
				</div>
			</div>
		</div>
		<script src="<c:url value='/js/cloudeploy/domain/domain-list.js' />"></script>
	</div>
</body>
</html>