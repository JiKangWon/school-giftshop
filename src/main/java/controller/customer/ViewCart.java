package controller.customer;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import database.CartDAO;
import model.Cart;
import model.User;

@WebServlet("/ViewCart")
public class ViewCart extends HttpServlet {
	/**
	 * 
	 */
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

		ArrayList<Cart> allCarts = CartDAO.selectAll();
		ArrayList<Cart> userCarts = new ArrayList<>();

		for (Cart c : allCarts) {
			if (c.getUser() != null && c.getUser().getId().equals(user.getId())) {
				userCarts.add(c);
			}
		}

		request.setAttribute("cartItems", userCarts);
		RequestDispatcher rd = request.getRequestDispatcher("/customer/viewcart.jsp");
		rd.forward(request, response);
	}
}
