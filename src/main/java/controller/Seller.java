package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;

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
                // Tương ứng với chức năng "Quản lý danh mục" 
                viewName = "products.jsp"; // Bạn sẽ tạo file này sau
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
