package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Product;
import model.ProductImage;

public class ProductImageDAO {
    public static ArrayList<ProductImage> selectAll(){
        ArrayList<ProductImage> arr = new ArrayList<>();
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM product_images";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Long id = rs.getObject("id", Long.class);
                Long productId = rs.getObject("product_id", Long.class);
                Product product = ProductDAO.selectById(productId);
                String imgLink = rs.getString("imgLink");
                arr.add(new ProductImage(id, product, imgLink));
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return arr;
    }

    public static ProductImage selectById(Long id){
        if (id == null) return null;
        ProductImage pi = null;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "SELECT * FROM product_images WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                Long productId = rs.getObject("product_id", Long.class);
                Product product = ProductDAO.selectById(productId);
                String imgLink = rs.getString("imgLink");
                pi = new ProductImage(id, product, imgLink);
            }
            rs.close();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return pi;
    }

    public static int insert(ProductImage pi){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "INSERT INTO product_images(id, product_id, imgLink) VALUES (?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            if (pi.getId() != null) st.setLong(1, pi.getId()); else st.setNull(1, java.sql.Types.BIGINT);
            if (pi.getProduct() != null && pi.getProduct().getId() != null) st.setLong(2, pi.getProduct().getId());
            else st.setNull(2, java.sql.Types.BIGINT);
            st.setString(3, pi.getImgLink());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int insert(ArrayList<ProductImage> arr){
        int count = 0;
        for(ProductImage p: arr) count += insert(p);
        return count;
    }

    public static int delete(ProductImage pi){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "DELETE FROM product_images WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, pi.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int update(ProductImage pi){
        int res = 0;
        try {
            Connection conn = JDBCUtil.getConnection();
            String sql = "UPDATE product_images SET product_id=?, imgLink=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            if (pi.getProduct() != null && pi.getProduct().getId() != null) st.setLong(1, pi.getProduct().getId());
            else st.setNull(1, java.sql.Types.BIGINT);
            st.setString(2, pi.getImgLink());
            st.setLong(3, pi.getId());
            res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
