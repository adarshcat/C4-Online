package c4online.sessions;

import java.util.concurrent.ConcurrentHashMap;

import c4online.security.Security;

public class SessionManager {
	public static final String sessionCookieId = "sessionId";

	private static ConcurrentHashMap<String, User> sessionMap;

	public static void initialise() {
		sessionMap = new ConcurrentHashMap<String, User>();
	}

	public static User getUserFromSessionId(String sessionId) {
		if (!doesSessionExist(sessionId)) return null;

		return sessionMap.get(sessionId);
	}

	public static String createSession(User user) {
		String sessionId = Security.generateSessionId();
		sessionMap.put(sessionId, user);

		return sessionId;
	}

	public static boolean doesSessionExist(String sessionId) {
		return sessionMap.containsKey(sessionId);
	}
}