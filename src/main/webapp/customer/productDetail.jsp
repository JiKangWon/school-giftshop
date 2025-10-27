<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Chi tiết sản phẩm</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<a href="${pageContext.request.contextPath}/CustomerHome">← Quay
		lại</a>

	<h2>${product.name}</h2>

	<p>
		<strong>Giá:</strong>
		<fmt:formatNumber value="${product.price}" type="currency"
			currencySymbol="VNĐ" groupingUsed="true" maxFractionDigits="0" />
	</p>

	<p>
		<strong>Số lượng còn lại:</strong> ${product.stock}
	</p>
	<p>
		<strong>Số lượt bán:</strong> ${totalSold}
	</p>
	<p>
		<strong>Mô tả:</strong> ${product.description}
	</p>
	<p>
		<strong>Danh mục:</strong> ${product.category.name}
	</p>

	<div class="product-images">
		<c:choose>
			<c:when test="${not empty images}">
				<c:forEach var="img" items="${images}">
					<img src="${img}" width="200" height="200" alt="${product.name}">
				</c:forEach>
			</c:when>
			<c:otherwise>
				<img src="/images/default.jpg" width="200" height="200"
					alt="${product.name}">
			</c:otherwise>
		</c:choose>
	</div>

	<form action="${pageContext.request.contextPath}/AddToCart"
		method="post">
		<input type="hidden" name="productId" value="${product.id}"> <label
			for="quantity">Số lượng:</label> <input type="number" id="quantity"
			name="quantity" value="1" min="1" max="${product.stock}">
		<button type="submit">Thêm vào giỏ hàng</button>
	</form>

	<hr>
	<h3>Đánh giá của khách hàng</h3>
	<c:choose>
		<c:when test="${empty reviews}">
			<p>Chưa có đánh giá nào cho sản phẩm này.</p>
		</c:when>
		<c:otherwise>
			<table border="1" cellpadding="6" cellspacing="0">
				<tr>
					<th>Người đánh giá</th>
					<th>Nội dung đánh giá</th>
					<th>Ngày đánh giá</th>
				</tr>
				<c:forEach var="r" items="${reviews}">
					<tr>
						<!-- ✅ Lấy tên người đánh giá -->
						<td>${r.order.user.name}</td>

						<!-- ✅ Lấy nội dung đánh giá -->
						<td>${r.review}</td>

						<!-- ✅ Lấy ngày đánh giá (nếu có receivedAt) -->
						<td><c:choose>
								<c:when test="${not empty r.receivedAt}">
                                ${r.receivedAt}
                            </c:when>
								<c:otherwise>
                                Chưa cập nhật
                            </c:otherwise>
							</c:choose></td>
					</tr>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</body>
</html>
