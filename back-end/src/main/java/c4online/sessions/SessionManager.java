package c4online.sessions;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
	public static final String sessionCookieId = "sessionId";

	private static ConcurrentHashMap<String, User> sessionMap;
	private static SecureRandom secureRandom;

	public static void initialise() {
		sessionMap = new ConcurrentHashMap<String, User>();
		secureRandom = new SecureRandom();
	}

	public static User getUserFromSessionId(String sessionId) {
		if (!doesSessionExist(sessionId)) return null;

		return sessionMap.get(sessionId);
	}

	public static String createSession(User user) {
		String sessionId = generateSessionId();
		sessionMap.put(sessionId, user);

		return sessionId;
	}

	public static boolean doesSessionExist(String sessionId) {
		return sessionMap.containsKey(sessionId);
	}

	private static String generateSessionId() {
		byte[] randomBytes = new byte[32];
		secureRandom.nextBytes(randomBytes);
		String id = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

		return id;
	}
}