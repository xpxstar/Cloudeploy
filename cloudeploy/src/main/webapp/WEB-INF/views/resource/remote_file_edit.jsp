<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>远程文件 | 编辑文件</title>
</head>
<body>
	<div id="d-main-content">
		<div role="form" class="col-sm-12" style="padding: 0px;">
			<div class="form-group">
				<label for="filePath" class="col-sm-12 control-label">文件位置</label>
				<div class="col-sm-5">
					<span id="filePath"></span>
				</div>
			</div>
			<div class="form-group">
				<label for="fileContent" class="col-sm-12 control-label"
					style="margin-top: 15px;">文件内容</label>
				<div class="col-sm-10">
					<textarea class="form-control" name="fileContent" id="fileContent"></textarea>
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-12" style="margin-top: 15px;">
					<a id="submit-btn" class="btn btn-default">提交</a>
				</div>
			</div>
		</div>
		<script src="<c:url value='/js/codemirror/codemirror.js' />"></script>
		<script
			src="<c:url value='/js/cloudeploy/resource/d-remote-file-edit.js' />"></script>
	</div>
</body>
</html>