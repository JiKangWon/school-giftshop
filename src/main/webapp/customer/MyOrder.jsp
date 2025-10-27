<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
<title>Đơn hàng của tôi</title>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 20px;
	background-color: #f9f9f9;
}

.tab-buttons {
	display: flex;
	justify-content: flex-start;
	gap: 10px;
	margin-bottom: 20px;
}

.tab-button {
	padding: 10px 20px;
	border: 1px solid #007bff;
	border-radius: 5px;
	background-color: white;
	cursor: pointer;
	transition: 0.2s;
}

.tab-button.active {
	background-color: #007bff;
	color: white;
}

.tab-content {
	display: none;
}

.tab-content.active {
	display: block;
}

.order-block {
	border: 1px solid #ddd;
	border-radius: 8px;
	background: white;
	padding: 15px;
	margin-bottom: 15px;
}

table {
	border-collapse: collapse;
	width: 100%;
	margin: 10px 0;
}

th, td {
	border: 1px solid #ccc;
	padding: 8px;
	text-align: left;
}

button {
	padding: 6px 12px;
	background-color: #007bff;
	color: white;
	border: none;
	border-radius: 4px;
	cursor: pointer;
}

button:hover {
	background-color: #0056b3;
}

input[type="text"] {
	padding: 5px;
	width: 60%;
}
</style>

<script>
function openTab(tabName) {
    const contents = document.querySelectorAll(".tab-content");
    contents.forEach(c => c.classList.remove("active"));

    const buttons = document.querySelectorAll(".tab-button");
    buttons.forEach(b => b.classList.remove("active"));

    document.getElementById(tabName).classList.add("active");
    document.getElementById(tabName + "-btn").classList.add("active");
}

window.onload = () => openTab("shipping");
</script>
</head>
<body>
	<fmt:setLocale value="vi_VN" />

	<%@ include file="/customer/header.jsp"%>

	<!-- Các nút chuyển tab -->
	<div class="tab-buttons">
		<button id="shipping-btn" class="tab-button"
			onclick="openTab('shipping')">🚚 Đang vận chuyển</button>
		<button id="review-btn" class="tab-button" onclick="openTab('review')">📝
			Đánh giá</button>
		<button id="completed-btn" class="tab-button"
			onclick="openTab('completed')">✅ Đã giao</button>
	</div>

	<!-- Tab: Đang vận chuyển -->
	<div id="shipping" class="tab-content">
		<c:if test="${empty processingOrders}">
			<p>Không có đơn hàng nào đang vận chuyển.</p>
		</c:if>
		<c:forEach var="order" items="${processingOrders}">
			<div class="order-block">
				<p>
					<strong>Mã đơn:</strong> ${order.id} | <strong>Ngày đặt:</strong>
					${order.createdAt} | <strong>Trạng thái:</strong> ${order.status}
				</p>
				<table>
					<tr>
						<th>Sản phẩm</th>
						<th>Số lượng</th>
						<th>Giá</th>
					</tr>
					<c:forEach var="op" items="${order.orderProducts}">
						<tr>
							<td>${op.product.name}</td>
							<td>${op.quantity}</td>
							<td><fmt:formatNumber value="${op.product.price}"
									type="number" groupingUsed="true" /> VNĐ</td>
						</tr>
					</c:forEach>
				</table>

				<form method="post">
					<input type="hidden" name="id" value="${order.id}"> <input
						type="hidden" name="action" value="received">
					<button type="submit">Đã nhận hàng</button>
				</form>
			</div>
		</c:forEach>
	</div>

	<!-- Tab: Đánh giá -->
	<div id="review" class="tab-content">
		<c:if test="${empty reviewProducts}">
			<p>Không có sản phẩm nào chờ đánh giá.</p>
		</c:if>
		<c:forEach var="op" items="${reviewProducts}">
			<div class="order-block">
				<p>
					<strong>Sản phẩm:</strong> ${op.product.name} | <strong>Số
						lượng:</strong> ${op.quantity} | <strong>Giá:</strong>
					<fmt:formatNumber value="${op.product.price}" type="number"
						groupingUsed="true" />
					VNĐ
				</p>
				<form method="post">
					<input type="hidden" name="id" value="${op.id}"> <input
						type="hidden" name="action" value="review"> <input
						type="text" name="review" placeholder="Viết đánh giá...">
					<button type="submit">Gửi đánh giá</button>
				</form>
			</div>
		</c:forEach>
	</div>

	<!-- Tab: Đã giao -->
	<div id="completed" class="tab-content">
		<c:if test="${empty completedOrders}">
			<p>Bạn chưa có đơn hàng nào đã giao.</p>
		</c:if>
		<c:forEach var="order" items="${completedOrders}">
			<div class="order-block">
				<p>
					<strong>Mã đơn:</strong> ${order.id} | <strong>Ngày hoàn
						thành:</strong> ${order.createdAt}
				</p>
				<table>
					<tr>
						<th>Sản phẩm</th>
						<th>Số lượng</th>
						<th>Đơn giá</th>
					</tr>
					<c:forEach var="op" items="${order.orderProducts}">
						<tr>
							<td>${op.product.name}</td>
							<td>${op.quantity}</td>
							<td><fmt:formatNumber value="${op.product.price}"
									type="number" groupingUsed="true" /> VNĐ</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</c:forEach>
	</div>

</body>
</html>
