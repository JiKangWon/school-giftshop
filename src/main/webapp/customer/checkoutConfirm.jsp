<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
<title>Xác nhận đơn hàng</title>
</head>
<body>
	<h2>Xác nhận đơn hàng</h2>

	<p>
		<strong>Tên:</strong> ${user.name}
	</p>
	<p>
		<strong>Số điện thoại:</strong> ${user.phone}
	</p>

	<c:choose>
		<c:when test="${not empty address}">
			<p>
				<strong>Địa chỉ:</strong> ${address.street}, ${address.ward},
				${address.district}, ${address.province}, ${address.country}
			</p>
		</c:when>
		<c:otherwise>
			<p>Chưa có địa chỉ giao hàng.</p>
		</c:otherwise>
	</c:choose>

	<h3>Giỏ hàng</h3>
	<form id="checkoutForm"
		action="${pageContext.request.contextPath}/CheckoutConfirm"
		method="post">
		<table border="1">
			<tr>
				<th>Chọn</th>
				<th>Tên sản phẩm</th>
				<th>Số lượng</th>
				<th>Giá (VND)</th>
				<th>Thành tiền (VND)</th>
			</tr>

			<c:set var="total" value="0" />
			<c:forEach var="item" items="${cart}">
				<c:set var="lineTotal" value="${item.quantity * item.product.price}" />
				<tr>
					<td><input type="checkbox" name="productIds"
						value="${item.product.id}" checked /></td>
					<td>${item.product.name}</td>
					<td>${item.quantity}</td>
					<td><fmt:formatNumber value="${item.product.price}"
							type="number" groupingUsed="true" /></td>
					<td><fmt:formatNumber value="${lineTotal}" type="number"
							groupingUsed="true" /></td>
				</tr>
				<c:set var="total" value="${total + lineTotal}" />
			</c:forEach>

			<tr>
				<td colspan="4" style="text-align: right;"><strong>Tổng
						tiền:</strong></td>
				<td><strong><fmt:formatNumber value="${total}"
							type="number" groupingUsed="true" /> VND</strong></td>
			</tr>
		</table>

		<br />
		<button type="submit">Xác nhận đặt hàng</button>
		<a href="${pageContext.request.contextPath}/ViewCart">
			<button type="button">Hủy và quay lại giỏ hàng</button>
		</a>
	</form>
</body>
</html>
