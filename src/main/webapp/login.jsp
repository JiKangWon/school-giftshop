<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form action="${pageContext.request.contextPath}/login" method="post">
		<label for="username">
			username:
			<input type="text" name="username" id="username">
		</label>
		<label for="password">
			password:
			<input type="password" name="password" id="password">
		</label>
		<button type="submit">Login</button>
	</form>
</body>
</html>