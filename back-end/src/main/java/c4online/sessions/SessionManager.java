package c4online.sessions;

import java.util.concurrent.ConcurrentHashMap;

import c4online.db.DatabaseManager;
import c4online.security.Security;

public class SessionManager {
	public static final String sessionCookieId = "sessionId";
	public static final int sessionAge = 60 * 60 * 24;

	private static ConcurrentHashMap<String, User> sessionMap;

	public static void initialise() {
		sessionMap = new ConcurrentHashMap<String, User>();
		
		// DEBUG - remove this block of code later
			String debugSession = "0mY_V04azp3XSblAXN5sLsehqM4xMrVautHi_WqJKwI";
			User debugUser = DatabaseManager.getUserDataById(1);
			sessionMap.put(debugSession, debugUser);

			String debugSession2 = "xpzlVLGERirG4ogooNTr8CSMI-Tuz8McvM79Seb6T5g";
			User debugUser2 = DatabaseManager.getUserDataById(2);
			sessionMap.put(debugSession2, debugUser2);
		// ----------------------------------------
	}
	
	// session update related functions
	public static String createSession(User user) {
		String sessionId = Security.generateSessionId();
		
		// delete any previous entry with the same user id
		deleteSessionByUserId(user.id);
		// add the new session into the map
		sessionMap.put(sessionId, user);

		return sessionId;
	}
	
	public static boolean deleteSessionByUserId(int userId) {
		boolean somethingRemoved = false;
		
		for (ConcurrentHashMap.Entry<String, User> pair : sessionMap.entrySet()) {
			if (pair.getValue().id == userId) {
				User removed = sessionMap.remove(pair.getKey());
				if (removed != null) somethingRemoved = true;
			}
		}
		
		return somethingRemoved;
	}

	public static void updateSessionByUserId(int userId, User user) {
		for (ConcurrentHashMap.Entry<String, User> pair : sessionMap.entrySet()) {
			if (pair.getValue().id == userId) {
				sessionMap.put(pair.getKey(), user);
			}
		}
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