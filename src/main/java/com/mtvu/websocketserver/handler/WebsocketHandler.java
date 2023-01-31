package com.mtvu.websocketserver.handler;


import com.mtvu.websocketserver.config.WebSocketSecurityConfigurator;
import com.mtvu.websocketserver.domain.GenericMessage;
import com.mtvu.websocketserver.jackson.GenericMessageDecoder;
import com.mtvu.websocketserver.jackson.GenericMessageEncoder;
import com.mtvu.websocketserver.service.SessionManagementService;
import io.quarkus.arc.All;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Authenticated
@ServerEndpoint(
        value = "/ws",
        configurator = WebSocketSecurityConfigurator.class,
        encoders = { GenericMessageEncoder.class },
        decoders = { GenericMessageDecoder.class })
public class WebsocketHandler {

    private JsonWebToken principal;

    private final Map<Class<? extends GenericMessage>, GenericMessageHandler<? extends GenericMessage>> handlerMap;

    private SessionManagementService sessionManagementService;

    public WebsocketHandler(@All List<GenericMessageHandler<? extends GenericMessage>> handlers, JsonWebToken principal,
                            SessionManagementService sessionManagementService) {
        handlerMap = new ConcurrentHashMap<>();
        for (var genericMessageHandler : handlers) {
            handlerMap.put(genericMessageHandler.handleMessageType(), genericMessageHandler);
        }
        this.principal = principal;
        this.sessionManagementService = sessionManagementService;
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessionManagementService.registerSession(principal.getName(), principal.getExpirationTime(), session);
    }

    @OnClose
    public void onClose() {
        var username = principal.getName();
        sessionManagementService.deactivateSession(username);
    }

    @OnError
    public void onError(Throwable throwable) {
        var username = principal.getName();
        sessionManagementService.deactivateSession(username);
    }

    @OnMessage
    public void onMessage(GenericMessage message) {
        var username = principal.getName();
        GenericMessageHandler handler = handlerMap.get(message.getClass());
        handler.handleMessage(username, message);
    }

    @OnMessage
    public void onPongMessage(PongMessage message) {
        var username = principal.getName();
        var pongMessage = StandardCharsets.UTF_8.decode(message.getApplicationData()).toString();
        var session = sessionManagementService.getSession(username);
        if (session.getPingMessage().equals(pongMessage)) {
            session.setAlive(true);
        }
    }

}
