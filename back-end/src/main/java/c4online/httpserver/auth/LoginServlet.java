package c4online.httpserver.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import c4online.db.DatabaseManager;
import c4online.httpserver.WebPage;
import c4online.security.Security;

@SuppressWarnings("serial")
public class LoginServlet extends WebPage{

	public LoginServlet(String _htmlFilePath) {
		super(_htmlFilePath);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String usernameOrEmail = req.getParameter("usernameOrEmail");
		String password = req.getParameter("password");

		if (usernameOrEmail != null && password != null) {
			// try authenticating the user
			String passwordHash = Security.passToHash(password);
			int userId = DatabaseManager.userdb.validateAccount(usernameOrEmail, passwordHash);
			
			System.out.println(passwordHash);
			System.out.println(userId);
		}
	}
}
