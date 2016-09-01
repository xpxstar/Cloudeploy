<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Orchestration</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
		<div class="intro">
			<h1>Orchestration&nbsp;Manager</h1>
				<p class="lead" style="margin-top:10px">
					<em>Orchestration&nbsp;</em>is a scheme on how to deploy or configure middleware components. You can design your application task by dragging different components from bars on the right, connecting them with edges which means dependent correlation, setting properties of a component by double-click on nodes.
				</p>
		</div>
		
		<div id="app-panel" class="content-board">
			<div id="graph-panel" class="col-sm-10" style="overflow: auto;">
				<div id="tmp-panel"></div>
			</div>
			<div id="group-list" class="col-sm-2">
				<div>
					<div id="btn-save" class="save-btn">
						<i class="fa fa-save"></i> Save
					</div>
					<div id="btn-save-as" class="save-btn" style="display: none;">
						<i class="fa fa-save"></i><i class="fa fa-pencil"></i> Save As
					</div>
				</div>
				<!-- panel group for operations component list -->
				<div id="panel-group-operations" class="panel-group" role="tablist"
					aria-multiselectable="true"></div>
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
						<h4 class="modal-title" id="myModalLabel">middleware-parameters</h4>
					</div>
					<div class="modal-body">
						<form role="form">
							<div class="row">
								<div class="col-sm-3">
									<div class="form-group">
										<label for="containerName" class="control-label pull-left"
											style="padding-right: 1em;">middleware-name:</label> 
										<input id="containerName" name="containerName" class="form-control">
									</div>
									<div class="form-group">
										<label for="containerPort" class="control-label pull-left"
											style="padding-right: 2em;">middleware-port:</label> 
										<input type="number"
											id="containerPort" name="containerPort" class="form-control">
									</div>
									<div class="form-group">
										<label for="initCount" class="control-label pull-left">primary-instances:</label>
										<input type="number" id="initCount" name="initCount"
											class="form-control">
									</div>
									<div class="form-group">
										<label for="maxCount" class="control-label pull-left">max-instances:</label>
										<input type="number" id="maxCount" name="maxCount"
											class="form-control">
									</div>
									<!-- <div class="form-group">
										<label for="maxMem" class="control-label pull-left">内存限制:</label>
										<input type="number" id="maxMem" name="maxMem"
											class="form-control">
									</div>
									<div class="form-group">
										<label for="checkCmd" class="control-label pull-left">服务检测cmd:</label>
										<input id="checkCmd" name="checkCmd" class="form-control">
									</div> -->
								</div>
								<!-- end left col -->
								<div class="col-sm-9">
									<div style="margin-top:25px;">
										<table class="table table-bordered table-condensed" id="templates">
										    <thead></thead>
											<tbody></tbody>
											<tfoot>
											 <tr>
											  <td colspan="4" style="padding-left:0; padding-right:0; text-align:center;">
											  	<a href="#" id = "addTemplate">
											  		<i class="fa fa-plus"></i>
											  		<span>add templates</span>
											  	</a>
											  </td>
											 </tr>
											</tfoot>
										</table>
									</div>
									<hr>
									<div style="margin-top:25px;">
										<table class="table table-bordered table-condensed" id="attributes">
											<thead></thead>
											<tbody></tbody>
											<tfoot>
											 <tr>
											  <td colspan="3" style="padding-left:0; padding-right:0; text-align:center;">
											  	<a href="#" role="button" id="addAttribute">
											  		<i class="fa fa-plus"></i>
											  		<span>add properties</span>
											  	</a>
											  </td>
											 </tr>
											</tfoot>
										</table>
									</div>
									<hr>
									<div style="margin-top:25px;">
										<table class="table table-bordered table-condensed" id="services">
											<thead></thead>
											<tbody></tbody>
											<tfoot>
											 <tr>
											  <td colspan="3" style="padding-left:0; padding-right:0; text-align:center;">
											  	<a href="#" role="button" id="addService">
											  		<i class="fa fa-plus"></i>
											  		<span>add services</span>
											  	</a>
											  </td>
											 </tr>
											</tfoot>
										</table>
									</div>
								</div>
								<!-- end right col -->
							</div>
						</form>
						<table id="actionParams"
							class="table table-bordered table-condensed">
							<thead>
								<tr>
									<!-- <th style="width: 20%;">参数</th>
									<th style="width: 60%;">值</th>
									<th>描述</th>
									 -->
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default editCancel"
							data-dismiss="modal">cancel</button>
						<button type="button" class="btn btn-primary editSave">save</button>
					</div>
				</div>
			</div>
		</div>
		<!-- task commit start -->
		<div style="margin-top: 200px;" class="modal fade" id="saveModal"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">save application</h4>
					</div>
					<div class="modal-body">
						<div style="width: 50%;">
							<div class="form-group">
								<label for="appName">application name</label> <input type="text"
									class="form-control" id="appName" placeholder="application name">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">cancel</button>
						<button type="button" id="save-app-btn" class="btn btn-primary">save</button>
					</div>
				</div>
			</div>
		</div>
		<!-- task commit end -->
		<!-- operation start -->
		<div style="margin-top: 200px;" class="modal fade" id="operationModal"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">actions</h4>
					</div>
					<div class="modal-body">
						<ul class="operation-list">
							<li id="start" data-oper-type="START" data-view-id="1">
								<div class="oper-icon">
									<i class="fa fa-play text-success"></i>
								</div>
								<div class="oper-text">start</div>
							</li>
							<li id="stop" data-oper-type="STOP" data-view-id="1">
								<div class="oper-icon">
									<i class="fa fa-stop text-danger"></i>
								</div>
								<div class="oper-text">stop</div>
							</li>
							<li id="remove" data-oper-type="REMOVE" data-view-id="2">
								<div class="oper-icon">
									<i class="fa fa-minus text-danger"></i>
								</div>
								<div class="oper-text">remove</div>
							</li>
							<li id="copy" data-oper-type="COPY" data-view-id="2">
								<div class="oper-icon">
									<i class="fa fa-plus text-primary"></i>
								</div>
								<div class="oper-text">copy</div>
							</li>
							<li id="fail" data-oper-type="FAIL" data-view-id="3">
								<div class="oper-icon">
									<i class="fa fa-times text-danger"></i>
								</div>
								<div class="oper-text">fail test</div>
							</li>
							<li id="fail" data-oper-type="START" data-view-id="3">
								<div class="oper-icon">
									<i class="fa fa-play text-success"></i>
								</div>
								<div class="oper-text">recover</div>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<!-- operation end -->
		<!-- node details end -->
		<script src="<c:url value='/js/jsPlumb/dom.jsPlumb-1.7.2.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/app/graph/j-class.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/app/graph/node.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/app/app-plumb.js' />"></script>
		<script
			src="<c:url value='/js/cloudeploy/app/app-orchestration.js' />"></script>
	</div>
</body>
</html>