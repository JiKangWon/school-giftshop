<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<fmt:setLocale value="vi_VN" />

<%-- Tiêu đề --%>
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2">Báo cáo doanh thu</h1>
</div>

<%-- Form lọc theo ngày --%>
<div class="card shadow-sm mb-4">
    <div class="card-body">
        <%-- Form trỏ về action /report của SellerServlet --%>
        <form action="${pageContext.request.contextPath}/seller-page/report" method="get">
            <div class="row g-3 align-items-end">
                <div class="col-md-5">
                    <label for="startDate" class="form-label">Từ ngày</label>
                    <%-- Dùng ${requestScope.startDate} để giữ lại giá trị đã chọn --%>
                    <input type="date" class="form-control" id="startDate" name="startDate" value="${requestScope.startDate}">
                </div>
                <div class="col-md-5">
                    <label for="endDate" class="form-label">Đến ngày</label>
                    <input type="date" class="form-control" id="endDate" name="endDate" value="${requestScope.endDate}">
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="bi bi-search"></i> Xem báo cáo
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Hiển thị thông báo (nếu có) --%>
<c:if test="${not empty requestScope.error}">
    <div class="alert alert-danger">${requestScope.error}</div>
</c:if>

<%-- Các thẻ thống kê --%>
<div class="row g-3 mb-4">
    <div class="col-md-6">
        <div class="card shadow-sm text-center">
            <div class="card-body">
                <h5 class="card-title text-success">Tổng doanh thu</h5>
                <p class="card-text fs-3 fw-bold">
                    <fmt:formatNumber value="${requestScope.totalRevenue}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                </p>
                <small class="text-muted">(Từ ${requestScope.startDate} đến ${requestScope.endDate})</small>
            </div>
        </div>
    </div>
    <div class="col-md-6">
        <div class="card shadow-sm text-center">
            <div class="card-body">
                <h5 class="card-title text-primary">Tổng đơn hàng (hoàn thành)</h5>
                <p class="card-text fs-3 fw-bold">
                    ${requestScope.totalOrdersCount}
                </p>
                 <small class="text-muted">(Từ ${requestScope.startDate} đến ${requestScope.endDate})</small>
            </div>
        </div>
    </div>
</div>

<h3 class="h4">Chi tiết đơn hàng đã hoàn thành</h3>
<div class="card shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Mã Đơn Hàng</th>
                        <th>Khách Hàng</th>
                        <th>Ngày Hoàn Thành</th>
                        <th>Tổng Tiền</th>
                        <th class="text-center">Chi tiết</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty requestScope.reportOrders}">
                        <tr>
                            <td colspan="5" class="text-center p-4">Không tìm thấy đơn hàng nào đã hoàn thành trong khoảng thời gian này.</td>
                        </tr>
                    </c:if>
                    
                    <%-- Lặp qua danh sách đã được format ngày --%>
                    <c:forEach var="entry" items="${requestScope.reportOrders}" varStatus="loop">
                        <c:set var="order" value="${entry.order}" />
                        <tr>
                            <td><span class="fw-bold font-monospace">#${order.id}</span></td>
                            <td>${order.user.name}</td>
                            <td>${entry.createdAtFormatted}</td>
                            <td class="text-danger fw-bold">
                                <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                            </td>
                            <td class="text-center">
                                <%-- Nút bấm để xem chi tiết sản phẩm (mở pop-up/modal) --%>
                                <button type="button" class="btn btn-sm btn-outline-info" 
                                        data-bs-toggle="modal" data-bs-target="#orderDetailModal_${order.id}">
                                    <i class="bi bi-eye"></i> Xem
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%-- Modals (Cửa sổ Pop-up) cho chi tiết đơn hàng --%>
<%-- Đặt vòng lặp này bên ngoài bảng để tạo các modal --%>
<c:forEach var="entry" items="${requestScope.reportOrders}">
     <c:set var="order" value="${entry.order}" />
    <div class="modal fade" id="orderDetailModal_${order.id}" tabindex="-1" aria-labelledby="modalLabel_${order.id}" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalLabel_${order.id}">Chi tiết đơn hàng #${order.id}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p><strong>Khách hàng:</strong> ${order.user.name} (${order.user.phone})</p>
                    <p><strong>Ngày đặt:</strong> ${entry.createdAtFormatted}</p>
                    <p><strong>Tổng tiền:</strong> 
                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                    </p>
                    <hr>
                    <strong>Các sản phẩm trong đơn:</strong>
                    <table class="table table-sm table-bordered mt-2">
                        <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th class="text-center">Số lượng</th>
                                <th class="text-end">Đơn giá (lúc mua)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${order.orderProducts}">
                                <tr>
                                    <td>${item.product.name}</td>
                                    <td class="text-center">${item.quantity}</td>
                                    <td class="text-end">
                                        <%-- Lưu ý: DAO CŨ đang lấy giá sản phẩm HIỆN TẠI (p.price) --%>
                                        <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                </div>
            </div>
        </div>
    </div>
</c:forEach>