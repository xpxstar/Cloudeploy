<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>软件管理 | 安装软件</title>
</head>
<body>
	<div id="d-main-content">
	<div class="panel panel-default">
		<div class="panel-heading">
			安装软件向导
			<div class="pull-right">
				<a href="javascript:dSoftwareInstall.wizardBtnClick(0)" class="wizard-btn previous"><span class="glyphicon glyphicon-circle-arrow-left"></span></a>
				<a href="javascript:dSoftwareInstall.wizardBtnClick(1)" class="wizard-btn next active"><span class="glyphicon glyphicon-circle-arrow-right"></span></a>
			</div>
		</div>
		<div class="panel-body">
			<form id="install-form" class="form-horizontal col-sm-12" role="form">
				<div class="wizard-sec active" data-seq="1">
					<div class="form-group">
						<label for="softwareType" class="col-sm-2 control-label">选择软件类型</label>
						<div class="col-sm-5">
							<select name="softwareType" class="form-control">
							</select>
						</div>
					</div>
	
					<div class="form-group">
						<label for="softwareName" class="col-sm-2 control-label">选择软件</label>
						<div class="col-sm-5">
							<select name="softwareName" class="form-control">
							</select>
							<input type="hidden" name="actionId">
						</div>
					</div>
					
					<div class="form-group">
						<label for="installName" class="col-sm-2 control-label">别名</label>
						<div class="col-sm-5">
							<input type="text" name="installName" class="form-control" />(用于标识这次安装)
						</div>
					</div>
				</div>
				<div class="wizard-sec" data-seq="2">
					<div class="form-group">
						<label for="initParams" class="col-sm-2 control-label">初始化参数</label>
						<div class="col-sm-10">
							<table id="initParams"
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
							<a id="install-btn" href="#" class="btn btn-default">开始安装</a>
						</div>
					</div>
				</div>
			</form>

		</div>
	</div>
	<script src="<c:url value='/js/cloudeploy/map.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/resource/d-software-instance-install.js' />"></script>
	</div>
</body>
</html>