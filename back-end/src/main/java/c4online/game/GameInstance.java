package c4online.game;

import org.eclipse.jetty.websocket.api.Session;

import c4online.websocket.WebSocketComm;

import java.util.HashMap;

public class GameInstance {
	public final static int GAME_ABANDONED_TIME = 15 * 1000; // time for game to be considered abandoned by a player in millis

	Player player1;
	Player player2;

	Connect4Session gameSession;

	GameInstance(Player player1, Player player2){
		this.player1 = player1;
		this.player2 = player2;
	}

	void initialise() {
		try {
			this.player1.websocketSession.getRemote().sendString(WebSocketComm.constructJSONPacket(WebSocketComm.match, player2.parseToJSON()));
			this.player2.websocketSession.getRemote().sendString(WebSocketComm.constructJSONPacket(WebSocketComm.match, player1.parseToJSON()));

			System.out.println("A match is made! " + player1.username + " and " + player2.username);
		} catch (Exception e) {
			terminateGameWithAbort();
		}

		gameSession = new Connect4Session();
	}

	// runs a periodic game instance management task
	void runTask() {
		terminateGameIfTimedOut();
		sendBoardDataToPlayers();
	}

	public boolean parseMessage(int playerId, String method, String param) {
		Player player = isPlayerInThisGame(playerId);
		if (player == null) return false;

		// parse game related types of commands here
		if (method.equals(WebSocketComm.play)){
			try {
				gameSession.playPosition(Integer.parseInt(param), getPlayerEnumFromId(playerId));
				sendBoardDataToPlayers();
			} catch (Exception ignored) {}
		}

		return true;
	}


	// functions for sending data over to the players via websocket
	private void sendBoardDataToPlayers() {
		String data = WebSocketComm.encodeBoardToString(gameSession.board);
		broadcastMessage(data);
	}

	private void broadcastMessage(String msg) {
		sendToPlayer1(msg);
		sendToPlayer2(msg);
	}

	private void sendToPlayer1(String msg){
		try {
			player1.websocketSession.getRemote().sendString(msg);
		} catch (Exception ignored) {}
	}

	private void sendToPlayer2(String msg){
		try {
			player2.websocketSession.getRemote().sendString(msg);
		} catch (Exception ignored) {}
	}
	
	private void sendGameStateToReturningPlayer(Player returningPlayer) {
		sendBoardDataToPlayers();
	}
	// --------------------------------------------------------------


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

	private void terminateGameWithAbort(){
		System.out.println("Game terminated: aborted");

		// TODO: notify the players that the game is over and the fact that it has been aborted

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

	Player.type getPlayerEnumFromId(int playerId){
		if (player1.id == playerId) return Player.type.PLAYER1;
		else if (player2.id == playerId) return Player.type.PLAYER2;

		return Player.type.NONE;
	}
	// --------------------------
}
