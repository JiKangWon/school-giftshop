package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Address;
import model.OrderProduct;
import model.User;
import model.transport.MapData;
import service.TransportService;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import database.OrderProductDAO;

/**
 * Servlet implementation class Transport
 */
public class Transport extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static TransportService transportService = new TransportService();
    private static Gson gson = new Gson(); 

    // (Hàm init() và doGet() giữ nguyên)
    @Override
    public void init() throws ServletException {
         super.init();
         try {
             transportService.loadGraph();
         } catch (Exception e) {
             System.err.println("Lỗi nghiêm trọng khi tải đồ thị vận chuyển!");
             e.printStackTrace();
         }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         String action = request.getPathInfo();
         if (action == null) action = "/";
         switch (action) {
             case "/order-map":
                 showOrderMap(request, response);
                 break;
             case "/update-address":
                 showUpdateAddressPage(request, response);
                 break;
             default:
                 response.sendRedirect(request.getContextPath());
         }
    }
    // (Hàm showOrderMap() giữ nguyên)
     private void showOrderMap(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         try {
             long opId = Long.parseLong(request.getParameter("op_id"));
             OrderProduct op = OrderProductDAO.selectById(opId); 
             if (op == null) {
                  request.setAttribute("error", "Không tìm thấy chi tiết đơn hàng.");
             } else {
                 MapData mapData = transportService.getMapDataForOrderProduct(op);
                 request.setAttribute("orderProduct", mapData.getOrderProduct());
                 request.setAttribute("totalDistance", mapData.getTotalDistance());
                 request.setAttribute("allNodesJson", gson.toJson(mapData.getAllNodes()));
                 request.setAttribute("allEdgesJson", gson.toJson(mapData.getAllEdges()));
                 request.setAttribute("pathNodesJson", gson.toJson(mapData.getPath()));
                 // (CẬP NHẬT) Truyền ID trực tiếp
                 request.setAttribute("currentAddressId", mapData.getCurrentAddressId());
             }
         } catch (Exception e) {
             e.printStackTrace();
             request.setAttribute("error", "Lỗi khi tìm đường đi: " + e.getMessage());
         }
         RequestDispatcher rd = request.getRequestDispatcher("/transport/order_map.jsp");
         rd.forward(request, response);
     }
    // (Hàm showUpdateAddressPage() giữ nguyên)
     private void showUpdateAddressPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         HttpSession session = request.getSession();
         User user = (User) session.getAttribute("user");
         if (user == null || user.getIsWarehouse() == 0) {
             response.sendRedirect(request.getContextPath() + "/login");
             return;
         }
         RequestDispatcher rd = request.getRequestDispatcher("/transport/update_address.jsp");
         rd.forward(request, response);
     }
     
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();

        if ("/update-address".equals(action)) {
            handleUpdateAddress(request, response);
        } else {
            doGet(request, response);
        }
    }

    /**
     * (CẬP NHẬT) Xử lý POST từ trang cập nhật vị trí của Kho hàng
     */
    private void handleUpdateAddress(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user"); // Đây là user Kho hàng

        if (user == null || user.getIsWarehouse() == 0 || user.getAddress() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            long opId = Long.parseLong(request.getParameter("orderProductId"));
            
            // (CẬP NHẬT) Lấy ID địa chỉ của kho hàng
            long warehouseAddressId = user.getAddress().getId(); 

            // (CẬP NHẬT) Gọi DAO với ID
            boolean success = OrderProductDAO.updateCurrentLocation(opId, warehouseAddressId);

            if (success) {
                request.setAttribute("success", "Cập nhật vị trí cho OrderProduct #" + opId + " thành công.");
            } else {
                request.setAttribute("error", "Không thể cập nhật vị trí cho OrderProduct #" + opId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
        }
        RequestDispatcher rd = request.getRequestDispatcher("/transport/update_address.jsp");
        rd.forward(request, response);
    }
}
