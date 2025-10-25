<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
<title>Giỏ hàng của tôi</title>
<style>
table {
	border-collapse: collapse;
	width: 80%;
	margin: auto;
}

th, td {
	border: 1px solid #ccc;
	padding: 8px;
	text-align: center;
}

th {
	background-color: #f2f2f2;
}

.action-btn {
	padding: 4px 8px;
	cursor: pointer;
}

#total {
	font-weight: bold;
	color: red;
}
</style>
</head>
<body>
	<%@ include file="/customer/header.jsp"%>

	<h2 style="text-align: center;">🛒 Giỏ hàng của bạn</h2>

	<c:choose>
		<c:when test="${empty cartItems}">
			<p style="text-align: center;">Giỏ hàng hiện đang trống.</p>
		</c:when>
		<c:otherwise>
			<form id="checkoutForm"
				action="${pageContext.request.contextPath}/Checkout" method="get">
				<table>
					<tr>
						<th>Chọn</th>
						<th>Tên sản phẩm</th>
						<th>Giá</th>
						<th>Số lượng</th>
						<th>Thành tiền</th>
						<th>Hành động</th>
					</tr>

					<c:forEach var="item" items="${cartItems}">
						<tr>
							<td><input type="checkbox" class="product-check"
								data-total="${item.product.price * item.quantity}"
								data-productid="${item.product.id}" /></td>
							<td>${item.product.name}</td>
							<td><fmt:formatNumber value="${item.product.price}"
									type="currency" currencySymbol="VNĐ" groupingUsed="true"
									maxFractionDigits="0" /></td>
							<td>
								<form action="${pageContext.request.contextPath}/CartAction"
									method="post" style="display: inline;">
									<input type="hidden" name="productId"
										value="${item.product.id}" />
									<button type="submit" name="action" value="decrease"
										class="action-btn">-</button>
								</form> ${item.quantity}
								<form action="${pageContext.request.contextPath}/CartAction"
									method="post" style="display: inline;">
									<input type="hidden" name="productId"
										value="${item.product.id}" />
									<button type="submit" name="action" value="increase"
										class="action-btn">+</button>
								</form>
							</td>
							<td><fmt:formatNumber
									value="${item.product.price * item.quantity}" type="currency"
									currencySymbol="VNĐ" groupingUsed="true" maxFractionDigits="0" />
							</td>
							<td>
								<form action="${pageContext.request.contextPath}/CartAction"
									method="post" style="display: inline;">
									<input type="hidden" name="productId"
										value="${item.product.id}" />
									<button type="submit" name="action" value="remove"
										class="action-btn" style="color: red;">Xóa</button>
								</form>
							</td>
						</tr>
					</c:forEach>
				</table>

				<div style="text-align: center; margin-top: 20px;">
					<p>
						Tổng cộng các sản phẩm đã chọn: <span id="total">0 VNĐ</span>
					</p>
					<input type="hidden" name="productIds" id="productIds" />
					<button type="submit" style="padding: 10px 20px;">Đặt hàng</button>
				</div>
			</form>

			<script>
                function updateTotalAndIds() {
                    let total = 0;
                    let selectedIds = [];
                    document.querySelectorAll('.product-check').forEach(cb => {
                        if(cb.checked) {
                            total += parseFloat(cb.dataset.total);
                            selectedIds.push(cb.dataset.productid);
                        }
                    });
                    document.getElementById('total').textContent = total.toLocaleString('vi-VN') + ' VNĐ';
                    document.getElementById('productIds').value = selectedIds.join(',');
                }

                document.querySelectorAll('.product-check').forEach(cb => {
                    cb.addEventListener('change', updateTotalAndIds);
                });
            </script>
		</c:otherwise>
	</c:choose>
</body>
</html>
