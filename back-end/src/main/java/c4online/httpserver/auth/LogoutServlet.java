package c4online.httpserver.auth;

import c4online.httpserver.WebPage;
import c4online.httpserver.service.ServiceManager;
import c4online.sessions.SessionManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends WebPage {

    public LogoutServlet() {
        super("", auth_type.AUTH_SESSION_REQ, ServiceManager.homeHandler);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        Cookie sessionCookie = getSessionCookie(req.getCookies());
        if (sessionCookie == null){
            fallbackOrError(resp);
            return;
        }

        String sessionId = sessionCookie.getValue();

        SessionManager.deleteSessionBySessionId(sessionId);

        try {
            resp.sendRedirect(ServiceManager.homeHandler);
        } catch (IOException ignored) {
        }
    }
}
