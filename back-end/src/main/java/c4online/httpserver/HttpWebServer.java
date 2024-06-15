package c4online.httpserver;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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

		// Initialise and attach the user authentication manager
		AuthManager authManager = new AuthManager(servletContext);
		authManager.attach();

		// create a resource handler for serving web pages
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(false);
		resourceHandler.setResourceBase("src/main/resources/front-end");

		ContextHandler resourceContext = new ContextHandler();
		resourceContext.setHandler(resourceHandler);

		server.setHandler(new ContextHandlerCollection(servletContext, resourceContext));
	}

	public void run() throws Exception {
		server.start();
		server.join();
	}
}
