<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- URI mới --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- URI mới --%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Danh sách yêu thích - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa;
    }
    .product-card img {
        height: 200px;
        object-fit: cover;
    }
    .product-card .card-title {
        height: 3em; /* Giới hạn chiều cao tên SP (khoảng 2 dòng) */
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
    }
    .price {
        font-weight: bold;
        color: #dc3545; /* Màu đỏ cho giá */
    }
</style>
</head>
<body>
    <fmt:setLocale value="vi_VN" />

    <%-- Include header đã style --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4 mb-5">
        <h2 class="text-center mb-4 fw-bold"><i class="bi bi-heart-fill text-danger"></i> Danh sách yêu thích</h2>

        <c:choose>
            <c:when test="${empty favorites}">
                <div class="alert alert-info text-center" role="alert">
                    Danh sách yêu thích của bạn đang trống. <a href="${pageContext.request.contextPath}/CustomerHome" class="alert-link">Khám phá thêm sản phẩm</a>!
                </div>
            </c:when>
            <c:otherwise>
                <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
                    <c:forEach var="f" items="${favorites}" varStatus="loop">
                        <div class="col">
                            <div class="card h-100 shadow-sm product-card">
                                <a href="${pageContext.request.contextPath}/ProductDetail?id=${f.product.id}">
                                    <%-- Giả sử productImages[loop.index] là URL ảnh --%>
                                    <img src="${productImages[loop.index]}" class="card-img-top" alt="${f.product.name}">
                                </a>
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title">
                                        <a href="${pageContext.request.contextPath}/ProductDetail?id=${f.product.id}" class="text-decoration-none text-dark">
                                            ${f.product.name}
                                        </a>
                                    </h5>
                                    <p class="card-text price mb-3">
                                        <fmt:formatNumber value="${f.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                    </p>

                                    <%-- Nút xóa đặt ở cuối card --%>
                                    <div class="mt-auto text-center">
                                        <form action="${pageContext.request.contextPath}/RemoveWishlist" method="post">
                                            <input type="hidden" name="productId" value="${f.product.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger w-100">
                                                <i class="bi bi-trash3"></i> Xóa khỏi yêu thích
                                            </button>
                                        </form>
                                         <%-- Có thể thêm nút "Thêm vào giỏ hàng" ở đây nếu muốn --%>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div> <%-- End row --%>
            </c:otherwise>
        </c:choose>

         <%-- Nút quay lại trang chủ --%>
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/CustomerHome" class="btn btn-outline-secondary">
                 <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
            </a>
        </div>

    </div> <%-- End container --%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>