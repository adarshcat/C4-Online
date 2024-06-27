package c4online.game;

import c4online.db.DatabaseManager;
import org.eclipse.jetty.websocket.api.Session;

import c4online.websocket.WebSocketComm;

import java.util.HashMap;

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
			this.player1.websocketSession.getRemote().sendString(WebSocketComm.constructJSONPacket(WebSocketComm.match, player2.parseToJSON()));
			this.player2.websocketSession.getRemote().sendString(WebSocketComm.constructJSONPacket(WebSocketComm.match, player1.parseToJSON()));

			System.out.println("A match is made! " + player1.username + " and " + player2.username);
		} catch (Exception e) {
			terminateGameWithAbort();
		}

		gameSession = new Connect4Session();

		sendColorDataToPlayers();
		sendTurnDataToPlayers();
	}

	// runs a periodic game instance management task
	void runTask() {
		terminateGameIfTimedOut();
		sendBoardDataToPlayers();
		sendTimeDataToPlayers();
	}

	public boolean parseMessage(int playerId, String method, String param) {
		Player player = isPlayerInThisGame(playerId);
		if (player == null) return false;

		// parse game related types of commands here
		if (method.equals(WebSocketComm.play)){
			try {
				int playPosition = Integer.parseInt(param);
				Connect4Session.game_state gameState = gameSession.playPosition(playPosition, getPlayerEnumFromId(playerId));
				sendBoardDataToPlayers();
				sendPlayDataToOtherPlayer(playerId, playPosition);

				parseGameState(gameState);
			} catch (Exception ignored) {}
		} else if (method.equals(WebSocketComm.resign)){
			Player.type playerEnum = getPlayerEnumFromId(player.id);
			if (playerEnum == Player.type.PLAYER1){
				// If it's player 1 sending resign request, terminate game with player 2 winning
				terminateGameWithWin(Player.type.PLAYER2);
			} else if (playerEnum == Player.type.PLAYER2){
				// If it's player 2 sending resign request, terminate game with player 1 winning
				terminateGameWithWin(Player.type.PLAYER1);
			}
		}

		return true;
	}

	private void parseGameState(Connect4Session.game_state gameState){
		if (gameState == Connect4Session.game_state.NONE) return;

		if (gameState == Connect4Session.game_state.P1_WIN) terminateGameWithWin(Player.type.PLAYER1);
		else if (gameState == Connect4Session.game_state.P2_WIN) terminateGameWithWin(Player.type.PLAYER2);
		else if (gameState == Connect4Session.game_state.DRAW) terminateGameWithDraw();
	}

	// functions for sending data over to the players via websocket
	private void sendBoardDataToPlayers() {
		String data = WebSocketComm.encodeBoardToString(gameSession.board);
		broadcastMessage(data);
	}

	private void sendPlayDataToOtherPlayer(int playerId, int playPosition){
		Player.type playerEnum = getPlayerEnumFromId(playerId);

		if (playerEnum == Player.type.PLAYER1){
			sendToPlayer2(WebSocketComm.constructJSONPacket(WebSocketComm.played, String.valueOf(playPosition)));
		} else if (playerEnum == Player.type.PLAYER2){
			sendToPlayer1(WebSocketComm.constructJSONPacket(WebSocketComm.played, String.valueOf(playPosition)));
		}
	}

	private void sendColorDataToPlayers(){
		sendToPlayer1(WebSocketComm.constructJSONPacket(WebSocketComm.color, WebSocketComm.PLAYER1_COLOR));
		sendToPlayer2(WebSocketComm.constructJSONPacket(WebSocketComm.color, WebSocketComm.PLAYER2_COLOR));
	}

	private void sendTurnDataToPlayers(){
		broadcastMessage(WebSocketComm.constructJSONPacket(WebSocketComm.turn, WebSocketComm.getColorStringFromEnum(gameSession.turn)));
	}

	private void sendTimeDataToPlayers(){
		int player1TimeLeft = gameSession.getTimeLeft(Player.type.PLAYER1);
		int player2TimeLeft = gameSession.getTimeLeft(Player.type.PLAYER2);

		String param = "{\"" + WebSocketComm.PLAYER1_COLOR + "\": " + player1TimeLeft + ", \""+WebSocketComm.PLAYER2_COLOR + "\": " + player2TimeLeft + "}";

		broadcastMessage(WebSocketComm.constructJSONPacket(WebSocketComm.time, param));
	}

	private void sendOpponentDataToPlayers(){
		// sends info of their opponents to the players
		sendToPlayer1(WebSocketComm.constructJSONPacket(WebSocketComm.match, player2.parseToJSON()));
		sendToPlayer2(WebSocketComm.constructJSONPacket(WebSocketComm.match, player1.parseToJSON()));
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
		sendOpponentDataToPlayers();
		sendColorDataToPlayers();
		sendBoardDataToPlayers();
		sendTurnDataToPlayers();
	}
	// --------------------------------------------------------------


	// game termination functions
	private void terminateGameIfTimedOut() {
		// terminates the game with the other player winning, if a player's last ping is beyond a certain time limit, or they ran out on clock
		int player1TimeLeft = gameSession.getTimeLeft(Player.type.PLAYER1);
		int player2TimeLeft = gameSession.getTimeLeft(Player.type.PLAYER2);

		if (player1.timeSinceLastPing() > GAME_ABANDONED_TIME || player1TimeLeft <= 0) {
			terminateGameWithWin(Player.type.PLAYER2);
			System.out.println("Timed out");
		}
		if (player2.timeSinceLastPing() > GAME_ABANDONED_TIME || player2TimeLeft <= 0) {
			terminateGameWithWin(Player.type.PLAYER1);
			System.out.println("Timed out");
		}
	}

	private void terminateGameWithWin(Player.type winner) {
		if (winner == Player.type.NONE) return;

		String winString = (winner == Player.type.PLAYER1)? player1.username : player2.username;
		System.out.println("Game terminated: " + winString + " wins");

		int ratingChange = RatingManager.calcRatingChange(player1.rating, player2.rating, winner);
		int player1dr = ratingChange;
		int player2dr = ratingChange;

		if (winner == Player.type.PLAYER1){
			DatabaseManager.ratingdb.updateGamePlayed(player1.id, true, false);
			DatabaseManager.ratingdb.updateGamePlayed(player2.id, false, false);

			player2dr *= -1;
		}
		else if (winner == Player.type.PLAYER2){
			DatabaseManager.ratingdb.updateGamePlayed(player1.id, false, false);
			DatabaseManager.ratingdb.updateGamePlayed(player2.id, true, false);

			player1dr *= -1;
		}

		// update the player's rating in the database
		RatingManager.updateRatingInDB(player1dr, player2dr, player1.id, player2.id);

		String player1Packet = WebSocketComm.constructGameTermPacket("win", WebSocketComm.getColorStringFromEnum(winner), player1dr);
		String player2Packet = WebSocketComm.constructGameTermPacket("win", WebSocketComm.getColorStringFromEnum(winner), player2dr);

		sendToPlayer1(player1Packet);
		sendToPlayer2(player2Packet);

		GameManager.terminateGame(this);
	}

	private void terminateGameWithAbort(){
		System.out.println("Game terminated: aborted");

		String packet = WebSocketComm.constructGameTermPacket("abort", "", 0);
		broadcastMessage(packet);

		GameManager.terminateGame(this);
	}

	private void terminateGameWithDraw(){
		System.out.println("Game terminated: draw");

		DatabaseManager.ratingdb.updateGamePlayed(player1.id, false, true);
		DatabaseManager.ratingdb.updateGamePlayed(player2.id, false, true);

		String packet = WebSocketComm.constructGameTermPacket("draw", "", 0);
		broadcastMessage(packet);

		GameManager.terminateGame(this);
	}
	// ----------------------------


	// player management functions
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
