package c4online.sessions;

import java.sql.Timestamp;

public class User {
	String username;
	String email;
	Timestamp lastLogin;
	Timestamp createdAt;

	int rating;
	int gamesPlayed;
	int gamesWon;
	int gamesLost;
	int gamesDraw;
}
