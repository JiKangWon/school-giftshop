package controller.customer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.OrderDAO;
import database.OrderProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Order;
import model.OrderProduct;
import model.User;

@WebServlet("/MyOrder")
public class MyOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Hien thi danh sach don hang
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/customer/login.jsp");
			return;
		}

		long userId = user.getId();

		// 1. DS don hang dang giao (processing)
		List<Order> processingOrders = OrderDAO.selectByUserIdAndStatus(userId, "processing");
		for (Order order : processingOrders) {
			order.setOrderProducts(OrderProductDAO.selectByOrderId(order.getId()));
		}

		// 2. DS sp cho danh gia (Đã cập nhật SQL trong DAO)
		List<OrderProduct> reviewProducts = OrderProductDAO.getReviewableProducts(userId);

		// 3. DS don hang da hoan thanh (completed)
		List<Order> completedOrders = OrderDAO.selectByUserIdAndStatus(userId, "completed");
		for (Order order : completedOrders) {
			order.setOrderProducts(OrderProductDAO.selectByOrderId(order.getId()));
		}
		
		// 4. (MỚI) DS sp đang chờ duyệt trả hàng
		List<OrderProduct> processingReturnProducts = OrderProductDAO.getProcessingReturnProducts(userId);
		
		// 5. (MỚI) DS sp đã trả hàng thành công
		List<OrderProduct> confirmedReturnProducts = OrderProductDAO.getConfirmedReturnProducts(userId);
		
		
        // ===== BẮT ĐẦU PHẦN FORMAT NGÀY (GIỮ NGUYÊN) =====
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // 1. Format 'processingOrders' (List<Order>)
        List<Map<String, Object>> formattedProcessing = new ArrayList<>();
        for (Order order : processingOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("order", order); 
            if (order.getCreatedAt() != null) {
                map.put("createdAtFormatted", order.getCreatedAt().format(formatter));
            } else {
                map.put("createdAtFormatted", "N/A");
            }
            formattedProcessing.add(map);
        }
        
        // 2. Format 'reviewProducts' (List<OrderProduct>)
        List<Map<String, Object>> formattedReview = new ArrayList<>();
         for (OrderProduct op : reviewProducts) {
            Map<String, Object> map = new HashMap<>();
            map.put("item", op); // Gửi đối tượng item gốc
            if (op.getReceivedAt() != null) {
                map.put("receivedAtFormatted", op.getReceivedAt().format(formatter));
            } else {
            	// Nếu SP đã giao (completed) nhưng chưa có receivedAt (lỗi logic cũ), dùng tạm ngày order
            	if (op.getOrder() != null && op.getOrder().getCreatedAt() != null) {
            		map.put("receivedAtFormatted", op.getOrder().getCreatedAt().format(formatter));
            	} else {
            		map.put("receivedAtFormatted", "N/A");
            	}
            }
            formattedReview.add(map);
        }

        // 3. Format 'completedOrders' (List<Order>)
        List<Map<String, Object>> formattedCompleted = new ArrayList<>();
        for (Order order : completedOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("order", order);
            if (order.getCreatedAt() != null) {
                map.put("completedAtFormatted", order.getCreatedAt().format(formatter));
            } else {
                 map.put("completedAtFormatted", "N/A");
            }
            formattedCompleted.add(map);
        }
        
        // 4. (MỚI) Format 'processingReturnProducts'
        List<Map<String, Object>> formattedProcessingReturn = new ArrayList<>();
        for (OrderProduct op : processingReturnProducts) {
             Map<String, Object> map = new HashMap<>();
             map.put("item", op);
             formattedProcessingReturn.add(map);
        }

        // 5. (MỚI) Format 'confirmedReturnProducts'
        List<Map<String, Object>> formattedConfirmedReturn = new ArrayList<>();
        for (OrderProduct op : confirmedReturnProducts) {
             Map<String, Object> map = new HashMap<>();
             map.put("item", op);
             formattedConfirmedReturn.add(map);
        }

        // ===== KẾT THÚC PHẦN FORMAT NGÀY =====

		request.setAttribute("processingOrders", formattedProcessing); 
		request.setAttribute("reviewProducts", formattedReview); 
		request.setAttribute("completedOrders", formattedCompleted); 
		
		// Gửi 2 list mới
		request.setAttribute("processingReturnProducts", formattedProcessingReturn);
		request.setAttribute("confirmedReturnProducts", formattedConfirmedReturn);

		request.getRequestDispatcher("/customer/MyOrder.jsp").forward(request, response);
	}

	// Xử lý POST (thêm action "request_return")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc UTF-8 cho lý do trả hàng
		String action = request.getParameter("action");
		String errorMessage = null;
		String successMessage = null;

        try {
            if ("received".equals(action)) {
                String orderIdStr = request.getParameter("orderId"); 
                long orderId = Long.parseLong(orderIdStr); 
                OrderProductDAO.markOrderCompleted(orderId);
                successMessage = "Đã xác nhận nhận hàng!";
                
            } else if ("review".equals(action)) {
                String opIdStr = request.getParameter("orderProductId");
                long orderProductId = Long.parseLong(opIdStr);
                String review = request.getParameter("review");
                
                if(review != null && !review.trim().isEmpty()) {
                    OrderProductDAO.submitReview(orderProductId, review.trim());
                    successMessage = "Gửi đánh giá thành công!";
                } else {
                    errorMessage = "Nội dung đánh giá không được để trống.";
                }
            
            // --- START: LOGIC MỚI ---
            } else if ("request_return".equals(action)) {
                String opIdStr = request.getParameter("orderProductId");
                long orderProductId = Long.parseLong(opIdStr);
                String returnReason = request.getParameter("returnReason");
                
                if (returnReason == null || returnReason.trim().isEmpty()) {
                    errorMessage = "Vui lòng nhập lý do trả hàng.";
                } else {
                    boolean result = OrderProductDAO.requestReturn(orderProductId, returnReason.trim());
                    if (result) {
                        successMessage = "Yêu cầu trả hàng đã được gửi. Vui lòng chờ Shop xử lý.";
                    } else {
                        errorMessage = "Gửi yêu cầu thất bại. Sản phẩm có thể đang được xử lý.";
                    }
                }
            // --- END: LOGIC MỚI ---
                
            }
        } catch (NumberFormatException e) {
             e.printStackTrace();
             errorMessage = "Lỗi: ID không hợp lệ (null hoặc sai định dạng).";
        } catch (Exception e) {
             e.printStackTrace();
             errorMessage = "Lỗi khi xử lý: " + e.getMessage();
        }

        HttpSession session = request.getSession();
        if(errorMessage != null) {
             session.setAttribute("MyOrderError", errorMessage);
        }
        if(successMessage != null) {
             session.setAttribute("MyOrderSuccess", successMessage);
        }

		response.sendRedirect(request.getContextPath() + "/MyOrder");
	}
}