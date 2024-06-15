package c4online.httpserver;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.eclipse.jetty.servlet.ServletContextHandler;

public class HttpWebServer {
	private final int PORT;
	Server server;
	
	@SuppressWarnings("serial")
	public HttpWebServer(int _port) {
		PORT = _port;
		
		server = new Server(PORT);
		
		// create and attach servlets for handling http requests
		ServletContextHandler servletContext = new ServletContextHandler();
		servletContext.setContextPath("/");
		
		servletContext.addServlet(new ServletHolder(new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
				
			}
			
		 }), "/");
		
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
