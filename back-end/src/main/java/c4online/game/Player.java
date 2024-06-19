package c4online.game;

import org.eclipse.jetty.websocket.api.Session;

import c4online.sessions.User;

public class Player extends User{
	public Session websocketSession = null;
	private long lastPing = System.currentTimeMillis();
	
	public enum type{PLAYER1, PLAYER2, NONE};
	
	public Player(User user) {
		id = user.id;
		username = user.username;
		email = user.email;
		lastLogin = user.lastLogin;
		createdAt = user.createdAt;

		rating = user.rating;
		gamesPlayed= user.gamesPlayed;
		gamesWon = user.gamesWon;
		gamesLost = user.gamesLost;
		gamesDraw = user.gamesDraw;
	}
	
	public void ping() {
		lastPing = System.currentTimeMillis();
	}
	
	public long timeSinceLastPing() {
		return System.currentTimeMillis() - lastPing;
	}
}
