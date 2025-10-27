<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Đăng nhập - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa; /* Màu nền xám nhạt */
    }
</style>
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5 col-xl-4">
                
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-body p-4 p-md-5">

                        <h3 class="text-center mb-4 fw-bold">Đăng nhập</h3>
                        
                        <form action="${pageContext.request.contextPath}/login" method="post">
                            
                            <c:if test="${not empty requestScope.err}">
                                <div class="alert alert-danger" role="alert">
                                    ${requestScope.err}
                                </div>
                            </c:if>

                            <div class="mb-3 input-group">
                                <span class="input-group-text"><i class="bi bi-person-fill"></i></span>
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="username" name="username"
                                           placeholder="Tên đăng nhập" required>
                                    <label for="username">Tên đăng nhập</label>
                                </div>
                            </div>

                            <div class="mb-3 input-group">
                                <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                                <div class="form-floating">
                                    <input type="password" class="form-control" id="password" name="password"
                                           placeholder="Mật khẩu" required>
                                    <label for="password">Mật khẩu</label>
                                </div>
                            </div>
                            
                            <div class="d-flex justify-content-end mb-3">
                                <a href="#" class="text-decoration-none small">Quên mật khẩu?</a>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-primary btn-lg fw-bold">Đăng nhập</button>
                            </div>
                            
                            <div class="text-center">
                                <span class="text-muted">Chưa có tài khoản?</span>
                                <a href="${pageContext.request.contextPath}/register" class="text-decoration-none fw-bold">Đăng ký ngay</a>
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
