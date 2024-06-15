package c4online.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
	static Connection connection;

	final static String DB_NAME = "c4online";
	final static String DB_HOST = "localhost";
	final static int DB_PORT = 3306;

	final static String DB_USER = "adarsh";
	final static String DB_PASS = "123";

	// db management classes
	public static UserDB userdb;

	public static void initialise() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"+DB_HOST+":"+DB_PORT+"/"+DB_NAME, DB_USER, DB_PASS);
		} catch (SQLException e) {
			System.out.println("Unable to open a connection to the database");
			e.printStackTrace();
		}

		userdb = new UserDB(connection);
	}
}
