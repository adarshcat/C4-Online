package c4online.websocket;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;

import c4online.game.GameInstance;
import c4online.game.GameManager;
import c4online.game.Player;
import c4online.sessions.SessionManager;
import c4online.sessions.User;

@WebSocket
public class GameWebSocket {
	String sessionId;
	Player player;

	GameInstance gameInstance; // This caches the current associated game instance this session is a part of

	private void startNewSession() {
		GameManager.addNewPlayer(player);
	}

	private void connectToPreviousSession() {
		// make sure to update all the previous session objects in existing player objects to the newest session. So all the game updates are sent to the newest socket
		Player oldPlayer = GameManager.connectPreviousPlayer(player);

		// In case the oldPlayer is still null (unlikely) just abort, close the connection and leave
		if (oldPlayer == null) {
			player.websocketSession.close();
			return;
		}

		player = oldPlayer;

		System.out.println("Connecting " + player.username + " to a previous game session");
	}
	

	@OnWebSocketConnect
	public void onConnect(Session session) {
		ServletUpgradeRequest req = (ServletUpgradeRequest) session.getUpgradeRequest();
		List<HttpCookie> cookies = req.getCookies();

		// Process cookies to get session cookies or other information
		String sessionId = null;
		if (cookies != null) {
			for (HttpCookie cookie : cookies) {
				if (cookie.getName().equals(SessionManager.sessionCookieId) && SessionManager.doesSessionExist(cookie.getValue()))
					sessionId = cookie.getValue();
			}
		}

		if (sessionId != null) {
			// It's a completely valid session
			this.sessionId = sessionId;

			// convert the user information into player class and store  the websocket session in there
			User user = SessionManager.getUserFromSessionId(sessionId);
			
			Player player = new Player(user);
			this.player = player;
			player.websocketSession = session;

			// Check if the user is returning after a connection break
			GameManager.player_state gameSessionExists = GameManager.doesConnectionExist(player.id);
			if (gameSessionExists == GameManager.player_state.NONE) {
				// the client is new and doesn't have any ongoing game sessions
				startNewSession();
			} else {
				// the client has a pre-existing game session, revert the state to that session
				connectToPreviousSession();
			}

		} else {
			// It's an invalid session, reject it
			session.close();
		}
	}

	@OnWebSocketMessage
	public void onMessage(Session session, String message) {
		// extract the relevant fields from the json packet
		HashMap<String, String> jsonData = WebSocketComm.parseJSONPacket(message);
		if (jsonData == null){
			System.out.println("Invalid data");
			return;
		}

		String method = jsonData.get(WebSocketComm.METHOD_FIELD_ID);
		String param = jsonData.get(WebSocketComm.PARAM_FIELD_ID);
		// ------------------------------------------------

		// if it's a ping message, parse it early and return
		if (method.equals(WebSocketComm.ping)) {
			player.ping();
			return;
		} else if (method.equals(WebSocketComm.cancelMatchmaking)){
			GameManager.removeFromMatchmaking(player.id);
			return;
		}

		if (gameInstance == null) {
			gameInstance = GameManager.forwardPlayerMessage(player.id, method, param);
		} else {
			gameInstance.parseMessage(player.id, method, param);
		}
	}

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		System.out.println("Connection closed with " + player.username + " reason: " + reason);
	}
}
