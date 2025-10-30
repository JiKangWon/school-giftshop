<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- 
    product: Đối tượng Product cần sửa (do doGet hoặc reloadUpdateForm gửi)
    categories: Danh sách tất cả category (do doGet hoặc reloadUpdateForm gửi)
    currentImages: Danh sách ảnh hiện tại (do doGet hoặc reloadUpdateForm gửi)
    oldInput: Dữ liệu người dùng nhập bị lỗi (do doPost/reloadUpdateForm gửi)
    error: Thông báo lỗi (do doPost/reloadUpdateForm gửi)
--%>

<c:set var="prod" value="${requestScope.product}" />
<c:set var="old" value="${requestScope.oldInput}" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2">Cập nhật sản phẩm #${prod.id}</h1>
    <a href="${pageContext.request.contextPath}/seller-page/products" class="btn btn-sm btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Quay lại
    </a>
</div>

<div class="card shadow-sm">
    <div class="card-body">
        <%-- Form trỏ đến action /update-product của SellerServlet --%>
        <form action="${pageContext.request.contextPath}/seller-page/update-product" method="post" enctype="multipart/form-data">
            
            <%-- ID sản phẩm BẮT BUỘC phải có --%>
            <input type="hidden" name="productId" value="${prod.id}">

             <%-- Hiển thị lỗi nếu có --%>
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger" role="alert">
                    ${requestScope.error}
                </div>
            </c:if>

            <div class="mb-3">
                <label for="productName" class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                <%-- Ưu tiên hiển thị 'oldInput' nếu có lỗi, nếu không thì hiển thị 'product.name' --%>
                <input type="text" class="form-control" id="productName" name="productName"
                       value="${not empty old.productName ? old.productName : prod.name}" required>
            </div>

             <div class="row mb-3">
                <div class="col-md-4">
                    <label for="price" class="form-label">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                    <input type="number" step="1000" min="0" class="form-control" id="price" name="price"
                           value="${not empty old.price ? old.price : prod.price}" required>
                </div>
                <div class="col-md-4">
                    <label for="stock" class="form-label">Tồn kho <span class="text-danger">*</span></label>
                    <input type="number" min="0" class="form-control" id="stock" name="stock"
                           value="${not empty old.stock ? old.stock : prod.stock}" required>
                </div>
                 <div class="col-md-4">
                    <label for="status" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                    <%-- Ưu tiên 'oldInput', sau đó mới đến 'product.status' --%>
                    <c:set var="currentStatus" value="${not empty old.status ? old.status : prod.status}" />
                    <select class="form-select" id="status" name="status" required>
                        <option value="active" ${currentStatus == 'active' ? 'selected' : ''}>Đang bán (active)</option>
                        <option value="hide" ${currentStatus == 'hide' ? 'selected' : ''}>Ẩn (hide)</option>
                    </select>
                </div>
            </div>

            <div class="mb-3">
                <label for="categoryId" class="form-label">Phân loại <span class="text-danger">*</span></label>
                 <%-- Ưu tiên 'oldInput', sau đó mới đến 'product.category.id' --%>
                 <c:set var="currentCategory" value="${not empty old.categoryId ? old.categoryId : prod.category.id}" />
                <select class="form-select" id="categoryId" name="categoryId" required>
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="cat" items="${requestScope.categories}">
                        <%-- Chuyển đổi cat.id sang String để so sánh an toàn với param (có thể là String) --%>
                        <option value="${cat.id}" ${currentCategory eq cat.id ? 'selected' : ''}>
                            ${cat.name}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Mô tả</label>
                <textarea class="form-control" id="description" name="description" rows="5">${not empty old.description ? old.description : prod.description}</textarea>
            </div>

            <hr>
            
            <%-- Hiển thị ảnh hiện tại và cho phép xóa --%>
            <div class="mb-3">
                 <label class="form-label">Ảnh hiện tại (Chọn ảnh để xóa)</label>
                 <div class="d-flex flex-wrap gap-2">
                     <c:if test="${empty currentImages}">
                         <small class="text-muted">Sản phẩm này chưa có ảnh.</small>
                     </c:if>
                     <c:forEach var="img" items="${requestScope.currentImages}">
                         <div class="text-center border p-2 rounded position-relative" style="width: 120px;">
                             <%-- Sử dụng contextPath để đảm bảo link ảnh đúng --%>
                             <img src="${pageContext.request.contextPath}${img.imgLink}" alt="Ảnh sản phẩm" class="img-fluid mb-1" style="width: 100px; height: 100px; object-fit: cover;">
                             <div class="form-check">
                                 <%-- Checkbox này sẽ gửi 'img.id' đi khi form submit --%>
                                 <input class="form-check-input" type="checkbox" name="deleteImages" value="${img.id}" id="delImg_${img.id}">
                                 <label class="form-check-label" for="delImg_${img.id}" title="Xóa ảnh này">Xóa</label>
                             </div>
                         </div>
                     </c:forEach>
                 </div>
            </div>

            <%-- Thêm ảnh mới --%>
            <div class="mb-3">
                <label for="productImages" class="form-label">Thêm ảnh mới (Chọn nhiều ảnh)</label>
                <input class="form-control" type="file" id="productImages" name="productImages" multiple accept="image/png, image/jpeg, image/gif">
                 <small class="form-text text-muted">Chỉ chọn file nếu bạn muốn thêm ảnh mới.</small>
            </div>

            <hr>
            <div class="d-grid">
                <button type="submit" class="btn btn-primary btn-lg">
                    <i class="bi bi-check-circle-fill"></i> Cập nhật sản phẩm
                </button>
            </div>

        </form>
    </div>
</div>