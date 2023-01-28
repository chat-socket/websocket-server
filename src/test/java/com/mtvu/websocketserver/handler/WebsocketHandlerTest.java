package com.mtvu.websocketserver.handler;


import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@QuarkusTest
public class WebsocketHandlerTest {

    private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

    @TestHTTPResource("/ws")
    URI uri;

    @Test
    public void testWebsocketChat() throws Exception {
        var configBuilder = ClientEndpointConfig.Builder.create();
        configBuilder.configurator(new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put("Cookie", Arrays.asList("token=abc"));
            }
        });
        ClientEndpointConfig clientConfig = configBuilder.build();
        var container = ContainerProvider.getWebSocketContainer();
        try (Session session = container.connectToServer(Client.class, clientConfig, uri)) {
            Assertions.assertEquals("CONNECT", MESSAGES.poll(10, TimeUnit.SECONDS));
            Assertions.assertEquals("User stu joined", MESSAGES.poll(10, TimeUnit.SECONDS));
            session.getAsyncRemote().sendText("hello world");
            Assertions.assertEquals(">> stu: hello world", MESSAGES.poll(10, TimeUnit.SECONDS));
        }
    }

    public static class Client extends Endpoint {

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            MESSAGES.add("CONNECT");
            final RemoteEndpoint.Basic remote = session.getBasicRemote();
            session.addMessageHandler(String.class, text -> {
                try {
                    remote.sendText("Got your message (" + text + "). Thanks !");
                } catch (IOException ioe) {
                    // handle send failure here
                }
            });
            // Send a message to indicate that we are ready,
            // as the message handler may not be registered immediately after this callback.
            session.getAsyncRemote().sendText("_ready_");
        }
    }
}