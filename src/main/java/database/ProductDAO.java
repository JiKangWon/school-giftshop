package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;

import model.Category;
import model.Product;

public class ProductDAO {
	// Thêm 2 phương thức này vào file database/ProductDAO.java
	public static int toggleProductStatus(Long productId) {
        int res = 0;
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = JDBCUtil.getConnection();
            
            // Dùng SQL CASE WHEN để lật ngược trạng thái
            String sql = "UPDATE products " +
                         "SET status = CASE " +
                         "    WHEN status = 'active' THEN 'hide' " +
                         "    ELSE 'active' " +
                         "END " +
                         "WHERE id = ?";
                         
            st = conn.prepareStatement(sql);
            st.setLong(1, productId);
            res = st.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
            JDBCUtil.closeConnection(conn);
        }
        return res;
    }
    /**
     * Đếm tổng số sản phẩm trong CSDL
     */
    public static int getTotalProductCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM products";
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Luôn đóng tài nguyên
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
            JDBCUtil.closeConnection(conn);
        }
        return count;
    }

    /**
     * Lấy danh sách TẤT CẢ sản phẩm theo phân trang (10 sản phẩm/trang)
     * (Dựa trên hàm selectAll() của bạn)
     */
    public static ArrayList<Product> selectAllPaginated(int pageNumber, int pageSize) {
        ArrayList<Product> arr = new ArrayList<>();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            conn = JDBCUtil.getConnection();
            
            // Sử dụng cú pháp OFFSET...FETCH của SQL Server để phân trang
            String sql = "SELECT * FROM products " +
                         "ORDER BY id DESC " + // Sắp xếp theo ID mới nhất
                         "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            
            st = conn.prepareStatement(sql);
            
            // Tính toán offset (ví dụ: trang 1 offset 0, trang 2 offset 10)
            int offset = (pageNumber - 1) * pageSize;
            
            st.setInt(1, offset);
            st.setInt(2, pageSize);
            
            rs = st.executeQuery();
            
            // Lặp qua kết quả và tạo đối tượng (giống hệt hàm selectAll() của bạn)
            while (rs.next()) {
                Long id = rs.getObject("id", Long.class);
                Integer stock = rs.getObject("stock", Integer.class);
                BigDecimal price = rs.getBigDecimal("price");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Integer categoryId = rs.getObject("category_id", Integer.class);
                Category category = CategoryDAO.selectById(categoryId);
                String status = rs.getString("status");
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                Product p = new Product(id, stock, price, name, description, category, status, createdAt);
                arr.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Luôn đóng tài nguyên
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
            JDBCUtil.closeConnection(conn);
        }
        return arr;
    }
    public static ArrayList<Product> selectAll(){
        ArrayList<Product> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM products";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Long id = rs.getObject("id", Long.class);
                Integer stock = rs.getObject("stock", Integer.class);
                BigDecimal price = rs.getBigDecimal("price");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Integer categoryId = rs.getObject("category_id", Integer.class);
                Category category = CategoryDAO.selectById(categoryId);
                String status = rs.getString("status");
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                Product p = new Product(id, stock, price, name, description, category, status, createdAt);
                arr.add(p);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public static Product selectById(Long id){
        if (id == null) return null;
        Product p = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM products WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                Integer stock = rs.getObject("stock", Integer.class);
                BigDecimal price = rs.getBigDecimal("price");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Integer categoryId = rs.getObject("category_id", Integer.class);
                Category category = CategoryDAO.selectById(categoryId);
                String status = rs.getString("status");
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                p = new Product(id, stock, price, name, description, category, status, createdAt);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return p;
    }

    public static int insert(Product p){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO products(id, stock, price, name, description, category_id, status, createdAt) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if (p.getId() != null) st.setLong(1, p.getId()); else st.setNull(1, java.sql.Types.BIGINT);
            if (p.getStock() != null) st.setInt(2, p.getStock()); else st.setNull(2, java.sql.Types.INTEGER);
            st.setBigDecimal(3, p.getPrice());
            st.setString(4, p.getName());
            st.setString(5, p.getDescription());
            if (p.getCategory() != null && p.getCategory().getId() != null) st.setInt(6, p.getCategory().getId());
            else st.setNull(6, java.sql.Types.INTEGER);
            st.setString(7, p.getStatus());
            if (p.getCreatedAt() != null) st.setObject(8, p.getCreatedAt());
            else st.setNull(8, java.sql.Types.TIMESTAMP);
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<Product> arr){
        int count = 0;
        for(Product p: arr) count += insert(p);
        return count;
    }

    public static int delete(Product p){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM products WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, p.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int update(Product p){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE products SET stock=?, price=?, name=?, description=?, category_id=?, status=?, createdAt=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            if (p.getStock() != null) st.setInt(1, p.getStock()); else st.setNull(1, java.sql.Types.INTEGER);
            st.setBigDecimal(2, p.getPrice());
            st.setString(3, p.getName());
            st.setString(4, p.getDescription());
            if (p.getCategory() != null && p.getCategory().getId() != null) st.setInt(5, p.getCategory().getId());
            else st.setNull(5, java.sql.Types.INTEGER);
            st.setString(6, p.getStatus());
            if (p.getCreatedAt() != null) st.setObject(7, p.getCreatedAt()); else st.setNull(7, java.sql.Types.TIMESTAMP);
            st.setLong(8, p.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
