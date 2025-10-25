package controller.customer;

import java.io.IOException;

import database.AddressDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Address;
import model.User;
import database.UserDAO;

@WebServlet("/CustomerProfile")
public class CustomerProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		try {
			User user = (User) session.getAttribute("user");
			Address address = AddressDAO.selectById(user.getAddress().getId());
			request.setAttribute("user", user);
			request.setAttribute("address", address);

			RequestDispatcher rd = request.getRequestDispatcher("/customer/customerprofile.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(500, "Lỗi tải thông tin cá nhân");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Long userId = Long.parseLong(request.getParameter("userId"));
			String fullName = request.getParameter("fullName");
			String phone = request.getParameter("phone");

			Long addressId = Long.parseLong(request.getParameter("addressId"));
			String country = request.getParameter("country");
			String province = request.getParameter("province");
			String district = request.getParameter("district");
			String ward = request.getParameter("ward");
			String street = request.getParameter("street");

			// Cập nhật user
			User user = UserDAO.selectById(userId);
			user.setName(fullName);
			user.setPhone(phone);
			UserDAO.update(user);

			// Cập nhật address
			Address address = new Address(addressId, country, province, district, ward, street);
			AddressDAO.update(address);

			// Cập nhật lại session
			HttpSession session = request.getSession();
			session.setAttribute("user", user);

			response.sendRedirect(request.getContextPath() + "/CustomerProfile");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(500, "Lỗi cập nhật thông tin cá nhân");
		}
	}
}
