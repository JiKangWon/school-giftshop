<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Kênh Người Bán - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa;
    }
    .sidebar {
        height: 100vh; /* Sidebar cao hết màn hình */
        position: sticky;
        top: 0;
        background-color: #fff;
        border-right: 1px solid #dee2e6;
    }
    .main-content {
        padding-top: 20px;
    }
</style>
</head>
<body>

    <header class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold text-primary" href="${pageContext.request.contextPath}/seller/home">
                School Giftshop 
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#sellerNavbar">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="sellerNavbar">
                <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle"></i>
                            <c:if test="${not empty sessionScope.user}">
                                ${sessionScope.user.name}
                            </c:if>
                            <c:if test="${empty sessionScope.user}">
                                Tài khoản
                            </c:if>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <li><a class="dropdown-item" href="#">Thông tin tài khoản</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/seller-page/logout">Đăng xuất</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </header>

    <div class="container-fluid">
        <div class="row">
            
            <nav class="col-md-3 col-lg-2 d-md-block sidebar collapse p-3">
                <div class="position-sticky">
                    <ul class="nav flex-column nav-pills">
                        <li class="nav-item">
                            <a class="nav-link active" href="${pageContext.request.contextPath}/seller-page/home">
                                <i class="bi bi-house-door-fill"></i> Trang chủ
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/seller-page/products">
                                <i class="bi bi-box-seam-fill"></i> Quản lý sản phẩm
                            </a>
                        </li>
                         <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/seller-page/add-product">
                                <i class="bi bi-plus-circle-fill"></i> Đăng sản phẩm
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/seller-page/orders">
                                <i class="bi bi-clipboard-check-fill"></i> Lịch sử bán hàng
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/seller-page/report">
                                <i class="bi bi-bar-chart-line-fill"></i> Báo cáo doanh thu
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>

            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 main-content">
                
                <c:if test="${not empty requestScope.view}">
                    <jsp:include page="${requestScope.view}" />
                </c:if>
                <c:if test="${empty requestScope.view}">
                    <h2>Chào mừng đến với Kênh Người Bán</h2>
                    <p>Vui lòng chọn một chức năng từ thanh bên trái.</p>
                </c:if>           
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>