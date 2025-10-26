<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- BỎ HẾT CÁC THẺ <html>, <head>, <body>... CHỈ GIỮ LẠI NỘI DUNG --%>
<div class="p-4 bg-white rounded shadow-sm">
    <h1 class="h3">Chào mừng bạn, ${sessionScope.user.name}!</h1>
    <p>Đây là trang báo cáo doanh số</p>
</div>