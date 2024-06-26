package c4online.game;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.websocket.api.Session;

class Matchmaker {
	private CopyOnWriteArrayList<Player> players;
	
	Matchmaker(){
		players = new CopyOnWriteArrayList<Player>();
	}
	
	// runs a periodic matchmaking task which returns a game instance when a match is made
	GameInstance runTask() {
		return matchmake();
	}
	
	
	// functions related to querying
	boolean isPlayerInQueue(int playerId) {
		for (Player player : players) {
			if (player.id == playerId) return true;
		}
		
		return false;
	}
	
	
	// functions related to updating matchmaking
	void queuePlayer(Player newPlayer) {
		// add the user into the queue
		players.add(newPlayer);
	}

	boolean removePlayer(int playerId){
		for (int i=0; i<players.size(); i++) {
			Player player = players.get(i);
			if (player.id == playerId) {
				players.remove(i);
				return true;
			}
		}

		return false;
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
