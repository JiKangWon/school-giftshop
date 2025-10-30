<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Đơn hàng của tôi - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa;
    }
    .order-card .card-header {
        font-weight: 500;
    }
    .order-card table {
        margin-bottom: 0;
    }
</style>
</head>
<body>
    <fmt:setLocale value="vi_VN" />

    <%-- Include header đã style --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4 mb-5">
        <h2 class="mb-4 text-center fw-bold">Đơn hàng của tôi</h2>

        <ul class="nav nav-tabs nav-fill mb-3" id="orderTab" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="shipping-tab" data-bs-toggle="tab" data-bs-target="#shipping-tab-pane" type="button" role="tab" aria-controls="shipping-tab-pane" aria-selected="true">
                    <i class="bi bi-truck"></i> Đang vận chuyển
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="review-tab" data-bs-toggle="tab" data-bs-target="#review-tab-pane" type="button" role="tab" aria-controls="review-tab-pane" aria-selected="false">
                    <i class="bi bi-pencil-square"></i> Chờ đánh giá
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="completed-tab" data-bs-toggle="tab" data-bs-target="#completed-tab-pane" type="button" role="tab" aria-controls="completed-tab-pane" aria-selected="false">
                    <i class="bi bi-check2-circle"></i> Đã giao
                </button>
            </li>
        </ul>

        <div class="tab-content" id="orderTabContent">

            <div class="tab-pane fade show active" id="shipping-tab-pane" role="tabpanel" aria-labelledby="shipping-tab" tabindex="0">
                <c:choose>
                    <c:when test="${empty processingOrders}">
                        <div class="alert alert-info text-center" role="alert">
                            Không có đơn hàng nào đang vận chuyển.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <%-- VÒNG LẶP 1: ĐANG VẬN CHUYỂN --%>
                        <c:forEach var="entry" items="${processingOrders}">
                            <c:set var="order" value="${entry.order}" /> <%-- Lấy order từ map --%>
                            <div class="card shadow-sm mb-3 order-card">
                                <div class="card-header bg-white">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span><strong>Mã đơn:</strong> #${order.id}</span>
                                        <span class="badge bg-primary">${order.status}</span>
                                    </div>
                                    <%-- SỬA LỖI: Dùng biến đã format --%>
                                    <small class="text-muted">Ngày đặt: ${entry.createdAtFormatted}</small>
                                </div>
                                <div class="card-body p-0">
                                    <div class="table-responsive">
                                        <table class="table table-sm table-striped mb-0">
                                            <thead>
                                                <tr>
                                                    <th>Sản phẩm</th>
                                                    <th class="text-center">Số lượng</th>
                                                    <th class="text-end">Đơn giá</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="op" items="${order.orderProducts}">
                                                    <tr>
                                                        <td>${op.product.name}</td>
                                                        <td class="text-center">${op.quantity}</td>
                                                        <td class="text-end">
                                                            <fmt:formatNumber value="${op.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <div class="card-footer bg-white text-end">
                                    <%-- Form xác nhận đã nhận hàng (Trỏ về MyOrderServlet) --%>
                                    <form method="post" action="${pageContext.request.contextPath}/MyOrder" class="d-inline-block">
                                        <input type="hidden" name="orderId" value="${order.id}">
                                        <input type="hidden" name="action" value="received">
                                        <button type="submit" class="btn btn-success btn-sm">
                                            <i class="bi bi-check-lg"></i> Đã nhận hàng
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="tab-pane fade" id="review-tab-pane" role="tabpanel" aria-labelledby="review-tab" tabindex="0">
                 <c:choose>
                    <c:when test="${empty reviewProducts}">
                        <div class="alert alert-info text-center" role="alert">
                             Không có sản phẩm nào chờ đánh giá.
                        </div>
                    </c:when>
                    <c:otherwise>
                         <%-- VÒNG LẶP 2: ĐÁNH GIÁ (Dùng 'entry' và 'item') --%>
                        <c:forEach var="entry" items="${reviewProducts}">
                            <c:set var="item" value="${entry.item}" /> <%-- Lấy item từ map --%>
                            <div class="card shadow-sm mb-3 order-card">
                                <div class="card-body">
                                    <div class="row align-items-center">
                                        <div class="col-md-8">
                                            <h5 class="card-title">${item.product.name}</h5>
                                            <p class="card-text mb-1">
                                                <small class="text-muted">Số lượng: ${item.quantity} | Giá:
                                                    <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                </small>
                                            </p>
                                             <small class="text-muted">Ngày nhận: ${entry.receivedAtFormatted}</small> <%-- SỬA LỖI: Dùng biến đã format --%>
                                        </div>
                                        <div class="col-md-4">
                                            <%-- Form gửi đánh giá (Trỏ về MyOrderServlet) --%>
                                            <form method="post" action="${pageContext.request.contextPath}/MyOrder">
                                                <input type="hidden" name="orderProductId" value="${item.id}"> <%-- Gửi ID của OrderProduct --%>
                                                <input type="hidden" name="action" value="review">
                                                <div class="input-group input-group-sm">
                                                    <input type="text" class="form-control" name="review" placeholder="Viết đánh giá của bạn..." required>
                                                    <button type="submit" class="btn btn-primary">
                                                         <i class="bi bi-send"></i> Gửi
                                                    </button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                     </c:otherwise>
                </c:choose>
            </div>

            <div class="tab-pane fade" id="completed-tab-pane" role="tabpanel" aria-labelledby="completed-tab" tabindex="0">
                 <c:choose>
                    <c:when test="${empty completedOrders}">
                         <div class="alert alert-info text-center" role="alert">
                             Bạn chưa có đơn hàng nào hoàn thành.
                        </div>
                    </c:when>
                    <c:otherwise>
                         <%-- VÒNG LẶP 3: ĐÃ GIAO --%>
                        <c:forEach var="entry" items="${completedOrders}">
                             <c:set var="order" value="${entry.order}" /> <%-- Lấy order từ map --%>
                             <div class="card shadow-sm mb-3 order-card">
                                <div class="card-header bg-white">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span><strong>Mã đơn:</strong> #${order.id}</span>
                                        <span class="badge bg-success">${order.status}</span>
                                    </div>
                                    <%-- SỬA LỖI: Dùng biến đã format --%>
                                    <small class="text-muted">Ngày hoàn thành: ${entry.completedAtFormatted}</small> 
                                </div>
                                <div class="card-body p-0">
                                     <div class="table-responsive">
                                        <table class="table table-sm table-striped mb-0">
                                             <thead>
                                                <tr>
                                                    <th>Sản phẩm</th>
                                                    <th class="text-center">Số lượng</th>
                                                    <th class="text-end">Đơn giá</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="op" items="${order.orderProducts}">
                                                    <tr>
                                                        <td>${op.product.name}</td>
                                                        <td class="text-center">${op.quantity}</td>
                                                        <td class="text-end">
                                                            <fmt:formatNumber value="${op.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div> <%-- End Tab Content --%>
    </div> <%-- End Container --%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>