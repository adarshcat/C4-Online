package c4online.httpserver.service;

import c4online.game.GameManager;
import c4online.httpserver.WebPage;
import c4online.httpserver.auth.AuthManager;
import c4online.sessions.SessionManager;
import c4online.sessions.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PlayServlet extends WebPage{

	public PlayServlet(String _htmlFilePath) {
		super(_htmlFilePath, WebPage.auth_type.AUTH_SESSION_REQ, AuthManager.loginHandler);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		super.doGet(req, resp);

		String value = req.getParameter("autoplay");

		if (value != null) return; // If autoplay url param already exists, redirect is already done, leave

		Cookie[] cookies = req.getCookies();
		Cookie sessionCookie = getSessionCookie(cookies);

		if (sessionCookie != null) {
			User user = SessionManager.getUserFromSessionId(sessionCookie.getValue());
			GameManager.player_state gameSessionExists = GameManager.doesConnectionExist(user.id);

			if (gameSessionExists != GameManager.player_state.NONE){
				// This means they already have a active game session to their name
                try {
                    resp.sendRedirect(ServiceManager.playHandler+"?autoplay=true");
                } catch (IOException ignored) {
                }
            }
		}
	}
}
