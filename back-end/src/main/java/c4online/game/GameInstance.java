package c4online.game;

import org.eclipse.jetty.websocket.api.Session;

import c4online.websocket.WebSocketComm;

public class GameInstance {
	public final static int GAME_ABANDONED_TIME = 60 * 1000; // time for game to be considered abandoned by a player in millis
	
	enum players_enum{PLAYER1, PLAYER2};
	Player player1;
	Player player2;
	
	GameInstance(Player player1, Player player2){
		this.player1 = player1;
		this.player2 = player2;
	}
	
	void initialise() {
		try {
			this.player1.websocketSession.getRemote().sendString("Match is created with: " + player2.username);
			this.player2.websocketSession.getRemote().sendString("Match is created with: " + player1.username);
			
			System.out.println("A match is made! " + player1.username + " and " + player2.username);
		} catch (Exception e) {
			GameManager.terminateGame(this);
		}
	}
	
	// runs a periodic game instance management task 
	void runTask() {
		terminateGameIfTimedOut();
	}
	
	public boolean parseMessage(int playerId, String message) {
		Player player = isPlayerInThisGame(playerId);
		
		if (player == null) return false;
		
		if (message.equals(WebSocketComm.ping)) {
			player.ping();
		} else {
			System.out.println("Parsing message: " + message + " from: " + player.username);
		}
		
		return true;
	}
	
	
	// game termination functions
	private void terminateGameIfTimedOut() {
		if (player1.timeSinceLastPing() > GAME_ABANDONED_TIME) {
			terminateGameWithWin(players_enum.PLAYER2);
			System.out.println("Timed out");
		}
		if (player2.timeSinceLastPing() > GAME_ABANDONED_TIME) {
			terminateGameWithWin(players_enum.PLAYER1);
			System.out.println("Timed out");
		}
	}
	
	private void terminateGameWithWin(players_enum winner) {
		String winString = (winner == players_enum.PLAYER1)? player1.username : player2.username;
		System.out.println("Game terminated: " + winString + " wins");
		
		// TODO: notify the players that the game is over and who wins (ofc with the rating changes and outcomes)
		
		GameManager.terminateGame(this);
	}
	// ----------------------------
	
	
	// user management functions
	Player updateSessionIfPresent(int playerId, Session newSession) {
		Player player = isPlayerInThisGame(playerId);
		
		if (player == null) return null;
		
		player.websocketSession.close();
		player.websocketSession = newSession;
		return player;
	}
	
	Player isPlayerInThisGame(int playerId) {
		if (player1.id == playerId) return player1;
		else if (player2.id == playerId) return player2;
		
		return null;
	}
	// --------------------------
}
