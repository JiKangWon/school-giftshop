package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.FavoriteList;
import model.Product;
import model.User;
import database.FavoriteListDAO;
import database.ProductDAO;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/AddToWishlist")
public class AddToWishlist extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			User user = (User) request.getSession().getAttribute("user");
			if (user == null) {
				response.sendRedirect(request.getContextPath() + "/Login");
				return;
			}

			Long productId = Long.parseLong(request.getParameter("productId"));
			Product product = ProductDAO.selectById(productId);

			if (FavoriteListDAO.selectByKey(user.getId(), productId) == null) {
				FavoriteList f = new FavoriteList(user, product, LocalDateTime.now());
				FavoriteListDAO.insert(f);
			}

			response.sendRedirect(request.getHeader("Referer"));
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/CustomerHome");
		}
	}
}
