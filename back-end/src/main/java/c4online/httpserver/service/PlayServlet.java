package c4online.httpserver.service;

import c4online.httpserver.WebPage;
import c4online.httpserver.auth.AuthManager;

@SuppressWarnings("serial")
public class PlayServlet extends WebPage{

	public PlayServlet(String _htmlFilePath) {
		super(_htmlFilePath, WebPage.auth_type.AUTH_SESSION_REQ, AuthManager.loginHandler);
	}

}
