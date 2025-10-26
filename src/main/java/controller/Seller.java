package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;
import model.User;

import java.io.IOException;
import java.util.ArrayList;

import database.ProductDAO;

/**
 * Servlet implementation class Seller
 */
public class Seller extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Seller() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Kiểm tra xem người dùng đã đăng nhập và có phải là seller không
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if(user == null || user.getIsSeller()==0) {
            // Nếu không phải, đá về trang chủ
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Lấy đường dẫn con (ví dụ: /home, /products)
        String action = request.getPathInfo();
        if (action == null) {
            action = "/home"; // Mặc định
        }

        String viewName = ""; // Tên file .jsp con sẽ được nạp
        
        switch (action) {
            case "/home":
                // Tương ứng với chức năng "Lịch sử bán hàng" 
                viewName = "home.jsp"; 
                break;
            case "/products":
                try {
                    // 1. Định nghĩa số sản phẩm mỗi trang
                    int pageSize = 10;
                    
                    // 2. Lấy số trang hiện tại từ URL (mặc định là trang 1)
                    int currentPage = 1;
                    if (request.getParameter("page") != null) {
                        try {
                            currentPage = Integer.parseInt(request.getParameter("page"));
                        } catch (NumberFormatException e) {
                            currentPage = 1; // Nếu nhập bậy thì về trang 1
                        }
                    }

                    // 3. Lấy TỔNG số sản phẩm (Không cần sellerId)
                    int totalProducts = ProductDAO.getTotalProductCount();
                    
                    // 4. Tính TỔNG số trang
                    int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
                    
                    // 5. Lấy danh sách sản phẩm CHỈ CỦA TRANG HIỆN TẠI (Không cần sellerId)
                    ArrayList<Product> productList = ProductDAO.selectAllPaginated(currentPage, pageSize);
                    
                    // 6. Đặt các biến vào request để JSP sử dụng
                    request.setAttribute("productList", productList);
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("currentPage", currentPage);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Tên file .jsp con sẽ được nạp
                viewName = "products.jsp"; 
                break;
            case "/add-product":
                // Tương ứng với chức năng "Đăng sản phẩm" 
                viewName = "add_product.jsp"; // Bạn sẽ tạo file này sau
                break;
            case "/orders":
                viewName = "orders.jsp"; // Bạn sẽ tạo file này sau
                break;
            case "/report":
                // Tương ứng với chức năng "Báo cáo doanh thu" 
                viewName = "report.jsp"; // Bạn sẽ tạo file này sau
                break;
            case "/toggle-status": { // Thêm case mới
                try {
                    // 1. Lấy ID sản phẩm từ URL
                    Long productId = Long.parseLong(request.getParameter("id"));
                    
                    // 2. Gọi DAO để cập nhật trạng thái
                    ProductDAO.toggleProductStatus(productId);
                    
                    // 3. Lấy lại số trang hiện tại để quay về đúng trang đó
                    String page = request.getParameter("page");
                    if (page == null || page.isBlank()) {
                        page = "1";
                    }
                    
                    // 4. QUAN TRỌNG: Dùng redirect (PRG) để tải lại trang
                    // (Chúng ta giả định URL servlet là /kenh-ban)
                    response.sendRedirect(request.getContextPath() + "/seller-page/products?page=" + page);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    // Nếu lỗi, chuyển về trang products
                    response.sendRedirect(request.getContextPath() + "/seller-page/products");
                }
                return; // Dừng thực thi sau khi redirect
            }
            default:
                viewName = "home.jsp";
        }
        
        // Đặt tên file "view" con vào request
        request.setAttribute("view", viewName);
        
        // Forward tới trang layout CHÍNH (base.jsp)
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/seller/base.jsp");
        rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
