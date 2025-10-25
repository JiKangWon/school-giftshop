<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<header>
	<h2>Xin chào, ${sessionScope.user.name}</h2>
	<nav>
		<a href="${pageContext.request.contextPath}/CustomerHome">Trang
			chủ</a> | <a href="${pageContext.request.contextPath}/CustomerProfile">Thông
			tin cá nhân</a> | <a href="${pageContext.request.contextPath}/Wishlist">Danh
			mục yêu thích</a> | <a href="${pageContext.request.contextPath}/ViewCart">Giỏ
			hàng</a> | <a href="${pageContext.request.contextPath}/MyOrder">Đơn
			hàng của tôi</a> | <a href="${pageContext.request.contextPath}/Logout"
			style="color: red;">Đăng xuất</a>
	</nav>
</header>
<hr>
