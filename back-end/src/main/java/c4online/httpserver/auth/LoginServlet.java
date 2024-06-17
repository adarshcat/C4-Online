package c4online.httpserver.auth;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import c4online.db.DatabaseManager;
import c4online.httpserver.WebPage;
import c4online.security.Security;
import c4online.sessions.SessionManager;
import c4online.sessions.User;

@SuppressWarnings("serial")
public class LoginServlet extends WebPage{

	public LoginServlet(String _htmlFilePath) {
		super(_htmlFilePath, WebPage.auth_type.AUTH_ONLY_NO_SESSION);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String usernameOrEmail = req.getParameter("usernameOrEmail");
		String password = req.getParameter("password");

		if (usernameOrEmail != null && password != null) {
			// try authenticating the user
			String passwordHash = Security.passToHash(password);
			int userId = DatabaseManager.userdb.validateAccount(usernameOrEmail, passwordHash);
			
			if (userId != -1) {
				// create User data structure from the database and create a session with it
				User user = DatabaseManager.userdb.getUserDataById(userId);
				String sessionId = SessionManager.createSession(user);
				
				// Update last login for the user
				DatabaseManager.userdb.updateLastLogin(userId);
				
				// store the create session id as a cookie in the client's browser
				Cookie loginCookie = new Cookie(SessionManager.sessionCookieId, sessionId);
				loginCookie.setMaxAge(SessionManager.sessionAge);
				resp.addCookie(loginCookie);
			} else {
				// login failed because username/Email or password didn't match
				try {
					resp.sendRedirect(AuthManager.loginHandler+"?status=failed");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
