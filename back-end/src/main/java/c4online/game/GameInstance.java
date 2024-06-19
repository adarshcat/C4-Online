package c4online.game;

import org.eclipse.jetty.websocket.api.Session;

import c4online.websocket.WebSocketComm;

public class GameInstance {
	public final static int GAME_ABANDONED_TIME = 60 * 1000; // time for game to be considered abandoned by a player in millis

	Player player1;
	Player player2;

	Connect4Session gameSession;

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

		gameSession = new Connect4Session();
	}

	// runs a periodic game instance management task
	void runTask() {
		terminateGameIfTimedOut();
		sendBoardDataToPlayers();
	}

	public boolean parseMessage(int playerId, String message) {
		// TODO: refactor this so that it uses json to receive messages probably use handler objects?
		Player player = isPlayerInThisGame(playerId);

		if (player == null) return false;

		if (message.equals(WebSocketComm.ping)) {
			player.ping();
			return true; // TODO: REMOVE THIS IN THE FUTURE RIGHT NOW I ADDED THIS TO AVOID ANNOYING PING FLOODING THE STDOUT
		} else{
			String[] msgComponents = message.split(" ");
			if (msgComponents.length == 2 && msgComponents[0].equals(WebSocketComm.play)) {
				try {
					gameSession.playPosition(Integer.parseInt(msgComponents[1]));
					sendBoardDataToPlayers();
				} catch (Exception e) {}
			}
		}

		System.out.println("Parsing message: " + message + " from: " + player.username);

		return true;
	}

	private void sendBoardDataToPlayers() {
		String data = WebSocketComm.encodeBoardToString(gameSession.board);
		broadcastMessage(data);
	}

	private void broadcastMessage(String msg) {
		try {
			player1.websocketSession.getRemote().sendString(msg);
		} catch (Exception e) {}
		try {
			player2.websocketSession.getRemote().sendString(msg);
		} catch (Exception e) {}
	}
	
	
	private void sendGameStateToReturningPlayer(Player returningPlayer) {
		sendBoardDataToPlayers();
	}


	// game termination functions
	private void terminateGameIfTimedOut() {
		if (player1.timeSinceLastPing() > GAME_ABANDONED_TIME) {
			terminateGameWithWin(Player.type.PLAYER2);
			System.out.println("Timed out");
		}
		if (player2.timeSinceLastPing() > GAME_ABANDONED_TIME) {
			terminateGameWithWin(Player.type.PLAYER1);
			System.out.println("Timed out");
		}
	}

	private void terminateGameWithWin(Player.type winner) {
		if (winner == Player.type.NONE) return;

		String winString = (winner == Player.type.PLAYER1)? player1.username : player2.username;
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
		
		sendGameStateToReturningPlayer(player);
		return player;
	}

	Player isPlayerInThisGame(int playerId) {
		if (player1.id == playerId) return player1;
		else if (player2.id == playerId) return player2;

		return null;
	}
	// --------------------------
}
