<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Templates</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
		<div class="deploy-bar"></div>
		<div class="intro">
			<h1>Templates&nbsp;Manager</h1>
				<p class="lead" style="margin-top:10px">
					<em>Templates&nbsp;</em> define configure file of a component. This is used for Expert to create and upload templates which will be used to describe parameters dependent correlation. Templates can be converted into configure file by specific key-value parameters.
				</p>
		</div>
	<div class="row content-board">
			<div id = "d-app-list" class="itemlist col-sm-2">
				<h4><i class="fa fa-calendar"></i> applications：</h4>
				<div class="nav nav-list col-sm-12">
					<ul></ul>
				</div>
			</div>
			<div id = "d-container-list" class="itemlist col-sm-2 hide">
				<div class="col-sm-12"><i class="fa fa-cubes"></i> containers：</div>
				<div class="nav nav-list col-sm-12">
					<ul></ul>
				</div>
			</div>
			<div id = "d-tempt-list" class="col-sm-10">
				<div class="menu-bar">
					<a class="btn btn-default"
						href="javascript:dCustomFiles.addFileClick()"> <span
						class="glyphicon glyphicon-plus"></span> new template
					</a>
				</div>
				<table id="fileTable" class="table table-condensed table-boxed">
					<thead>
						<tr>
							<th class="hide">No.</th>
							<th>name</th>
							<th>create-time</th>
							<th>modify-time</th>
							<th>action</th>
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
								<h4 class="modal-title">new file</h4>
							</div>
							<div class="modal-body" style="height: 250px">
								<div role="form" class="col-sm-12">
									<div class="form-group">
										<div class="col-sm-12" style="margin-top: 20px;">
											<a href="#" id="file-upload" class="btn btn-primary">select file</a>
										</div>
									</div>
									<div class="form-group">
										<label for="fileName" class="col-sm-12 control-label">file name</label>
										<div class="col-sm-12">
											<div class="col-sm-6" style="padding-left:0px;">
												<input class="form-control" type="text" name="fileName"
													id="fileName" />
											</div>
											<div class="col-sm-6">
												<a id="upload-btn" class="btn btn-default">upload</a>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">close</button>
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
								<h4 class="modal-title">template
									<span id="scanFileName" class="control-label"></span>
									</h4>
							</div>
							<div  class="modal-body" style="height: 500px">
								<textarea id="scanFileBody" readonly style="width:100%;height:100%">
								</textarea>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">close</button>
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