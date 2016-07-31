<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>软件管理 | 修改配置</title>
</head>
<body>
	<div id="d-main-content">
		<div class="panel panel-default">
			<div class="panel-heading">
				软件配置参数
			</div>
			<div class="panel-body">
				<form class="form-horizontal col-sm-12" role="form">
	
					<div class="form-group">
						<label for="softwareName" class="col-sm-2 control-label">软件名称</label>
						<div class="col-sm-5">
							<span id="softwareName">
							</span>
						</div>
					</div>
					
					<div class="form-group">
						<label for="alias" class="col-sm-2 control-label">安装别名</label>
						<div class="col-sm-5">
							<span id="alias">
							</span>
						</div>
					</div>
					<div class="form-group">
						<label for="params-tbl" class="col-sm-2 control-label">参数</label>
						<div class="col-sm-10">
							<table id="params-tbl"
								class="table table-bordered table-condensed">
								<thead>
									<tr>
										<th style="width: 30%;">属性</th>
										<th style="width: 30%;">值</th>
										<th>描述</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
	
					<div class="form-group">
						<div class="col-sm-offset-2 col-sm-10">
							<a id="save-btn" href="#" class="btn btn-default">保存修改</a>
						</div>
					</div>
				</form>
	
			</div>
		</div>
		<script src="<c:url value='/js/cloudeploy/resource/d-software-instance-config.js' />"></script>
	</div>
</body>
</html>