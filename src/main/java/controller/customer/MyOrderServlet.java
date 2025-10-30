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
			response.sendRedirect(request.getContextPath() + "/customer/login.jsp"); // Thêm contextPath
			return;
		}

		// DS don hang dang giao (Gọi hàm mới)
		List<Order> processingOrders = OrderDAO.selectByUserIdAndStatus(user.getId(), "processing");
		for (Order order : processingOrders) {
			// Giữ nguyên logic N+1 của bạn
			order.setOrderProducts(OrderProductDAO.selectByOrderId(order.getId()));
		}

		// DS sp cho danh gia (Gọi hàm mới)
		List<OrderProduct> reviewProducts = OrderProductDAO.getReviewableProducts(user.getId());

		// DS don hang da hoan thanh (Gọi hàm mới)
		List<Order> completedOrders = OrderDAO.selectByUserIdAndStatus(user.getId(), "completed");
		for (Order order : completedOrders) {
			// Giữ nguyên logic N+1 của bạn
			order.setOrderProducts(OrderProductDAO.selectByOrderId(order.getId()));
		}
		
		
        // ===== BẮT ĐẦU PHẦN SỬA LỖI (FIX LỖI LocalDateTime) =====
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // 1. Chuyển đổi 'processingOrders' (List<Order>) thành List<Map>
        List<Map<String, Object>> formattedProcessing = new ArrayList<>();
        for (Order order : processingOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("order", order); // Gửi đối tượng order gốc
            if (order.getCreatedAt() != null) {
                map.put("createdAtFormatted", order.getCreatedAt().format(formatter));
            } else {
                map.put("createdAtFormatted", "N/A");
            }
            formattedProcessing.add(map);
        }
        
        // 2. Chuyển đổi 'reviewProducts' (List<OrderProduct>) thành List<Map>
        // (Fix luôn cho tab review nếu sau này bạn cần dùng ngày)
        List<Map<String, Object>> formattedReview = new ArrayList<>();
         for (OrderProduct op : reviewProducts) {
            Map<String, Object> map = new HashMap<>();
            map.put("item", op); // Gửi đối tượng item gốc
            if (op.getReceivedAt() != null) {
                map.put("receivedAtFormatted", op.getReceivedAt().format(formatter));
            } else {
                map.put("receivedAtFormatted", "N/A");
            }
            formattedReview.add(map);
        }

        // 3. Chuyển đổi 'completedOrders' (List<Order>) thành List<Map>
        List<Map<String, Object>> formattedCompleted = new ArrayList<>();
        for (Order order : completedOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("order", order);
            if (order.getCreatedAt() != null) {
                 // Bạn nên có cột 'completedAt' trong CSDL, tạm dùng 'createdAt'
                map.put("completedAtFormatted", order.getCreatedAt().format(formatter));
            } else {
                 map.put("completedAtFormatted", "N/A");
            }
            formattedCompleted.add(map);
        }
        
        // ===== KẾT THÚC PHẦN SỬA LỖI =====

        // 4. Gửi danh sách ĐÃ ĐỊNH DẠNG (formatted) sang JSP
		request.setAttribute("processingOrders", formattedProcessing); // Sửa
		request.setAttribute("reviewProducts", formattedReview); // Sửa
		request.setAttribute("completedOrders", formattedCompleted); // Sửa

		request.getRequestDispatcher("/customer/MyOrder.jsp").forward(request, response);
	}

	// Xử lý POST (đánh dấu hoàn thành hoặc gửi đánh giá)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		String errorMessage = null;

        try {
            if ("received".equals(action)) {
                // SỬA LỖI: Lấy "orderId" (từ JSP) thay vì "id"
                String orderIdStr = request.getParameter("orderId"); 
                long orderId = Long.parseLong(orderIdStr); // Dòng 115 (tương đương)
                OrderProductDAO.markOrderCompleted(orderId); // Gọi hàm mới
                
            } else if ("review".equals(action)) {
                // SỬA LỖI: Lấy "orderProductId" (từ JSP) thay vì "id"
                String opIdStr = request.getParameter("orderProductId");
                long orderProductId = Long.parseLong(opIdStr); // Dòng 115 (tương đương)
                String review = request.getParameter("review");
                
                if(review != null && !review.trim().isEmpty()) {
                    OrderProductDAO.submitReview(orderProductId, review.trim()); // Gọi hàm mới
                } else {
                    errorMessage = "Nội dung đánh giá không được để trống.";
                }
            }
        } catch (NumberFormatException e) {
             e.printStackTrace();
             errorMessage = "Lỗi: ID không hợp lệ (null hoặc sai định dạng).";
        } catch (Exception e) {
             e.printStackTrace();
             errorMessage = "Lỗi khi xử lý: " + e.getMessage();
        }

        if(errorMessage != null) {
             request.getSession().setAttribute("MyOrderError", errorMessage); // Lưu lỗi vào session
        }

		response.sendRedirect(request.getContextPath() + "/MyOrder");
	}
}
