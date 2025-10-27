package controller.customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.FavoriteListDAO;
import database.ProductImageDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FavoriteList;
import model.Product;
import model.ProductImage;
import model.User;

@WebServlet("/Wishlist")
public class Wishlist extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		User user = (User) session.getAttribute("user");

		try {
			List<FavoriteList> favorites = FavoriteListDAO.selectByUserId(user.getId());
			List<String> productImages = new ArrayList<>();

			for (FavoriteList f : favorites) {
				Product p = f.getProduct();
				List<ProductImage> imgs = ProductImageDAO.selectByProductId(p.getId());
				if (!imgs.isEmpty()) {
					productImages.add(imgs.get(0).getImgLink());
				} else {
					productImages.add(request.getContextPath() + "/images/default.jpg");
				}
			}

			request.setAttribute("favorites", favorites);
			request.setAttribute("productImages", productImages);

			request.getRequestDispatcher("/customer/wishlist.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(500, "Lỗi tải danh sách sản phẩm yêu thích");
		}
	}
}
