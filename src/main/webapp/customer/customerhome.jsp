<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Trang chủ khách hàng</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
.filter-bar {
	text-align: center;
	margin: 20px 0;
}

input[type="text"] {
	padding: 6px;
	width: 250px;
}

button {
	padding: 6px 12px;
	cursor: pointer;
}

.product-container {
	display: flex;
	flex-wrap: wrap;
	justify-content: center;
	gap: 20px;
	margin-top: 30px;
}

.product-item {
	border: 1px solid #ccc;
	padding: 10px;
	border-radius: 8px;
	text-align: center;
	width: 220px;
}
</style>
</head>
<body>
	<%@ include file="/customer/header.jsp"%>

	<!-- Thanh tìm kiếm và lọc -->
	<div class="filter-bar">
		<!-- Form tìm kiếm -->
		<form action="${pageContext.request.contextPath}/CustomerHome"
			method="get" style="display: inline-block; margin-right: 20px;">
			<input type="text" name="search" placeholder="Nhập tên sản phẩm..."
				value="${param.search != null ? param.search : ''}" />
			<button type="submit">Tìm kiếm</button>
		</form>

		<!-- Form lọc theo danh mục -->
		<form action="${pageContext.request.contextPath}/CustomerHome"
			method="get" style="display: inline-block;">
			<label for="category">Lọc theo danh mục:</label> <select
				name="categoryId" id="category" onchange="this.form.submit()">
				<option value="">-- Tất cả --</option>
				<c:forEach var="c" items="${categories}">
					<option value="${c.id}"
						<c:if test="${param.categoryId != null && param.categoryId eq c.id.toString()}">selected</c:if>>
						${c.name}</option>
				</c:forEach>
			</select>
		</form>
	</div>

	<hr>

	<!-- Danh sách sản phẩm -->
	<div class="product-container">
		<c:forEach var="p" items="${products}" varStatus="loop">
			<div class="product-item">
				<img src="${productImages[loop.index]}" width="150" height="150"
					alt="${p.name}">
				<h3>
					<a
						href="${pageContext.request.contextPath}/ProductDetail?id=${p.id}">
						${p.name} </a>
				</h3>
				<p>
					<fmt:formatNumber value="${p.price}" type="currency"
						currencySymbol="VNĐ" groupingUsed="true" maxFractionDigits="0" />
				</p>

				<!-- Thêm vào giỏ hàng -->
				<form action="${pageContext.request.contextPath}/AddToCart"
					method="post" style="display: inline-block;">
					<input type="hidden" name="productId" value="${p.id}"> <label
						for="cartQuantity_${p.id}">SL:</label> <input type="number"
						id="cartQuantity_${p.id}" name="quantity" value="1" min="1"
						max="${p.stock}" style="width: 50px;">
					<button type="submit">Thêm vào giỏ hàng</button>
				</form>

				<!-- Thêm vào danh mục yêu thích -->
				<form action="${pageContext.request.contextPath}/AddToWishlist"
					method="post" style="display: inline-block;">
					<input type="hidden" name="productId" value="${p.id}">
					<button type="submit">Yêu thích</button>
				</form>
			</div>
		</c:forEach>

		<c:if test="${empty products}">
			<p style="text-align: center;">Không tìm thấy sản phẩm phù hợp.</p>
		</c:if>
	</div>
</body>
</html>
