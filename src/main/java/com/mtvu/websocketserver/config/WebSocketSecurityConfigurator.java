package com.mtvu.websocketserver.config;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;

public class WebSocketSecurityConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        try {
            String subProtocol = request.getHeaders().get("Sec-WebSocket-Protocol").get(0).split(",")[0];
            response.getHeaders().put("sec-websocket-protocol", List.of(subProtocol));
        } catch (Throwable ignore) {
        }
    }
}
