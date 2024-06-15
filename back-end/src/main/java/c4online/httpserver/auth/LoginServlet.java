package c4online.httpserver.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import c4online.httpserver.WebPage;

@SuppressWarnings("serial")
public class LoginServlet extends WebPage{

	public LoginServlet(String _htmlFilePath) {
		super(_htmlFilePath);
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String username = req.getParameter("username");
    	String password = req.getParameter("password");
    	String email = req.getParameter("email");
    	
    	if (username != null && password != null && email != null) {
    		// try authenticating the user
    		
    		System.out.println(username);
    		System.out.println(email);
    		System.out.println(password);
    	}
	}
}
