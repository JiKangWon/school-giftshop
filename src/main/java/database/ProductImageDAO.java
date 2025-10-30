package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Product;
import model.ProductImage;

public class ProductImageDAO {
	public static ArrayList<ProductImage> selectAll() {
		ArrayList<ProductImage> arr = new ArrayList<>();
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "SELECT * FROM product_images";
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Long id = rs.getObject("id", Long.class);
				Long productId = rs.getObject("product_id", Long.class);
				Product product = ProductDAO.selectById(productId);
				String imgLink = rs.getString("imgLink");
				arr.add(new ProductImage(id, product, imgLink));
			}
			rs.close();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}

	public static ProductImage selectById(Long id) {
		if (id == null)
			return null;
		ProductImage pi = null;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "SELECT * FROM product_images WHERE id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setLong(1, id);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				Long productId = rs.getObject("product_id", Long.class);
				Product product = ProductDAO.selectById(productId);
				String imgLink = rs.getString("imgLink");
				pi = new ProductImage(id, product, imgLink);
			}
			rs.close();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pi;
	}

	public static int insert(ProductImage pi) {
		int res = 0;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "INSERT INTO product_images(product_id, imgLink) VALUES (?,?)";
			PreparedStatement st = conn.prepareStatement(sql);
			if (pi.getProduct() != null && pi.getProduct().getId() != null)
				st.setLong(1, pi.getProduct().getId());
			else
				st.setNull(1, java.sql.Types.BIGINT);
			st.setString(2, pi.getImgLink());
			res = st.executeUpdate();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public static int insert(long productId, String imgLink) throws SQLException {
        int result = 0;
        Connection conn = null;
        PreparedStatement st = null;

        // Câu lệnh SQL INSERT vào bảng product_images
        // Đảm bảo tên bảng và tên cột khớp với CSDL của bạn (product_id, imgLink)
        String sql = "INSERT INTO dbo.product_images (product_id, imgLink) VALUES (?, ?)";

        try {
            conn = JDBCUtil.getConnection(); // Lấy kết nối
            st = conn.prepareStatement(sql); // Chuẩn bị câu lệnh

            // Gán giá trị cho các tham số (?)
            st.setLong(1, productId);   // Tham số thứ nhất: product_id
            st.setString(2, imgLink); // Tham số thứ hai: imgLink

            // Thực thi câu lệnh INSERT và lấy số dòng bị ảnh hưởng
            result = st.executeUpdate();

        } finally {
            // Đóng PreparedStatement và Connection trong khối finally để đảm bảo tài nguyên được giải phóng
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Ghi log lỗi nếu không đóng được statement
            }
            JDBCUtil.closeConnection(conn); // Đóng kết nối
        }
        return result; // Trả về số dòng đã được thêm
    }
	public static int insert(ArrayList<ProductImage> arr) {
		int count = 0;
		for (ProductImage p : arr)
			count += insert(p);
		return count;
	}

	public static int delete(ProductImage pi) {
		int res = 0;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "DELETE FROM product_images WHERE id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setLong(1, pi.getId());
			res = st.executeUpdate();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int update(ProductImage pi) {
		int res = 0;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "UPDATE product_images SET product_id=?, imgLink=? WHERE id=?";
			PreparedStatement st = conn.prepareStatement(sql);
			if (pi.getProduct() != null && pi.getProduct().getId() != null)
				st.setLong(1, pi.getProduct().getId());
			else
				st.setNull(1, java.sql.Types.BIGINT);
			st.setString(2, pi.getImgLink());
			st.setLong(3, pi.getId());
			res = st.executeUpdate();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// lay danh sach
	public static ArrayList<ProductImage> selectByProductId(Long productId) {
		ArrayList<ProductImage> arr = new ArrayList<>();
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "SELECT * FROM product_images WHERE product_id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setLong(1, productId);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Long id = rs.getObject("id", Long.class);
				Product product = ProductDAO.selectById(productId);
				String imgLink = rs.getString("imgLink");
				arr.add(new ProductImage(id, product, imgLink));
			}
			rs.close();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}
}
