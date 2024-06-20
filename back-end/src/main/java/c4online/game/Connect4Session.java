package c4online.game;

class Connect4Session {
	final static int WIDTH = 7;
	final static int HEIGHT = 6;
	
	Player.type[][] board;
	Player.type turn = Player.type.PLAYER1;
	
	Connect4Session(){
		clearBoard();
	}
	
	boolean playPosition(int column) {
		if (column < 0 || column >= WIDTH) return false;
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
}
