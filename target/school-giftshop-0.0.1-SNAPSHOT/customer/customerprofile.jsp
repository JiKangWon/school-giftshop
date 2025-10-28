<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- Sử dụng JSTL --%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Thông tin cá nhân - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa;
    }
    /* Style cho input readonly để phân biệt */
    input[readonly] {
        background-color: #e9ecef;
        cursor: not-allowed;
    }
</style>
</head>
<body>

    <%-- Include header đã style --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4 mb-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <h2 class="mb-4 text-center fw-bold">Thông tin cá nhân</h2>

                <%-- Nút bật/tắt chỉnh sửa --%>
                <div class="form-check form-switch mb-3 d-flex justify-content-end">
                    <input class="form-check-input" type="checkbox" role="switch" id="editToggle">
                    <label class="form-check-label ms-2" for="editToggle">Chỉnh sửa thông tin</label>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/CustomerProfile">
                    <%-- Lấy user và address từ request attribute (Servlet đã set) --%>
                    <c:set var="user" value="${requestScope.user}" />
                    <c:set var="address" value="${requestScope.address}" />

                    <%-- Truyền ID ẩn --%>
                    <input type="hidden" name="userId" value="${user.id}">
                    <input type="hidden" name="addressId" value="${not empty address ? address.id : ''}"> <%-- Để trống nếu address null --%>

                    <%-- Card Thông tin cơ bản --%>
                    <div class="card shadow-sm mb-4">
                        <div class="card-header">
                            <h5 class="mb-0">Thông tin tài khoản</h5>
                        </div>
                        <div class="card-body">
                            <div class="mb-3 row">
                                <label for="staticUsername" class="col-sm-3 col-form-label">Tên đăng nhập</label>
                                <div class="col-sm-9">
                                    <%-- Tên đăng nhập thường không cho sửa --%>
                                    <input type="text" readonly class="form-control-plaintext" id="staticUsername" value="${user.userName}">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="fullName" class="col-sm-3 col-form-label">Họ tên</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="fullName" name="fullName" value="${user.name}" readonly>
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="phone" class="col-sm-3 col-form-label">Số điện thoại</label>
                                <div class="col-sm-9">
                                    <input type="tel" class="form-control" id="phone" name="phone" value="${user.phone}" readonly>
                                </div>
                            </div>
                             <div class="mb-3 row">
                                 <label class="col-sm-3 col-form-label">Mật khẩu</label>
                                <div class="col-sm-9">
                                    <a href="${pageContext.request.contextPath}/ChangePassword" class="btn btn-outline-secondary btn-sm">
                                        <i class="bi bi-key-fill"></i> Đổi mật khẩu
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                     <%-- Card Địa chỉ --%>
                    <div class="card shadow-sm mb-4">
                        <div class="card-header">
                            <h5 class="mb-0">Địa chỉ giao hàng</h5>
                        </div>
                        <div class="card-body">
                             <div class="mb-3 row">
                                <label for="addressNumber" class="col-sm-3 col-form-label">Số nhà/Tên đường</label>
                                <div class="col-sm-9">
                                     <%-- Gộp số nhà và đường vào 1 ô --%>
                                    <input type="text" class="form-control" id="addressNumber" name="addressNumber" value="${user.addressNumber}" readonly placeholder="Số nhà, tên đường...">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="ward" class="col-sm-3 col-form-label">Phường/Xã</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="ward" name="ward" value="${not empty address ? address.ward : ''}" readonly>
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="district" class="col-sm-3 col-form-label">Quận/Huyện</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="district" name="district" value="${not empty address ? address.district : ''}" readonly>
                                </div>
                            </div>
                             <div class="mb-3 row">
                                <label for="province" class="col-sm-3 col-form-label">Tỉnh/Thành phố</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="province" name="province" value="${not empty address ? address.province : ''}" readonly>
                                </div>
                            </div>
                             <div class="mb-3 row">
                                <label for="country" class="col-sm-3 col-form-label">Quốc gia</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="country" name="country" value="${not empty address ? address.country : ''}" readonly>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%-- Nút Cập nhật --%>
                    <div class="text-center">
                        <button type="submit" class="btn btn-primary btn-lg" id="btnUpdate" disabled>
                             <i class="bi bi-check-circle-fill"></i> Cập nhật thông tin
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        const toggle = document.getElementById("editToggle");
        // Chỉ chọn các input có thể chỉnh sửa (không bao gồm username)
        const inputs = document.querySelectorAll("form input[type=text]:not(#staticUsername), form input[type=tel]");
        const btnUpdate = document.getElementById("btnUpdate");

        toggle.addEventListener("change", function() {
            const editable = this.checked;
            inputs.forEach(i => i.readOnly = !editable);
            btnUpdate.disabled = !editable;

             // Thêm/Xóa class background khi readonly thay đổi
            inputs.forEach(i => {
                if (i.readOnly) {
                    i.classList.add('bg-light'); // Thêm nền xám nhẹ khi readonly
                } else {
                    i.classList.remove('bg-light'); // Bỏ nền xám khi sửa
                }
            });
        });

         // Khởi tạo trạng thái ban đầu của background
        inputs.forEach(i => {
            if (i.readOnly) {
                i.classList.add('bg-light');
            }
        });
    </script>

</body>
</html>