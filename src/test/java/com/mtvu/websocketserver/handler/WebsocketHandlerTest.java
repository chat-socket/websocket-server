package com.mtvu.websocketserver.handler;

import com.mtvu.websocketserver.domain.GenericMessage;
import com.mtvu.websocketserver.domain.message.ChatMessage;
import com.mtvu.websocketserver.domain.message.MessageType;
import com.mtvu.websocketserver.domain.message.TextMessageContent;
import com.mtvu.websocketserver.handler.config.KafkaTestResourceLifecycleManager;
import com.mtvu.websocketserver.jackson.GenericMessageEncoder;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.awaitility.Awaitility.await;


import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.websocket.*;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;


@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
@QuarkusTestResource(OidcWiremockTestResource.class)
public class WebsocketHandlerTest {

    private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

    @TestHTTPResource("/ws")
    URI uri;

    @Inject
    @Any
    InMemoryConnector connector;

    @Test
    public void testWebsocketChat() throws Exception {
        var configBuilder = ClientEndpointConfig.Builder.create();
        var username = "alice";
        List<String> subProtocols = new ArrayList<>();
        subProtocols.add("access_token");
        subProtocols.add(OidcWiremockTestResource.getAccessToken(username, Set.of("user")));
        configBuilder.preferredSubprotocols(subProtocols);
        configBuilder.encoders(List.of(GenericMessageEncoder.class));
        ClientEndpointConfig clientConfig = configBuilder.build();
        var container = ContainerProvider.getWebSocketContainer();
        try (Session session = container.connectToServer(Client.class, clientConfig, uri)) {
            Assertions.assertEquals("READY", MESSAGES.poll(10, TimeUnit.SECONDS));
            GenericMessage message = ChatMessage.builder()
                    .messageType(MessageType.COUPLE)
                    .attachments(new ArrayList<>())
                    .date(OffsetDateTime.now())
                    .replyTo(null)
                    .sender(username)
                    .receiver("bob")
                    .content(new TextMessageContent("Bla"))
                    .build();
            message.setChannel("message");

            session.getBasicRemote().sendObject(message);

            InMemorySink<ChatMessage> messagingTopicOut = connector.sink("messaging-topic-out");

            await().<List<? extends Message<ChatMessage>>>until(messagingTopicOut::received, t -> t.size() == 1);
            ChatMessage messageReceived = messagingTopicOut.received().get(0).getPayload();
            Assertions.assertNotNull(messageReceived);
            Assertions.assertEquals(username, messageReceived.getSender());
        }
    }

    public static class Client extends Endpoint {

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            MESSAGES.add("READY");
        }
    }
}