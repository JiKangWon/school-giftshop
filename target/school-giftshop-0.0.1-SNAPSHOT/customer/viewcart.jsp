<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- URI mới --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- URI mới --%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Giỏ hàng - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa;
    }
    .table th, .table td {
        vertical-align: middle;
    }
    .quantity-input {
         width: 60px; /* Điều chỉnh độ rộng ô số lượng nếu cần */
         text-align: center;
    }
    .total-section {
        font-size: 1.2rem;
        font-weight: bold;
    }
    #total {
        color: #dc3545; /* Màu đỏ cho tổng tiền */
    }
</style>
</head>
<body>
    <fmt:setLocale value="vi_VN" />

    <%-- Include header đã style --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4 mb-5">
        <h2 class="text-center mb-4 fw-bold"><i class="bi bi-cart3"></i> Giỏ hàng của bạn</h2>

        <c:choose>
            <c:when test="${empty cartItems}">
                <div class="alert alert-info text-center" role="alert">
                    Giỏ hàng hiện đang trống. <a href="${pageContext.request.contextPath}/CustomerHome" class="alert-link">Tiếp tục mua sắm</a>.
                </div>
            </c:when>
            <c:otherwise>
                <%-- Form chính bao quanh bảng và nút đặt hàng --%>
                <form id="checkoutForm" action="${pageContext.request.contextPath}/Checkout" method="get">
                    <div class="card shadow-sm border-0">
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover table-striped align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th scope="col" class="text-center" style="width: 5%;">
                                                <%-- Checkbox chọn tất cả (Tùy chọn) --%>
                                                <%-- <input class="form-check-input" type="checkbox" id="selectAllCheckbox"> --%>
                                            </th>
                                            <th scope="col" style="width: 40%;">Sản phẩm</th>
                                            <th scope="col" class="text-end" style="width: 15%;">Đơn giá</th>
                                            <th scope="col" class="text-center" style="width: 15%;">Số lượng</th>
                                            <th scope="col" class="text-end" style="width: 15%;">Thành tiền</th>
                                            <th scope="col" class="text-center" style="width: 10%;">Xóa</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="item" items="${cartItems}">
                                            <tr>
                                                <td class="text-center">
                                                    <input class="form-check-input product-check" type="checkbox"
                                                           data-total="${item.product.price * item.quantity}"
                                                           data-productid="${item.product.id}">
                                                </td>
                                                <td>
                                                    <%-- TODO: Thêm ảnh sản phẩm nếu có --%>
                                                    ${item.product.name}
                                                </td>
                                                <td class="text-end">
                                                    <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                </td>
                                                <td class="text-center">
                                                    <%-- Nhóm nút tăng giảm số lượng --%>
                                                    <div class="input-group input-group-sm justify-content-center">
                                                         <%-- Nút Giảm --%>
                                                        <form action="${pageContext.request.contextPath}/CartAction" method="post" class="d-inline">
                                                            <input type="hidden" name="productId" value="${item.product.id}" />
                                                            <button type="submit" name="action" value="decrease" class="btn btn-outline-secondary btn-sm" ${item.quantity <= 1 ? 'disabled' : ''}>
                                                                <i class="bi bi-dash-lg"></i>
                                                            </button>
                                                        </form>

                                                         <%-- Hiển thị số lượng (có thể làm input) --%>
                                                        <span class="quantity-input border-top border-bottom px-2">${item.quantity}</span>

                                                         <%-- Nút Tăng --%>
                                                        <form action="${pageContext.request.contextPath}/CartAction" method="post" class="d-inline">
                                                            <input type="hidden" name="productId" value="${item.product.id}" />
                                                            <button type="submit" name="action" value="increase" class="btn btn-outline-secondary btn-sm" ${item.quantity >= item.product.stock ? 'disabled' : ''}>
                                                                <i class="bi bi-plus-lg"></i>
                                                            </button>
                                                        </form>
                                                    </div>
                                                </td>
                                                <td class="text-end">
                                                    <fmt:formatNumber value="${item.product.price * item.quantity}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                </td>
                                                <td class="text-center">
                                                    <%-- Nút Xóa --%>
                                                    <form action="${pageContext.request.contextPath}/CartAction" method="post" class="d-inline">
                                                        <input type="hidden" name="productId" value="${item.product.id}" />
                                                        <button type="submit" name="action" value="remove" class="btn btn-sm btn-outline-danger" title="Xóa sản phẩm">
                                                            <i class="bi bi-trash3"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div> <%-- End table-responsive --%>
                        </div> <%-- End card-body --%>
                    </div> <%-- End card --%>

                    <%-- Phần Tổng tiền và Đặt hàng --%>
                    <div class="d-flex justify-content-end align-items-center mt-4 p-3 bg-white rounded shadow-sm">
                        <div class="me-3 total-section">
                            Tổng tiền (<span id="selectedCount">0</span> sản phẩm): <span id="total">0 ₫</span>
                        </div>
                        <input type="hidden" name="productIds" id="productIds" />
                        <button type="submit" class="btn btn-primary btn-lg" id="checkoutButton" disabled>
                            <i class="bi bi-bag-check-fill"></i> Đặt hàng
                        </button>
                    </div>
                </form>

                <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        const checkboxes = document.querySelectorAll('.product-check');
                        const totalElement = document.getElementById('total');
                        const selectedCountElement = document.getElementById('selectedCount');
                        const productIdsInput = document.getElementById('productIds');
                        const checkoutButton = document.getElementById('checkoutButton');
                        // const selectAllCheckbox = document.getElementById('selectAllCheckbox'); // Nếu có checkbox chọn tất cả

                        function updateTotalAndIds() {
                            let total = 0;
                            let selectedCount = 0;
                            let selectedIds = [];
                            checkboxes.forEach(cb => {
                                if (cb.checked) {
                                    total += parseFloat(cb.dataset.total);
                                    selectedIds.push(cb.dataset.productid);
                                    selectedCount++;
                                }
                            });
                            totalElement.textContent = total.toLocaleString('vi-VN') + ' ₫';
                            productIdsInput.value = selectedIds.join(',');
                            selectedCountElement.textContent = selectedCount;

                            // Bật/tắt nút Đặt hàng
                            checkoutButton.disabled = selectedCount === 0;

                            // (Tùy chọn) Cập nhật trạng thái checkbox chọn tất cả
                            // if (selectAllCheckbox) {
                            //     selectAllCheckbox.checked = selectedCount === checkboxes.length && checkboxes.length > 0;
                            //     selectAllCheckbox.indeterminate = selectedCount > 0 && selectedCount < checkboxes.length;
                            // }
                        }

                        checkboxes.forEach(cb => {
                            cb.addEventListener('change', updateTotalAndIds);
                        });

                        // (Tùy chọn) Xử lý checkbox chọn tất cả
                        // if (selectAllCheckbox) {
                        //     selectAllCheckbox.addEventListener('change', function() {
                        //         checkboxes.forEach(cb => {
                        //             cb.checked = this.checked;
                        //         });
                        //         updateTotalAndIds();
                        //     });
                        // }

                        // Tính tổng lần đầu khi tải trang (nếu có checkbox nào được check sẵn)
                        updateTotalAndIds();
                    });
                </script>
            </c:otherwise>
        </c:choose>
    </div> <%-- End container --%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>