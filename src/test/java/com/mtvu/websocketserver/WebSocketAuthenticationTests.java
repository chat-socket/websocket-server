package com.mtvu.websocketserver;

import com.mtvu.websocketserver.domain.Message;
import com.mtvu.websocketserver.domain.TransferMessage;
import com.mtvu.websocketserver.mocks.AuthenticationUserMocks;
import com.mtvu.websocketserver.utility.LoggingStompSessionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Collections;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * @author mvu
 * @project chat-socket
 **/
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketAuthenticationTests {
    @LocalServerPort
    private int rdmServerPort;
    private WebSocketStompClient stompClient;
    private StompSession session;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Value("${jms.messaging.topic}")
    String messagingTopic;


    @BeforeEach
    public void setupMockServer() {
        var webSocketClient = new StandardWebSocketClient();
        var sockJsClient = new SockJsClient(Collections.singletonList(new WebSocketTransport(webSocketClient)));
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        var mockAuthentication = AuthenticationUserMocks.getAuth("user1", "sample");
        when(authenticationProvider.authenticate(any())).thenReturn(mockAuthentication);
    }

    @AfterEach
    public void tearDownServer() {
        if (session != null) {
            session.disconnect();
        }
    }

    @Test
    void performWhenNoBearerTokenThenDeniesAccess() {
        try {
            System.out.print("Current port: " + rdmServerPort);
            session = stompClient.connect(String.format("ws://localhost:%d/websocket", rdmServerPort),
                    new StompSessionHandlerAdapter(){}).get(5, SECONDS);
            Assert.isTrue(false, "An exception should be thrown before reaching this line");
        } catch (Exception ex) {
            String message = ex.getMessage();
            Assert.isTrue(message.contains("Connection closed"), "Connection should be closed due to AccessDenied");
        }
    }

    @Test
    void performWhenHasValidBearerTokenThenAllowsAccess() throws Exception {
        var handshakeHeaders = new WebSocketHttpHeaders();
        var connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer bearer-token-mocked");
        var message = "myMessage";
        session = stompClient.connect(String.format("ws://localhost:%d/websocket", rdmServerPort), handshakeHeaders,
            connectHeaders, new LoggingStompSessionHandler(Message.class) {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    session.subscribe("/topic/message", this);
                    session.send("/app/message", new Message("test", message));
                }
            }).get(5, SECONDS);
        jmsTemplate.setReceiveTimeout(10_000);

        var response = jmsTemplate.receiveAndConvert(messagingTopic);
        assert response instanceof TransferMessage;
        String receivedMessage = ((TransferMessage) response).getMessage();
        Assert.isTrue(message.equals(receivedMessage), message + " vs " + receivedMessage);
    }
}
