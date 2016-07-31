<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>模板管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
		<div class="deploy-bar"></div>
		<div class="row" >
			<div id = "d-app-list" class="itemlist col-sm-2">
				<div class="col-sm-12"><i class="fa fa-calendar"></i> 应用列表：</div>
				<div class="nav nav-list col-sm-12">
					<ul></ul>
				</div>
			</div>
			<div id = "d-container-list" class="itemlist col-sm-2 hide">
				<div class="col-sm-12"><i class="fa fa-cubes"></i> 容器列表：</div>
				<div class="nav nav-list col-sm-12">
					<ul></ul>
				</div>
			</div>
			<div id = "d-tempt-list" class="col-sm-10">
				<div class="menu-bar">
					<a class="btn btn-default"
						href="javascript:dCustomFiles.addFileClick()"> <span
						class="glyphicon glyphicon-plus"></span> 添加新的自定义文件
					</a>
				</div>
				<table id="fileTable" class="table table-condensed table-boxed">
					<thead>
						<tr>
							<th class="hide">编号</th>
							<th>名称</th>
							<th>创建时间</th>
							<th>修改时间</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
				<div id="addCustomFileModal" class="modal fade" data-backdrop="static">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">
									<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
								</button>
								<h4 class="modal-title">添加新的自定义文件</h4>
							</div>
							<div class="modal-body" style="height: 250px">
								<div role="form" class="col-sm-12">
									<div class="form-group">
										<div class="col-sm-12" style="margin-top: 20px;">
											<a href="#" id="file-upload" class="btn btn-primary">选择文件</a>
										</div>
									</div>
									<div class="form-group">
										<label for="fileName" class="col-sm-12 control-label">文件名称</label>
										<div class="col-sm-12">
											<div class="col-sm-6" style="padding-left:0px;">
												<input class="form-control" type="text" name="fileName"
													id="fileName" />
											</div>
											<div class="col-sm-6">
												<a id="upload-btn" class="btn btn-default">上传</a>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.modal -->
				<div id="scanFileModal" class="modal fade" data-backdrop="static">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">
									<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
								</button>
								<h4 class="modal-title">模板文件
									<span id="scanFileName" class="control-label"></span>
									</h4>
							</div>
							<div  class="modal-body" style="height: 500px">
								<textarea id="scanFileBody" readonly style="width:100%;height:100%">
								</textarea>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
			</div>
		</div>
		
	<script src="<c:url value='/js/cloudeploy/tempt/d-tempt-main.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/tempt/d-custom-tempt-list.js' />"></script>
	<script src="<c:url value='/js/jquery/uploadify/jquery.uploadify.min.js' />"></script>
	</div>
</body>
</html>