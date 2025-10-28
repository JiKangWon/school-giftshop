<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- URI mới --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- URI mới --%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${product.name} - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa;
    }
    .product-price {
        font-size: 1.5rem;
        font-weight: bold;
        color: #dc3545; /* Màu đỏ */
    }
    .carousel-item img {
        max-height: 450px; /* Giới hạn chiều cao ảnh carousel */
        object-fit: contain; /* Hiển thị toàn bộ ảnh, không cắt xén */
        margin: auto; /* Căn giữa ảnh trong carousel */
    }
    .single-product-image img {
         max-height: 450px;
         object-fit: contain;
         display: block; /* Để căn giữa hoạt động */
         margin: auto;
    }
    .review-item {
        border-bottom: 1px solid #eee;
    }
    .review-item:last-child {
        border-bottom: none;
    }
</style>
</head>
<body>
    <fmt:setLocale value="vi_VN" />

    <%-- Include header đã style --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4 mb-5">
        <div class="card shadow-sm border-0">
            <div class="card-body p-4">
                <div class="row g-4"> <%-- g-4 tạo khoảng cách giữa các cột --%>

                    <%-- Cột bên trái: Hình ảnh sản phẩm --%>
                    <div class="col-md-6">
                        <c:choose>
                            <%-- Nếu có nhiều hơn 1 ảnh, dùng Carousel --%>
                            <c:when test="${not empty images && images.size() > 1}">
                                <div id="productImageCarousel" class="carousel slide" data-bs-ride="carousel">
                                    <div class="carousel-indicators">
                                        <c:forEach var="img" items="${images}" varStatus="loop">
                                            <button type="button" data-bs-target="#productImageCarousel" data-bs-slide-to="${loop.index}" class="${loop.first ? 'active' : ''}" aria-current="${loop.first ? 'true' : 'false'}" aria-label="Slide ${loop.count}"></button>
                                        </c:forEach>
                                    </div>
                                    <div class="carousel-inner rounded border">
                                        <c:forEach var="img" items="${images}" varStatus="loop">
                                            <div class="carousel-item ${loop.first ? 'active' : ''}">
                                                <img src="${img}" class="d-block w-100" alt="${product.name} - ảnh ${loop.count}">
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <button class="carousel-control-prev" type="button" data-bs-target="#productImageCarousel" data-bs-slide="prev">
                                        <span class="carousel-control-prev-icon bg-dark rounded-circle p-2" aria-hidden="true"></span>
                                        <span class="visually-hidden">Previous</span>
                                    </button>
                                    <button class="carousel-control-next" type="button" data-bs-target="#productImageCarousel" data-bs-slide="next">
                                        <span class="carousel-control-next-icon bg-dark rounded-circle p-2" aria-hidden="true"></span>
                                        <span class="visually-hidden">Next</span>
                                    </button>
                                </div>
                            </c:when>
                             <%-- Nếu có đúng 1 ảnh --%>
                            <c:when test="${not empty images && images.size() == 1}">
                                 <div class="single-product-image text-center rounded border p-2">
                                    <img src="${images[0]}" class="img-fluid" alt="${product.name}">
                                </div>
                            </c:when>
                            <%-- Nếu không có ảnh nào, dùng ảnh mặc định --%>
                            <c:otherwise>
                                <div class="single-product-image text-center rounded border p-2">
                                     <%-- TODO: Thay đổi đường dẫn ảnh mặc định nếu cần --%>
                                    <img src="${pageContext.request.contextPath}/images/default.jpg" class="img-fluid" alt="Ảnh mặc định">
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <%-- Cột bên phải: Thông tin chi tiết và mua hàng --%>
                    <div class="col-md-6 d-flex flex-column">
                        <h2 class="mb-3">${product.name}</h2>

                        <p class="product-price mb-3">
                            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                        </p>

                        <div class="mb-3">
                             <span class="me-3">
                                <i class="bi bi-box-seam"></i> Tồn kho: <span class="fw-bold">${product.stock}</span>
                            </span>
                             <span>
                                <i class="bi bi-bag-check-fill text-success"></i> Đã bán: <span class="fw-bold">${totalSold}</span>
                            </span>
                        </div>

                         <p class="text-muted mb-3">
                            <i class="bi bi-tag-fill"></i> Danh mục: ${product.category.name}
                        </p>

                        <h5 class="mt-2">Mô tả sản phẩm</h5>
                        <p>${product.description}</p> <%-- Sử dụng <p> thay vì <strong> --%>

                        <%-- Form thêm vào giỏ hàng (đặt ở cuối cột) --%>
                        <div class="mt-auto"> <%-- Đẩy form xuống dưới cùng --%>
                            <hr>
                             <form action="${pageContext.request.contextPath}/AddToCart" method="post">
                                <input type="hidden" name="productId" value="${product.id}">
                                <div class="row g-2 align-items-center">
                                    <div class="col-auto">
                                        <label for="quantity" class="col-form-label">Số lượng:</label>
                                    </div>
                                    <div class="col-auto">
                                        <%-- Dùng select nếu số lượng ít, hoặc number input --%>
                                        <input type="number" id="quantity" name="quantity" class="form-control form-control-sm" value="1" min="1" max="${product.stock}" style="width: 70px;">
                                    </div>
                                    <div class="col">
                                        <button type="submit" class="btn btn-primary w-100">
                                            <i class="bi bi-cart-plus-fill"></i> Thêm vào giỏ hàng
                                        </button>
                                    </div>
                                     <%-- Thêm nút Yêu thích nếu cần --%>
                                    <div class="col-auto">
                                         <form action="${pageContext.request.contextPath}/AddToWishlist" method="post" class="d-inline">
                                            <input type="hidden" name="productId" value="${product.id}">
                                            <button class="btn btn-outline-danger" type="submit" title="Thêm vào yêu thích">
                                                <i class="bi bi-heart-fill"></i>
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div> <%-- End Row --%>
            </div> <%-- End Card Body --%>
        </div> <%-- End Card --%>

        <%-- Phần Đánh giá --%>
        <div class="card shadow-sm border-0 mt-4">
            <div class="card-header">
                <h3 class="mb-0">Đánh giá của khách hàng</h3>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty reviews}">
                        <p class="text-muted">Chưa có đánh giá nào cho sản phẩm này.</p>
                    </c:when>
                    <c:otherwise>
                        <ul class="list-group list-group-flush">
                             <c:forEach var="r" items="${reviews}">
                                <li class="list-group-item review-item px-0 py-3">
                                    <div class="d-flex w-100 justify-content-between mb-1">
                                        <h6 class="mb-0"><i class="bi bi-person-circle me-2"></i>${r.order.user.name}</h6>
                                        <small class="text-muted">
                                            <fmt:formatDate value="${r.receivedAt}" pattern="dd/MM/yyyy HH:mm"/> <%-- Giả định receivedAt là ngày đánh giá --%>
                                        </small>
                                    </div>
                                    <p class="mb-1">${r.review}</p>
                                     <%-- TODO: Thêm hiển thị sao đánh giá nếu có --%>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <%-- Nút quay lại trang chủ --%>
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/CustomerHome" class="btn btn-outline-secondary">
                 <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
            </a>
        </div>

    </div> <%-- End Container --%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>