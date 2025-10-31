<%-- File: src/main/webapp/transport/update_address.jsp (Đã nâng cấp) --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kho hàng - Cập nhật vị trí</title>
    
    <%-- Nạp Bootstrap CSS và Icons --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <style>
        body {
            background-color: #f0f2f5; /* Màu nền xám nhạt */
        }
    </style>
</head>
<body class="d-flex align-items-center justify-content-center" style="min-height: 100vh;">

    <div class="container" style="max-width: 500px;">
        <div class="card shadow-lg border-0">
            
            <%-- Header (Theme "Blue / White") --%>
            <div class="card-header bg-primary text-white text-center p-3">
                <h4 class="mb-0"><i class="bi bi-person-workspace"></i> Trang Kho Hàng</h4>
            </div>
            
            <div class="card-body p-4">
                
                <%-- Thông tin người dùng kho hàng --%>
                <div class="text-center mb-3">
                    <p class="h5">Chào mừng, <strong>${sessionScope.user.name}</strong></p>
                    <span class="text-muted small">
                        <i class="bi bi-geo-alt-fill"></i> Vị trí của bạn: 
                        <strong>${sessionScope.user.address.name}</strong>
                    </span>
                </div>
                <hr>

                <h5 class="card-title text-center mb-3">Cập nhật Vị trí Đơn hàng</h5>
                <p class="text-center text-muted small">
                    Xác nhận hàng đã đến kho của bạn.
                </p>

                <%-- Form cập nhật --%>
                <%-- 
                  Lưu ý: Đã sửa 'action' trỏ về /transport/update-address
                  để khớp với Transport.java (Servlet) của bạn.
                --%>
                <form method="POST" action="${pageContext.request.contextPath}/transport/update-address">
                    
                    <%-- Hiển thị thông báo (nếu có) --%>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger d-flex align-items-center" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <div>${error}</div>
                        </div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="alert alert-success d-flex align-items-center" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            <div>${success}</div>
                        </div>
                    </c:if>

                    <%-- Input --%>
                    <div class="mb-3">
                        <label for="orderProductId" class="form-label fw-bold">Nhập Mã Chi Tiết Đơn Hàng (ID):</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-qr-code-scan"></i></span>
                            <input type="text" class="form-control" id="orderProductId" name="orderProductId" 
                                   required placeholder="Ví dụ: 10000030"
                                   value="${param.orderProductId}"> <%-- Giữ lại giá trị cũ nếu có lỗi --%>
                        </div>
                    </div>
                    
                    <%-- Submit --%>
                    <button type="submit" class="btn btn-primary w-100 btn-lg">
                        <i class="bi bi-box-arrow-in-down"></i> Xác nhận đã đến kho
                    </button>
                </form>
                
            </div>
        </div>
    </div>

    <%-- Nạp Bootstrap JS (nếu cần) --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>