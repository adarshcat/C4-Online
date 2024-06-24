package c4online.websocket;

import c4online.game.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;

public class WebSocketComm {
	public static String match = "match"; // method name when broadcasting match begun data to players
	public static String color = "color"; // method name when broadcasting color data to players
	public static String ping = "ping"; // method name when receiving pings from players
	public static String play = "play"; // method name when receiving play position from players
	public static String board = "board"; // method name when broadcasting board state to players
	public static String played = "played"; // method name when broadcasting play position to other player
	public static String turn = "turn"; // method name when broadcasting turn position to players
	public static String time = "time"; // method name when broadcasting time data to players

	public static final String METHOD_FIELD_ID = "method";
	public static final String PARAM_FIELD_ID = "param";

	public static final String PLAYER1_COLOR = "red";
	public static final String PLAYER2_COLOR = "blue";
	
	private static final int PLAYER1 = 1;
	private static final int PLAYER2 = 2;
	private static final int EMPTY = 0;
	
	public static String encodeBoardToString(Player.type[][] boardState) {
		StringBuilder param = new StringBuilder();
		for (int j = 0; j< boardState[0].length; j++) {
			for (int i = 0; i< boardState.length; i++) {
				if (boardState[i][j] == Player.type.PLAYER1)
					param.append(PLAYER1);
				else if (boardState[i][j] == Player.type.PLAYER2)
					param.append(PLAYER2);
				else
					param.append(EMPTY);

				param.append(" ");
			}
		}
		
		return constructJSONPacket(board, param.toString());
	}

	public static String constructJSONPacket(String methodName, String paramStr){
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put(METHOD_FIELD_ID, methodName);
		rootNode.put(PARAM_FIELD_ID, paramStr);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (JsonProcessingException ignored) {
			return "";
		}
	}


	public static HashMap<String, String> parseJSONPacket(String jsonPacket){
		ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(jsonPacket);

			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put(METHOD_FIELD_ID, node.get(METHOD_FIELD_ID).asText());
			hashMap.put(PARAM_FIELD_ID, node.get(PARAM_FIELD_ID).asText());

			return hashMap;
        } catch (Exception e) {
			return null;
        }
	}

	public static String getTurnStringFromEnum(Player.type playerEnum){
		if (playerEnum == Player.type.PLAYER1) return PLAYER1_COLOR;
		else if (playerEnum == Player.type.PLAYER2) return PLAYER2_COLOR;

		return null;
	}
}
