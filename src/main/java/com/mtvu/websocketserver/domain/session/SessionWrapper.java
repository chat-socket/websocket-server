package com.mtvu.websocketserver.domain.session;

import lombok.Getter;

import javax.websocket.Session;
import java.time.Instant;

@Getter
public class SessionWrapper {
    private final String userId;
    private final Session session;

    private String pingMessage;
    private boolean isAlive;
    private final Instant tokenExpirationTime;

    public SessionWrapper(String userId, Session session, long tokenExpirationTime) {
        this.userId = userId;
        this.session = session;
        this.tokenExpirationTime = Instant.ofEpochSecond(tokenExpirationTime);
        this.isAlive = true;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    public boolean isTokenExpired() {
        return Instant.now().isAfter(this.tokenExpirationTime);
    }

    public void setPingMessage(String pingMessage) {
        this.pingMessage = pingMessage;
    }

    public String getPingMessage() {
        return pingMessage;
    }
}
