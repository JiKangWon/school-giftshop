package controller.customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.CategoryDAO;
import database.ProductDAO;
import database.ProductImageDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Category;
import model.Product;
import model.ProductImage;

@WebServlet("/CustomerHome")
public class CustomerHome extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		try {
			List<Category> categories = CategoryDAO.selectAll();
			request.setAttribute("categories", categories);

			String search = request.getParameter("search");
			String categoryIdParam = request.getParameter("categoryId");

			List<Product> products;

			if (search != null && !search.trim().isEmpty()) {
				products = ProductDAO.searchByName(search.trim());
			} else if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
				int categoryId = Integer.parseInt(categoryIdParam);
				products = ProductDAO.selectByCategory(categoryId);
			} else {
				products = ProductDAO.selectAll();
			}

			ArrayList<String> productImages = new ArrayList<>();
			for (Product p : products) {
				List<ProductImage> imgs = ProductImageDAO.selectByProductId(p.getId());
				if (!imgs.isEmpty()) {
					productImages.add(imgs.get(0).getImgLink());
				} else {
					productImages.add("/images/default.jpg");
				}
			}

			request.setAttribute("products", products);
			request.setAttribute("productImages", productImages);

			RequestDispatcher rd = request.getRequestDispatcher("/customer/customerhome.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(500, "Lỗi tải danh sách sản phẩm");
		}
	}
}
