package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Address;
import util.Encryption;

import java.io.IOException;
import java.util.ArrayList;

import database.AddressDAO;
import database.UserDAO;

/**
 * Servlet implementation class Register
 */
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = "/register.jsp";
		// load Addresses Data
		ArrayList<Address> arrAddress = AddressDAO.selectAll();
		request.setAttribute("arrAddress", arrAddress);
		
	    // Forward
		RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String errorMessage = "";
		// Form Register handling
	    String userName = request.getParameter("username");
	    String password = request.getParameter("password");
	    String confirmPassword = request.getParameter("confirm_password");
	    String name = request.getParameter("name");
	    String addressIdStr = request.getParameter("address_id");
	    String addressNumber = request.getParameter("addressNumber");
	    String phone = request.getParameter("phone");
	    Address address = null;
	    // Parsing error
	    try {
	    	Long addressId = Long.parseLong(addressIdStr);
	    	address = AddressDAO.selectById(addressId);
	    } catch (Exception e) {
	    	errorMessage = "Invalid input";
	    	e.printStackTrace();
	    }
	    // userName exist in DB
	    if (UserDAO.isExistInDB(userName)==1) {
	    	errorMessage = "username exist in DB";
	    }
	    // password doesn't match
	    if (!password.equals(confirmPassword)) {
	    	errorMessage = "password doesn't match";
	    }
	    
	    // Hash password
	    password = Encryption.toSHA256(password);
	    // create new User
	    if(errorMessage.length()==0) {
	    	UserDAO.insert(userName, password, name, address, addressNumber, phone);
	    	response.sendRedirect(request.getContextPath()+"/login");
	    	return;
	    }
	    
	    request.setAttribute("errorMessage", errorMessage);
	    RequestDispatcher rd = getServletContext().getRequestDispatcher("/register.jsp");
	    rd.forward(request, response);
	}

}
