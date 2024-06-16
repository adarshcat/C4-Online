package c4online.httpserver.auth;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class AuthManager {
	final static String loginHandler = "/login";
	final static String registerHandler = "/register";

	final String loginHtmlPath = "auth/login/login.html";
	final String registerHtmlPath = "auth/register/register.html";
	
	ServletContextHandler servletContext;

	public AuthManager(ServletContextHandler _servletContext) {
		servletContext = _servletContext;
	}

	public void attach() {
		LoginServlet loginServlet = new LoginServlet(loginHtmlPath);
		RegisterServlet registerServlet = new RegisterServlet(registerHtmlPath);

		servletContext.addServlet(new ServletHolder(loginServlet), loginHandler);
		servletContext.addServlet(new ServletHolder(registerServlet), registerHandler);
	}
}
