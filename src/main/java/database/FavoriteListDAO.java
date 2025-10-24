package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

import model.FavoriteList;
import model.Product;
import model.User;

public class FavoriteListDAO {
    public static ArrayList<FavoriteList> selectAll(){
        ArrayList<FavoriteList> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM favorite_lists";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Long userId = rs.getObject("user_id", Long.class);
                Long productId = rs.getObject("product_id", Long.class);
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                User u = UserDAO.selectById(userId);
                Product p = ProductDAO.selectById(productId);
                arr.add(new FavoriteList(u, p, createdAt));
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public static FavoriteList selectByKey(Long userId, Long productId){
        if (userId == null || productId == null) return null;
        FavoriteList f = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM favorite_lists WHERE user_id=? AND product_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            st.setLong(2, productId);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                User u = UserDAO.selectById(userId);
                Product p = ProductDAO.selectById(productId);
                f = new FavoriteList(u, p, createdAt);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return f;
    }

    public static int insert(FavoriteList f){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO favorite_lists(user_id, product_id, createdAt) VALUES (?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if (f.getUser() != null && f.getUser().getId() != null) st.setLong(1, f.getUser().getId()); else st.setNull(1, java.sql.Types.BIGINT);
            if (f.getProduct() != null && f.getProduct().getId() != null) st.setLong(2, f.getProduct().getId()); else st.setNull(2, java.sql.Types.BIGINT);
            if (f.getCreatedAt() != null) st.setObject(3, f.getCreatedAt()); else st.setNull(3, java.sql.Types.TIMESTAMP);
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<FavoriteList> arr){
        int count = 0;
        for(FavoriteList f: arr) count += insert(f);
        return count;
    }

    public static int delete(FavoriteList f){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM favorite_lists WHERE user_id=? AND product_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, f.getUser().getId());
            st.setLong(2, f.getProduct().getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int update(FavoriteList f){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE favorite_lists SET createdAt=? WHERE user_id=? AND product_id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            if (f.getCreatedAt() != null) st.setObject(1, f.getCreatedAt()); else st.setNull(1, java.sql.Types.TIMESTAMP);
            st.setLong(2, f.getUser().getId());
            st.setLong(3, f.getProduct().getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
