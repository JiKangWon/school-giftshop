<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="model.User, model.Address"%>

<%
User user = (User) request.getAttribute("user");
Address address = (Address) request.getAttribute("address");
%>

<html>
<head>
<title>Thông tin cá nhân</title>

</head>
<body>
	<%@ include file="/customer/header.jsp"%>

	<div class="profile-container">
		<h2>Thông tin cá nhân</h2>

		<div class="toggle-edit">
			<label><input type="checkbox" id="editToggle"> Chỉnh
				sửa</label>
		</div>

		<form method="post" action="CustomerProfile">
			<input type="hidden" name="userId" value="<%=user.getId()%>">
			<input type="hidden" name="addressId"
				value="<%=address != null ? address.getId() : 0%>">

			<div class="form-group">
				<label>Họ tên</label> <input type="text" name="fullName"
					value="<%=user.getName()%>" readonly>
			</div>


			<div class="form-group">
				<label>Số điện thoại</label> <input type="tel" name="phone"
					value="<%=user.getPhone()%>" readonly>
			</div>
			<div class="form-group">
				<a href="ChangePassword" class="btn-password">Đổi mật khẩu</a>
			</div>

			<hr>
			<h3>Địa chỉ</h3>

			<div class="form-group">
				<label>Quốc gia</label> <input type="text" name="country"
					value="<%=address != null ? address.getCountry() : ""%>" readonly>
			</div>

			<div class="form-group">
				<label>Tỉnh/Thành phố</label> <input type="text" name="province"
					value="<%=address != null ? address.getProvince() : ""%>" readonly>
			</div>

			<div class="form-group">
				<label>Quận/Huyện</label> <input type="text" name="district"
					value="<%=address != null ? address.getDistrict() : ""%>" readonly>
			</div>

			<div class="form-group">
				<label>Phường/Xã</label> <input type="text" name="ward"
					value="<%=address != null ? address.getWard() : ""%>" readonly>
			</div>

			<div class="form-group">
				<label>Đường</label> <input type="text" name="street"
					value="<%=address != null ? address.getStreet() : ""%>" readonly>
			</div>

			<div class="actions">
				<button type="submit" class="btn-update" id="btnUpdate" disabled>Cập
					nhật</button>
			</div>
		</form>
	</div>

	<script>
    const toggle = document.getElementById("editToggle");
    const inputs = document.querySelectorAll("input[type=text], input[type=email], input[type=tel]");
    const btnUpdate = document.getElementById("btnUpdate");

    toggle.addEventListener("change", function() {
        const editable = this.checked;
        inputs.forEach(i => i.readOnly = !editable);
        btnUpdate.disabled = !editable;
    });
</script>

</body>
</html>
