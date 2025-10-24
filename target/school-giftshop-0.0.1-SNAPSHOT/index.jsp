<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <link href="./static/css/login.css" rel="stylesheet">
<title>login</title>
</head>
<body>
	<div id="form-ctn">
		<h1 >LOGIN</h1>
		<form action="${pageContext.request.contextPath}/login" method="post">
			<label for="username">
				username:
				<input class="form-control" type="text" name="username" id="username">
			</label>
			<label for="password">
				password:
				<input class="form-control" type="password" name="password" id="password">
			</label>
			<button type="submit">Login</button>
		</form>
	</div>
</body>
</html>