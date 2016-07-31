<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>远程文件 | 列表</title>
</head>
<body>
	<div id="d-main-content">
		<div class="menu-bar">
			<a class="btn btn-default"
				href="javascript:dRemoteFiles.addFileClick()"> <span
				class="glyphicon glyphicon-plus"></span> 添加新的远程文件
			</a>
		</div>
		<table id="fileTable" class="table table-condensed table-boxed">
			<thead>
				<tr>
					<th class="hide">编号</th>
					<th>名称</th>
					<th>文件路径</th>
					<th>创建时间</th>
					<th>修改时间</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
		<div id="addRemoteFileModal" class="modal fade">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title">添加远程文件</h4>
					</div>
					<div class="modal-body" style="height: 250px">
						<div role="form" class="col-sm-12">
							<div class="form-group">
								<label for="fileName" class="col-sm-12 control-label">文件名称</label>
								<div class="col-sm-12">
									<input class="form-control" type="text" name="fileName"
										id="fileName" />
								</div>
							</div>
							<div class="form-group">
								<label for="filePath" class="col-sm-12 control-label">远程文件路径</label>
								<div class="col-sm-12">
									<input class="form-control" type="text" name="filePath"
										id="filePath" />
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						<button type="button" id="save-btn" class="btn btn-primary">保存</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>
		<!-- /.modal -->
	
		<script src="<c:url value='/js/cloudeploy/resource/d-remote-file-list.js' />"></script>
	</div>
</body>
</html>