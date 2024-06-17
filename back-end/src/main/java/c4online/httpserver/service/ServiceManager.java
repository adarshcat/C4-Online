package c4online.httpserver.service;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServiceManager {
	public final static String homeHandler = "/home";
	public final static String playHandler = "/play";
	public final static String userInfoHandler = "/userinfo";

	final String homeHtmlPath = "service/home/home.html";
	final String playHtmlPath = "";
	
	ServletContextHandler servletContext;

	public ServiceManager(ServletContextHandler _servletContext) {
		servletContext = _servletContext;
	}

	public void attach() {
		HomeServlet homeServlet = new HomeServlet(homeHtmlPath);
		UserInfoServlet userInfoServlet = new UserInfoServlet();
		
		servletContext.addServlet(new ServletHolder(homeServlet), homeHandler);
		servletContext.addServlet(new ServletHolder(userInfoServlet), userInfoHandler);
	}
}
