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
import c4online.websocket.GameWebSocket;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
				try {
					resp.sendRedirect(ServiceManager.homeHandler);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}), "");
		
		
		// Setup WebSocket handler
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(GameWebSocket.class);
            }
        };
        ServletContextHandler wsContext = new ServletContextHandler();
        wsContext.setContextPath("/");
        wsContext.setHandler(wsHandler);
        
		
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

		
		// attach all the handlers
		server.setHandler(new HandlerList(wsContext, resourceContext, servletContext));
		
		
		// Initialise and attach the user authentication manager
		AuthManager authManager = new AuthManager(servletContext);
		authManager.attach();
		
		// Initialise and attach the game pages manager
		ServiceManager serviceManager = new ServiceManager(servletContext);
		serviceManager.attach();
	}

	public void run() throws Exception {
		server.start();
		server.join();
	}
}
