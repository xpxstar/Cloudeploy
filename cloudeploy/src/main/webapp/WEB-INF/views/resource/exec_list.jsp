<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>自定义资源 | 列表</title>
</head>
<body>
	<div id="d-main-content">
		<div class="menu-bar">
			<a class="btn btn-default" href="javascript:dExecs.addExecClick()">
				<span class="glyphicon glyphicon-plus"></span> 定义新脚本
			</a>
		</div>
		<table id="execTable" class="table table-condensed table-boxed">
			<thead>
				<tr>
					<th class="hide">编号</th>
					<th>名称</th>
					<th>脚本内容</th>
					<th>创建时间</th>
					<th>修改时间</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
		<div id="addExecModal" class="modal fade" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title">定义新脚本</h4>
					</div>
					<div class="modal-body">
						<p>
							<label for="execName" class="control-label">脚本名称</label> <input
								class="form-control" type="text" name="execName" id="execName" />
							<label for="execContent" class="control-label">脚本内容</label>
							<textarea class="form-control"
								name="execContent" id="execContent"></textarea>
						</p>
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
		<script src="<c:url value='/js/codemirror/codemirror.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/resource/d-exec-list.js' />"></script>
	</div>
</body>
</html>