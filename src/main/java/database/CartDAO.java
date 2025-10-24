package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

import model.Cart;
import model.Product;
import model.User;

public class CartDAO {
    public static ArrayList<Cart> selectAll(){
        ArrayList<Cart> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM carts";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Long userId = rs.getObject("user_id", Long.class);
                Long productId = rs.getObject("product_id", Long.class);
                Integer quantity = rs.getObject("quantity", Integer.class);
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                User u = UserDAO.selectById(userId);
                Product p = ProductDAO.selectById(productId);
                Cart c = new Cart(u, p, quantity, createdAt);
                arr.add(c);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    // Composite key select by userId & productId
    public static Cart selectByKey(Long userId, Long productId){
        if (userId == null || productId == null) return null;
        Cart c = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM carts WHERE user_id=? AND product_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            st.setLong(2, productId);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                Integer quantity = rs.getObject("quantity", Integer.class);
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                User u = UserDAO.selectById(userId);
                Product p = ProductDAO.selectById(productId);
                c = new Cart(u, p, quantity, createdAt);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return c;
    }

    public static int insert(Cart c){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO carts(user_id, product_id, quantity, createdAt) VALUES (?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if (c.getUser() != null && c.getUser().getId() != null) st.setLong(1, c.getUser().getId()); else st.setNull(1, java.sql.Types.BIGINT);
            if (c.getProduct() != null && c.getProduct().getId() != null) st.setLong(2, c.getProduct().getId()); else st.setNull(2, java.sql.Types.BIGINT);
            if (c.getQuantity() != null) st.setInt(3, c.getQuantity()); else st.setNull(3, java.sql.Types.INTEGER);
            if (c.getCreatedAt() != null) st.setObject(4, c.getCreatedAt()); else st.setNull(4, java.sql.Types.TIMESTAMP);
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<Cart> arr){
        int count = 0;
        for(Cart c: arr) count += insert(c);
        return count;
    }

    public static int delete(Cart c){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM carts WHERE user_id=? AND product_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, c.getUser().getId());
            st.setLong(2, c.getProduct().getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int update(Cart c){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE carts SET quantity=?, createdAt=? WHERE user_id=? AND product_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            if (c.getQuantity() != null) st.setInt(1, c.getQuantity()); else st.setNull(1, java.sql.Types.INTEGER);
            if (c.getCreatedAt() != null) st.setObject(2, c.getCreatedAt()); else st.setNull(2, java.sql.Types.TIMESTAMP);
            st.setLong(3, c.getUser().getId());
            st.setLong(4, c.getProduct().getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
