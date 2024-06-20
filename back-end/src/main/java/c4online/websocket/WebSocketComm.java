package c4online.websocket;

import c4online.game.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;

public class WebSocketComm {
	public static String match = "match";
	public static String ping = "ping";
	public static String play = "play";
	public static String board = "board";

	public static final String METHOD_FIELD_ID = "method";
	public static final String PARAM_FIELD_ID = "param";
	
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
}
