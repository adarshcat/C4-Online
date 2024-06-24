package c4online.game;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameManager {
	private static CopyOnWriteArrayList<GameInstance> gameInstances;
	private static Matchmaker matchmaker;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public enum player_state{NONE, MATCHMAKING, IN_GAME};
	
	public static void initialise() {
		matchmaker = new Matchmaker();
		gameInstances = new CopyOnWriteArrayList<GameInstance>();
		startGameTask();
	}
	
	private static void startGameTask() {
		GameTask gameTask = new GameTask(gameInstances, matchmaker);
		// executes the run function in teh gameTask object every 2 seconds
		scheduler.scheduleAtFixedRate(gameTask, 0, 500, TimeUnit.MILLISECONDS);
	}
	
	// game management related functions
	public static void terminateGame(GameInstance gameInst) {
		// game termination procedure: close player1's connection, player2's connection and then remove gameInstance
		gameInst.player1.websocketSession.close();
		gameInst.player2.websocketSession.close();
		gameInstances.remove(gameInst);
	}
	// ---------------------------------
	
	
	// user management related functions
	public static void addNewPlayer(Player newPlayer) {
		matchmaker.queuePlayer(newPlayer);
		System.out.println("Added "+newPlayer.username+" to matchmaking queue");
	}
	
	public static Player connectPreviousPlayer(Player player) {
		Player oldPlayer = matchmaker.updateSessionIfPresent(player.id, player.websocketSession);
		if (oldPlayer == null) {
			for (GameInstance gInst : gameInstances) {
				oldPlayer = gInst.updateSessionIfPresent(player.id, player.websocketSession);
				if (oldPlayer != null) return oldPlayer;
			}
		}
		
		return oldPlayer;
	}
	
	public static player_state doesConnectionExist(int playerId) {
		if (matchmaker.isPlayerInQueue(playerId)) {
			return player_state.MATCHMAKING;
		} else {
			for (GameInstance gInst : gameInstances) {
				if (gInst.isPlayerInThisGame(playerId) != null)
					return player_state.IN_GAME;
			}
		}
		
		return player_state.NONE;
	}
	
	public static GameInstance forwardPlayerMessage(int playerId, String method, String param) {
		for (GameInstance gInst : gameInstances) {
			if (gInst.parseMessage(playerId, method, param)) return gInst;
		}
		
		return null;
	}
	// ----------------------------------
}
