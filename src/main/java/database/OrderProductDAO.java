package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Address; // (MỚI) Import
import model.Order;
import model.OrderProduct;
import model.Product;
import model.User; // (MỚI) Import

public class OrderProductDAO {
	
	// (MỚI) SQL Query chuẩn, JOIN với Address (cho currentLocation)
    private static final String SELECT_OP_SQL = 
        "SELECT op.*, " +
        "a.id AS a_id, a.street AS a_street, a.ward AS a_ward, " +
        "a.district AS a_district, a.province AS a_province, a.country AS a_country " +
        "FROM [dbo].[Order_Products] op " +
        "LEFT JOIN [dbo].[Addresses] a ON op.currentLocation = a.id "; // Nối currentLocation (ID) với Address(id)

	
	/**
	 * (CẬP NHẬT) HÀM NỘI BỘ (Helper)
	 * Lấy OrderProduct từ ResultSet (đã JOIN)
	 */
	private static OrderProduct mapResultSetToOrderProduct(ResultSet rs) throws SQLException {
		Long id = rs.getObject("id", Long.class);
		Long orderId = rs.getObject("order_id", Long.class);
		Long productId = rs.getObject("product_id", Long.class);
		Integer quantity = rs.getObject("quantity", Integer.class);
		String review = rs.getString("review");
		LocalDateTime receivedAt = rs.getObject("receivedAt", LocalDateTime.class);
		
		// (Đã thay đổi) long currentAddressId = rs.getLong("currentLocation");
		
		Boolean isReturn = rs.getBoolean("isReturn");
		String returnStatus = rs.getString("returnStatus");
		String returnReason = rs.getString("returnReason");

		// Lấy Order và Product (N+1 query, giữ nguyên logic cũ)
		Order order = OrderDAO.selectById(orderId);
		Product product = ProductDAO.selectById(productId);

		// (MỚI) Lấy đối tượng Address từ currentLocation (đã JOIN)
        Address currentLocationAddr = null;
        long currentAddressId = rs.getLong("currentLocation"); // Lấy ID
        
        if (currentAddressId > 0 && rs.getLong("a_id") > 0) { // Kiểm tra JOIN có thành công không
            currentLocationAddr = new Address();
            currentLocationAddr.setId(rs.getLong("a_id"));
            currentLocationAddr.setStreet(rs.getString("a_street"));
            currentLocationAddr.setWard(rs.getString("a_ward"));
            currentLocationAddr.setDistrict(rs.getString("a_district"));
            currentLocationAddr.setProvince(rs.getString("a_province"));
            currentLocationAddr.setCountry(rs.getString("a_country"));
        } else if (currentAddressId > 0) {
            // Fallback: Nếu JOIN thất bại (ví dụ: đang JOIN op, chưa JOIN a)
            // Tạm thời chỉ set ID, nhưng N+1 query
            // Tốt nhất là đảm bảo mọi câu SELECT đều dùng SELECT_OP_SQL
             currentLocationAddr = AddressDAO.selectById(currentAddressId); // Cần AddressDAO.selectById
        }

		return new OrderProduct(id, order, product, quantity, review, receivedAt, 
                                currentLocationAddr, // (Đã thay đổi)
								isReturn, returnStatus, returnReason);
	}
	
	public static ArrayList<OrderProduct> selectAll() {
		ArrayList<OrderProduct> arr = new ArrayList<>();
		String sql = SELECT_OP_SQL; // (CẬP NHẬT) Dùng SQL JOIN
		try (Connection conn = JDBCUtil.getConnection();
			 PreparedStatement st = conn.prepareStatement(sql);
			 ResultSet rs = st.executeQuery()) {
			
			while (rs.next()) {
				arr.add(mapResultSetToOrderProduct(rs)); // (CẬP NHẬT)
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}

	public static OrderProduct selectById(Long id) {
		if (id == null)
			return null;
		OrderProduct op = null;
		String sql = SELECT_OP_SQL + " WHERE op.id = ?"; // (CẬP NHẬT) Dùng SQL JOIN
		try (Connection conn = JDBCUtil.getConnection();
			 PreparedStatement st = conn.prepareStatement(sql)) {
			
			st.setLong(1, id);
			try (ResultSet rs = st.executeQuery()) {
				if (rs.next()) {
					op = mapResultSetToOrderProduct(rs); // (CẬP NHẬT)
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return op;
	}

	// Hàm insert này ít dùng (chủ yếu dùng insert(ArrayList))
	public static int insert(OrderProduct op) {
		int res = 0;
		// (CẬP NHẬT) SQL
		String sql = "INSERT INTO [dbo].[Order_Products](id, order_id, product_id, quantity, review, receivedAt, currentLocation, isReturn, returnStatus, returnReason) VALUES (?,?,?,?,?,?,?,?,?,?)";
		try (Connection conn = JDBCUtil.getConnection();
			 PreparedStatement st = conn.prepareStatement(sql)) {
			
			// ... (Các trường 1-6 không đổi) ...
			if (op.getId() != null) st.setLong(1, op.getId());
			else st.setNull(1, java.sql.Types.BIGINT);
			
			if (op.getOrder() != null) st.setLong(2, op.getOrder().getId());
			else st.setNull(2, java.sql.Types.BIGINT);
			
			if (op.getProduct() != null) st.setLong(3, op.getProduct().getId());
			else st.setNull(3, java.sql.Types.BIGINT);
			
			if (op.getQuantity() != null) st.setInt(4, op.getQuantity());
			else st.setNull(4, java.sql.Types.INTEGER);
			
			st.setString(5, op.getReview());
			
			if (op.getReceivedAt() != null) st.setObject(6, op.getReceivedAt());
			else st.setNull(6, java.sql.Types.TIMESTAMP);
			
			// (CẬP NHẬT) Set ID của currentLocation
			if (op.getCurrentLocation() != null) {
				st.setLong(7, op.getCurrentLocation().getId());
			} else {
				st.setNull(7, java.sql.Types.BIGINT);
			}
			
			// (Các trường 8, 9, 10 không đổi)
			st.setBoolean(8, op.getIsReturn());
			st.setString(9, op.getReturnStatus());
			st.setString(10, op.getReturnReason());
			
			res = st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// (CẬP NHẬT) insert(ArrayList<OrderProduct> arr)
	// Hàm này dùng khi checkout.
	public static int insert(ArrayList<OrderProduct> arr) {
		int count = 0;
		
        // (MỚI) Lấy địa chỉ của Seller để làm currentLocation mặc định
        User seller = UserDAO.selectSeller();
        Long sellerAddressId = null;
        if (seller != null && seller.getAddress() != null) {
            sellerAddressId = seller.getAddress().getId();
        } else {
             System.err.println("OrderProductDAO.insert: LỖI NGHIÊM TRỌNG, KHÔNG TÌM THẤY ĐỊA CHỈ SELLER!");
             // return 0; // Có thể dừng nếu muốn
        }

		try (Connection conn = JDBCUtil.getConnection()) {
			// (CẬP NHẬT) Thêm [dbo]. và currentLocation
			String sql = "INSERT INTO [dbo].[Order_Products](order_id, product_id, quantity, currentLocation) VALUES (?, ?, ?, ?)";
			PreparedStatement st = conn.prepareStatement(sql);
			for (OrderProduct op : arr) {
				st.setLong(1, op.getOrder().getId());
				st.setLong(2, op.getProduct().getId());
				st.setInt(3, op.getQuantity());
				
                // (CẬP NHẬT) Set ID địa chỉ của Seller
                if (sellerAddressId != null) {
                    st.setLong(4, sellerAddressId); 
                } else {
                    st.setNull(4, java.sql.Types.BIGINT); // Hoặc 1 ID mặc định
                }
				
				int res = st.executeUpdate();
				// System.out.println("OrderProduct insert result: " + res); // (Bỏ log)
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
			String sql = "DELETE FROM [dbo].[Order_Products] WHERE id = ?"; // (CẬP NHẬT) [dbo].
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
		// (CẬP NHẬT) SQL
		String sql = "UPDATE [dbo].[Order_Products] SET order_id=?, product_id=?, quantity=?, review=?, receivedAt=?, currentLocation=?, isReturn=?, returnStatus=?, returnReason=? WHERE id=?";
		try (Connection conn = JDBCUtil.getConnection();
			 PreparedStatement st = conn.prepareStatement(sql)) {
			
			// ... (Các trường 1-5 giữ nguyên) ...
			if (op.getOrder() != null) st.setLong(1, op.getOrder().getId());
			else st.setNull(1, java.sql.Types.BIGINT);
			
			if (op.getProduct() != null) st.setLong(2, op.getProduct().getId());
			else st.setNull(2, java.sql.Types.BIGINT);
			
			if (op.getQuantity() != null) st.setInt(3, op.getQuantity());
			else st.setNull(3, java.sql.Types.INTEGER);
			
			st.setString(4, op.getReview());
			
			if (op.getReceivedAt() != null) st.setObject(5, op.getReceivedAt());
			else st.setNull(5, java.sql.Types.TIMESTAMP);
			
			// (CẬP NHẬT) Set ID của currentLocation
			if (op.getCurrentLocation() != null) {
				st.setLong(6, op.getCurrentLocation().getId());
			} else {
				st.setNull(6, java.sql.Types.BIGINT);
			}
			
			// (Các trường 7, 8, 9 giữ nguyên)
			st.setBoolean(7, op.getIsReturn());
			st.setString(8, op.getReturnStatus());
			st.setString(9, op.getReturnReason());
			
			st.setLong(10, op.getId());
			
			res = st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// (CẬP NHẬT) Hàm này đã cũ, nhưng sửa lại cho đúng logic mới
	// (Ghi chú: hàm getReviewableProducts mới hơn)
	public static List<OrderProduct> selectProductsForReview(long userId) {
		List<OrderProduct> list = new ArrayList<>();
		// (CẬP NHẬT) SQL
		// Logic "đã nhận" giờ là o.status = 'completed'
		String sql = "SELECT op.* " // (Chỉ cần select op.*, vì sẽ dùng mapResultSetToOrderProduct)
				+ "FROM [dbo].[Order_Products] op "
				+ "JOIN [dbo].[Orders] o ON op.order_id = o.id "
				+ "WHERE o.user_id = ? AND o.status = 'completed' AND op.review IS NULL";

		try (Connection conn = JDBCUtil.getConnection(); 
			 PreparedStatement st = conn.prepareStatement(sql)) {
			
			st.setLong(1, userId);
			ResultSet rs = st.executeQuery();
			
			// (CẬP NHẬT) Giờ chúng ta cần JOIN với Address, nhưng câu SQL trên chưa JOIN
			// => Cách 1: Sửa SQL
			// => Cách 2: Chịu N+1 query (vì mapResultSetToOrderProduct sẽ gọi AddressDAO)
			// (Chọn cách 2 để giữ logic đơn giản)
			
			// Chúng ta không thể dùng mapResultSetToOrderProduct vì SQL không khớp
			// Vẫn phải map thủ công
			while (rs.next()) {
				// (Map thủ công như code cũ)
				long opId = rs.getLong("id");
                long orderId = rs.getLong("order_id");
                long productId = rs.getLong("product_id");
                int quantity = rs.getInt("quantity");
                String review = rs.getString("review");
                LocalDateTime receivedAt = rs.getObject("receivedAt", LocalDateTime.class);
                long currentAddressId = rs.getLong("currentLocation"); // (Đã thay đổi)

                Order order = OrderDAO.selectById(orderId);
                Product product = ProductDAO.selectById(productId);
                Address currentAddress = AddressDAO.selectById(currentAddressId); // (N+1 query)
                
				Boolean isReturn = rs.getBoolean("isReturn");
				String returnStatus = rs.getString("returnStatus");
				String returnReason = rs.getString("returnReason");

                OrderProduct op = new OrderProduct(opId, order, product, quantity, review, receivedAt, currentAddress,
                                                   isReturn, returnStatus, returnReason);
				list.add(op);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean submitReview(long orderProductId, String review) {
		String sql = "UPDATE [dbo].[Order_Products] SET review = ? WHERE id = ?"; // (CẬP NHẬT) [dbo].
		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
			st.setString(1, review);
			st.setLong(2, orderProductId);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// (CẬP NHẬT) Sửa hàm markOrderCompleted
	public static boolean markOrderCompleted(long orderId) {
		
	    // (MỚI) Lấy địa chỉ của Customer (người mua)
	    Order order = OrderDAO.selectById(orderId);
	    if (order == null || order.getUser() == null || order.getUser().getAddress() == null) {
	        System.err.println("markOrderCompleted: Không tìm thấy địa chỉ của Customer cho Order ID: " + orderId);
	        return false;
	    }
	    long customerAddressId = order.getUser().getAddress().getId();

	    String sqlOrder = "UPDATE [dbo].[Orders] SET status = 'completed' WHERE id = ?";
	    // (CẬP NHẬT) Set currentLocation = ID địa chỉ của Customer
	    String sqlOP = "UPDATE [dbo].[Order_Products] SET currentLocation = ?, receivedAt = GETDATE() WHERE order_id = ?";

	    try (Connection conn = JDBCUtil.getConnection();
	         PreparedStatement stOrder = conn.prepareStatement(sqlOrder);
	         PreparedStatement stOP = conn.prepareStatement(sqlOP)) {

	        stOrder.setLong(1, orderId);
	        stOrder.executeUpdate();

            stOP.setLong(1, customerAddressId); // (Đã thay đổi)
	        stOP.setLong(2, orderId);
	        stOP.executeUpdate();

	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	// (CẬP NHẬT) selectByOrderId (Dùng cho Report)
	public static List<OrderProduct> selectByOrderId(long orderId) {
		List<OrderProduct> list = new ArrayList<>();
		
		// (CẬP NHẬT) Dùng SQL JOIN (SELECT_OP_SQL) để lấy Address
		String sql = SELECT_OP_SQL + " WHERE op.order_id = ?";
		
		try (Connection conn = JDBCUtil.getConnection(); 
			 PreparedStatement st = conn.prepareStatement(sql)) {
			
			st.setLong(1, orderId);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				// (CẬP NHẬT) Dùng hàm helper
				// (Lưu ý: mapResultSetToOrderProduct đã bao gồm cả Product, 
				// nên câu SQL cũ (JOIN products) có thể gây N+1 nếu mapResultSetToOrderProduct
				// lại gọi ProductDAO.selectById. Đây là N+1 của N+1)
				
				// (Quyết định: Vẫn dùng mapResultSetToOrderProduct để đảm bảo tính nhất quán)
				list.add(mapResultSetToOrderProduct(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static int getTotalOrderProductCount() {
		int count = 0;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM [dbo].[Order_Products]"; // (CẬP NHẬT) [dbo].
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

    public static List<OrderProduct> selectAllPaginated(int pageNumber, int pageSize) {
        List<OrderProduct> arr = new ArrayList<>();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        String sql = SELECT_OP_SQL + // (CẬP NHẬT) Dùng SQL JOIN
                     "ORDER BY op.id DESC " + // (CẬP NHẬT) Thêm 'op.'
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try {
            conn = JDBCUtil.getConnection();
            st = conn.prepareStatement(sql);
            
            int offset = (pageNumber - 1) * pageSize;
            st.setInt(1, offset);
            st.setInt(2, pageSize);
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                arr.add(mapResultSetToOrderProduct(rs)); // (CẬP NHẬT)
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
    
    // (CẬP NHẬT) getReviewableProducts
    public static List<OrderProduct> getReviewableProducts(long userId) {
        List<OrderProduct> arr = new ArrayList<>();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        // (CẬP NHẬT) Dùng SQL JOIN (SELECT_OP_SQL)
        String sql = SELECT_OP_SQL +
                     "JOIN [dbo].[Orders] o ON op.order_id = o.id " +
                     "WHERE o.user_id = ? AND o.status = 'completed' AND op.review IS NULL " +
                     "AND (op.isReturn = 0 OR op.returnStatus = 'rejected')";
        
        try {
            conn = JDBCUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            rs = st.executeQuery();
            
            while (rs.next()) {
                arr.add(mapResultSetToOrderProduct(rs)); // (CẬP NHẬT)
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
    
    
    // ============================================================
    // CÁC HÀM TRẢ HÀNG (Giữ nguyên, chỉ thêm [dbo].)
    // ============================================================

    public static boolean requestReturn(long orderProductId, String reason) {
        String sql = "UPDATE [dbo].[Order_Products] SET isReturn = 1, returnStatus = 'processing', returnReason = ? WHERE id = ? AND (isReturn = 0 OR returnStatus = 'rejected')";
        try (Connection conn = JDBCUtil.getConnection(); 
             PreparedStatement st = conn.prepareStatement(sql)) {
            
            st.setString(1, (reason != null) ? reason : "");
            st.setLong(2, orderProductId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<OrderProduct> getProcessingReturnProducts(long userId) {
        List<OrderProduct> list = new ArrayList<>();
        // (CẬP NHẬT) Dùng SQL JOIN (SELECT_OP_SQL)
        String sql = SELECT_OP_SQL
                + "JOIN [dbo].[Products] p ON op.product_id = p.id " // Cần JOIN Products (nếu helper không JOIN)
                + "JOIN [dbo].[Orders] o ON op.order_id = o.id "
                + "WHERE o.user_id = ? AND op.isReturn = 1 AND op.returnStatus = 'processing'";

        try (Connection conn = JDBCUtil.getConnection(); 
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, userId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                 // (CẬP NHẬT)
                 // Quyết định: Dùng mapResultSetToOrderProduct để nhất quán
                 // (Mặc dù nó sẽ N+1 query cho Product và Order)
                 list.add(mapResultSetToOrderProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static List<OrderProduct> getConfirmedReturnProducts(long userId) {
        List<OrderProduct> list = new ArrayList<>();
        // (CẬP NHẬT) Dùng SQL JOIN (SELECT_OP_SQL)
        String sql = SELECT_OP_SQL
                + "JOIN [dbo].[Products] p ON op.product_id = p.id " // Cần JOIN Products (nếu helper không JOIN)
                + "JOIN [dbo].[Orders] o ON op.order_id = o.id "
                + "WHERE o.user_id = ? AND op.returnStatus = 'confirmed'";

        try (Connection conn = JDBCUtil.getConnection(); 
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, userId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                 list.add(mapResultSetToOrderProduct(rs)); // (CẬP NHẬT)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<OrderProduct> getPendingReturns() {
        List<OrderProduct> list = new ArrayList<>();
        String sql = SELECT_OP_SQL + " WHERE op.isReturn = 1 AND op.returnStatus = 'processing' ORDER BY op.id DESC"; // (CẬP NHẬT)
        
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToOrderProduct(rs)); // (CẬP NHẬT)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean confirmReturn(long orderProductId) {
        String sql = "UPDATE [dbo].[Order_Products] SET returnStatus = 'confirmed' WHERE id = ? AND returnStatus = 'processing'"; // (CẬP NHẬT)
        try (Connection conn = JDBCUtil.getConnection(); 
             PreparedStatement st = conn.prepareStatement(sql)) {
            
            st.setLong(1, orderProductId);
            
            // TODO: Thêm logic hoàn tiền tại đây
            
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean rejectReturn(long orderProductId) {
        String sql = "UPDATE [dbo].[Order_Products] SET isReturn = 0, returnStatus = 'rejected' WHERE id = ? AND returnStatus = 'processing'"; // (CẬP NHẬT)
        try (Connection conn = JDBCUtil.getConnection(); 
             PreparedStatement st = conn.prepareStatement(sql)) {
            
            st.setLong(1, orderProductId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ============================================================
    // (MỚI) HÀM CHO TRANSPORT SERVLET
    // ============================================================
    
    /**
     * (MỚI) Cập nhật vị trí (currentLocation) cho một OrderProduct.
     * Dùng bởi Kho hàng (Warehouse).
     */
    public static boolean updateCurrentLocation(long orderProductId, long warehouseAddressId) {
        String sql = "UPDATE [dbo].[Order_Products] SET currentLocation = ? WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection(); 
             PreparedStatement st = conn.prepareStatement(sql)) {
            
            st.setLong(1, warehouseAddressId);
            st.setLong(2, orderProductId);
            
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

