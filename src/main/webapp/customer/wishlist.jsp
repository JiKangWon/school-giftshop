<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Danh sách yêu thích</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<%@ include file="/customer/header.jsp"%>

	<div class="product-container">
		<c:forEach var="f" items="${favorites}" varStatus="loop">
			<div class="product-item">
				<img src="${productImages[loop.index]}" width="150" height="150"
					alt="${f.product.name}">
				<h3>
					<a href="ProductDetail?id=${f.product.id}">${f.product.name}</a>
				</h3>
				<p>
					<fmt:formatNumber value="${f.product.price}" type="currency"
						currencySymbol="VNĐ" groupingUsed="true" maxFractionDigits="0" />
				</p>
				<!-- Nút xóa khỏi yêu thích -->
				<form action="RemoveWishlist" method="post">
					<input type="hidden" name="productId" value="${f.product.id}">
					<button type="submit" class="remove-btn">Xóa khỏi yêu
						thích</button>
				</form>
			</div>
		</c:forEach>
	</div>

</body>
</html>
