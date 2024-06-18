package c4online.websocket;

import java.net.HttpCookie;
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
	
	GameInstance gameInstance; // This caches of the current associated game instance this session is a part of
	
	private void startNewSession() {
		GameManager.addNewUser(player);
	}
	
	private void connectToPreviousSession() {
		// make sure to update all the previous session objects in existing player objects to the newest session. So all the game updates are sent to the newest socket
		player = GameManager.connectPreviousUser(player);
		
		// TODO: Make sure to send the necessary data to the newly logged in client about the previous session's state
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
			
			System.out.println(player.username + " has connected with user id " + player.id);

			// Check if the user is returning after a connection break
			GameManager.user_state gameSessionExists = GameManager.doesConnectionExist(player.id);
			
			if (gameSessionExists == GameManager.user_state.NONE) {
				// the client is new and doesnt have any ongoing game sessions
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
		System.out.println("A new message is received: " + message);
		// TODO: Whenever there's a message, ignore it if matchmaking isn't complete. Accept it if matchmaking is complete
		// ask the GameManager for the gameInstance the client is associated with. Cache it and store into gameInstance for future comm. 
	}

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		System.out.println("Connection closed, reason: " + reason);
	}
}
