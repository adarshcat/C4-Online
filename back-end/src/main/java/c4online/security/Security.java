package c4online.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Security {

	private static SecureRandom secureRandom;
	private static MessageDigest shaEncoder;

	public static void initialise() {
		secureRandom = new SecureRandom();
		try {
			shaEncoder = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static String generateSessionId() {
		byte[] randomBytes = new byte[32];
		secureRandom.nextBytes(randomBytes);
		String id = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

		return id;
	}
	
	public static String passToHash(String pass) {
		byte[] passwordBytes = pass.getBytes(StandardCharsets.UTF_8);
		byte[] hashedBytes = shaEncoder.digest(passwordBytes);
		
		String base64Hash = Base64.getEncoder().encodeToString(hashedBytes);
		
		return base64Hash;
	}
}
