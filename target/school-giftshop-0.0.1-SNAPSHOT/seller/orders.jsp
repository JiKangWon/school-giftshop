<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<fmt:setLocale value="vi_VN" />

<%-- Tiêu đề --%>
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2">Lịch sử bán hàng (Chi tiết sản phẩm đã bán)</h1>
</div>

<%-- Hiển thị thông báo (nếu có) --%>
<c:if test="${not empty requestScope.error}">
    <div class="alert alert-danger">${requestScope.error}</div>
</c:if>

<%-- Hiển thị nếu không có đơn hàng --%>
<c:if test="${empty requestScope.orderProductList}">
    <div class="alert alert-info">Chưa có sản phẩm nào được bán.</div>
</c:if>

<div class="card shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>STT</th>
                        <th>Mã Đơn Hàng</th>
                        <th>Khách Hàng</th>
                        <th>Sản Phẩm</th>
                        <th class="text-center">Số Lượng</th>
                        <th>Ngày Đặt Hàng</th>
                        <th>Trạng Thái Đơn</th>
                        <!-- <th>Vị trí</th> -->
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="entry" items="${requestScope.orderProductList}" varStatus="loop">
                        <%-- Lấy đối tượng OrderProduct từ map --%>
                        <c:set var="item" value="${entry.item}" />
                        <tr>
                            <%-- Tính STT dựa trên trang hiện tại --%>
                            <td>${(requestScope.currentPage - 1) * 10 + loop.count}</td>
                            <td>
                                <span class="badge bg-secondary font-monospace">#${item.order.id}</span> <%-- Có thể link đến chi tiết đơn hàng --%>
                            </td>
                            <td>${item.order.user.name}</td>
                            <td>${item.product.name}</td>
                            <td class="text-center">${item.quantity}</td>
                            <td>${entry.orderCreatedAtFormatted}</td> <%-- Dùng ngày đã format --%>
                            <td>
                                <%-- Badge màu theo trạng thái --%>
                                <span class="badge 
                                    <c:choose>
                                        <c:when test="${item.order.status == 'completed'}">bg-success</c:when>
                                        <c:when test="${item.order.status == 'processing'}">bg-primary</c:when>
                                        <c:when test="${item.order.status == 'pending'}">bg-warning</c:when>
                                        <c:when test="${item.order.status == 'returned'}">bg-danger</c:when>
                                        <c:otherwise>bg-secondary</c:otherwise>
                                    </c:choose>
                                ">${item.order.status}</span>
                            </td>
                            <!-- <td>${item.currentLocation}</td> --> 
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<c:if test="${requestScope.totalPages > 1}">
    <nav aria-label="Page navigation" class="mt-4">
        <ul class="pagination justify-content-center">
            
            <li class="page-item ${requestScope.currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/seller-page/orders?page=${requestScope.currentPage - 1}">
                    Lùi
                </a>
            </li>

            <c:forEach var="i" begin="1" end="${requestScope.totalPages}">
                <li class="page-item ${requestScope.currentPage == i ? 'active' : ''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/seller-page/orders?page=${i}">${i}</a>
                </li>
            </c:forEach>
            
            <li class="page-item ${requestScope.currentPage == requestScope.totalPages ? 'disabled' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/seller-page/orders?page=${requestScope.currentPage + 1}">
                    Tiến
                </a>
            </li>
        </ul>
    </nav>
</c:if>