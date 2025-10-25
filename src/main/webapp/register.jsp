<%@ page import="model.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>login</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<div class="container">
	<form action="${pageContext.request.contextPath}/register" method="post" novalidate>
        <div class="mb-3">
            <label for="username" class="form-label">Tên đăng nhập (username)</label>
            <input required type="text" class="form-control" id="username" name="username"
                   placeholder="Tên đăng nhập" value="${param.username}">
        </div>

        <div class="mb-3">
            <label for="password" class="form-label">Mật khẩu</label>
            <input required type="password" class="form-control" id="password" name="password"
                   placeholder="Mật khẩu (ít nhất 6 ký tự)" pattern=".{6,}">
        </div>
        
        <div class="mb-3">
            <label for="confirm_password" class="form-label">Nhập lại mật khẩu</label>
            <input required type="password" class="form-control" id="confirm_password" name="confirm_password"
                   placeholder="Mật khẩu (ít nhất 6 ký tự)" pattern=".{6,}">
        </div>

        <div class="mb-3">
            <label for="name" class="form-label">Họ và tên</label>
            <input type="text" class="form-control" id="name" name="name" placeholder="Họ tên"
                   value="${param.name}">
        </div>

        <div class="mb-3">
            <label for="address_id" class="form-label">Địa chỉ (Address)</label>
            <select id="address_id" name="address_id" class="form-select" required>
                <option value="">-- Chọn địa chỉ --</option>
                <c:forEach var="address" items="${arrAddress}">
                	<option value="${address.id}">
                		${address.country }
                		<c:if test="${not empty address.province}">, ${address.province}</c:if>
                		<c:if test="${not empty address.district}">, ${address.district}</c:if>
                		<c:if test="${not empty address.ward}">, ${address.ward}</c:if>
                		<c:if test="${not empty address.street}">, ${address.street}</c:if>
                	</option>
                </c:forEach>
            </select>
        </div>

        <div class="mb-3">
            <label for="addressNumber" class="form-label">Số nhà / số căn</label>
            <input type="text" class="form-control" id="addressNumber" name="addressNumber"
                   placeholder="Số nhà" value="${param.addressNumber}">
        </div>

        <div class="mb-3">
            <label for="phone" class="form-label">Số điện thoại</label>
            <input type="tel" class="form-control" id="phone" name="phone" placeholder="0123456789"
                   pattern="[0-9()+\- ]{7,20}" value="${param.phone}">
        </div>

        <button type="submit" class="btn btn-primary">Đăng ký</button>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-link">Quay về đăng nhập</a>
    </form>
    </div>
</body>
</html>