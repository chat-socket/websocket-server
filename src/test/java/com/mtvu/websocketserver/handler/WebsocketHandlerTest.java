package com.mtvu.websocketserver.handler;

import com.mtvu.websocketserver.domain.GenericMessage;
import com.mtvu.websocketserver.domain.MessageAction;
import com.mtvu.websocketserver.domain.message.ChatMessage;
import com.mtvu.websocketserver.domain.message.MessageType;
import com.mtvu.websocketserver.domain.message.TextMessageContent;
import com.mtvu.websocketserver.config.KafkaTestResourceLifecycleManager;
import com.mtvu.websocketserver.jackson.GenericMessageEncoder;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource;
import io.smallrye.reactive.messaging.kafka.Record;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;


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
    public void whenNoValidAccessTokenThenRejectTheConnectionRequest() {
        Assertions.assertThrows(IOException.class, () -> {
            try (Session ignored = openConnection("invalid-access-token")) {
                Assertions.fail("Invalid access token should be rejected");
            }
        });
    }

    @Test
    public void whenHasValidAccessTokenThenEstablishTheConnection() throws Exception {
        var username = "alice";
        var accessToken = OidcWiremockTestResource.getAccessToken(username, Set.of("user"));

        try (Session ignored = openConnection(accessToken)) {
            Assertions.assertEquals("READY", MESSAGES.poll(10, TimeUnit.SECONDS));
        }
    }

    @Test
    public void whenUserReceiveAMessageFromKafkaThenDeliverItToUser() throws Exception {
        var username = "alice";
        var accessToken = OidcWiremockTestResource.getAccessToken(username, Set.of("user"));

        try (Session ignored = openConnection(accessToken)) {
            Assertions.assertEquals("READY", MESSAGES.poll(10, TimeUnit.SECONDS));

            InMemorySource<Record<String, String>> messagingTopicIn = connector.source("messaging-topic-in");
            String notification = "{\"from\":\"alice\"}";
            messagingTopicIn.send(Record.of(username, notification));

            Assertions.assertEquals(notification, MESSAGES.poll(10, TimeUnit.SECONDS));
        }
    }


    @Test
    public void whenUserSendAMessageThenPublishToKafkaTopic() throws Exception {
        var username = "alice";
        var accessToken = OidcWiremockTestResource.getAccessToken(username, Set.of("user"));

        try (Session session = openConnection(accessToken)) {
            Assertions.assertEquals("READY", MESSAGES.poll(10, TimeUnit.SECONDS));
            GenericMessage message = ChatMessage.builder()
                    .messageType(MessageType.COUPLE)
                    .attachments(new ArrayList<>())
                    .date(OffsetDateTime.now())
                    .replyTo(null)
                    .sender(username)
                    .groupId("direct:abc")
                    .content(new TextMessageContent("Bla"))
                    .build();
            message.setChannel("message");
            message.setMessageAction(MessageAction.CREATE);

            session.getBasicRemote().sendObject(message);

            InMemorySink<Record<String, ChatMessage>> sink = connector.sink("messaging-topic-out");

            await().<List<? extends Message<Record<String, ChatMessage>>>>until(sink::received, t -> t.size() == 1);
            var messageReceived = sink.received().get(0).getPayload();
            Assertions.assertNotNull(messageReceived);
            Assertions.assertEquals(username, messageReceived.key());
            Assertions.assertEquals(MessageType.COUPLE, messageReceived.value().getMessageType());
        }
    }

    private Session openConnection(String accessToken) throws DeploymentException, IOException {
        var configBuilder = ClientEndpointConfig.Builder.create();
        List<String> subProtocols = new ArrayList<>();
        subProtocols.add("access_token");
        subProtocols.add(accessToken);
        configBuilder.preferredSubprotocols(subProtocols);
        configBuilder.encoders(List.of(GenericMessageEncoder.class));
        ClientEndpointConfig clientConfig = configBuilder.build();
        var container = ContainerProvider.getWebSocketContainer();
        return container.connectToServer(Client.class, clientConfig, uri);
    }

    public static class Client extends Endpoint {

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            MESSAGES.add("READY");
            session.addMessageHandler(String.class, MESSAGES::add);
        }
    }
}