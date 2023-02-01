package com.mtvu.websocketserver.service;

import com.mtvu.websocketserver.domain.session.SessionWrapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionManagementService {

    public static final Logger LOGGER = Logger.getLogger(SessionManagementService.class);

    private final Map<String, SessionWrapper> sessions = new ConcurrentHashMap<>();

    public void registerSession(String userId, long tokenExpirationTime , Session session) {
        sessions.put(userId, new SessionWrapper(userId, session, tokenExpirationTime));
    }

    public Collection<SessionWrapper> getSessions() {
        return sessions.values();
    }

    public SessionWrapper getSession(String userId) {
        return sessions.get(userId);
    }

    public void deactivateSession(String userId) {
        try {
            var session = getSession(userId);
            session.getSession().close();
        } catch (IOException e) {
            LOGGER.debugv("Exception occurred when deactivating user {}, ", userId, e);
        } finally {
            sessions.remove(userId);
        }
    }
}
