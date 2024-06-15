package c4online.httpserver.auth;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class AuthManager {
	final String loginHtmlPath = "auth/login/login.html";
	final String registerHtmlPath = "";
	
	ServletContextHandler servletContext;
	
	public AuthManager(ServletContextHandler _servletContext) {
		servletContext = _servletContext;
	}
	
	public void attach() {
		LoginServlet loginServlet = new LoginServlet(loginHtmlPath);
		
		servletContext.addServlet(new ServletHolder(loginServlet), "/login");
	}
}
