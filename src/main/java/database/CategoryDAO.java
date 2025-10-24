package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Category;

public class CategoryDAO {
    public static ArrayList<Category> selectAll(){
        ArrayList<Category> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM categories";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Integer id = rs.getObject("id", Integer.class);
                String name = rs.getString("name");
                arr.add(new Category(id, name));
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public static Category selectById(Integer id){
        if (id == null) return null;
        Category c = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM categories WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                String name = rs.getString("name");
                c = new Category(id, name);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return c;
    }

    public static int insert(Category c){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO categories(id, name) VALUES (?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if (c.getId() != null) st.setInt(1, c.getId()); else st.setNull(1, java.sql.Types.INTEGER);
            st.setString(2, c.getName());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<Category> arr){
        int count = 0;
        for(Category c: arr) count += insert(c);
        return count;
    }

    public static int delete(Category c){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM categories WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, c.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int update(Category c){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE categories SET name=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, c.getName());
            st.setInt(2, c.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
