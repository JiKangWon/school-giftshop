package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Statement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import model.Order;
import model.OrderProduct;
import model.User;

public class OrderDAO {
	public static ArrayList<Order> selectAll() {
		ArrayList<Order> arr = new ArrayList<>();
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "SELECT * FROM orders";
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}

	public static Order selectById(Long id) {
		if (id == null)
			return null;
		Order o = null;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "SELECT * FROM orders WHERE id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setLong(1, id);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	public static int insert(Order order) {
		int res = 0;
		try (Connection conn = JDBCUtil.getConnection()) {
			String sql = "INSERT INTO orders(user_id, createdAt, status, total_amount) VALUES (?,?,?,?)";
			System.out.println("SQL Order insert: " + sql);
			PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			st.setLong(1, order.getUser().getId());
			st.setObject(2, order.getCreatedAt());
			st.setString(3, order.getStatus());
			st.setBigDecimal(4, order.getTotalAmount());

			System.out
					.println("Order params: user_id=" + order.getUser().getId() + ", createdAt=" + order.getCreatedAt()
							+ ", status=" + order.getStatus() + ", totalAmount=" + order.getTotalAmount());

			res = st.executeUpdate();
			System.out.println("Order insert result: " + res);

			ResultSet rs = st.getGeneratedKeys();
			if (rs.next()) {
				order.setId(rs.getLong(1));
				System.out.println("Generated Order ID: " + order.getId());
			}

			rs.close();
			st.close();

		} catch (Exception e) {
			System.out.println("OrderDAO insert error:");
			e.printStackTrace();
		}
		return res;
	}

	public static int insert(ArrayList<Order> arr) {
		int count = 0;
		for (Order o : arr)
			count += insert(o);
		return count;
	}

	public static int delete(Order o) {
		int res = 0;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "DELETE FROM orders WHERE id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setLong(1, o.getId());
			res = st.executeUpdate();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int update(Order o) {
		int res = 0;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "UPDATE orders SET user_id=?, createdAt=?, status=?, total_amount=? WHERE id=?";
			PreparedStatement st = conn.prepareStatement(sql);
			if (o.getUser() != null && o.getUser().getId() != null)
				st.setLong(1, o.getUser().getId());
			else
				st.setNull(1, java.sql.Types.BIGINT);
			if (o.getCreatedAt() != null)
				st.setObject(2, o.getCreatedAt());
			else
				st.setNull(2, java.sql.Types.TIMESTAMP);
			st.setString(3, o.getStatus());
			st.setBigDecimal(4, o.getTotalAmount());
			st.setLong(5, o.getId());
			res = st.executeUpdate();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<Order> selectProcessingOrdersByUser(long userId) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT * FROM orders WHERE user_id = ? AND status = 'processing' ORDER BY createdAt DESC";
		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
			st.setLong(1, userId);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Order o = new Order();
				o.setId(rs.getLong("id"));
				o.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
				o.setStatus(rs.getString("status"));
				list.add(o);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Order> selectCompletedOrdersByUser(long userId) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT * FROM orders WHERE user_id = ? AND status = 'completed' ORDER BY createdAt DESC";
		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
			st.setLong(1, userId);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Order o = new Order();
				o.setId(rs.getLong("id"));
				o.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
				o.setStatus(rs.getString("status"));
				list.add(o);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static int getTotalOrderCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM dbo.orders";
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
	
	public static List<Order> selectAllWithDetailsPaginated(int pageNumber, int pageSize) {
        List<Order> orderList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        // Sắp xếp theo ngày mới nhất và dùng OFFSET...FETCH để phân trang
        String sql = "SELECT * FROM dbo.orders " +
                     "ORDER BY createdAt DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try {
            conn = JDBCUtil.getConnection();
            st = conn.prepareStatement(sql);
            
            // Tính toán offset
            int offset = (pageNumber - 1) * pageSize;
            st.setInt(1, offset);
            st.setInt(2, pageSize);
            
            rs = st.executeQuery();
            
            // Lặp qua 10 đơn hàng của trang này
            while (rs.next()) {
                long orderId = rs.getLong("id");
                long userId = rs.getLong("user_id");
                LocalDateTime createdAt = rs.getObject("createdAt", LocalDateTime.class);
                String status = rs.getString("status");
                BigDecimal totalAmount = rs.getBigDecimal("total_amount");

                // SỬ DỤNG CÁC HÀM DAO CŨ (KHÔNG THAY ĐỔI)
                // 1. Lấy thông tin User (Khách hàng)
                User customer = UserDAO.selectById(userId); 
                
                // 2. Lấy danh sách sản phẩm (Sử dụng hàm selectByOrderId hiện có)
                List<OrderProduct> items = OrderProductDAO.selectByOrderId(orderId);

                // Tạo đối tượng Order đầy đủ
                Order order = new Order();
                order.setId(orderId);
                order.setUser(customer); // Gán đối tượng User
                order.setCreatedAt(createdAt);
                order.setStatus(status);
                order.setTotalAmount(totalAmount);
                order.setOrderProducts(items); // Gán danh sách sản phẩm

                orderList.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (st != null) st.close(); } catch (Exception e) { e.printStackTrace(); }
            JDBCUtil.closeConnection(conn);
        }
        return orderList;
    }
	public static int updateStatus(long orderId, String status) {
	    int res = 0;
	    try {
	        Connection conn = JDBCUtil.getConnection();
	        String sql = "UPDATE dbo.orders SET status = ? WHERE id = ?";
	        PreparedStatement st = conn.prepareStatement(sql);
	        st.setString(1, status);
	        st.setLong(2, orderId);
	        res = st.executeUpdate();
	        st.close();
	        JDBCUtil.closeConnection(conn);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return res;
	}
}
