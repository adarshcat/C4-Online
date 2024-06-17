package c4online.httpserver.service;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import c4online.sessions.SessionManager;
import c4online.sessions.User;

@SuppressWarnings("serial")
public class UserInfoServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] requestCookies = req.getCookies();
		Cookie sessionCookie = getSessionCookie(requestCookies);
		
		if (sessionCookie != null) {
			// The client has a session cookie
			String sessionId = sessionCookie.getValue();
			if (SessionManager.doesSessionExist(sessionId)) {
				// The client has a valid session cookie/logged in
				handleValidSession(resp, sessionId);
			} else {
				// The client has a invalid session cookie/bad session
				handleInvalidSession(resp);
			}
		} else {
			// The client doesn't have a session cookie
			handleInvalidSession(resp);
		}
	}
	
	private void handleValidSession(HttpServletResponse resp, String sessionId) {
		User user = SessionManager.getUserFromSessionId(sessionId);
		String jsonData = user.parseToJSON();
		
		resp.setContentType("application/json");
		try {
			resp.getWriter().write(jsonData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	private void handleInvalidSession(HttpServletResponse resp) {
		try {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	protected Cookie getSessionCookie(Cookie[] cookies) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(SessionManager.sessionCookieId)) {
					if (SessionManager.doesSessionExist(cookie.getValue())) {
						return cookie;
					}
				}
			}
		}
		
		return null;
	}

}
