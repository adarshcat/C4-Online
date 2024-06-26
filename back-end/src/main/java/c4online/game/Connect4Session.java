package c4online.game;

class Connect4Session {
	final static int WIDTH = 7;
	final static int HEIGHT = 6;

	final static long TOTAL_GAME_TIME = 1000 * 60 * 3; // 3 minutes in millis
	
	Player.type[][] board;
	Player.type turn = Player.type.PLAYER1;

	private long player1TimeLeft = TOTAL_GAME_TIME;
	private long player2TimeLeft = TOTAL_GAME_TIME;

	private long player1LastClockStart = 0;
	private long player2LastClockStart = 0;

	enum game_state{P1_WIN, P2_WIN, DRAW, NONE};
	
	Connect4Session(){
		clearBoard();

		player1LastClockStart = System.currentTimeMillis();
		player2LastClockStart = System.currentTimeMillis();
	}

	game_state playPosition(int column, Player.type playingPlayer) {
		if (column < 0 || column >= WIDTH || playingPlayer == Player.type.NONE || playingPlayer != turn) return game_state.NONE;
		boolean played = false;
		
		for (int j=HEIGHT-1; j>=0; j--) {
			if (board[column][j] == Player.type.NONE) {
				board[column][j] = turn;
				played = true;
				switchTurn();
				break;
			}
		}

		Player.type winningPlayer = checkWin();
		if (winningPlayer != Player.type.NONE){
			if (winningPlayer == Player.type.PLAYER1){
				System.out.println("Player 1 won!");
				return game_state.P1_WIN;
			}
			else if (winningPlayer == Player.type.PLAYER2){
				System.out.println("Player 2 won!");
				return game_state.P2_WIN;
			}
		}

		boolean isFilled = isFilled();
		if (isFilled) return game_state.DRAW;
		
		return game_state.NONE;
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

	boolean isFilled(){
		for (int i=0; i<WIDTH; i++) {
			for (int j=0; j<HEIGHT; j++) {
				if (board[i][j] == Player.type.NONE) return false;
			}
		}

		return true;
	}

	Player.type checkWin(){
		for (int i=0; i<board.length; i++){
			for (int j=0; j<board[i].length; j++){
				// continue to next iteration if the current place is empty
				if (board[i][j] == Player.type.NONE) continue;

				// check if there are 4 spaces to the right
				if (i <= board.length - 4){
					// check if the 4 places to the right contain the same player pieces
					Player.type currentPiece = board[i][j];
					boolean matching = true;
					for (int k=i; k<i+4; k++){
						if (currentPiece != board[k][j]){
							matching = false;
							break;
						}
					}

					if (matching) return currentPiece;
				}


				// check if there are 4 spaces to the bottom
				if (j <= board[i].length - 4){
					// check if the 4 places to the bottom contain the same player pieces
					Player.type currentPiece = board[i][j];
					boolean matching = true;
					for (int k=j; k<j+4; k++){
						if (currentPiece != board[i][k]){
							matching = false;
							break;
						}
					}

					if (matching) return currentPiece;
				}


				// check if there are 4 spaces to the bottom right
				if (i <= board.length - 4 && j <= board[i].length - 4){
					// check if the 4 places to the bottom right contain the same player pieces
					Player.type currentPiece = board[i][j];
					boolean matching = true;
					for (int k=0; k<4; k++){
						if (currentPiece != board[i+k][j+k]){
							matching = false;
							break;
						}
					}

					if (matching) return currentPiece;
				}


				// check if there are 4 spaces to the bottom left
				if (i >= 3 && j <= board[i].length - 4){
					// check if the 4 places to the bottom left contain the same player pieces
					Player.type currentPiece = board[i][j];
					boolean matching = true;
					for (int k=0; k<4; k++){
						if (currentPiece != board[i-k][j+k]){
							matching = false;
							break;
						}
					}

					if (matching) return currentPiece;
				}
			}
		}

		return Player.type.NONE;
	}
}
