package controller.customer;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.Order;
import model.OrderProduct;
import model.User;
import database.CartDAO;
import database.OrderDAO;
import database.OrderProductDAO;
import database.ProductDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/CheckoutConfirm")
public class CheckoutConfirmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/customer/login.jsp");
			return;
		}

		// Lay cac sp da duoc chon tu form
		String[] productIdsStr = request.getParameterValues("productIds");
		if (productIdsStr == null || productIdsStr.length == 0) {
			response.sendRedirect(request.getContextPath() + "/customer/viewcart.jsp");
			return;
		}

		List<Integer> productIds = new ArrayList<Integer>();
		for (String pid : productIdsStr) {
			productIds.add(Integer.parseInt(pid));
		}

		List<Cart> selectedCartItems = CartDAO.selectByUserIds(user.getId(), productIds);

		if (selectedCartItems.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/customer/viewcart.jsp");
			return;
		}

		BigDecimal totalAmount = BigDecimal.ZERO;
		for (Cart c : selectedCartItems) {
			BigDecimal lineTotal = c.getProduct().getPrice().multiply(BigDecimal.valueOf(c.getQuantity()));
			totalAmount = totalAmount.add(lineTotal);
		}

		Order order = new Order();
		order.setUser(user);
		order.setCreatedAt(LocalDateTime.now());
		order.setStatus("Processing");
		order.setTotalAmount(totalAmount);

		OrderDAO.insert(order);

		List<OrderProduct> orderProducts = new ArrayList<OrderProduct>();
		for (Cart c : selectedCartItems) {
			OrderProduct op = new OrderProduct();
			op.setOrder(order);
			op.setProduct(c.getProduct());
			op.setQuantity(c.getQuantity());
			orderProducts.add(op);
			try {
			    ProductDAO.decreaseStock(c.getProduct().getId(), c.getQuantity());
			} catch (SQLException e) {
			    e.printStackTrace();
			    request.setAttribute("error", "Lỗi: Không thể cập nhật kho cho sản phẩm " + c.getProduct().getName() + ". Vui lòng thử lại.");
			    
			    RequestDispatcher rd = request.getRequestDispatcher("/customer/viewcart.jsp"); // Hoặc /checkoutConfirm.jsp
			    rd.forward(request, response);
			    return; 
			}
			CartDAO.deleteByUserIdAndProductId(user.getId(), c.getProduct().getId());
		}
		OrderProductDAO.insert((ArrayList<OrderProduct>) orderProducts);

		List<Cart> sessionCart = (List<Cart>) session.getAttribute("cart");
		List<Cart> newSessionCart = new ArrayList<Cart>();
		if (sessionCart != null) {
			for (Cart c : sessionCart) {
				boolean keep = true;
				for (Integer pid : productIds) {
					if (c.getProduct().getId().intValue() == pid.intValue()) {
						keep = false;
						break;
					}
				}
				if (keep) {
					newSessionCart.add(c);
				}
			}
		}
		session.setAttribute("cart", newSessionCart);
		response.sendRedirect(request.getContextPath() + "/MyOrder");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
