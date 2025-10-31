<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Kho hàng - Cập nhật vị trí</title>
    <%-- (Thêm link Bootstrap nếu cần) --%>
</head>
<body>
    <h2>Chào mừng kho hàng, ${sessionScope.user.name}</h2>
    <p>Vị trí của bạn: <strong>${sessionScope.user.address.name}</strong></p>
    <hr/>
    <h4>Cập nhật vị trí đơn hàng (Xác nhận hàng đã đến kho)</h4>

    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p style="color: green;">${success}</p>
    </c:if>

    <form method="POST" action="${pageContext.request.contextPath}/transport-page/update-address">
        <label for="orderProductId">Nhập Mã Chi Tiết Đơn Hàng (OrderProduct ID):</label>
        <br>
        <input type="text" id="orderProductId" name="orderProductId" required>
        <br><br>
        <button type="submit">Xác nhận đã đến kho</button>
    </form>
</body>
</html>
