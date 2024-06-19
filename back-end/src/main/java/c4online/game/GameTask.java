package c4online.game;

import java.util.concurrent.CopyOnWriteArrayList;

public class GameTask implements Runnable{
	
	private static CopyOnWriteArrayList<GameInstance> gameInstances;
	private static Matchmaker matchmaker;
	
	GameTask(CopyOnWriteArrayList<GameInstance> _gameInstances, Matchmaker _matchmaker){
		gameInstances = _gameInstances;
		matchmaker = _matchmaker;
	}
	
	@Override
	public void run() {
		GameInstance newGame = matchmaker.runTask();
		if (newGame != null)
			createGame(newGame);
		
		for (GameInstance ginst : gameInstances) {
			ginst.runTask();
		}
	}
	
	private static void createGame(GameInstance gameInst) {
		gameInstances.add(gameInst);
		gameInst.initialise();
	}
}
