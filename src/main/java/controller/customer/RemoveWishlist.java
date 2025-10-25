package controller.customer;

import java.io.IOException;

import database.FavoriteListDAO;
import database.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.FavoriteList;
import model.Product;
import model.User;

/**
 * Servlet implementation class RemoveWishlist
 */
@WebServlet("/RemoveWishlist")
public class RemoveWishlist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("Login");
            return;
        }

        String productIdStr = request.getParameter("productId");
        if (productIdStr == null || productIdStr.isEmpty()) {
            response.sendRedirect("FavoriteList");
            return;
        }

        try {
            Long productId = Long.parseLong(productIdStr);

            Product product = ProductDAO.selectById(productId);
            FavoriteList f = new FavoriteList(user, product, null);

            int result = FavoriteListDAO.delete(f);

            if (result > 0) {
                System.out.println("✅ Xóa khỏi danh mục yêu thích thành công!");
            } else {
                System.out.println("⚠ Không tìm thấy mục để xóa.");
            }

            response.sendRedirect("Wishlist");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Wishlist");
        }
	}
    

}
