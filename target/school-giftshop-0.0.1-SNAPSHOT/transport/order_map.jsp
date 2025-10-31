<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lộ trình Giao hàng - SP #${orderProduct.id}</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <!-- Nạp thư viện Vis.js (CSS) -->
    <script type="text/javascript" src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>
    <style>
        body { background-color: #f8f9fa; }
        #transport-map {
            width: 100%;
            height: 600px; 
            border: 1px solid #dee2e6;
            border-radius: 0.375rem;
            background-color: #ffffff;
        }
    </style>
</head>
<body class="container mt-4 mb-5">

    <c:choose>
        <%-- TRƯỜNG HỢP 1: CÓ LỖI (Lỗi Java/Service) --%>
        <c:when test="${not empty error}">
            <div class="alert alert-danger shadow-sm">
                <h4 class="alert-heading"><i class="bi bi-exclamation-triangle-fill"></i> Đã xảy ra lỗi</h4>
                <p>${error}</p>
                <hr>
                <a href="${pageContext.request.contextPath}/MyOrder" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Quay lại Đơn hàng
                </a>
            </div>
        </c:when>

        <%-- TRƯỜNG HỢP 2: KHÔNG CÓ LỖI NHƯNG KHÔNG TÌM THẤY ĐƠN HÀNG --%>
        <c:when test="${empty orderProduct}">
            <div class="alert alert-warning shadow-sm">
                <h4><i class="bi bi-search"></i> Không tìm thấy thông tin.</h4>
                <p>Không thể tìm thấy chi tiết đơn hàng bạn yêu cầu.</p>
                <a href="${pageContext.request.contextPath}/MyOrder" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Quay lại Đơn hàng
                </a>
            </div>
        </c:when>

        <%-- TRƯỜNG HỢP 3: THÀNH CÔNG --%>
        <c:otherwise>
            <div class="row g-4">
                <div class="col-lg-4">
                    <!-- Thông tin đơn hàng -->
                    <div class="card shadow-sm">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0"><i class="bi bi-box-seam"></i> Lộ trình Giao hàng</h5>
                        </div>
                        <div class="card-body">
                            <h6 class="card-title">Sản phẩm: ${orderProduct.product.name}</h6>
                            <p class="card-text">
                                <strong>Mã SP:</strong> #${orderProduct.id}<br>
                                <strong>Số lượng:</strong> ${orderProduct.quantity}
                            </p>
                            
                            <c:choose>
                                <%-- 
                                  So sánh với Address.name. 
                                  Giả định "Processing" (currentLocation=NULL)
                                --%>
                                <c:when test="${orderProduct.currentLocation == null}">
                                    <div class="alert alert-info py-2">
                                        <i class="bi bi-info-circle"></i> <strong>Trạng thái:</strong> Đang xử lý
                                    </div>
                                </c:when>
                                <c:when test="${orderProduct.currentLocation.name == 'completed'}"> <%-- (Giả định 'completed' là tên của 1 Address) --%>
                                    <div class="alert alert-success py-2">
                                        <i class="bi bi-check-circle"></i> <strong>Trạng thái:</strong> Đã giao
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-warning py-2">
                                        <i class="bi bi-truck"></i> <strong>Vị trí hiện tại:</strong> ${orderProduct.currentLocation.name}
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <!-- Hiển thị quãng đường -->
                            <c:if test="${totalDistance > 0}">
                                <div class="alert alert-light py-2">
                                    <i class="bi bi-geo-alt"></i> <strong>Tổng lộ trình:</strong>
                                    <fmt:formatNumber value="${totalDistance}" maxFractionDigits="1" /> km
                                </div>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/MyOrder" class="btn btn-secondary w-100 mt-2">
                                <i class="bi bi-arrow-left"></i> Quay lại Đơn hàng
                            </a>
                        </div>
                    </div>
                </div>

                <div class="col-lg-8">
                    <!-- Bản đồ ảo -->
                    <div class="card shadow-sm">
                        <div class="card-header">
                            <h5 class="mb-0">Bản đồ Vận chuyển (Toàn bộ hệ thống)</h5>
                        </div>
                        <div class="card-body">
                            <c:if test="${empty allNodesJson}">
                                <div class="alert alert-info">Không tìm thấy dữ liệu bản đồ.</div>
                            </c:if>

                            <c:if test="${not empty allNodesJson}">
                                <%-- 
                                  Thẻ div này sẽ được map_script.js tìm thấy 
                                  data-current-address-id dùng để truyền ID vị trí hiện tại cho JS
                                --%>
                                <div id="transport-map" data-current-address-id="${currentAddressId}"></div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>


    <%-- 
      DỮ LIỆU JSON (AN TOÀN) 
      Các thẻ script này sẽ được render bởi JSTL (trên server) 
      để lưu trữ dữ liệu JSON một cách an toàn.
      JavaScript (map_script.js) sẽ đọc từ các thẻ này.
    --%>
    <c:if test="${not empty allNodesJson}">
        <script type="application/json" id="all-nodes-data"><c:out value="${allNodesJson}"/></script>
        <script type="application/json" id="all-edges-data"><c:out value="${allEdgesJson}"/></script>
        <script type="application/json" id="path-nodes-data"><c:out value="${pathNodesJson}"/></script>
    </c:if>
    
    <%-- 
      NẠP TỆP JAVASCRIPT ĐỂ VẼ BẢN ĐỒ 
      (Phải đặt ở cuối cùng, sau khi các thẻ JSON ở trên đã được render)
    --%>
    <script src="${pageContext.request.contextPath}/transport/map_script.js"></script>

</body>
</html>

