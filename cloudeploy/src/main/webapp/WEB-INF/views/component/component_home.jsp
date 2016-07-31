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
<link href="<c:url value='/css/bootstrap/bwizard.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/font-awesome-4.2.0/css/font-awesome.min.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/bootstrap/bootstrap.custom.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/uploadify/uploadify.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/codemirror/codemirror.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/cloudeploy.css' />" type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/component.css' />" type="text/css" rel="stylesheet">
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
	        		<li><a href="/cloudeploy/views/task/home">任务管理</a></li>
	        		<li><a href="/cloudeploy/views/resource/home">资源管理</a></li>
	        		<li><a href="#">主机管理</a></li>
	        		<li class="active"><a href="/cloudeploy/views/component/home">组件管理</a></li>
	        		<li ><a href="/cloudeploy/views/action/home">活动管理</a></li>
	      		</ul>
    		</div><!-- /.navbar-collapse -->
		</div>
	</nav>
	<div class="deploy-bar"></div>
	<div class="d-main row" >
		

		<div class="col-sm-10 col-md-offset-2">
			<div class="d-main-menu">
					<ul class="nav nav-tabs">
						<li role="presentation" id="sys_component" class="active">
							<a href="#" >系统组件</a>
						</li>
						<li role="presentation" id="cus_component">
							<a  href="#">用户组件</a>
						</li>
						<li>
							<botton href="#" class="btn btn-info hide"  id="add_componenttype">添加组件类型</botton>
						</li>
						<li>
							<botton href="#" class="btn btn-success hide"  id="add_component">添加组件</botton>
						</li>
						
					</ul>
			</div>
			<div class="d-main-content">
				<div id="group-list" class="col-sm-3">
					<!-- panel group for operations component list -->
					<div id="panel-group-operations" class="panel-group" role="tablist"
					aria-multiselectable="true"></div>
				</div>
				<div id="group-list" class="col-sm-8">
					<!-- node details start -->
					<div id="detailModal" class="hide" comid="0">
						<div>
							<div class="modal-content">
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">operation name</h4>
								</div>
								<div class="modal-body">
									<form class="form-horizontal col-sm-12" role="form">
										<div class="form-group pull-left" style="width: 40%;" >
											<label for="comName" class="control-label">选择操作</label> <select
												id="comName" name="comName" class="form-control">
											</select>
										</div>
										<div class="form-group pull-right" style="width: 60%;">
											<label for="actionEdit" class="control-label">修改操作名称</label> 
											<div>
												<input type="text"
													id="actionEdit" name="actionEdit" class="form-control pull-left" style="width: 68%;"/>
												<a class="btn btn-primary pull-right disabled" id="changeActionName" style="width: 30%;" href="javascript:componentPanel.changeActionName()">确定</a>
											</div>
										</div>
									</form>
									<form id="custom-controls" class="form-horizontal col-sm-12" role="form">
									</form>
									<table id="comParams"
										class="table table-bordered table-condensed">
										<thead>
											<tr>
												<th style="width: 20%;">参数</th>
												<th style="width: 40%;">值</th>
												<th>描述</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</div>
								<div class="modal-footer">
									
									<!-- <button type="button" id="saveParamBtn" class="btn btn-primary editSave">保存</button> -->
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="addCustomTypeModal" class="modal fade" data-backdrop="static" typeId="0">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title">添加自定义组件类型</h4>
					</div>
					<div class="modal-body" style="height: 250px">
						<div role="form" class="col-sm-12">
							<div class="form-group">
								<label for="display_name" class="col-sm-12 control-label">类型显示名称</label>
								<div class="col-sm-12">
									<div class="col-sm-6" style="padding-left:0px;">
										<input class="form-control" type="text" name="displayname"
											id="displayname" />
									</div>
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-12 control-label" >类型名称</label>
								<div class="col-sm-12">
									<div class="col-sm-6" style="padding-left:0px;">
										<input class="form-control" type="text" name="typename"
											id="typename" />
									</div>
								</div>
							</div>
						</div>
						<div id="type-error" class="alert alert-danger hide">
					        	<a class="close" id="close-type-alert">×</a>
					        	<strong>不能为空</strong> 
      					</div>
					</div>
					<div class="modal-footer">
						<button type="button" id="save_type" class="btn btn-primary" >保存</button>
						<button type="button" class="btn btn-default" data-dismiss="modal" aria-hidden="true">关闭</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>
		<!-- /.modal -->
		<!-- .modal -->
		<div  id="addCustomComponentModal" class="modal fade" style=" margin-top:100px" data-backdrop="static" comid="0">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title">创建组件<a class="close" data-dismiss="modal" aria-hidden="true"><span class="glyphicon glyphicon-remove"></span></a></h4>
					</div>
					<div class="modal-body">
						<div class="row" style="margin:0; background-color:#cfeaf8">
							<div class="col-md-12">
								<div id="wizard" class="wizard">
									<div>
										<div class="wizard-inner">
											<div role="form" class="col-sm-12">
												<div class="form-group">
													<div id="file-error" class="alert alert-danger ">
					        							<a class="close" id="close-file-alert">×</a>
					        							<strong>请将model压缩成zip文件上传.</strong> 
      												</div>
      												<div class="col-sm-12" style="margin-top: 20px;">
														<a href="#" id="file-upload" class="btn btn-primary">选择文件</a>
													</div>
													
												</div>

												<div class="form-group">
													<div class="col-sm-12 pitem">
														<div class="col-sm-5">
															<label for="com_display_name" class="col-sm-12 control-label">组件显示名称</label>
														</div>
														<div class="col-sm-6" style="padding-left:0px;">
															<input class="form-control" type="text" name="com_display_name" id="com_display_name" />
														</div>
													</div>
												</div>
												<div class="form-group">
													<div class="col-sm-12 pitem">
														<div class="col-sm-5">
															<label for="com_name" class="col-sm-12 control-label">组件名称</label>
														</div>
														<div class="col-sm-6" style="padding-left:0px;">
															<input class="form-control" type="text" name="com_name" id="com_name" disabled/>
														</div>
													</div>
												</div>
												<div class="form-group">
													<div class="col-sm-12">
														<div class="col-sm-5 pitem">
															<label for="belong" class="col-sm-12 control-label">所属类型</label>
														</div>
														<div class="col-sm-6" style="padding-left:0px;">
															<select class="form-control" type="text" name="belong"
																		id="belong" >
															</select>
														</div>
													</div>
												</div>
												<div class="form-group">
													<div class="col-sm-12 pitem">
														<div class="col-sm-5">
															<label for="repeatable" class="col-sm-12 control-label">可重复部署</label>
														</div>
														<div class="col-sm-6" style="padding-left:0px;">
															<select class="form-control" name="repeatable"
																		id="repeatable" >
																<option value="false">否</option>
																<option value="true">是</option>
															</select>
														</div>
													</div>
												</div>
												<div class="form-group hide" id="identy">
													<div class="col-sm-12 pitem">
														<div class="col-sm-5">
															<label for="repeatable" class="col-sm-12 control-label">重复标识</label>
														</div>
														<div class="col-sm-6" style="padding-left:0px;">
															<input class="form-control" type="text" name="identify" id="identify" />
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="wizard-action">
											<button id="createOpAction" class="btn btn-primary btn-create disabled" type="button">创建</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
	</div>
	<script src="<c:url value='/js/jquery/jquery-1.11.0.js' />"></script>
	<script src="<c:url value='/js/jquery/json/jquery.json-2.4.min.js' />"></script>
	<script src="<c:url value='/js/bootstrap/bootstrap.min.js' />"></script>
	<script src="<c:url value='/js/jquery/jquery-validation/jquery.validate.min.js' />"></script>
	<script src="<c:url value='/js/jquery/dateFormat/jquery.dateFormat.js' />"></script>
	<script src="<c:url value='/js/jquery/uploadify/jquery.uploadify.min.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/uri.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/common.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/map.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/task/task-panel-html.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/component/d-component-main.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/component/d-component-create.js' />"></script>
</body>
</html>