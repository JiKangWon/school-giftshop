package controller.customer;

import java.io.IOException;
import java.util.List;
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
			response.sendRedirect("customer/login.jsp");
			return;
		}

		// DS don hang dang giao
		List<Order> processingOrders = OrderDAO.selectProcessingOrdersByUser(user.getId());
		for (Order order : processingOrders) {
			order.setOrderProducts(OrderProductDAO.selectByOrderId(order.getId()));
		}

		// DS sp cho danh gia
		List<OrderProduct> reviewProducts = OrderProductDAO.selectProductsForReview(user.getId());

		// DS don hang da hoan thanh
		List<Order> completedOrders = OrderDAO.selectCompletedOrdersByUser(user.getId());
		for (Order order : completedOrders) {
			order.setOrderProducts(OrderProductDAO.selectByOrderId(order.getId()));
		}

		request.setAttribute("processingOrders", processingOrders);
		request.setAttribute("reviewProducts", reviewProducts);
		request.setAttribute("completedOrders", completedOrders);

		request.getRequestDispatcher("/customer/MyOrder.jsp").forward(request, response);
	}

	// Xử lý POST (đánh dấu hoàn thành hoặc gửi đánh giá)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		long id = Long.parseLong(request.getParameter("id"));

		if ("received".equals(action)) {
			OrderProductDAO.markOrderCompleted(id);
		} else if ("review".equals(action)) {
			String review = request.getParameter("review");
			OrderProductDAO.submitReview(id, review);
		}

		response.sendRedirect(request.getContextPath() + "/MyOrder");
	}
}
