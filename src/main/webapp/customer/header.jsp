<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%-- Sử dụng Bootstrap Navbar --%>
<nav class="navbar navbar-expand-lg navbar-light bg-light shadow-sm sticky-top">
  <div class="container">
    <%-- Brand/Tên cửa hàng (Có thể thêm logo) --%>
    <a class="navbar-brand fw-bold text-primary" href="${pageContext.request.contextPath}/CustomerHome">
        School Giftshop
    </a>

    <%-- Nút Toggler cho màn hình nhỏ --%>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#customerNavbar" aria-controls="customerNavbar" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <%-- Nội dung Navbar --%>
    <div class="collapse navbar-collapse" id="customerNavbar">
      <%-- Menu điều hướng chính (canh giữa hoặc trái tùy ý) --%>
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <a class="nav-link active" aria-current="page" href="${pageContext.request.contextPath}/CustomerHome">Trang chủ</a>
        </li>
         <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/Wishlist">Yêu thích</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/MyOrder">Đơn hàng</a>
        </li>
        <%-- Thêm các link khác nếu cần --%>
      </ul>

      <%-- Menu bên phải (Tài khoản, Giỏ hàng, Đăng xuất) --%>
      <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-lg-center">
          <%-- Giỏ hàng --%>
          <li class="nav-item me-lg-2">
               <a class="nav-link" href="${pageContext.request.contextPath}/ViewCart">
                    <i class="bi bi-cart-fill"></i> Giỏ hàng
                    <%-- TODO: Thêm badge số lượng sản phẩm trong giỏ --%>
                    <%-- <span class="badge rounded-pill bg-danger">3</span> --%>
               </a>
          </li>

          <%-- Dropdown Tài khoản --%>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownUser" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              <i class="bi bi-person-circle"></i>
              <c:if test="${not empty sessionScope.user}">
                ${sessionScope.user.name} <%-- Hiển thị tên người dùng --%>
              </c:if>
              <c:if test="${empty sessionScope.user}">
                Tài khoản
              </c:if>
            </a>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownUser">
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/CustomerProfile">Thông tin cá nhân</a></li>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/ChangePassword">Đổi mật khẩu</a></li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/Logout">Đăng xuất</a></li>
            </ul>
          </li>
      </ul>
    </div>
  </div>
</nav>

<%-- Không cần thẻ <hr> nữa vì navbar đã có border/shadow --%>