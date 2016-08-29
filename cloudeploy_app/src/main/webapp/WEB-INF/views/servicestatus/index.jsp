<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
	<div class="intro">
			<h1>Services&nbsp;Manager</h1>
				<p class="lead" style="margin-top:10px">
					<em>Services&nbsp;</em> are runtime component instances. When component instances are in running status, they become services which we can access through url. Services Manager lists all services with their status, hosts as well as properties in form of key-value pair.
				</p>
		</div>
			<iframe src="${url}" frameborder="0" width="100%" height="100%">
			</iframe>
	</div>
</body>
