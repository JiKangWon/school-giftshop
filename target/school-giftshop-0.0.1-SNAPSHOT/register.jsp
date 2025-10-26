<%@ page import="model.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Đăng ký - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    /* Thêm màu nền cho body */
    body {
        background-color: #f8f9fa; /* Màu bg-light của Bootstrap */
    }
</style>
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-10 col-lg-8 col-xl-6">
                
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-body p-4 p-md-5">

                        <h3 class="text-center mb-4 fw-bold">Tạo tài khoản</h3>
                        <p class="text-center text-muted mb-4">Tham gia School Giftshop ngay hôm nay!</p>
                        
                        <form action="${pageContext.request.contextPath}/register" method="post" novalidate>
                            
                            <div class="mb-3 input-group">
                                <span class="input-group-text"><i class="bi bi-person-fill"></i></span>
                                <div class="form-floating">
                                    <input required type="text" class="form-control" id="username" name="username"
                                           placeholder="Tên đăng nhập" value="${param.username}">
                                    <label for="username">Tên đăng nhập (username)</label>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3 input-group">
                                        <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                                        <div class="form-floating">
                                            <input required type="password" class="form-control" id="password" name="password"
                                                   placeholder="Mật khẩu" pattern=".{6,}">
                                            <label for="password">Mật khẩu</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3 input-group">
                                        <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                                        <div class="form-floating">
                                            <input required type="password" class="form-control" id="confirm_password" name="confirm_password"
                                                   placeholder="Nhập lại mật khẩu" pattern=".{6,}">
                                            <label for="confirm_password">Nhập lại mật khẩu</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="mb-3 input-group">
                                <span class="input-group-text"><i class="bi bi-person-vcard"></i></span>
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="name" name="name" placeholder="Họ tên"
                                           value="${param.name}">
                                    <label for="name">Họ và tên</label>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-8">
                                    <div class="mb-3 input-group">
                                        <span class="input-group-text"><i class="bi bi-geo-alt-fill"></i></span>
                                        <div class="form-floating">
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
                                            <label for="address_id">Địa chỉ (Address)</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3 input-group">
                                         <span class="input-group-text"><i class="bi bi-house-fill"></i></span>
                                        <div class="form-floating">
                                            <input type="text" class="form-control" id="addressNumber" name="addressNumber"
                                                   placeholder="Số nhà" value="${param.addressNumber}">
                                            <label for="addressNumber">Số nhà</LAbel>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3 input-group">
                                <span class="input-group-text"><i class="bi bi-telephone-fill"></i></span>
                                <div class="form-floating">
                                    <input type="tel" class="form-control" id="phone" name="phone" placeholder="0123456789"
                                           pattern="[0-9()+\- ]{7,20}" value="${param.phone}">
                                    <label for="phone">Số điện thoại</label>
                                </div>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-primary btn-lg">Đăng ký</button>
                            </div>
                            
                            <div class="text-center">
                                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">Đã có tài khoản? Quay về đăng nhập</a>
                            </div>

                        </form>
                        </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>