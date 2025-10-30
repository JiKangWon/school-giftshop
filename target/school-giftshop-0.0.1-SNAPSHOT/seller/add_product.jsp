<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2">Đăng sản phẩm mới</h1>
    <a href="${pageContext.request.contextPath}/kenh-ban/products" class="btn btn-sm btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Quay lại danh sách
    </a>
</div>

<div class="card shadow-sm">
    <div class="card-body">
        <%-- ***** THAY ĐỔI ENCTYPE Ở ĐÂY ***** --%>
        <form action="${pageContext.request.contextPath}/seller-page/add-product" method="post" enctype="multipart/form-data">

             <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger" role="alert">
                    ${requestScope.error}
                </div>
            </c:if>
            <c:set var="oldInput" value="${requestScope.oldInput}" />

            <%-- Các trường thông tin sản phẩm giữ nguyên --%>
            <div class="mb-3">
                <label for="productName" class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="productName" name="productName"
                       value="${not empty oldInput.productName ? oldInput.productName : ''}" required>
            </div>
            <%-- ... (Giá, Tồn kho, Phân loại, Mô tả) ... --%>
            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="price" class="form-label">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                    <input type="number" step="1000" min="0" class="form-control" id="price" name="price"
                           value="${not empty oldInput.price ? oldInput.price : ''}" required>
                           <small class="form-text text-muted">Nhập số không có dấu phẩy.</small>
                </div>
                <div class="col-md-6">
                    <label for="stock" class="form-label">Tồn kho <span class="text-danger">*</span></label>
                    <input type="number" min="0" class="form-control" id="stock" name="stock"
                           value="${not empty oldInput.stock ? oldInput.stock : ''}" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="categoryId" class="form-label">Phân loại <span class="text-danger">*</span></label>
                <select class="form-select" id="categoryId" name="categoryId" required>
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="cat" items="${requestScope.categories}">
                        <option value="${cat.id}" ${not empty oldInput.categoryId && oldInput.categoryId eq cat.id ? 'selected' : ''}>
                            ${cat.name}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Mô tả</label>
                <textarea class="form-control" id="description" name="description" rows="5">${not empty oldInput.description ? oldInput.description : ''}</textarea>
            </div>

            <%-- ***** THÊM TRƯỜNG UPLOAD ẢNH ***** --%>
            <div class="mb-3">
                <label for="productImages" class="form-label">Hình ảnh sản phẩm (Chọn nhiều ảnh)</label>
                <input class="form-control" type="file" id="productImages" name="productImages" multiple accept="image/png, image/jpeg, image/gif">
                 <small class="form-text text-muted">Chọn 1 hoặc nhiều ảnh. Định dạng: JPG, PNG, GIF.</small>
            </div>
            <%-- ***** KẾT THÚC TRƯỜNG UPLOAD ẢNH ***** --%>

            <hr>
            <div class="d-grid">
                <button type="submit" class="btn btn-primary btn-lg">
                    <i class="bi bi-plus-circle-fill"></i> Đăng sản phẩm
                </button>
            </div>
        </form>
    </div>
</div>