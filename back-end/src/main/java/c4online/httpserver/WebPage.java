package c4online.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import c4online.sessions.SessionManager;

@SuppressWarnings("serial")
public class WebPage extends HttpServlet{
	static final String ROOT = "src/main/resources/front-end/";
	final protected String htmlFilePath;
	
	public enum auth_type{AUTH_SESSION_REQ, AUTH_ALLOW_ALL, AUTH_ONLY_NO_SESSION};
	final auth_type authRequired;
	private final String invalidAuthFallback;

	public WebPage(String _htmlFilePath, auth_type _authRequired) {
		htmlFilePath = _htmlFilePath;
		authRequired = _authRequired;
		invalidAuthFallback = null;
	}
	
	public WebPage(String _htmlFilePath, auth_type _authRequired, String _invalidAuthFallback) {
		htmlFilePath = _htmlFilePath;
		authRequired = _authRequired;
		invalidAuthFallback = _invalidAuthFallback;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] cookies = req.getCookies();
		Cookie sessionCookie = getSessionCookie(cookies);
		
		if (sessionCookie == null && authRequired == auth_type.AUTH_SESSION_REQ) {
			// If session cookie is not present and the session is required, fallback/error and return
			fallbackOrError(resp);
			return;
		} else if (sessionCookie != null && authRequired == auth_type.AUTH_ONLY_NO_SESSION) {
			// If session cookie is present and the session is allowed for not required, fallback/error and return
			fallbackOrError(resp);
			return;
		}
		
		serveHtml(resp);
	}

	protected void serveHtml(HttpServletResponse resp){
		String responseHtml = "";

		try (BufferedReader br = Files.newBufferedReader(Paths.get(ROOT + htmlFilePath))) {
			for (String line; (line = br.readLine()) != null;) {
				responseHtml += line+"\n";
			}
		} catch (Exception e) {
			System.err.println("Unable to open html file at: " + ROOT+htmlFilePath);
			e.printStackTrace();
		}

		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		try {
			resp.getWriter().write(responseHtml);
		} catch (IOException e) {
			System.err.println("Unable to serve opened html file");
			e.printStackTrace();
		}
	}
	
	private void fallbackOrError(HttpServletResponse resp) {
		try {
			if (invalidAuthFallback == null) {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			} else {
				resp.sendRedirect(invalidAuthFallback);
				resp.setStatus(HttpServletResponse.SC_OK);
			}
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
