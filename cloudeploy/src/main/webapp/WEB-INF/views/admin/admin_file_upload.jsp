<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>自定义资源 | 编辑文件</title>
</head>
<body>
	<div id="d-main-content">
		<div role="form" class="col-sm-12" style="padding:0px;">
			<div class="form-group">
				<label for="filePath" class="col-sm-12 control-label">文件目标位置</label>
				<div class="col-sm-5">
					<input class="form-control" type="text" name="filePath" id="filePath" />
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-12" style="margin-top:20px;">
					<a href="#" id="file-upload" class="btn btn-primary">选择文件</a>
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-12" style="margin-top:20px;">
					<a id="upload-btn" class="btn btn-default">上传</a>
				</div>
			</div>
		</div>
		<script src="<c:url value='/js/jquery/uploadify/jquery.uploadify.min.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/d-custom.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/d-custom-file-upload.js' />"></script>
	</div>
</body>
</html>