package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Order;
import model.OrderProduct;
import model.Product;

public class OrderProductDAO {
	public static ArrayList<OrderProduct> selectAll() {
		ArrayList<OrderProduct> arr = new ArrayList<>();
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "SELECT * FROM order_products";
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}

	public static OrderProduct selectById(Long id) {
		if (id == null)
			return null;
		OrderProduct op = null;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "SELECT * FROM order_products WHERE id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setLong(1, id);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return op;
	}

	public static int insert(OrderProduct op) {
		int res = 0;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "INSERT INTO order_products(id, order_id, product_id, quantity, review, receivedAt, currentLocation) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement st = conn.prepareStatement(sql);
			if (op.getId() != null)
				st.setLong(1, op.getId());
			else
				st.setNull(1, java.sql.Types.BIGINT);
			if (op.getOrder() != null && op.getOrder().getId() != null)
				st.setLong(2, op.getOrder().getId());
			else
				st.setNull(2, java.sql.Types.BIGINT);
			if (op.getProduct() != null && op.getProduct().getId() != null)
				st.setLong(3, op.getProduct().getId());
			else
				st.setNull(3, java.sql.Types.BIGINT);
			if (op.getQuantity() != null)
				st.setInt(4, op.getQuantity());
			else
				st.setNull(4, java.sql.Types.INTEGER);
			st.setString(5, op.getReview());
			if (op.getReceivedAt() != null)
				st.setObject(6, op.getReceivedAt());
			else
				st.setNull(6, java.sql.Types.TIMESTAMP);
			st.setString(7, op.getCurrentLocation());
			res = st.executeUpdate();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int insert(ArrayList<OrderProduct> arr) {
		int count = 0;
		try (Connection conn = JDBCUtil.getConnection()) {
			// Thêm currentLocation vào SQL
			String sql = "INSERT INTO order_products(order_id, product_id, quantity, currentLocation) VALUES (?, ?, ?, ?)";
			PreparedStatement st = conn.prepareStatement(sql);
			for (OrderProduct op : arr) {
				st.setLong(1, op.getOrder().getId());
				st.setLong(2, op.getProduct().getId());
				st.setInt(3, op.getQuantity());
				st.setString(4, "Processing"); // Truyền giá trị mặc định

				int res = st.executeUpdate();
				System.out.println("OrderProduct insert result: " + res);
				count += res;
			}
			st.close();
		} catch (Exception e) {
			System.out.println("OrderProductDAO insert error:");
			e.printStackTrace();
		}
		return count;
	}

	public static int delete(OrderProduct op) {
		int res = 0;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "DELETE FROM order_products WHERE id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setLong(1, op.getId());
			res = st.executeUpdate();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int update(OrderProduct op) {
		int res = 0;
		try {
			Connection conn = JDBCUtil.getConnection();
			String sql = "UPDATE order_products SET order_id=?, product_id=?, quantity=?, review=?, receivedAt=?, currentLocation=? WHERE id=?";
			PreparedStatement st = conn.prepareStatement(sql);
			if (op.getOrder() != null && op.getOrder().getId() != null)
				st.setLong(1, op.getOrder().getId());
			else
				st.setNull(1, java.sql.Types.BIGINT);
			if (op.getProduct() != null && op.getProduct().getId() != null)
				st.setLong(2, op.getProduct().getId());
			else
				st.setNull(2, java.sql.Types.BIGINT);
			if (op.getQuantity() != null)
				st.setInt(3, op.getQuantity());
			else
				st.setNull(3, java.sql.Types.INTEGER);
			st.setString(4, op.getReview());
			if (op.getReceivedAt() != null)
				st.setObject(5, op.getReceivedAt());
			else
				st.setNull(5, java.sql.Types.TIMESTAMP);
			st.setString(6, op.getCurrentLocation());
			st.setLong(7, op.getId());
			res = st.executeUpdate();
			st.close();
			JDBCUtil.closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<OrderProduct> selectProductsForReview(long userId) {
		List<OrderProduct> list = new ArrayList<>();
		String sql = "SELECT op.id AS op_id, op.order_id, op.product_id, op.quantity, op.review, op.receivedAt, op.currentLocation, "
				+ "p.name AS productName, p.price AS productPrice, "
				+ "o.createdAt AS orderCreatedAt, o.status AS orderStatus " + "FROM order_products op "
				+ "JOIN products p ON op.product_id = p.id " + "JOIN orders o ON op.order_id = o.id "
				+ "WHERE o.user_id = ? AND op.currentLocation = 'completed' AND op.review IS NULL";

		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
			st.setLong(1, userId);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				OrderProduct op = new OrderProduct();

				op.setId(rs.getLong("op_id"));
				op.setQuantity(rs.getInt("quantity"));
				op.setReview(rs.getString("review"));
				java.sql.Timestamp ts = rs.getTimestamp("receivedAt");
				if (ts != null) {
					op.setReceivedAt(ts.toLocalDateTime());
				}
				op.setCurrentLocation(rs.getString("currentLocation"));

				Product p = new Product();
				p.setId(rs.getLong("product_id"));
				p.setName(rs.getString("productName"));
				p.setPrice(rs.getBigDecimal("productPrice"));
				op.setProduct(p);

				Order order = new Order();
				order.setId(rs.getLong("order_id"));
				java.sql.Timestamp ot = rs.getTimestamp("orderCreatedAt");
				if (ot != null) {
					order.setCreatedAt(ot.toLocalDateTime());
				}
				order.setStatus(rs.getString("orderStatus"));
				op.setOrder(order);

				list.add(op);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean submitReview(long orderProductId, String review) {
		String sql = "UPDATE order_products SET review = ? WHERE id = ?";
		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
			st.setString(1, review);
			st.setLong(2, orderProductId);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean markOrderCompleted(long orderId) {
	    String sqlOrder = "UPDATE orders SET status = 'completed' WHERE id = ?";
	    String sqlOP = "UPDATE order_products SET currentLocation = 'completed', receivedAt = GETDATE() WHERE order_id = ?";

	    try (Connection conn = JDBCUtil.getConnection();
	         PreparedStatement stOrder = conn.prepareStatement(sqlOrder);
	         PreparedStatement stOP = conn.prepareStatement(sqlOP)) {

	        stOrder.setLong(1, orderId);
	        stOrder.executeUpdate();

	        stOP.setLong(1, orderId);
	        stOP.executeUpdate();

	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	public static List<OrderProduct> selectByOrderId(long orderId) {
		List<OrderProduct> list = new ArrayList<>();
		String sql = "SELECT op.*, p.name AS productName, p.price AS productPrice " + "FROM order_products op "
				+ "JOIN products p ON op.product_id = p.id " + "WHERE op.order_id = ?";
		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
			st.setLong(1, orderId);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				OrderProduct op = new OrderProduct();
				Product p = new Product();
				p.setId(rs.getLong("product_id"));
				p.setName(rs.getString("productName"));
				p.setPrice(rs.getBigDecimal("productPrice"));
				op.setProduct(p);
				op.setQuantity(rs.getInt("quantity"));
				op.setId(rs.getLong("id"));
				list.add(op);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}