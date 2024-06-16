package c4online.sessions;

import java.util.concurrent.ConcurrentHashMap;

import c4online.security.Security;

public class SessionManager {
	public static final String sessionCookieId = "sessionId";
	public static final int sessionAge = 60 * 60;

	private static ConcurrentHashMap<String, User> sessionMap;

	public static void initialise() {
		sessionMap = new ConcurrentHashMap<String, User>();
	}
	
	// session update related functions
	public static String createSession(User user) {
		String sessionId = Security.generateSessionId();
		sessionMap.put(sessionId, user);

		return sessionId;
	}
	
	
	// session query related functions
	public static User getUserFromSessionId(String sessionId) {
		if (!doesSessionExist(sessionId)) return null;

		return sessionMap.get(sessionId);
	}

	public static boolean doesSessionExist(String sessionId) {
		return sessionMap.containsKey(sessionId);
	}
}