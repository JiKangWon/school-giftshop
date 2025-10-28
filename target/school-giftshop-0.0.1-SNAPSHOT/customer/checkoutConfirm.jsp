<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- URI mới --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- URI mới --%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Xác nhận đơn hàng - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa;
    }
    .table th, .table td {
        vertical-align: middle;
    }
    .total-row strong {
        font-size: 1.1em;
    }
</style>
</head>
<body>

    <%-- Include header đã style --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4 mb-5">
        <h2 class="mb-4 text-center fw-bold">Xác nhận đơn hàng</h2>

        <%-- Thông tin giao hàng --%>
        <div class="card mb-4 shadow-sm">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="bi bi-person-fill"></i> Thông tin người nhận</h5>
            </div>
            <div class="card-body">
                <p class="card-text"><strong>Tên:</strong> ${sessionScope.user.name}</p>
                <p class="card-text"><strong>Số điện thoại:</strong> ${sessionScope.user.phone}</p>
            </div>
        </div>

        <div class="card mb-4 shadow-sm">
             <div class="card-header bg-info text-white">
                <h5 class="mb-0"><i class="bi bi-geo-alt-fill"></i> Địa chỉ giao hàng</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty address}">
                        <p class="card-text">
                            ${sessionScope.user.addressNumber} ${address.street}, ${address.ward}, ${address.district}, ${address.province}, ${address.country}
                        </p>
                        <%-- TODO: Thêm nút thay đổi địa chỉ nếu cần --%>
                         <a href="${pageContext.request.contextPath}/CustomerProfile" class="btn btn-sm btn-outline-secondary">Thay đổi địa chỉ</a>
                    </c:when>
                    <c:otherwise>
                        <p class="text-danger">Bạn chưa cập nhật địa chỉ giao hàng.</p>
                         <a href="${pageContext.request.contextPath}/CustomerProfile" class="btn btn-sm btn-warning">Cập nhật địa chỉ ngay</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <%-- Chi tiết giỏ hàng --%>
        <h3 class="mb-3">Chi tiết đơn hàng</h3>
        <form id="checkoutForm" action="${pageContext.request.contextPath}/CheckoutConfirm" method="post">
            <div class="card shadow-sm">
                <div class="card-body p-0"> <%-- p-0 để table sát viền card --%>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0"> <%-- mb-0 loại bỏ margin đáy của table --%>
                            <thead class="table-light">
                                <tr>
                                    <th scope="col" class="text-center" style="width: 5%;">Chọn</th>
                                    <th scope="col" style="width: 45%;">Tên sản phẩm</th>
                                    <th scope="col" class="text-center" style="width: 10%;">Số lượng</th>
                                    <th scope="col" class="text-end" style="width: 20%;">Đơn giá</th>
                                    <th scope="col" class="text-end" style="width: 20%;">Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:set var="total" value="0" />
                                <c:forEach var="item" items="${cart}">
                                    <c:set var="lineTotal" value="${item.quantity * item.product.price}" />
                                    <tr>
                                        <td class="text-center">
                                            <div class="form-check d-flex justify-content-center">
                                                 <%-- Đảm bảo checkbox gửi đúng giá trị khi form submit --%>
                                                <input class="form-check-input" type="checkbox" name="productIds"
                                                       value="${item.product.id}" checked id="check_${item.product.id}">
                                            </div>
                                        </td>
                                        <td>${item.product.name}</td>
                                        <td class="text-center">${item.quantity}</td>
                                        <td class="text-end">
                                            <fmt:setLocale value="vi_VN"/>
                                            <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                        </td>
                                        <td class="text-end">
                                             <fmt:setLocale value="vi_VN"/>
                                            <fmt:formatNumber value="${lineTotal}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                        </td>
                                    </tr>
                                    <c:set var="total" value="${total + lineTotal}" />
                                </c:forEach>
                            </tbody>
                             <%-- Footer của bảng để hiển thị tổng tiền --%>
                            <tfoot>
                                <tr class="table-light total-row">
                                    <td colspan="4" class="text-end"><strong>Tổng tiền:</strong></td>
                                    <td class="text-end">
                                        <strong>
                                            <fmt:setLocale value="vi_VN"/>
                                            <fmt:formatNumber value="${total}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                        </strong>
                                    </td>
                                </tr>
                             </tfoot>
                        </table>
                    </div> <%-- End table-responsive --%>
                </div> <%-- End card-body --%>
            </div> <%-- End card --%>

            <%-- Nút bấm xác nhận/hủy --%>
            <div class="d-flex justify-content-between mt-4">
                <a href="${pageContext.request.contextPath}/ViewCart" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Hủy và quay lại giỏ hàng
                </a>
                <%-- Chỉ cho phép đặt hàng nếu có địa chỉ --%>
                <button type="submit" class="btn btn-primary btn-lg" ${empty address ? 'disabled' : ''}>
                    <i class="bi bi-check-circle-fill"></i> Xác nhận đặt hàng
                </button>
            </div>
            <c:if test="${empty address}">
                 <div class="alert alert-danger mt-3" role="alert">
                    Vui lòng cập nhật địa chỉ giao hàng trước khi đặt hàng.
                </div>
            </c:if>

        </form>
    </div> <%-- End container --%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <%-- (Tùy chọn) JavaScript để tính lại tổng tiền nếu bỏ check sản phẩm --%>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const checkboxes = document.querySelectorAll('input[name="productIds"]');
            const totalElement = document.querySelector('.total-row td:last-child strong'); // Phần tử hiển thị tổng tiền

            // Lưu tổng tiền ban đầu
            const initialTotal = parseFloat('${total}'); // Lấy giá trị total từ JSTL

            // Hàm tính lại tổng tiền
            function updateTotal() {
                let currentTotal = 0;
                checkboxes.forEach(checkbox => {
                    if (checkbox.checked) {
                        // Tìm dòng cha (tr) và lấy giá trị thành tiền
                        const row = checkbox.closest('tr');
                        const lineTotalText = row.querySelector('td:last-child').textContent.trim();
                        // Chuyển đổi text thành số (loại bỏ ký hiệu tiền tệ và dấu phẩy)
                        const lineTotalValue = parseFloat(lineTotalText.replace(/[₫.,]/g, ''));
                        if (!isNaN(lineTotalValue)) {
                             currentTotal += lineTotalValue;
                        }
                    }
                });
                // Cập nhật hiển thị tổng tiền
                // Bạn cần hàm format tiền tệ bằng JavaScript nếu muốn hiển thị đẹp như JSTL
                totalElement.textContent = formatCurrency(currentTotal) + ' ₫';
            }

            // Hàm format tiền tệ đơn giản (có thể cải thiện)
            function formatCurrency(number) {
                 return number.toLocaleString('vi-VN');
            }


            // Thêm sự kiện 'change' cho mỗi checkbox
            checkboxes.forEach(checkbox => {
                checkbox.addEventListener('change', updateTotal);
            });

            // Gọi hàm tính tổng lần đầu (có thể không cần nếu mặc định check hết)
            // updateTotal();
        });
    </script>

</body>
</html>