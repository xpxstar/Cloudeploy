<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Cloudeploy | Task | Panel</title>
</head>
<body>
	<div id="d-main-content">
		<div id="task-panel" class="row">
			<div class="d-menu col-sm-1">
				<ul>
					<li id="btn-create-task">
						<div class="menu-btn active"
							onclick="javascript:taskPanel.changeView(1);">
							<div class="menu-icon">
								<i class="fa fa-plus"></i>
							</div>
							<div class="menu-text">创建任务</div>
						</div>
					</li>
					<li id="btn-edit-task">
						<div class="menu-btn"
							onclick="javascript:void(0);">
							<div class="menu-icon">
								<i class="fa fa-edit"></i>
							</div>
							<div class="menu-text">编辑任务</div>
						</div>
					</li>
					<li id="btn-task-list">
						<div class="menu-btn"
							onclick="javascript:taskPanel.changeView(3);">
							<div class="menu-icon">
								<i class="fa fa-list"></i>
							</div>
							<div class="menu-text">任务列表</div>
						</div>
					</li>
				</ul>
			</div>
			<div id="graph-panel" class="col-sm-9" style="overflow:auto;">
				<div id="tmp-panel"></div>
			</div>
			<div id="task-list" class="col-sm-9" style="margin-top: 30px;">
				<table class="table table-bordered table-condensed">
					<thead>
						<tr>
							<th style="display: none;">#</th>
							<th>任务名称</th>
							<th>创建时间</th>
							<th>最后修改</th>
							<th>最后执行</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
			<div id="group-list" class="col-sm-2">
				<div>
					<div id="btn-save" class="save-btn">
						<i class="fa fa-save"></i> 保存
					</div>
					<div id="btn-save-as" class="save-btn">
						<i class="fa fa-save"></i><i class="fa fa-pencil"></i> 另存为
					</div>
				</div>
				<!-- panel group for operations component list -->
				<div id="panel-group-operations" class="panel-group" role="tablist"
					aria-multiselectable="true"></div>
					<!-- 要去掉加号 -->
				<div>
					<div class="save-btn" id="self-com">自定义组件：</div>
				</div>
				<div id="panel-group-operations2" class="panel-group" role="tablist"
					aria-multiselectable="true"></div>
					<!-- 要去掉加号 -->
				<div>
				<div class="panel panel-default" id="self-action">
					<div class="panel-heading" role="tab" >
						<h4 class="panel-title">
							<a data-toggle="collapse" href="#action-options" aria-expanded="true" aria-controls="self-action">
								自定义活动 &nbsp;<span class="badge">0</span>
							</a>
						</h4>
					</div>
					<div id="action-options" class="panel-collapse collapse" role="tabpanel">
						<ul class="list-group"></ul>
					</div>
				</div>
			</div>
		</div>

		<!-- node details start -->
		<div class="modal fade" id="detailModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close editCancel"
							data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">operation name</h4>
					</div>
					<div class="modal-body">
						<label for="hostName" class="control-label">选择机器节点</label>
						<div id="hostNames">
							<ul></ul>
						</div>
						<form class="form-horizontal col-sm-12" role="form">
							<div id="actionOp" class="form-group pull-left" style="width: 50%;">
								<label for="actionName" class="control-label">选择操作</label> <select
									id="actionName" name="actionName" class="form-control">
								</select>
							</div>
						</form>
						<form id="custom-controls" class="form-horizontal col-sm-12" role="form">
						</form>
						<table id="actionParams"
							class="table table-bordered table-condensed">
							<thead>
								<tr>
									<th style="width: 20%;">参数</th>
									<th style="width: 60%;">值</th>
									<th>描述</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default editCancel"
							data-dismiss="modal">取消</button>
						<button type="button" class="btn btn-primary editSave">保存</button>
					</div>
				</div>
			</div>
		</div>
		<!-- node details end -->
		<!-- node set label start -->
		<div class="modal fade" id="labelModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">编辑连接类型</h4>
					</div>
					<div class="modal-body">
						<div style="width: 50%;">
							<select id="linkType" name="linkType" class="form-control">
								<option value="before">before</option>
								<option value="listen">listen</option>
							</select>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						<button type="button" id="save-label-btn" class="btn btn-primary">保存</button>
					</div>
				</div>
			</div>
		</div>
		<!-- node set label end -->

		<!-- task commit start -->
		<div style="margin-top:200px;" class="modal fade" id="saveModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">保存任务</h4>
					</div>
					<div class="modal-body">
						<div style="width: 50%;">
							<div class="form-group">
								<label for="task-name">任务名称</label> <input type="text"
									class="form-control" id="task-name" placeholder="task name">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						<button type="button" id="save-task-btn" class="btn btn-primary" >保存</button>
					</div>
				</div>
			</div>
		</div>
		<!-- task commit end -->
		<script src="<c:url value='/js/jsPlumb/dom.jsPlumb-1.7.2.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/task/graph/j-class.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/task/graph/node.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/task/task-plumb.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/task/task-panel-html.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/task/task-panel-html-act.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/task/task-panel.js' />"></script>
	</div>
</body>
</html>