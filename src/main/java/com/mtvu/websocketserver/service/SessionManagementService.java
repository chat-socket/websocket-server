package com.mtvu.websocketserver.service;

import com.mtvu.websocketserver.domain.session.SessionWrapper;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionManagementService {

    private Map<String, SessionWrapper> sessions = new ConcurrentHashMap<>();

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
        sessions.remove(userId);
    }
}
