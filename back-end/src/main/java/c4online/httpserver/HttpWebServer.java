package c4online.httpserver;

import org.eclipse.jetty.server.Server;

public class HttpWebServer {
	private final int PORT;
	Server server;
	
	public HttpWebServer(int _port) {
		PORT = _port;
		
		server = new Server(PORT);
		
	}
	
	public void run() throws Exception {
		
		server.start();
		server.join();
	}
}
