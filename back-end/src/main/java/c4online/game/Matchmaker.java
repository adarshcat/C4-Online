package c4online.game;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.websocket.api.Session;

class Matchmaker {
	private CopyOnWriteArrayList<Player> players;
	
	Matchmaker(){
		players = new CopyOnWriteArrayList<Player>();
	}
	
	// functions related to querying
	boolean isUserInQueue(int playerId) {
		for (Player player : players) {
			if (player.id == playerId) return true;
		}
		
		return false;
	}
	
	// functions related to updating matchmaking
	GameInstance queueUser(Player newPlayer) {
		// add the user into the queue and try matchmaking
		players.add(newPlayer);
		
		return matchmake();
	}
	
	Player updateSessionIfPresent(int playerId, Session newSession) {
		for (Player player : players) {
			if (player.id == playerId) {
				player.websocketSession.close();
				player.websocketSession = newSession;
				return player;
			}
		}
		
		return null;
	}
	
	GameInstance matchmake() {
		GameInstance gameInst = null;
		if (players.size() >= 2) {
			gameInst = new GameInstance(players.get(0), players.get(1));
			players.remove(1);
			players.remove(0);
		}
		
		return gameInst;
	}
}
