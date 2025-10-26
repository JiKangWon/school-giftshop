package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import util.Encryption;

import java.io.IOException;

import database.UserDAO;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("username");
        String password = request.getParameter("password");
        String err = "";
        
        // 1. Xác thực đầu vào
        if(userName == null || userName.isBlank() || password == null || password.isBlank()) {
            err = "Vui lòng nhập đủ tên đăng nhập và mật khẩu!";
            request.setAttribute("err", err);
            
            // Nếu lỗi, forward về trang index.jsp để hiển thị lỗi
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
    		rd.forward(request, response);
    		return; // Dừng thực thi
        }
        
        // 2. Kiểm tra với Database
        password = Encryption.toSHA256(password);
        User user = UserDAO.login(userName, password);
        System.out.println(user);
        // 3. Xử lý kết quả đăng nhập
        if (user != null) {
            // ĐĂNG NHẬP THÀNH CÔNG
            
            // 3.1. Lưu người dùng vào session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // 3.2. Quyết định URL chuyển hướng dựa trên vai trò (role)
            String url = "/customer/home.jsp"; // Mặc định là trang khách hàng
            
            if (user.getIsSeller()==1) {
                // QUAN TRỌNG: Chuyển hướng đến SellerServlet, không phải file JSP
                url = "/seller-page/home"; 
            }

            // 3.3. Dùng sendRedirect (Post-Redirect-Get)
            response.sendRedirect(request.getContextPath() + url);

        } else {
            // ĐĂNG NHẬP THẤT BẠI
            err = "Tên đăng nhập hoặc mật khẩu không đúng!";
            request.setAttribute("err", err);
            
            // 3.4. Dùng forward để gửi 'request' (chứa thông báo lỗi) đến trang index.jsp
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
    		rd.forward(request, response);
        }
	}

}
