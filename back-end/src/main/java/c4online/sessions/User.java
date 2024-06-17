package c4online.sessions;

import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class User {
	public int id;
	public String username;
	public String email;
	public Timestamp lastLogin;
	public Timestamp createdAt;

	public int rating;
	public int gamesPlayed;
	public int gamesWon;
	public int gamesLost;
	public int gamesDraw;
	
	public void printData() {
		System.out.print("id "+id+" ");
		System.out.print("username "+username+" ");
		System.out.print("email "+email+" ");
		System.out.print("lastLogin "+lastLogin.toString()+" ");
		System.out.println("createdAt "+createdAt.toString());
	}
	
	public String parseToJSON() {
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("username", username);
		map.put("email", email);
		map.put("createdAt", createdAt.toString());
		
		map.put("rating", String.valueOf(rating));
		map.put("gamesPlayed", String.valueOf(gamesPlayed));
		map.put("gamesWon", String.valueOf(gamesWon));
		map.put("gamesLost", String.valueOf(gamesLost));
		map.put("gamesDraw", String.valueOf(gamesDraw));
		
		String jsonData = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			jsonData = objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
		
		return jsonData;
	}
}
