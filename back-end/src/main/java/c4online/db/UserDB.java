package c4online.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDB {
	private static final String userTable = "users";
	//private static final String rankingsTable = "rankings";
	
	private Connection conn;

	public UserDB(Connection _conn) {
		conn = _conn;
	}

	public int validateAccount(String usernameOrEmail, String passHash) {
		try {
			String sqlQuery = "SELECT * FROM "+userTable+" WHERE (username = ? or email = ?) and password_hash = ?";
			
			PreparedStatement stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, usernameOrEmail);
			stmt.setString(2, usernameOrEmail);
			stmt.setString(3, passHash);
			
			ResultSet rset = stmt.executeQuery();
			int userId = -1;
			
			while(rset.next()) {
				userId = rset.getInt("id");
			}
			
			return userId;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
}
