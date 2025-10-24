package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCUtil {
	public static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://localhost:1433;databaseName=SCHOOL_GIFTSHOP;encrypt=false;loginTimeout=30";
			String userName = "sa";
			String password = "123456789";
			connection = DriverManager.getConnection(url, userName, password);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	public static void closeConnection(Connection connection) {
		try {
			if (connection!=null) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
