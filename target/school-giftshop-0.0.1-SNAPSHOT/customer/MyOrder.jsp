<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Đơn hàng của tôi - School Giftshop</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    body {
        background-color: #f8f9fa;
}
    .order-card .card-header {
        font-weight: 500;
}
    .order-card table {
        margin-bottom: 0;
}
    .nav-link.active {
    	font-weight: bold;
    }
</style>
</head>
<body>
    <fmt:setLocale value="vi_VN" />

    <%-- Include header đã style --%>
    <%@ include file="/customer/header.jsp"%>

    <div class="container mt-4 mb-5">
        <h2 class="mb-4 text-center fw-bold">Đơn hàng của tôi</h2>
        
        <%-- Hiển thị thông báo (nếu có) --%>
		<c:if test="${not empty sessionScope.MyOrderError}">
			<div class="alert alert-danger alert-dismissible fade show" role="alert">
				${sessionScope.MyOrderError}
				<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
			</div>
			<c:remove var="MyOrderError" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.MyOrderSuccess}">
			<div class="alert alert-success alert-dismissible fade show" role="alert">
				${sessionScope.MyOrderSuccess}
				<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
			</div>
			<c:remove var="MyOrderSuccess" scope="session" />
		</c:if>

        <ul class="nav nav-tabs nav-fill mb-3" id="orderTab" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="shipping-tab" data-bs-toggle="tab" data-bs-target="#shipping-tab-pane" type="button" role="tab" aria-controls="shipping-tab-pane" aria-selected="true">
     
               <i class="bi bi-truck"></i> Đang vận chuyển
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="review-tab" data-bs-toggle="tab" data-bs-target="#review-tab-pane" type="button" role="tab" aria-controls="review-tab-pane" aria-selected="false">
              
      <i class="bi bi-pencil-square"></i> Chờ xử lý
                </button>
            </li>
            <%-- (MỚI) Tab Đang chờ duyệt trả hàng --%>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="returning-tab" data-bs-toggle="tab" data-bs-target="#returning-tab-pane" type="button" role="tab" aria-controls="returning-tab-pane" aria-selected="false">
                    <i class="bi bi-arrow-clockwise"></i> Đang trả hàng
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="completed-tab" data-bs-toggle="tab" data-bs-target="#completed-tab-pane" type="button" role="tab" aria-controls="completed-tab-pane" aria-selected="false">
                    <i class="bi bi-check2-circle"></i> 
Đã hoàn thành
                </button>
            </li>
        </ul>

        <div class="tab-content" id="orderTabContent">

			<%-- 
			=================================================
			TAB 1: ĐANG VẬN CHUYỂN
			=================================================
			--%>
            <div class="tab-pane fade show active" id="shipping-tab-pane" role="tabpanel" aria-labelledby="shipping-tab" tabindex="0">
                <c:choose>
                 
   <c:when test="${empty processingOrders}">
                        <div class="alert alert-info text-center" role="alert">
                            Không có đơn hàng nào đang vận chuyển.
</div>
                    </c:when>
                    <c:otherwise>
                        <%-- VÒNG LẶP 1: ĐANG VẬN CHUYỂN --%>
                        <c:forEach var="entry" items="${processingOrders}">
   
                         <c:set var="order" value="${entry.order}" /> 
                            <div class="card shadow-sm mb-3 order-card">
                                <div class="card-header 
bg-white">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span><strong>Mã đơn:</strong> #${order.id}</span>
                   
                     <span class="badge bg-primary">${order.status}</span>
                                    </div>
                                    <small class="text-muted">Ngày đặt: ${entry.createdAtFormatted}</small>
                                </div>
                           
     <div class="card-body p-0">
                                    <div class="table-responsive">
                                        <table class="table table-sm table-striped mb-0">
                                            <%-- (Bỏ <thead> [15] [16] [17] [18]) --%>
                                            <tbody>
                       
                         <c:forEach var="op" items="${order.orderProducts}">
                                                    <tr>
                     
                                   <td>${op.product.name}</td>
                                                        <td class="text-center">x${op.quantity}</td>
        
                                                <td class="text-center">
                                                   
         <fmt:formatNumber value="${op.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                        </td>
                                                        <td class="text-end">
                                                                                        <a href="${pageContext.request.contextPath}/transport-page/order-map?op_id=${order.id}" target="_blank" class="btn btn-info btn-sm">
								    Xem lộ trình
								</a>
                                                        </td>
                              
                      </tr>
                                                </c:forEach>
                              
              </tbody>
                                        </table>
                                    </div>
          
                      </div>
                                <div class="card-footer bg-white text-end">
                                    <form method="post" action="${pageContext.request.contextPath}/MyOrder" class="d-inline-block">
                                        <input type="hidden" name="orderId" value="${order.id}">
               
                         <input type="hidden" name="action" value="received">
                                        <button type="submit" class="btn btn-success btn-sm">
                            
                <i class="bi bi-check-lg"></i> Đã nhận hàng
                                        </button>
                                    </form>
                                    
   
                             </div>
                            </div>
                        </c:forEach>
                   
 </c:otherwise>
                </c:choose>
            </div>

			<%-- 
			=================================================
			TAB 2: CHỜ XỬ LÝ (Đánh giá / Trả hàng)
			=================================================
			--%>
            <div class="tab-pane fade" id="review-tab-pane" role="tabpanel" aria-labelledby="review-tab" tabindex="0">
                 <c:choose>
                    <c:when test="${empty reviewProducts}">
              
          <div class="alert alert-info text-center" role="alert">
                             Không có sản phẩm nào chờ xử lý.
</div>
                    </c:when>
                    <c:otherwise>
                         <%-- VÒNG LẶP 2: SẢN PHẨM CHỜ ĐÁNH GIÁ/TRẢ HÀNG --%>
                        <c:forEach 
var="entry" items="${reviewProducts}">
                            <c:set var="item" value="${entry.item}" />
                            <div class="card shadow-sm mb-3 order-card">
                              
  <div class="card-body">
                                    <div class="row align-items-center">
                                        <div class="col-md-7">
                  
                          <h5 class="card-title">${item.product.name}</h5>
                                            <p class="card-text mb-1">
                           
                     <small class="text-muted">Số lượng: ${item.quantity} |
Giá:
                                                    <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                           
     </small>
                                            </p>
                                             <small class="text-muted">Đã nhận: ${entry.receivedAtFormatted}</small>
                                        </div>
                                        
                                        <%-- [39] (Đã di chuyển lên trên) --%>
                                        
                                        <div class="col-md-5">
              
                              <%-- Form 1: Gửi đánh giá --%>
                                            <form method="post" action="${pageContext.request.contextPath}/MyOrder" class="mb-2">
                
                                <input type="hidden" name="orderProductId" value="${item.id}"> 
                                                <input type="hidden" name="action" value="review">
        
                                        <div class="input-group input-group-sm">
                                                    <input type="text" class="form-control" name="review" placeholder="Viết đánh 
giá của bạn..." required>
                                                    <button type="submit" class="btn btn-primary">
                                          
               <i class="bi bi-send"></i> Gửi
                                                    </button>
                              
                  </div>
                                            </form>
                                            
                                            <%-- (MỚI) Nút 2: Trả hàng (Mở Modal) --%>
                                            <button type="button" class="btn btn-outline-danger btn-sm w-100" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#returnModal_${item.id}">
                                                <i class="bi bi-box-arrow-left"></i> Yêu cầu trả hàng
                                            </button>
                                            
                                            <%-- (MỚI) Hiển thị nếu bị từ chối trả hàng --%>
                                            <c:if test="${item.returnStatus == 'rejected'}">
                                            	<small class="text-danger d-block mt-1">Yêu cầu trả hàng trước đó đã bị từ chối.</small>
                                            </c:if>
                                      
  </div>
                                    </div>
                                </div>
                            </div>
  
                      </c:forEach>
                     </c:otherwise>
                </c:choose>
            </div>
            
            <%-- 
			=================================================
			(MỚI) TAB 3: ĐANG TRẢ HÀNG
			=================================================
			--%>
            <div class="tab-pane fade" id="returning-tab-pane" role="tabpanel" aria-labelledby="returning-tab" tabindex="0">
                 <c:choose>
                    <c:when test="${empty processingReturnProducts}">
                         <div class="alert alert-info text-center" role="alert">
                             Không có sản phẩm nào đang chờ duyệt trả hàng.
                         </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="entry" items="${processingReturnProducts}">
                            <c:set var="item" value="${entry.item}" />
                            <div class="card shadow-sm mb-3 order-card">
                                <div class="card-body">
                                    <div class="row align-items-center">
                                        <div class="col-md-8">
                                            <h5 class="card-title">${item.product.name}</h5>
                                            <p class="card-text mb-1">
                                                <small class="text-muted">Số lượng: ${item.quantity} |
                                                Giá: <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                </small>
                                            </p>
                                             <small class="text-muted"><strong>Lý do:</strong> ${item.returnReason}</small>
                                        </div>
                                        <div class="col-md-4 text-end">
                                            <span class="badge bg-warning text-dark"><i class="bi bi-arrow-clockwise"></i> Đang chờ duyệt</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                     </c:otherwise>
                </c:choose>
            </div>


			<%-- 
			=================================================
			TAB 4: ĐÃ HOÀN THÀNH (Đơn hàng cũ + SP đã trả)
			=================================================
			--%>
            <div class="tab-pane fade" id="completed-tab-pane" role="tabpanel" aria-labelledby="completed-tab" tabindex="0">
           
      <c:choose>
                    <c:when test="${empty completedOrders && empty confirmedReturnProducts}">
                         <div class="alert alert-info text-center" role="alert">
                             Bạn chưa có đơn hàng hoặc sản phẩm nào hoàn thành.
</div>
                    </c:when>
                    <c:otherwise>
                    
                    	<%-- (MỚI) HIỂN THỊ SP ĐÃ TRẢ THÀNH CÔNG --%>
                    	<c:if test="${not empty confirmedReturnProducts}">
                    		<h4 class="h5 mt-3">Sản phẩm đã trả thành công</h4>
                    		<c:forEach var="entry" items="${confirmedReturnProducts}">
	                            <c:set var="item" value="${entry.item}" />
	                            <div class="card shadow-sm mb-3 order-card">
	                                <div class="card-body">
	                                    <div class="row align-items-center">
	                                        <div class="col-md-8">
	                                            <h5 class="card-title">${item.product.name}</h5>
	                                            <p class="card-text mb-1">
	                                                <small class="text-muted">Số lượng: ${item.quantity} |
	                                                Giá: <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
	                                                </small>
	                                            </p>
	                                        </div>
	                                        <div class="col-md-4 text-end">
	                                            <span class="badge bg-success"><i class="bi bi-check-circle"></i> Đã trả hàng</span>
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
                        	</c:forEach>
                    	</c:if>
                    
                    	<%-- HIỂN THỊ CÁC ĐƠN HÀNG CŨ --%>
                    	<h4 class="h5 mt-4">Lịch sử đơn hàng đã giao</h4>
                        <c:forEach var="entry" items="${completedOrders}">
   
                          <c:set var="order" value="${entry.order}" /> 
                             <div class="card shadow-sm mb-3 order-card">
                                
<div class="card-header bg-white">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span><strong>Mã đơn:</strong> #${order.id}</span>
                 
                       <span class="badge bg-success">${order.status}</span>
                                    </div>

                                    <small class="text-muted">Ngày hoàn thành: ${entry.completedAtFormatted}</small> 
                                </div>
                       
         <div class="card-body p-0">
                                     <div class="table-responsive">
                                        <table class="table table-sm table-striped mb-0">
                                            <%-- (Bỏ <thead> [55] [56] [57] [58]) --%>
                                            <tbody>
                 
                               <c:forEach var="op" items="${order.orderProducts}">
                                                    <tr>
               
                                         <td>
                                                    	${op.product.name}
                                                    	<%-- (MỚI) Hiển thị nếu SP này đã trả --%>
                                                    	<c:if test="${op.returnStatus == 'confirmed'}">
                                                    		<span class="badge bg-secondary">Đã trả</span>
                                                    	</c:if>
                                                    </td>
                                                        <td class="text-center">x${op.quantity}</td>
  
                                                      <td class="text-end">
                                             
               <fmt:formatNumber value="${op.product.price}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                        </td>
                        
                            </tr>
                                                </c:forEach>
                        
                    </tbody>
                                        </table>
                                    </div>
    
                            </div>
                            </div>
                        </c:forEach>
                    
</c:otherwise>
                </c:choose>
            </div>
        </div> <%-- End Tab Content --%>
    </div> <%-- End Container --%>

	<%-- 
	=================================================
	(MỚI) MODALS TRẢ HÀNG
	=================================================
	--%>
	<c:forEach var="entry" items="${reviewProducts}">
		<c:set var="item" value="${entry.item}" />
		<div class="modal fade" id="returnModal_${item.id}" tabindex="-1" aria-labelledby="returnModalLabel_${item.id}" aria-hidden="true">
		  <div class="modal-dialog">
			<form method="post" action="${pageContext.request.contextPath}/MyOrder">
				<input type="hidden" name="action" value="request_return">
				<input type="hidden" name="orderProductId" value="${item.id}">
				
				<div class="modal-content">
				  <div class="modal-header">
					<h5 class="modal-title" id="returnModalLabel_${item.id}">Yêu cầu trả hàng</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				  </div>
				  <div class="modal-body">
					<p><strong>Sản phẩm:</strong> ${item.product.name}</p>
					<div class="mb-3">
					  <label for="returnReason_${item.id}" class="form-label">Vui lòng nhập lý do trả hàng (*)</label>
					  <textarea class="form-control" id="returnReason_${item.id}" name="returnReason" rows="3" required></textarea>
					</div>
				  </div>
				  <div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
					<button type="submit" class="btn btn-danger">Gửi yêu cầu</button>
				  </div>
				</div>
			</form>
		  </div>
		</div>
	</c:forEach>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <%-- (MỚI) Script để tự động chọn tab nếu có hash --%>
    <script>
		document.addEventListener("DOMContentLoaded", function() {
		  // Kiểm tra xem có thông báo lỗi hay thành công không
		  const hasAlert = document.querySelector(".alert-danger") || document.querySelector(".alert-success");
		  
		  if (hasAlert) {
			// Nếu có lỗi, ưu tiên hiển thị tab "Chờ xử lý" (nơi các action diễn ra)
			const reviewTabButton = document.getElementById('review-tab');
			if (reviewTabButton) {
			  var tab = new bootstrap.Tab(reviewTabButton);
			  tab.show();
			  return; // Dừng lại
			}
		  }
		  
		  // Logic chọn tab dựa trên URL hash (nếu có)
		  var hash = window.location.hash;
		  if (hash) {
			var triggerEl = document.querySelector('button[data-bs-target="' + hash + '"]');
			if (triggerEl) {
			  var tab = new bootstrap.Tab(triggerEl);
			  tab.show();
			}
		  }
		
		  // Lưu hash khi click tab
		  var tabTriggerList = [].slice.call(document.querySelectorAll('#orderTab button'));
		  tabTriggerList.forEach(function (tabTriggerEl) {
			tabTriggerEl.addEventListener('shown.bs.tab', function (event) {
			  window.location.hash = event.target.dataset.bsTarget;
			});
		  });
		});
	</script>

</body>
</html>