<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- Tiêu đề và nút Thêm mới --%>
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2">Quản lý sản phẩm</h1>
    <div class="btn-toolbar mb-2 mb-md-0">
        <%-- TODO: Đảm bảo link này trỏ đúng vào servlet (ví dụ: /kenh-ban/add-product) --%>
        <a href="${pageContext.request.contextPath}/seller-page/add-product" class="btn btn-sm btn-primary">
            <i class="bi bi-plus-circle"></i>
            Thêm sản phẩm mới
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Ảnh</th>
                        <th scope="col">Tên sản phẩm</th>
                        <th scope="col">Giá bán</th>
                        <th scope="col">Tồn kho</th>
                        <th scope="col">Trạng thái</th>
                        <th scope="col" class="text-center">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    
                    <c:forEach var="product" items="${requestScope.productList}" varStatus="loop">
                        <tr>
                            <%-- Tính STT dựa trên trang hiện tại --%>
                            <td>${(requestScope.currentPage - 1) * 10 + loop.count}</td>
                            <td>
                                <i class="bi bi-box-seam" style="font-size: 2rem; color: #6c757d;"></i>
                            </td>
                            <td>
                                <strong>${product.name}</strong>
                                <br>
                                <small class="text-muted">Danh mục: ${product.category.name}</small>
                            </td>
                            <td>
                                <fmt:setLocale value="vi_VN"/>
                                <fmt:formatNumber value="${product.price}" type="currency"/>
                            </td>
                            <td>${product.stock}</td>
                            <td>
                                <%-- Dùng cột 'status' từ CSDL của bạn --%>
                                <c:choose>
                                    <c:when test="${product.status == 'Đang bán'}">
                                        <span class="badge bg-success">Đang bán</span>
                                    </c:when>
                                    <c:when test="${product.status == 'Ẩn'}">
                                        <span class="badge bg-secondary">Ẩn</span>
                                    </c:when>
                                    <c:otherwise>
                                         <span class="badge bg-warning">${product.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <%-- Sửa lại <td class="text-center"> trong file seller/products.jsp --%>

                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/seller-page/update-product?id=${product.id}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil-square"></i> Sửa
                                </a>

                                <c:choose>
                                    <c:when test="${product.status == 'active'}">
                                        <%-- Nếu đang bán, hiển thị nút ẨN (màu xám) --%>
                                        <a href="${pageContext.request.contextPath}/seller-page/toggle-status?id=${product.id}&page=${requestScope.currentPage}" 
                                           class="btn btn-sm btn-outline-secondary"
                                           onclick="return confirm('Bạn có chắc muốn ẨN sản phẩm này?');">
                                            <i class="bi bi-eye-slash"></i> Ẩn
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <%-- Nếu đang Ẩn, hiển thị nút HIỆN (màu xanh) --%>
                                        <a href="${pageContext.request.contextPath}/seller-page/toggle-status?id=${product.id}&page=${requestScope.currentPage}" 
                                           class="btn btn-sm btn-outline-success"
                                           onclick="return confirm('Bạn có chắc muốn HIỆN sản phẩm này?');">
                                            <i class="bi bi-eye"></i> Hiện
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                                <a href="${pageContext.request.contextPath}/seller-page/delete-product?id=${product.id}" 
                                   class="btn btn-sm btn-outline-danger" 
                                   onclick="return confirm('Bạn có chắc chắn muốn XÓA sản phẩm này?');">
                                    <i class="bi bi-trash"></i> Xóa
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <c:if test="${empty requestScope.productList}">
                        <tr>
                            <td colspan="7" class="text-center p-4">
                                Không tìm thấy sản phẩm nào.
                            </td>
                        </tr>
                    </c:if>

                </tbody>
            </table>
        </div> <c:if test="${requestScope.totalPages > 1}">
            <nav aria-label="Page navigation" class="mt-4">
                <ul class="pagination justify-content-center">
                    
                    <li class="page-item ${requestScope.currentPage == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/seller-page/products?page=${requestScope.currentPage - 1}">
                            Lùi
                        </a>
                    </li>

                    <c:forEach var="i" begin="1" end="${requestScope.totalPages}">
                        <li class="page-item ${requestScope.currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/seller-page/products?page=${i}">${i}</a>
                        </li>
                    </c:forEach>
                    
                    <li class="page-item ${requestScope.currentPage == requestScope.totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/seller-page/products?page=${requestScope.currentPage + 1}">
                            Tiến
                        </a>
                    </li>
                </ul>
            </nav>
        </c:if>
        </div> </div> ```