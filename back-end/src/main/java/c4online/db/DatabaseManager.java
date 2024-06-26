package c4online.db;

import c4online.sessions.User;

import java.sql.*;

public class DatabaseManager {
	static Connection connection;

	final static String DB_NAME = "c4online";
	final static String DB_HOST = "localhost";
	final static int DB_PORT = 3306;

	final static String DB_USER = "adarsh";
	final static String DB_PASS = "123";

	// db management classes
	public static UserDB userdb;
	public static RatingDB ratingdb;

	public static void initialise() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"+DB_HOST+":"+DB_PORT+"/"+DB_NAME, DB_USER, DB_PASS);
		} catch (SQLException e) {
			System.out.println("Unable to open a connection to the database");
			e.printStackTrace();
		}

		userdb = new UserDB(connection);
		ratingdb = new RatingDB(connection);
	}

	// functions for converting database data into native data structures
	public static User getUserDataById(int userId) {
		User user = new User();

		boolean populated = userdb.populateUserDataById(userId, user); // populate with actual user info
		ratingdb.populateUserDataById(userId, user); // populate with ratings info

		if (!populated) return null;

		return user;
	}
}
