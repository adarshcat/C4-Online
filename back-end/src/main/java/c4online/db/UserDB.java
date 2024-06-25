package c4online.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import c4online.sessions.User;

public class UserDB {
	private static final String userTable = "users";
	//private static final String rankingsTable = "rankings";
	
	private Connection conn;

	public UserDB(Connection _conn) {
		conn = _conn;
	}
	
	// functions for updating the tables/entries
	public boolean addAccount(String username, String email, String passwordHash) {
		try {
		String sqlQuery = "INSERT INTO "+userTable+"(username, email, password_hash) values(?, ?, ?)";
		
		PreparedStatement stmt = conn.prepareStatement(sqlQuery);
		stmt.setString(1, username);
		stmt.setString(2, email);
		stmt.setString(3, passwordHash);
		
		int rowUpdated = stmt.executeUpdate();
		
		if (rowUpdated != 0) return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updateLastLogin(int userId) {
		try {
			String sqlQuery = "UPDATE "+userTable+" SET last_login = CURRENT_TIMESTAMP where id = ?";
			
			PreparedStatement stmt = conn.prepareStatement(sqlQuery);
			stmt.setInt(1, userId);
						
			int rowUpdated = stmt.executeUpdate();
			
			if (rowUpdated != 0) return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	// functions for validations and queries
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
	
	public boolean doesUserExist(String username) {
		try {
			String sqlQuery = "SELECT * FROM "+userTable+" WHERE username = ?";
			
			PreparedStatement stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, username);
			
			ResultSet rset = stmt.executeQuery();
			int userId = -1;
			
			while(rset.next()) {
				userId = rset.getInt("id");
			}
			
			if (userId != -1)
				return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		
		return false;
	}
	
	public boolean doesEmailExist(String email) {
		try {
			String sqlQuery = "SELECT * FROM "+userTable+" WHERE email = ?";
			
			PreparedStatement stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, email);
			
			ResultSet rset = stmt.executeQuery();
			int userId = -1;
			
			while(rset.next()) {
				userId = rset.getInt("id");
			}
			
			if (userId != -1)
				return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		
		return false;
	}

	// functions for converting database data into native data structures
	public boolean populateUserDataById(int userId, User user) {
		boolean populated = false;

		try {
			String sqlQuery = "SELECT * FROM "+userTable+" WHERE id = ?";

			PreparedStatement stmt = conn.prepareStatement(sqlQuery);
			stmt.setInt(1, userId);

			ResultSet rset = stmt.executeQuery();

			while(rset.next()) {
				populated = true;

				user.id = rset.getInt("id");
				user.username = rset.getString("username");
				user.email = rset.getString("email");
				user.createdAt = rset.getTimestamp("created_at");
				user.lastLogin = rset.getTimestamp("last_login");
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return populated;
	}
}
