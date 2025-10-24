package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;

import model.Order;
import model.User;

public class OrderDAO {
    public static ArrayList<Order> selectAll(){
        ArrayList<Order> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM orders";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Long id = rs.getObject("id", Long.class);
                Long userId = rs.getObject("user_id", Long.class);
                User user = UserDAO.selectById(userId);
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                String status = rs.getString("status");
                BigDecimal total = rs.getBigDecimal("total_amount");
                Order o = new Order(id, user, createdAt, status, total);
                arr.add(o);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public static Order selectById(Long id){
        if (id == null) return null;
        Order o = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM orders WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                Long userId = rs.getObject("user_id", Long.class);
                User user = UserDAO.selectById(userId);
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                String status = rs.getString("status");
                BigDecimal total = rs.getBigDecimal("total_amount");
                o = new Order(id, user, createdAt, status, total);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return o;
    }

    public static int insert(Order o){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO orders(id, user_id, createdAt, status, total_amount) VALUES (?,?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if (o.getId() != null) st.setLong(1, o.getId()); else st.setNull(1, java.sql.Types.BIGINT);
            if (o.getUser() != null && o.getUser().getId() != null) st.setLong(2, o.getUser().getId());
            else st.setNull(2, java.sql.Types.BIGINT);
            if (o.getCreatedAt() != null) st.setObject(3, o.getCreatedAt()); else st.setNull(3, java.sql.Types.TIMESTAMP);
            st.setString(4, o.getStatus());
            st.setBigDecimal(5, o.getTotalAmount());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<Order> arr){
        int count = 0;
        for(Order o: arr) count += insert(o);
        return count;
    }

    public static int delete(Order o){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM orders WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, o.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int update(Order o){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE orders SET user_id=?, createdAt=?, status=?, total_amount=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            if (o.getUser() != null && o.getUser().getId() != null) st.setLong(1, o.getUser().getId());
            else st.setNull(1, java.sql.Types.BIGINT);
            if (o.getCreatedAt() != null) st.setObject(2, o.getCreatedAt()); else st.setNull(2, java.sql.Types.TIMESTAMP);
            st.setString(3, o.getStatus());
            st.setBigDecimal(4, o.getTotalAmount());
            st.setLong(5, o.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
