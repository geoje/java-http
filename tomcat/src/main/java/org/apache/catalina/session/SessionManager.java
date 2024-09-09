package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.HttpRequest;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        HttpSession session = SESSIONS.get(id);

        if (session == null || session.getAttribute("user") == null) {
            return null;
        }

        return session;
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    public HttpSession getSession(HttpRequest request) {
        String sessionId = request.cookies().get(JSession.COOKIE_NAME);
        if (sessionId == null) {
            return null;
        }

        return SESSIONS.get(sessionId);
    }
}
