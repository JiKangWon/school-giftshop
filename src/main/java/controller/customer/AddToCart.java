package controller.customer;

import java.io.IOException;
import java.time.LocalDateTime;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import database.CartDAO;
import database.ProductDAO;
import model.Cart;
import model.Product;
import model.User;

@WebServlet("/AddToCart")
public class AddToCart extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/Login");
			return;
		}

		Long productId = Long.parseLong(request.getParameter("productId"));
		int quantity = Integer.parseInt(request.getParameter("quantity"));

		Product product = ProductDAO.selectById(productId);

		if (product == null) {
			response.sendRedirect(request.getContextPath() + "/CustomerHome?error=productNotFound");
			return;
		}

		// Neu san pham da co trong gio chi cap nhat so luong
		Cart existingCart = CartDAO.selectByKey(user.getId(), productId);
		if (existingCart != null) {
			existingCart.setQuantity(existingCart.getQuantity() + quantity);
			existingCart.setCreatedAt(LocalDateTime.now());
			CartDAO.update(existingCart);
		} else {
			// Chua co thi them moi
			Cart newCart = new Cart(user, product, quantity, LocalDateTime.now());
			CartDAO.insert(newCart);
		}
		response.sendRedirect(request.getContextPath() + "/ViewCart");
	}
}
