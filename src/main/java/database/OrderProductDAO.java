package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

import model.Order;
import model.OrderProduct;
import model.Product;

public class OrderProductDAO {
    public static ArrayList<OrderProduct> selectAll(){
        ArrayList<OrderProduct> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM order_products";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Long id = rs.getObject("id", Long.class);
                Long orderId = rs.getObject("order_id", Long.class);
                Long productId = rs.getObject("product_id", Long.class);
                Order order = OrderDAO.selectById(orderId);
                Product product = ProductDAO.selectById(productId);
                Integer quantity = rs.getObject("quantity", Integer.class);
                String review = rs.getString("review");
                LocalDateTime receivedAt = rs.getObject("receivedAt", LocalDateTime.class);
                String currentLocation = rs.getString("currentLocation");
                OrderProduct op = new OrderProduct(id, order, product, quantity, review, receivedAt, currentLocation);
                arr.add(op);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public static OrderProduct selectById(Long id){
        if (id == null) return null;
        OrderProduct op = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM order_products WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                Long orderId = rs.getObject("order_id", Long.class);
                Long productId = rs.getObject("product_id", Long.class);
                Order order = OrderDAO.selectById(orderId);
                Product product = ProductDAO.selectById(productId);
                Integer quantity = rs.getObject("quantity", Integer.class);
                String review = rs.getString("review");
                LocalDateTime receivedAt = rs.getObject("receivedAt", LocalDateTime.class);
                String currentLocation = rs.getString("currentLocation");
                op = new OrderProduct(id, order, product, quantity, review, receivedAt, currentLocation);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return op;
    }

    public static int insert(OrderProduct op){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO order_products(id, order_id, product_id, quantity, review, receivedAt, currentLocation) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if (op.getId() != null) st.setLong(1, op.getId()); else st.setNull(1, java.sql.Types.BIGINT);
            if (op.getOrder() != null && op.getOrder().getId() != null) st.setLong(2, op.getOrder().getId());
            else st.setNull(2, java.sql.Types.BIGINT);
            if (op.getProduct() != null && op.getProduct().getId() != null) st.setLong(3, op.getProduct().getId());
            else st.setNull(3, java.sql.Types.BIGINT);
            if (op.getQuantity() != null) st.setInt(4, op.getQuantity()); else st.setNull(4, java.sql.Types.INTEGER);
            st.setString(5, op.getReview());
            if (op.getReceivedAt() != null) st.setObject(6, op.getReceivedAt()); else st.setNull(6, java.sql.Types.TIMESTAMP);
            st.setString(7, op.getCurrentLocation());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<OrderProduct> arr){
        int count = 0;
        for(OrderProduct op: arr) count += insert(op);
        return count;
    }

    public static int delete(OrderProduct op){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM order_products WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, op.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int update(OrderProduct op){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE order_products SET order_id=?, product_id=?, quantity=?, review=?, receivedAt=?, currentLocation=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            if (op.getOrder() != null && op.getOrder().getId() != null) st.setLong(1, op.getOrder().getId());
            else st.setNull(1, java.sql.Types.BIGINT);
            if (op.getProduct() != null && op.getProduct().getId() != null) st.setLong(2, op.getProduct().getId());
            else st.setNull(2, java.sql.Types.BIGINT);
            if (op.getQuantity() != null) st.setInt(3, op.getQuantity()); else st.setNull(3, java.sql.Types.INTEGER);
            st.setString(4, op.getReview());
            if (op.getReceivedAt() != null) st.setObject(5, op.getReceivedAt()); else st.setNull(5, java.sql.Types.TIMESTAMP);
            st.setString(6, op.getCurrentLocation());
            st.setLong(7, op.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
