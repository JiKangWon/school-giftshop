package controller.customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.OrderProductDAO;
import database.ProductDAO;
import database.ProductImageDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.OrderProduct;
import model.Product;
import model.ProductImage;

@WebServlet("/ProductDetail")
public class ProductDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String idParam = request.getParameter("id");
		if (idParam == null || idParam.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/CustomerHome");
			return;
		}

		try {
			Long productId = Long.parseLong(idParam);
			Product product = ProductDAO.selectById(productId);

			if (product == null) {
				response.sendRedirect(request.getContextPath() + "/CustomerHome");
				return;
			}

			List<ProductImage> images = ProductImageDAO.selectByProductId(productId);
			List<String> imgLinks = new ArrayList<>();
			for (ProductImage img : images) {
				imgLinks.add(img.getImgLink());
			}
			if (imgLinks.isEmpty()) {
				imgLinks.add("/images/default.jpg");
			}

			List<OrderProduct> reviews = new ArrayList<>();
			for (OrderProduct op : OrderProductDAO.selectAll()) {
				if (op.getProduct() != null && op.getProduct().getId().equals(productId) && op.getReview() != null
						&& !op.getReview().trim().isEmpty()) {
					reviews.add(op);
				}
			}
			
			int totalSold = ProductDAO.countSold(productId);

			request.setAttribute("product", product);
			request.setAttribute("images", imgLinks);
			request.setAttribute("reviews", reviews);
			request.setAttribute("totalSold", totalSold);

			request.getRequestDispatcher("/customer/productDetail.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(500, "Lỗi tải chi tiết sản phẩm");
		}
	}
}
