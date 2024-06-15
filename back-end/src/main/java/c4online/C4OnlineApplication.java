package c4online;

import c4online.db.DatabaseManager;
import c4online.httpserver.HttpWebServer;
import c4online.security.Security;
import c4online.sessions.SessionManager;

public class C4OnlineApplication {

	public static void main(String[] args) {
		DatabaseManager.initialise(); // initialise the database
		SessionManager.initialise(); // initialise session manager
		Security.initialise(); // initialise the security class
		
		HttpWebServer httpWebServer = new HttpWebServer(80);

		try {
			httpWebServer.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
