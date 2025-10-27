package controller.customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.CartDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.User;
import model.Address;

@WebServlet("/Checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/Login");
			return;
		}

		String productIdsStr = request.getParameter("productIds"); // ví dụ: "1,3,5"
		List<Integer> productIds = new ArrayList<>();
		if (productIdsStr != null && !productIdsStr.isEmpty()) {
			for (String id : productIdsStr.split(",")) {
				productIds.add(Integer.parseInt(id));
			}
		}

		// Lấy giỏ hàng của user với các sản phẩm được chọn
		ArrayList<Cart> cart = CartDAO.selectByUserIds(user.getId(), productIds);

		if (cart.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/ViewCart");
			return;
		}

		// Lấy địa chỉ giao hàng từ User
		Address address = user.getAddress();

		request.setAttribute("user", user);
		request.setAttribute("cart", cart);
		request.setAttribute("address", address);
		request.getRequestDispatcher("/customer/checkoutConfirm.jsp").forward(request, response);
	}
}
