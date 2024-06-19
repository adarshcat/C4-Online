package c4online.websocket;

import c4online.game.Player;

public class WebSocketComm {
	public static String ping = "ping";
	public static String play = "play";
	
	private static final int PLAYER1 = 1;
	private static final int PLAYER2 = 2;
	private static final int EMPTY = 0;
	
	public static String encodeBoardToString(Player.type[][] board) {
		String encoded = "board ";
		for (int j=0; j<board[0].length; j++) {
			for (int i=0; i<board.length; i++) {
				if (board[i][j] == Player.type.PLAYER1)
					encoded += PLAYER1;
				else if (board[i][j] == Player.type.PLAYER2)
					encoded += PLAYER2;
				else
					encoded += EMPTY;
				
				encoded += " ";
			}
		}
		
		return encoded;
	}
}
