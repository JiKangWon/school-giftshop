<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- Sử dụng URI JSTL mới nhất cho Tomcat 10+ --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Trang chủ - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    /* CSS tùy chỉnh nhỏ */
    .product-card img {
        height: 200px; /* Giữ chiều cao ảnh đồng nhất */
        object-fit: cover; /* Đảm bảo ảnh không bị méo */
    }
    .product-card .card-title {
        height: 3em; /* Giới hạn chiều cao tên SP (khoảng 2 dòng) */
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2; /* Số dòng tối đa */
        -webkit-box-orient: vertical;
    }
    .price {
        font-weight: bold;
        color: #dc3545; /* Màu đỏ cho giá */
    }
</style>
</head>
<body>
    <%-- Bao gồm header (giả sử header.jsp cũng đã dùng Bootstrap) --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4">

        <div class="row g-3 align-items-center mb-4 p-3 bg-light rounded shadow-sm">
            <%-- Form Tìm kiếm --%>
            <div class="col-md-6">
                <form action="${pageContext.request.contextPath}/CustomerHome" method="get" class="input-group">
                    <input type="text" class="form-control" name="search" placeholder="Nhập tên sản phẩm..."
                           value="${param.search != null ? param.search : ''}" aria-label="Tìm kiếm sản phẩm">
                    <button class="btn btn-outline-primary" type="submit">
                        <i class="bi bi-search"></i> Tìm kiếm
                    </button>
                </form>
            </div>

            <%-- Form Lọc theo danh mục --%>
            <div class="col-md-6">
                <form action="${pageContext.request.contextPath}/CustomerHome" method="get" class="input-group">
                     <label class="input-group-text" for="categorySelect">Danh mục:</label>
                    <select class="form-select" name="categoryId" id="categorySelect" onchange="this.form.submit()">
                        <option value="">-- Tất cả --</option>
                        <c:forEach var="c" items="${categories}">
                            <option value="${c.id}"
                                <c:if test="${param.categoryId != null && param.categoryId eq c.id}">selected</c:if>>
                                ${c.name}
                            </option>
                        </c:forEach>
                    </select>
                </form>
            </div>
        </div>

        <hr class="mb-4">

        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
            <c:forEach var="p" items="${products}" varStatus="loop">
                <div class="col">
                    <div class="card h-100 shadow-sm product-card">
                        <a href="${pageContext.request.contextPath}/ProductDetail?id=${p.id}">
                             <%-- Giả sử productImages[loop.index] là URL ảnh --%>
                            <img src="${productImages[loop.index]}" class="card-img-top" alt="${p.name}">
                        </a>
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title">
                                <a href="${pageContext.request.contextPath}/ProductDetail?id=${p.id}" class="text-decoration-none text-dark">
                                    ${p.name}
                                </a>
                            </h5>
                            <p class="card-text price mb-3">
                                <fmt:formatNumber value="${p.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                            </p>

                            <%-- Phần action đặt ở cuối card --%>
                            <div class="mt-auto">
                                <%-- Form Thêm vào giỏ hàng --%>
                                <form action="${pageContext.request.contextPath}/AddToCart" method="post" class="mb-2">
                                    <input type="hidden" name="productId" value="${p.id}">
                                    <div class="input-group input-group-sm">
                                        <input type="number" class="form-control" name="quantity" value="1" min="1" max="${p.stock}" aria-label="Số lượng">
                                        <button class="btn btn-primary" type="submit">
                                            <i class="bi bi-cart-plus"></i> Thêm
                                        </button>
                                    </div>
                                </form>

                                <%-- Form Thêm vào yêu thích --%>
                                <form action="${pageContext.request.contextPath}/AddToWishlist" method="post" class="d-grid">
                                    <input type="hidden" name="productId" value="${p.id}">
                                    <button class="btn btn-outline-danger btn-sm" type="submit">
                                        <i class="bi bi-heart"></i> Yêu thích
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div> <%-- End row --%>

        <%-- Thông báo nếu không có sản phẩm --%>
        <c:if test="${empty products}">
            <div class="alert alert-warning text-center mt-4" role="alert">
                Không tìm thấy sản phẩm phù hợp.
            </div>
        </c:if>

        <%-- TODO: Thêm phân trang nếu cần --%>
        <%-- Bạn có thể thêm thanh phân trang Bootstrap ở đây nếu Servlet CustomerHome hỗ trợ --%>

    </div> <%-- End container --%>

    <%-- Bootstrap JS --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>