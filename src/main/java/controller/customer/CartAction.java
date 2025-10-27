package controller.customer;

import java.io.IOException;
import java.time.LocalDateTime;

import database.CartDAO;
import database.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Cart;
import model.Product;
import model.User;

@WebServlet("/CartAction")
public class CartAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect("Login");
			return;
		}

		String action = request.getParameter("action");
		String productIdStr = request.getParameter("productId");

		if (action == null || productIdStr == null) {
			response.sendRedirect("ViewCart");
			return;
		}

		try {
			Long productId = Long.parseLong(productIdStr);
			Product product = ProductDAO.selectById(productId);

			if (product == null) {
				response.sendRedirect("ViewCart");
				return;
			}

			Cart cartItem = CartDAO.selectByKey(user.getId(), productId);

			if (cartItem == null) {
				response.sendRedirect("ViewCart");
				return;
			}

			switch (action) {
			case "increase":
				cartItem.setQuantity(cartItem.getQuantity() + 1);
				cartItem.setCreatedAt(LocalDateTime.now());
				CartDAO.update(cartItem);
				break;

			case "decrease":
				int newQuantity = cartItem.getQuantity() - 1;
				if (newQuantity <= 0) {
					CartDAO.delete(cartItem);
				} else {
					cartItem.setQuantity(newQuantity);
					cartItem.setCreatedAt(LocalDateTime.now());
					CartDAO.update(cartItem);
				}
				break;

			case "remove":
				CartDAO.delete(cartItem);
				break;

			default:
				break;
			}

			response.sendRedirect("ViewCart");

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("ViewCart");
		}
	}
}
