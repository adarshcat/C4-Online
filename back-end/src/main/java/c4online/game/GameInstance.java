package c4online.game;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

public class GameInstance {
	Player player1;
	Player player2;
	
	GameInstance(Player player1, Player player2){
		this.player1 = player1;
		this.player2 = player2;
		
		try {
			this.player1.websocketSession.getRemote().sendString("Match is created with: " + player2.username);
			this.player2.websocketSession.getRemote().sendString("Match is created with: " + player1.username);
		} catch (IOException e) {
			GameManager.terminateGame(this);
		}
	}
	
	Player updateIfUserPresent(int playerId, Session newSession) {
		if (player1.id == playerId) {
			player1.websocketSession.close();
			player1.websocketSession = newSession;
			return player1;
		} else if (player2.id == playerId) {
			player2.websocketSession.close();
			player2.websocketSession = newSession;
			return player2;
		}
		
		return null;
	}
	
	boolean isUserInThisGame(int playerId) {
		return (player1.id == playerId || player2.id == playerId);
	}
}
