<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>自定义资源 | 执行命令</title>
</head>
<body>
	<div id="d-main-content">
		<div role="form" class="col-sm-12" style="padding: 0px;">
			<div class="form-group">
				<label for="command" class="col-sm-12 control-label">要执行的命令</label>
				<div class="col-sm-12">
					<textarea class="form-control code-area" name="command" id="command"></textarea>
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-12" style="margin-top: 15px;">
					<a id="submit-btn" class="btn btn-default">执行</a>
				</div>
			</div>
		</div>
		<script src="<c:url value='/js/codemirror/codemirror.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/resource/d-exec.js' />"></script>
	</div>
</body>
</html>