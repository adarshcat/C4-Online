package c4online.httpserver;

import org.eclipse.jetty.server.SameFileAliasChecker;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import c4online.httpserver.auth.AuthManager;

import org.eclipse.jetty.servlet.ServletContextHandler;

public class HttpWebServer {
	private final int PORT;
	Server server;

	public HttpWebServer(int _port) {
		PORT = _port;

		server = new Server(PORT);

		// create and attach servlets for handling http requests
		ServletContextHandler servletContext = new ServletContextHandler();
		servletContext.setContextPath("/");

		// create a resource handler for serving web pages
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase("src/main/resources/front-end");
		
		ContextHandler resourceContext = new ContextHandler("/front-end");
		resourceContext.setHandler(resourceHandler);
		resourceContext.addAliasCheck(new SameFileAliasChecker());

		server.setHandler(new HandlerList(resourceContext, servletContext));
		
		// Initialise and attach the user authentication manager
		AuthManager authManager = new AuthManager(servletContext);
		authManager.attach();
	}

	public void run() throws Exception {
		server.start();
		server.join();
	}
}
