package controller.customer;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// huy session
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		// Chuyển hướng về trang đăng nhập hoặc trang chủ
		response.sendRedirect(request.getContextPath() + "/CustomerHome");
	}
}