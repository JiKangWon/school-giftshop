package controller.customer;

import java.io.IOException;

import database.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

@WebServlet("/ChangePassword")
public class ChangePassword extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/customer/changepassword.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		if (user == null) {
			response.sendRedirect("Login");
			return;
		}

		String currentPassword = request.getParameter("currentPassword");
		String newPassword = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("confirmPassword");

		if (!newPassword.equals(confirmPassword)) {
			request.setAttribute("message", "Mật khẩu xác nhận không khớp!");
			request.getRequestDispatcher("/customer/changepassword.jsp").forward(request, response);
			return;
		}

		// kiem tra mk cu
		if (!user.getPassword().equals(currentPassword)) {
			request.setAttribute("message", "Mật khẩu hiện tại không đúng!");
			request.getRequestDispatcher("/customer/changepassword.jsp").forward(request, response);
			return;
		}

		// cap nhat mk moi
		user.setPassword(newPassword);
		boolean updated = UserDAO.updatePassword(user);

		if (updated) {
			session.setAttribute("user", user);
			request.setAttribute("message", "Đổi mật khẩu thành công!");
		} else {
			request.setAttribute("message", "Lỗi khi cập nhật mật khẩu!");
		}

		request.getRequestDispatcher("/customer/changepassword.jsp").forward(request, response);
	}
}
