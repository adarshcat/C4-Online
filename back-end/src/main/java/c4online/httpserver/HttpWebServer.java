package c4online.httpserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import c4online.httpserver.auth.AuthManager;
import c4online.httpserver.service.ServiceManager;

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

		
		// convert any symlink or aliases to the front-end directory to a real path
		Path webRootPath = null;
		try {
			webRootPath = new File("src/main/resources/front-end").toPath().toRealPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// create a resource handler for serving web pages
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(webRootPath.toString());
		
		ContextHandler resourceContext = new ContextHandler("/front-end");
		resourceContext.setHandler(resourceHandler);

		server.setHandler(new HandlerList(resourceContext, servletContext));
		
		
		// Initialise and attach the user authentication manager
		AuthManager authManager = new AuthManager(servletContext);
		authManager.attach();
		
		// Initialise and attach the game pages manager
		ServiceManager gamePagesManager = new ServiceManager(servletContext);
		gamePagesManager.attach();
	}

	public void run() throws Exception {
		server.start();
		server.join();
	}
}
