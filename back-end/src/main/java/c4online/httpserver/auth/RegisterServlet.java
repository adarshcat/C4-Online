package c4online.httpserver.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import c4online.db.DatabaseManager;
import c4online.httpserver.WebPage;
import c4online.security.Security;

@SuppressWarnings("serial")
public class RegisterServlet extends WebPage{
	public RegisterServlet(String _htmlFilePath) {
		super(_htmlFilePath);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String username = req.getParameter("username");
		String email = req.getParameter("email");
		String password = req.getParameter("password");

		if (username != null && email != null && password != null) {
			// try authenticating the user
			boolean doesUserExist = DatabaseManager.userdb.doesUserExist(username);
			boolean doesEmailExist = DatabaseManager.userdb.doesEmailExist(email);
			
			if (doesUserExist || doesEmailExist) {
				// email or username already exist, abort the registration
				System.out.println("Username/Email already present");
				return;
			}
			
			String passwordHash = Security.passToHash(password);
			
			System.out.println(DatabaseManager.userdb.addAccount(username, email, passwordHash));
		}
	}
}
