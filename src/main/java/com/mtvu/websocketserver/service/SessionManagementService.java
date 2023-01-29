package com.mtvu.websocketserver.service;

import com.mtvu.websocketserver.domain.message.ChatMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionManagementService {

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void registerSession(String userId, Session session) {
        sessions.put(userId, session);
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public void sendMessage(String from, ChatMessage message) {

    }
}
