package database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Import SQLException
import java.sql.Statement;  // Import Statement
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Category;
import model.Product;
// Không cần import model.User nữa nếu Product không liên kết trực tiếp

public class ProductDAO {

	/**
	 * Chuyển đổi trạng thái sản phẩm ('active' <-> 'hide').
	 * @param productId ID sản phẩm
	 * @return Số dòng bị ảnh hưởng
	 */
	public static int toggleProductStatus(Long productId) {
		int res = 0;
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = JDBCUtil.getConnection();
			// Giả sử status là 'active' và 'hide'
			String sql = "UPDATE products SET status = CASE WHEN status = 'active' THEN 'hide' ELSE 'active' END WHERE id = ?";
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
	 * Đếm tổng số sản phẩm trong CSDL.
	 * @return Tổng số sản phẩm
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
			try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
			try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
			JDBCUtil.closeConnection(conn);
		}
		return count;
	}

	/**
	 * Lấy danh sách TẤT CẢ sản phẩm theo phân trang.
	 * @param pageNumber Số trang hiện tại (bắt đầu từ 1)
	 * @param pageSize Số sản phẩm mỗi trang
	 * @return ArrayList các sản phẩm của trang đó
	 */
	public static ArrayList<Product> selectAllPaginated(int pageNumber, int pageSize) {
		ArrayList<Product> arr = new ArrayList<>();
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtil.getConnection();
			// JOIN để lấy tên category
			String sql = "SELECT p.*, c.name as category_name " +
						 "FROM products p " +
						 "LEFT JOIN categories c ON p.category_id = c.id " +
						 "ORDER BY p.id DESC " +
						 "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			st = conn.prepareStatement(sql);
			int offset = (pageNumber - 1) * pageSize;
			st.setInt(1, offset);
			st.setInt(2, pageSize);
			rs = st.executeQuery();

			while (rs.next()) {
				Product p = mapResultSetToProduct(rs); // Sử dụng hàm helper
				if (p != null) arr.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
			try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
			JDBCUtil.closeConnection(conn);
		}
		return arr;
	}

    /**
     * Thêm sản phẩm mới chỉ với 5 trường cơ bản và trả về ID được tạo tự động.
     * Các trường khác (status, createdAt) sẽ lấy giá trị DEFAULT từ CSDL.
     * @param p Đối tượng Product chứa thông tin cần insert (stock, price, name, description, category)
     * @return ID của sản phẩm mới, hoặc -1 nếu thất bại
     * @throws SQLException Nếu có lỗi SQL xảy ra
     */
    public static long insertAndGetId(Product p) throws SQLException {
        long generatedId = -1;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet generatedKeys = null;

        // Câu SQL chỉ insert 5 cột yêu cầu (status và createdAt có DEFAULT trong DB)
        String sql = "INSERT INTO products(stock, price, name, description, category_id) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try {
            conn = JDBCUtil.getConnection();
            st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // 1. stock
            if (p.getStock() != null) st.setInt(1, p.getStock()); else st.setNull(1, java.sql.Types.INTEGER);
            // 2. price
            st.setBigDecimal(2, p.getPrice());
            // 3. name
            st.setString(3, p.getName());
            // 4. description
            st.setString(4, p.getDescription());
            // 5. category_id
            if (p.getCategory() != null && p.getCategory().getId() != null) st.setInt(5, p.getCategory().getId());
            else st.setNull(5, java.sql.Types.INTEGER);

            int affectedRows = st.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getLong(1); // Lấy ID
                } else {
                    throw new SQLException("Thêm sản phẩm thành công nhưng không lấy được ID.");
                }
            } else {
                 throw new SQLException("Thêm sản phẩm thất bại, không có dòng nào bị ảnh hưởng.");
            }

        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (st != null) st.close(); } catch (SQLException e) { e.printStackTrace(); }
            JDBCUtil.closeConnection(conn);
        }
        return generatedId;
    }

	/**
	 * Lấy tất cả sản phẩm (nên hạn chế dùng nếu dữ liệu lớn, dùng phân trang thay thế).
	 * @return ArrayList tất cả sản phẩm
	 */
	public static ArrayList<Product> selectAll() {
		ArrayList<Product> arr = new ArrayList<>();
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtil.getConnection();
			// JOIN để lấy tên category
			String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id";
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			while (rs.next()) {
                Product p = mapResultSetToProduct(rs); // Sử dụng hàm helper
				if (p != null) arr.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
			try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
			JDBCUtil.closeConnection(conn);
		}
		return arr;
	}

	/**
	 * Lấy thông tin sản phẩm bằng ID.
	 * @param id ID sản phẩm
	 * @return Đối tượng Product hoặc null nếu không tìm thấy
	 */
	public static Product selectById(Long id) {
		if (id == null) return null;
		Product p = null;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtil.getConnection();
			// JOIN để lấy tên category
			String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
			st = conn.prepareStatement(sql);
			st.setLong(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
                p = mapResultSetToProduct(rs); // Sử dụng hàm helper
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
			try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
			JDBCUtil.closeConnection(conn);
		}
		return p;
	}

	/**
	 * Thêm sản phẩm mới (chỉ 5 trường cơ bản).
	 * Hàm này không trả về ID. Nên dùng insertAndGetId nếu cần ID.
	 * @param p Đối tượng Product
	 * @return Số dòng bị ảnh hưởng
	 */
	public static int insert(Product p) {
		int res = 0;
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = JDBCUtil.getConnection();
            // Chỉ insert 5 cột (status, createdAt có DEFAULT)
			String sql = "INSERT INTO products(stock, price, name, description, category_id) VALUES (?,?,?,?,?)";
			st = conn.prepareStatement(sql);

			if (p.getStock() != null) st.setInt(1, p.getStock()); else st.setNull(1, java.sql.Types.INTEGER);
			st.setBigDecimal(2, p.getPrice());
			st.setString(3, p.getName());
			st.setString(4, p.getDescription());
			if (p.getCategory() != null && p.getCategory().getId() != null) st.setInt(5, p.getCategory().getId());
			else st.setNull(5, java.sql.Types.INTEGER);

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
	 * Xóa sản phẩm theo ID.
	 * @param productId ID sản phẩm cần xóa
	 * @return Số dòng bị ảnh hưởng
	 */
	public static int deleteById(Long productId) {
		int res = 0;
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = JDBCUtil.getConnection();
			String sql = "DELETE FROM products WHERE id = ?";
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
	 * Cập nhật thông tin sản phẩm.
	 * @param p Đối tượng Product chứa thông tin mới
	 * @return Số dòng bị ảnh hưởng
	 */
	public static int update(Product p) {
		int res = 0;
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = JDBCUtil.getConnection();
			// Cập nhật các trường có thể thay đổi
			String sql = "UPDATE products SET stock=?, price=?, name=?, description=?, category_id=?, status=?, createdAt=? WHERE id=?";
			st = conn.prepareStatement(sql);
			if (p.getStock() != null) st.setInt(1, p.getStock()); else st.setNull(1, java.sql.Types.INTEGER);
			st.setBigDecimal(2, p.getPrice());
			st.setString(3, p.getName());
			st.setString(4, p.getDescription());
			if (p.getCategory() != null && p.getCategory().getId() != null) st.setInt(5, p.getCategory().getId());
			else st.setNull(5, java.sql.Types.INTEGER);
			st.setString(6, p.getStatus());
			// Cập nhật createdAt? Thông thường không nên, trừ khi có lý do đặc biệt
			if (p.getCreatedAt() != null) st.setObject(7, p.getCreatedAt()); else st.setNull(7, java.sql.Types.TIMESTAMP);
			st.setLong(8, p.getId());
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
	 * Lấy danh sách sản phẩm theo category ID.
	 * @param categoryId ID của category
	 * @return List các sản phẩm thuộc category đó
	 */
	public static List<Product> selectByCategory(int categoryId) {
		List<Product> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtil.getConnection();
			// JOIN để lấy tên category
			String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.category_id = ?";
			st = conn.prepareStatement(sql);
			st.setInt(1, categoryId);
			rs = st.executeQuery();
			while (rs.next()) {
                Product p = mapResultSetToProduct(rs); // Sử dụng hàm helper
				if (p != null) list.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
			try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
			JDBCUtil.closeConnection(conn);
		}
		return list;
	}

	/**
	 * Tìm kiếm sản phẩm theo tên (keyword).
	 * @param keyword Từ khóa tìm kiếm
	 * @return List các sản phẩm có tên chứa từ khóa
	 */
	public static List<Product> searchByName(String keyword) {
		List<Product> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtil.getConnection();
			 // JOIN để lấy tên category
			String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.name LIKE ?";
			st = conn.prepareStatement(sql);
			st.setString(1, "%" + keyword + "%");
			rs = st.executeQuery();
			while (rs.next()) {
                Product p = mapResultSetToProduct(rs); // Sử dụng hàm helper
				if (p != null) list.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
			try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
			JDBCUtil.closeConnection(conn);
		}
		return list;
	}

	/**
	 * Đếm tổng số lượng đã bán của một sản phẩm.
	 * @param productId ID sản phẩm
	 * @return Tổng số lượng đã bán
	 */
	public static int countSold(Long productId) {
		int sold = 0;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtil.getConnection();
			String sql = "SELECT SUM(quantity) AS total_sold FROM order_products WHERE product_id = ?";
			st = conn.prepareStatement(sql);
			st.setLong(1, productId);
			rs = st.executeQuery();
			if (rs.next())
				sold = rs.getInt("total_sold"); // Trả về 0 nếu SUM là NULL
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
			try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
			JDBCUtil.closeConnection(conn);
		}
		return sold;
	}

	/**
	 * Giảm số lượng tồn kho của sản phẩm.
	 * @param productId ID sản phẩm
	 * @param quantity Số lượng cần giảm
	 * @throws SQLException Nếu không tìm thấy sản phẩm hoặc có lỗi SQL
	 */
	public static void decreaseStock(long productId, int quantity) throws SQLException {
		String sql = "UPDATE products SET stock = stock - ? WHERE id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JDBCUtil.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, quantity);
			ps.setLong(2, productId);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 0) {
				 throw new SQLException("Giảm stock thất bại, không tìm thấy product ID: " + productId);
			}
		} finally {
			 try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
			JDBCUtil.closeConnection(conn);
		}
	}

    // --- HÀM HELPER để tạo đối tượng Product từ ResultSet ---
	/**
	 * Ánh xạ một dòng ResultSet sang đối tượng Product.
	 * @param rs ResultSet đang trỏ đến dòng cần ánh xạ
	 * @return Đối tượng Product hoặc null nếu có lỗi
	 * @throws SQLException Nếu có lỗi khi đọc ResultSet
	 */
    private static Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        if (rs == null) return null;

        Long id = rs.getObject("id", Long.class);
        Integer stock = rs.getObject("stock", Integer.class);
        BigDecimal price = rs.getBigDecimal("price");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Integer categoryId = rs.getObject("category_id", Integer.class);
        String categoryName = null;
        try { // Lấy categoryName nếu có cột đó (từ JOIN)
            categoryName = rs.getString("category_name");
        } catch (SQLException e) {
            // Bỏ qua nếu không có cột category_name (ví dụ khi gọi từ hàm chỉ select bảng products)
        }
        String status = rs.getString("status");
        LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
        // Không còn sellerId

        Category category = null;
        if (categoryId != null) {
            category = new Category();
            category.setId(categoryId);
            // Lấy tên category từ JOIN nếu có, nếu không thì để null
            category.setName(categoryName);
            // Nếu cần tên category đầy đủ mà không JOIN, phải gọi CategoryDAO.selectById(categoryId) ở đây
            // Tuy nhiên, việc JOIN trong câu SQL thường hiệu quả hơn
        }

        // Tạo đối tượng Product bằng setters (linh hoạt hơn constructor)
        Product p = new Product();
        p.setId(id);
        p.setStock(stock);
        p.setPrice(price);
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setStatus(status);
        p.setCreatedAt(createdAt);
        // Không setSeller nữa

        return p;
    }
}