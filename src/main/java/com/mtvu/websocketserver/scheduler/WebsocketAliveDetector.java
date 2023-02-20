package com.mtvu.websocketserver.scheduler;

import com.mtvu.websocketserver.service.SessionManagementService;
import io.quarkus.scheduler.Scheduled;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@ApplicationScoped
public class WebsocketAliveDetector {

    @Inject
    SessionManagementService sessionManagementService;

    private static final Logger LOGGER = Logger.getLogger(WebsocketAliveDetector.class);

    private final Random random = new Random();

    @Scheduled(every="${chat-socket.websocket.check-alive}")
    void checkSessionsAlive() {
        for (var session : sessionManagementService.getSessions()) {
            if (!session.isAlive() || session.isTokenExpired()) {
                sessionManagementService.deactivateSession(session.getUserId());
            }
            try {
                // We first mark the session non-alive
                // Then, we will send a ping message to the client
                // If the client is alive, it should respond with Pong message
                // The server will check the pong message and re-activate the session
                session.setAlive(false);
                var pingMessage = String.valueOf(random.nextInt());
                session.setPingMessage(pingMessage);
                var pingBuffer = ByteBuffer.wrap(pingMessage.getBytes(StandardCharsets.UTF_8));
                session.getSession().getBasicRemote().sendPing(pingBuffer);
            } catch (IOException e) {
                // Something goes wrong with this websocket connection
                // The next interval will deactivate this session
                LOGGER.debugv("Unable to ping user {}", session.getUserId(), e);
            }
        }
    }
}
