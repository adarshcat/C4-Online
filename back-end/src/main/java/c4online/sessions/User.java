package c4online.sessions;

import java.sql.Timestamp;

public class User {
	public int id;
	public String username;
	public String email;
	public Timestamp lastLogin;
	public Timestamp createdAt;

	public int rating;
	public int gamesPlayed;
	public int gamesWon;
	public int gamesLost;
	public int gamesDraw;
	
	public void printData() {
		System.out.print("id "+id+" ");
		System.out.print("username "+username+" ");
		System.out.print("email "+email+" ");
		System.out.print("lastLogin "+lastLogin.toString()+" ");
		System.out.println("createdAt "+createdAt.toString());
	}
}
