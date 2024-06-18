package c4online.game;

import java.util.concurrent.CopyOnWriteArrayList;

public class GameManager {
	private static CopyOnWriteArrayList<GameInstance> gameInstances;
	private static Matchmaker matchmaker;
	
	public enum user_state{NONE, MATCHMAKING, IN_GAME};
	
	public static void initialise() {
		matchmaker = new Matchmaker();
		gameInstances = new CopyOnWriteArrayList<GameInstance>();
	}
	
	public static void terminateGame(GameInstance gameInst) {
		// game termination procedure: close player1's connection, player2's connection and then remove gameInstance
		gameInst.player1.websocketSession.close();
		gameInst.player2.websocketSession.close();
		gameInstances.remove(gameInst);
	}
	
	public static void addNewUser(Player newPlayer) {
		GameInstance gameInst = matchmaker.queueUser(newPlayer);
		if (gameInst != null) {
			// A match is created and a new game instance is returned as a result
			gameInstances.add(gameInst);
			System.out.println("A match is made! " + gameInst.player1.username + " and " + gameInst.player2.username);
		}
	}
	
	public static Player connectPreviousUser(Player player) {
		Player oldPlayer = matchmaker.updateSessionIfPresent(player.id, player.websocketSession);
		if (oldPlayer == null) {
			for (GameInstance ginst : gameInstances) {
				oldPlayer = ginst.updateIfUserPresent(player.id, player.websocketSession);
				if (oldPlayer != null) return oldPlayer;
			}
		}
		
		return oldPlayer;
	}
	
	public static user_state doesConnectionExist(int userId) {
		if (matchmaker.isUserInQueue(userId)) {
			return user_state.MATCHMAKING;
		} else {
			for (GameInstance ginst : gameInstances) {
				if (ginst.isUserInThisGame(userId))
					return user_state.IN_GAME;
			}
		}
		
		return user_state.NONE;
	}
}
