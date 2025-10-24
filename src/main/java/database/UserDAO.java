package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.math.BigDecimal;

import model.*;

public class UserDAO {
    public static ArrayList<User> selectAll(){
        ArrayList<User> arrUser = new ArrayList<User>();
        try {
            Connection connection = JDBCUtil.getConnection();
            String sql = "SELECT * FROM USERS";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                Long id = rs.getLong("id");
                String userName = rs.getString("username");
                String password = rs.getString("password");
                BigDecimal balance = rs.getBigDecimal("balance");
                String name = rs.getString("name");
                Long addressId = rs.getLong("address_id");
                Address address = AddressDAO.selectById(addressId);
                String addressNumber = rs.getString("addressNumber");
                String phone = rs.getString("phone");
                LocalDateTime createdAt = rs.getObject("createdAt", java.time.LocalDateTime.class);
                int isSeller = rs.getInt("isSeller");
                User newUser = new User(id, userName, password, balance, name, address, addressNumber, phone, createdAt, isSeller);
                arrUser.add(newUser);
            }
            JDBCUtil.closeConnection(connection);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return arrUser;
    }

    public static User selectById(Long id) {
        User user = null;
        try {
            Connection connection = JDBCUtil.getConnection();
            String sql = "SELECT * FROM USERS WHERE ID=?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                String userName = rs.getString("username");
                String password = rs.getString("password");
                BigDecimal balance = rs.getBigDecimal("balance");
                String name = rs.getString("name");
                Long addressId = rs.getLong("address_id");
                Address address = AddressDAO.selectById(addressId);
                String addressNumber = rs.getString("addressNumber");
                String phone = rs.getString("phone");
                LocalDateTime createdAt = rs.getObject("createdAt", java.time.LocalDateTime.class);
                int isSeller = rs.getInt("isSeller");
                User newUser = new User(id, userName, password, balance, name, address, addressNumber, phone, createdAt, isSeller);
                user = newUser;
            }
            JDBCUtil.closeConnection(connection);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return user;
    }
    
    public static User selectByUserName(String userName) {
    	User user = null;
    	try {
    		Connection connection = JDBCUtil.getConnection();
    		String sql = "SELECT * FROM USERS WHERE USERNAME=?";
    		PreparedStatement st = connection.prepareStatement(sql);
    		st.setString(1, userName);
    		ResultSet rs = st.executeQuery();
    		while(rs.next()) {
    			Long id = rs.getLong("id");
                String password = rs.getString("password");
                BigDecimal balance = rs.getBigDecimal("balance");
                String name = rs.getString("name");
                Long addressId = rs.getLong("address_id");
                Address address = AddressDAO.selectById(addressId);
                String addressNumber = rs.getString("addressNumber");
                String phone = rs.getString("phone");
                LocalDateTime createdAt = rs.getObject("createdAt", java.time.LocalDateTime.class);
                int isSeller = rs.getInt("isSeller");
                User newUser = new User(id, userName, password, balance, name, address, addressNumber, phone, createdAt, isSeller);
                user = newUser;
    		}
    		st.close();
    		rs.close();
        	JDBCUtil.closeConnection(connection);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return user;
    }
    
    // trả về 1 nếu tồn tại, 0 nếu không
    public static int isExistInDB(Long id) {
        int exists = 0;
        try {
            Connection connection = JDBCUtil.getConnection();
            String sql = "SELECT COUNT(1) AS cnt FROM USERS WHERE ID = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                long cnt = rs.getLong("cnt");
                if (cnt > 0) exists = 1;
            }
            // đóng tài nguyên
            rs.close();
            st.close();
            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }
    public static int isExistInDB(String userName) {
        int exists = 0;
        try {
            Connection connection = JDBCUtil.getConnection();
            String sql = "SELECT COUNT(1) AS cnt FROM USERS WHERE username = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, userName);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                long cnt = rs.getLong("cnt");
                if (cnt > 0) exists = 1;
            }
            // đóng tài nguyên
            rs.close();
            st.close();
            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    public static int insert(User user) {
        int res = 0;
        try {
            Connection connection = JDBCUtil.getConnection();
            String sql = "INSERT INTO USERS(id, username, password, balance, name, address_id, addressNumber, phone, createdAt, isSeller) VALUES "
                    + "(?,?,?,?,?,?,?,?,?,?)" ;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setBigDecimal(4, user.getBalance());
            preparedStatement.setString(5, user.getName());
            // nếu address null thì setNull
            if (user.getAddress() != null && user.getAddress().getId() != null) {
                preparedStatement.setLong(6, user.getAddress().getId());
            } else {
                preparedStatement.setNull(6, java.sql.Types.BIGINT);
            }
            preparedStatement.setString(7, user.getAddressNumber());
            preparedStatement.setString(8, user.getPhone());
            // setObject với LocalDateTime (driver mới hỗ trợ), nếu null setTimestamp null
            if (user.getCreatedAt() != null) {
                preparedStatement.setObject(9, user.getCreatedAt());
            } else {
                preparedStatement.setNull(9, java.sql.Types.TIMESTAMP);
            }
            preparedStatement.setInt(10, user.getIsSeller());
            res = preparedStatement.executeUpdate();
            preparedStatement.close();
            JDBCUtil.closeConnection(connection);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public static int insert(String userName, String password, String name, Address address, String addressNumber, String phone) {
    	int res = 0;
    	try {
    		Connection connection = JDBCUtil.getConnection();
    		String sql = "INSERT INTO USERS(USERNAME, PASSWORD, NAME, ADDRESS_ID, ADDRESSNUMBER, PHONE)"
    				+ " VALUES(?,?,?,?,?,?)";
    		PreparedStatement st = connection.prepareStatement(sql);
    		st.setString(1, userName);
    		st.setString(2, password);
    		st.setString(3, name);
    		st.setLong(4, (address!=null)?address.getId():java.sql.Types.BIGINT);
    		st.setString(5, addressNumber);
    		st.setString(6, phone);
    		res = st.executeUpdate();
            st.close();
            JDBCUtil.closeConnection(connection);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return res;
    }

    public static int insert(ArrayList<User> arrUser) {
        int count = 0;
        for(User user: arrUser) {
            count += insert(user);
        }
        return count;
    }

    public static int delete(User user) {
        int res = 0;
        try {
            Connection connection = JDBCUtil.getConnection();
            String sql = "DELETE FROM USERS WHERE ID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, user.getId());
            res = preparedStatement.executeUpdate();
            preparedStatement.close();
            JDBCUtil.closeConnection(connection);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int delete(ArrayList<User> arrUser) {
        int count = 0;
        for(User user: arrUser) {
            count += delete(user);
        }
        return count;
    }

    public static int update(User user) {
        int res = 0;
        try {
            Connection connection = JDBCUtil.getConnection();
            String sql = "UPDATE USERS "
                    + "SET username=?, password=?, balance=?, name=?, address_id=?, addressNumber=?, phone=?, createdAt=?, isSeller=? "
                    + "WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setBigDecimal(3, user.getBalance());
            preparedStatement.setString(4, user.getName());
            if (user.getAddress() != null && user.getAddress().getId() != null) {
                preparedStatement.setLong(5, user.getAddress().getId());
            } else {
                preparedStatement.setNull(5, java.sql.Types.BIGINT);
            }
            preparedStatement.setString(6, user.getAddressNumber());
            preparedStatement.setString(7, user.getPhone());
            if (user.getCreatedAt() != null) {
                preparedStatement.setObject(8, user.getCreatedAt());
            } else {
                preparedStatement.setNull(8, java.sql.Types.TIMESTAMP);
            }
            preparedStatement.setInt(9, user.getIsSeller());
            preparedStatement.setLong(10, user.getId());
            res = preparedStatement.executeUpdate();
            preparedStatement.close();
            JDBCUtil.closeConnection(connection);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static User login(String userName, String password) {
    	User user = selectByUserName(userName);
    	if(user==null) {
    		return null;
    	} else {
    		if(password.equals(user.getPassword())) {
    			return user;
    		} else {
    			return null;
    		}
    	}
    }
}
