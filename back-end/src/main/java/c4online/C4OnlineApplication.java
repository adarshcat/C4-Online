package c4online;

import c4online.httpserver.HttpWebServer;

public class C4OnlineApplication {

	public static void main(String[] args) {
		HttpWebServer httpWebServer = new HttpWebServer(80);

		try {
			httpWebServer.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
