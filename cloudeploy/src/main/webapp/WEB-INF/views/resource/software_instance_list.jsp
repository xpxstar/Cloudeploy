<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>软件管理 | 软件列表</title>
</head>
<body>
	<div id="d-main-content">
		<table id="instance-table" class="table table-condensed table-boxed">
			<thead>
				<tr>
					<th class="hide">编号</th>
					<th>软件包</th>
					<th>名称</th>
					<th>状态</th>
					<th>安装时间</th>
					<th>最后修改时间</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
		<script src="<c:url value='/js/cloudeploy/resource/d-software-instance-list.js' />"></script>
	</div>
</body>
</html>