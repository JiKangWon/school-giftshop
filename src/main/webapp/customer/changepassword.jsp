<%@ page contentType="text/html; charset=UTF-8"%>

<html>
<head>
<title>Đổi mật khẩu</title>
</head>
<body>

	<%@ include file="/customer/header.jsp"%>

	<div class="container">
		<h2>Đổi mật khẩu</h2>
		<form method="post" action="ChangePassword">
			<div class="msg"><%=request.getAttribute("message") != null ? request.getAttribute("message") : ""%></div>

			<div class="form-group">
				<label>Mật khẩu hiện tại</label> <input type="password"
					name="currentPassword" required>
			</div>

			<div class="form-group">
				<label>Mật khẩu mới</label> <input type="password"
					name="newPassword" required>
			</div>

			<div class="form-group">
				<label>Nhập lại mật khẩu mới</label> <input type="password"
					name="confirmPassword" required>
			</div>

			<button type="submit">Xác nhận đổi mật khẩu</button>
		</form>

		<a href="CustomerProfile">⬅ Quay lại thông tin cá nhân</a>
	</div>

</body>
</html>
