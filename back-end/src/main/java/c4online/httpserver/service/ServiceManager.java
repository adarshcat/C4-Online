package c4online.httpserver.service;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServiceManager {
	public final static String homeHandler = "/home";
	public final static String playHandler = "/play";

	final String homeHtmlPath = "service/home/home.html";
	final String playHtmlPath = "";
	
	ServletContextHandler servletContext;

	public ServiceManager(ServletContextHandler _servletContext) {
		servletContext = _servletContext;
	}

	public void attach() {
		HomeServlet homeServlet = new HomeServlet(homeHtmlPath);
		
		servletContext.addServlet(new ServletHolder(homeServlet), homeHandler);
	}
}
