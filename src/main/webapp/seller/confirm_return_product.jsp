<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<fmt:setLocale value="vi_VN" />

<%-- Tiêu đề --%>
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2">Xác nhận yêu cầu trả hàng</h1>
</div>

<%-- Hiển thị thông báo (nếu có) --%>
<c:if test="${not empty param.error}">
    <div class="alert alert-danger">Lỗi khi xử lý: ${param.error}</div>
</c:if>
<c:if test="${not empty param.success}">
    <div class="alert alert-success">Đã xử lý yêu cầu thành công.</div>
</c:if>
<c:if test="${not empty requestScope.error}">
    <div class="alert alert-danger">Lỗi khi tải trang: ${requestScope.error}</div>
</c:if>

<div class="card shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Mã đơn</th>
                        <th>Khách hàng</th>
                        <th>Sản phẩm</th>
                        <th>Lý do trả hàng</th>
                        <th class="text-center">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty requestScope.pendingReturns}">
                        <tr>
                            <td colspan="5" class="text-center p-4">Không có yêu cầu trả hàng nào đang chờ xử lý.</td>
                        </tr>
                    </c:if>
                    
                    <c:forEach var="item" items="${requestScope.pendingReturns}">
                        <tr>
                            <%-- Mã đơn --%>
                            <td>
                                <c:if test="${not empty item.order}">
                                    <span class="fw-bold font-monospace">#${item.order.id}</span>
                                </c:if>
                            </td>
                            
                            <%-- Khách hàng --%>
                            <td>
                                <c:if test="${not empty item.order.user}">
                                    <div>${item.order.user.name}</div>
                                    <small class="text-muted">${item.order.user.phone}</small>
                                </c:if>
                            </td>
                            
                            <%-- Sản phẩm --%>
                            <td>
                                <c:if test="${not empty item.product}">
                                    ${item.product.name} (x${item.quantity})
                                </c:if>
                            </td>
                            
                            <%-- Lý do --%>
                            <td>
                                <span class="text-danger">${item.returnReason}</span>
                            </td>
                            
                            <%-- Hành động --%>
                            <td class="text-center" style="min-width: 200px;">
                                <%-- 
                                 Form chung trỏ về SellerServlet
                                 Chúng ta dùng PathInfo là /handle-return
                                --%>
                                <form method="POST" action="${pageContext.request.contextPath}/seller-page/handle-return" class="d-inline-block m-1">
                                    <input type="hidden" name="opId" value="${item.id}">
                                    <input type="hidden" name="decision" value="confirm">
                                    <button type="submit" class="btn btn-sm btn-success">
                                        <i class="bi bi-check-lg"></i> Đồng ý
                                    </button>
                                </form>
                                
                                <form method="POST" action="${pageContext.request.contextPath}/seller-page/handle-return" class="d-inline-block m-1">
                                    <input type="hidden" name="opId" value="${item.id}">
                                    <input type="hidden" name="decision" value="reject">
                                    <button type="submit" class="btn btn-sm btn-danger">
                                        <i class="bi bi-x-lg"></i> Từ chối
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>