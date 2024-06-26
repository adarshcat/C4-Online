package c4online.game;

class Connect4Session {
	final static int WIDTH = 7;
	final static int HEIGHT = 6;

	final static long TOTAL_GAME_TIME = 1000 * 60 * 1 / 6; // 3 minutes in millis
	
	Player.type[][] board;
	Player.type turn = Player.type.PLAYER1;

	private long player1TimeLeft = TOTAL_GAME_TIME;
	private long player2TimeLeft = TOTAL_GAME_TIME;

	private long player1LastClockStart = 0;
	private long player2LastClockStart = 0;
	
	Connect4Session(){
		clearBoard();

		player1LastClockStart = System.currentTimeMillis();
		player2LastClockStart = System.currentTimeMillis();
	}
	
	boolean playPosition(int column, Player.type playingPlayer) {
		if (column < 0 || column >= WIDTH || playingPlayer == Player.type.NONE || playingPlayer != turn) return false;
		boolean played = false;
		
		for (int j=HEIGHT-1; j>=0; j--) {
			if (board[column][j] == Player.type.NONE) {
				board[column][j] = turn;
				played = true;
				switchTurn();
				break;
			}
		}
		
		return played;
	}
	
	void switchTurn() {
		if (turn == Player.type.PLAYER1){ // It was player1's turn, so end their turn and start player2's turn
			player1TimeLeft -= System.currentTimeMillis() - player1LastClockStart;
			player2LastClockStart = System.currentTimeMillis();
		} else if (turn == Player.type.PLAYER2){ // It was player2's turn, so end their turn and start player1's turn
			player2TimeLeft -= System.currentTimeMillis() - player2LastClockStart;
			player1LastClockStart = System.currentTimeMillis();
		}

		turn = (turn == Player.type.PLAYER1)? Player.type.PLAYER2 : Player.type.PLAYER1;
	}
	
	void clearBoard() {
		board = new Player.type[WIDTH][HEIGHT];
		
		for (int i=0; i<WIDTH; i++) {
			for (int j=0; j<HEIGHT; j++) {
				board[i][j] = Player.type.NONE;
			}
		}
	}

	int getTimeLeft(Player.type playerEnum){
		if (playerEnum == Player.type.PLAYER1){
			// If it's player2's turn which means that player1's clock isn't ticking right now, send the last calculated time left for player1
			if (turn == Player.type.PLAYER2) return (int) player1TimeLeft;

			return (int) (player1TimeLeft - (System.currentTimeMillis() - player1LastClockStart));
		} else if (playerEnum == Player.type.PLAYER2){
			// If it's player1's turn which means that player2's clock isn't ticking right now, send the last calculated time left for player2
			if (turn == Player.type.PLAYER1) return (int) player2TimeLeft;

			return (int) (player2TimeLeft - (System.currentTimeMillis() - player2LastClockStart));
		}

		return 0;
	}
}
