<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- Thêm taglib JSTL --%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Đổi mật khẩu - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa; /* Màu nền xám nhạt */
    }
</style>
</head>
<body>

    <%-- Include header đã được style bằng Bootstrap --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4 mb-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow-sm border-0 rounded-3">
                    <div class="card-body p-4 p-md-5">
                        <h2 class="card-title text-center mb-4 fw-bold">Đổi mật khẩu</h2>

                        <%-- Hiển thị thông báo (nếu có) --%>
                        <c:if test="${not empty requestScope.message}">
                            <div class="alert alert-info" role="alert">
                                ${requestScope.message}
                            </div>
                        </c:if>
                         <c:if test="${not empty requestScope.error}"> <%-- Thêm xử lý lỗi nếu có --%>
                            <div class="alert alert-danger" role="alert">
                                ${requestScope.error}
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/ChangePassword"> <%-- Thêm contextPath cho action --%>

                            <div class="mb-3">
                                <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
                                <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                            </div>

                            <div class="mb-3">
                                <label for="newPassword" class="form-label">Mật khẩu mới</label>
                                <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                            </div>

                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Nhập lại mật khẩu mới</label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                            </div>

                            <div class="d-grid mt-4">
                                <button type="submit" class="btn btn-primary btn-lg">Xác nhận đổi mật khẩu</button>
                            </div>
                        </form>

                        <div class="text-center mt-3">
                             <%-- Sửa link quay lại --%>
                            <a href="${pageContext.request.contextPath}/CustomerProfile" class="btn btn-link text-decoration-none">
                                <i class="bi bi-arrow-left"></i> Quay lại thông tin cá nhân
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>